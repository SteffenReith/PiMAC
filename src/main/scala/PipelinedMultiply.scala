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

    // Start the pipeline
    val start = in Bool()

    // Indicates a completed multiplication
    val ready = in Bool()

    // The arguments
    val ops = slave Stream(MOps(width))

    // Gives the multiplied number
    val result  = master Stream(UInt(2 * width bits))

  }

  // Create the init stage of our pipeline
  val initStage  = Node()

  // Create all other stages for the multiplication
  private val mStages = for (i <- 0 until width) yield {Node()}

  // Connect the stages by simple pipeline-registers
  private val stageLinks = for (i <- 0 until (width - 1)) yield {StageLink(mStages(i), mStages(i + 1))}

  // Connect the init stage with the first stage for multiplication
  private val initLink = StageLink(initStage, mStages(0))

  // Create payloads
  val OPS = Payload(MOps(width))
  val ACC = Payload(UInt(2 * width bits))

  // Bind the init stage to in circuits interface
  io.ops.ready    := initStage.ready
  initStage.valid := io.ops.valid
  initStage(OPS)  := io.ops.payload

  // Init the accumulator in init stage
  initStage(ACC) := 0

  // Create the computation in each stage
  for(i <- 1 until width - 1) yield {

    mStages(i + 1)(OPS) := mStages(i)(OPS)
    mStages(i + 1)(ACC) := mStages(i)(ACC) + 1

  }

  // Connect the end of the pipeline to the output stream
  mStages(width - 1).ready := io.result.ready
  io.result.valid             := mStages(width - 1).valid
  io.result.payload           := mStages(width - 1)(ACC)

  // Build the complete pipeline
  Builder(initLink, stageLinks: _*)

}

object PipelinedMultiply {

  // Make a synchronous reset and use the rising edge for the clock
  val globalClockConfig = ClockDomainConfig(clockEdge        = RISING,
                                            resetKind        = SYNC,
                                            resetActiveLevel = HIGH)

  def main(args: Array[String]) : Unit = {

    def elaborate : PipelinedMultiply = {

      // Create a multiplier instance
      new PipelinedMultiply(16)

    }

    // Generate VHDL
    SpinalConfig(mergeAsyncProcess = true,
                 genVhdlPkg = true,
                 defaultConfigForClockDomains = globalClockConfig,
                 targetDirectory="gen/src/vhdl").generateVhdl(elaborate).printPruned()

    // Generate Verilog
    SpinalConfig(mergeAsyncProcess = true,
                 defaultConfigForClockDomains = globalClockConfig,
                 targetDirectory="gen/src/verilog").generateVerilog(elaborate).printPruned()

  }

}
