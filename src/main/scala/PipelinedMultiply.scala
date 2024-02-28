/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Tue Feb 13 22:29:34 CET 2024 
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 */
import scala.language.postfixOps

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._

// A container for the two arguments of an multiplication
case class MOps(width : Int) extends Bundle {

   val opA = UInt(width bits)
   val opB = UInt(width bits)

}

class PipelinedMultiply(width : Int) extends Component {

  // Check for correct width
  assert(width > 1, message = "ERROR: Width of PipelinedMultiply has to be at least 2!")

  val io = new Bundle {

    // The two arguments to by multiplied
    val ops = slave Stream(MOps(width))

    // Gives the multiplied number
    val result = master Stream(UInt(2 * width bits))

  }

  // Create all stages of the pipeline
  val mStages = Array.fill(width + 1)(Node())

  // Link the stages together
  val stageLinks = for (i <- 0 until width) yield StageLink(mStages(i), mStages(i + 1))

  // Some syntactic sugar for areas bound to a node
  class NodeArea(at : Int) extends NodeMirror(mStages(at))

  // Insert data in the multiplier pipeline
  val firstStage = mStages.head
  val firstInserter = new firstStage.Area {

    // Insert the arguments 
    arbitrateFrom(io.ops)

    // Data (argument and computed results) to be moved through the pipeline
    val OPS = insert(io.ops.payload)
    val ACC = insert(UInt(2 * width bits) init(0))

  }

  // Create the computation for each stage
  var oldInserter = firstInserter
  for (i <- 1 until width + 1) yield {

    // Build the next stage
    var newInserter = new NodeArea(i + 1) {
     
      val OPS = insert(oldInserter.OPS)
      val ACC = insert(oldInserter.ACC)

    }

    oldInserter = newInserter

  }

  // Connect the end of the pipeline to the output stream
  val finalStage = mStages.last
  var finalInserter = new finalStage.Area {

    // Move the results to the output stream
    arbitrateTo(io.result)
   
    // Get the computed result
    io.result.payload := finalStage.ACC

  } 

  // Build the complete pipeline
  Builder(stageLinks)

}

object PipelinedMultiply {

  // Make a synchronous reset and use the rising edge for the clock
  val globalClockConfig = ClockDomainConfig(clockEdge        = RISING,
                                            resetKind        = SYNC,
                                            resetActiveLevel = HIGH)

  def main(args: Array[String]) : Unit = {

    def elaborate(width : Int) : PipelinedMultiply = {

      // Create a multiplier instance
      new PipelinedMultiply(width)

    }

    // Generate VHDL
    SpinalConfig(mergeAsyncProcess = true,
                 genVhdlPkg = true,
                 defaultConfigForClockDomains = globalClockConfig,
                 targetDirectory="gen/src/vhdl").generateVhdl(elaborate(16)).printPruned()

    // Generate Verilog
    SpinalConfig(mergeAsyncProcess = true,
                 defaultConfigForClockDomains = globalClockConfig,
                 targetDirectory="gen/src/verilog").generateVerilog(elaborate(16)).printPruned()

  }

}
