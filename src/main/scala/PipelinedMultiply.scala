/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Tue Feb 13 22:29:34 CET 2024 
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 * Note:           Basic design by Charles Papon
 */
import scala.language.postfixOps

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._

class PipelinedMultiply(width : Int) extends Component {

  // Check for correct width
  assert(width > 1, message = "ERROR: Width of PipelinedMultiply has to be at least 2!")

  val io = new Bundle {
    val a      = in  UInt(width bits)
    val b      = in  UInt(width bits)
    val result = out UInt(2*width bits)
  }.setName("")

  // Let's define the Nodes for our pipeline
  val nodes = Array.fill(width)(Node())
  val connectors = Array.tabulate(width-1)(i => StageLink(nodes(i), nodes(i + 1)))
  val A = nodes(0).insert(io.a)
  val B = nodes(0).insert(io.b)
  
  val ACC = Array.tabulate(width)(i => Payload(UInt(width + i + 1 bits)))
  for(i <- 0 until width; node = nodes(i)) new node.Area {
    ACC(i) := (i == 0).mux[UInt](0, ACC(i-1)) +^ (A << i).andMask(B(i))
  }

  io.result := nodes.last(ACC.last)

  Builder(connectors)

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

