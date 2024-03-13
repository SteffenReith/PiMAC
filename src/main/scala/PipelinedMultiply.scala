/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Tue Feb 13 22:29:34 CET 2024 
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 * Note:           Basic pipeline design by Charles Papon
 */
import scala.language.postfixOps

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._

// A simple muliply and add implementation which calculates a * b + c
class PipelinedMultiply(width : Int) extends Component {

  // Check for correct width
  assert(width > 1, message = "ERROR: Width of PipelinedMultiply has to be at least 2!")

  val io = new Bundle {

    // Arguments for the multiplier to calculate a * b + c
    val a = in UInt (width bits)
    val b = in UInt (width bits)
    val c = in UInt (width bits)

    // Result of the multiplication
    val result = out UInt (2 * width bits)

  }

  // No prefixes for module/port signals
  io.setName("")

  // Let's define the nodes for our pipeline. We have "width" stages
  private val nodes = Array.fill(width)(Node())

  // Create the connectors 
  private val connectors = Array.tabulate(width - 1)(i => StageLink(nodes(i), nodes(i + 1)))

  // Add the arguments to the first stage
  private val A = nodes(0).insert(io.a)
  private val B = nodes(0).insert(io.b)
  private val C = nodes(0).insert(io.c)

  // Declare the payload for each stage (the intermediate value grows) 
  private val ACC = Array.tabulate(width)(i => Payload(UInt(width + i + 1 bits)))
 
  // Init the accumulator on the first stage and add the ith bit-product on stage i
  for(i <- 0 until width; node = nodes(i)) new node.Area {
    ACC(i) := (i == 0).mux[UInt](C, ACC(i-1)) +^ (A << i).andMask(B(i))
  }

  // Get the result from the last node
  io.result := nodes.last(ACC.last)

  // Build the pipeline
  Builder(connectors)

  // Calculate the latency of the pipeline
  private val latAtoR = LatencyAnalysis(io.a, io.result)
  private val latBtoR = LatencyAnalysis(io.b, io.result)
  def getLatency() = latAtoR

  // Check consistent latency
  assert(LatencyAnalysis(io.a, io.result) == LatencyAnalysis(io.b, io.result), "ERROR: Incorrect latency analysis!")
  
  // Give some general info
  printf(s"INFO: Latency of pipeline is ${LatencyAnalysis(io.a, io.result)} cycles\n")
  
}

object PipelinedMultiply {

  // Make a synchronous reset and use the rising edge for the clock
  private val globalClockConfig = ClockDomainConfig(clockEdge        = RISING,
                                                    resetKind        = SYNC,
                                                    resetActiveLevel = HIGH)

  // Suppose that the design runs with 100 MHz
  private val globalFrequency = FixedFrequency(100 MHz)

  def main(args: Array[String]) : Unit = {

    // The width of the generated multiplier
    val mWidth = 16

    def elaborate(width : Int) : PipelinedMultiply = {

      // Create a multiplier instance
      new PipelinedMultiply(width)

    }

    // Generate VHDL
    SpinalConfig(mergeAsyncProcess            = true,
                 genVhdlPkg                   = true,
                 defaultConfigForClockDomains = globalClockConfig,
                 defaultClockDomainFrequency  = globalFrequency,
                 targetDirectory="gen/src/vhdl").generateVhdl(elaborate(mWidth)).printPruned()

    // Generate Verilog
    SpinalConfig(mergeAsyncProcess              = true,
                 onlyStdLogicVectorAtTopLevelIo = true,
                 defaultConfigForClockDomains   = globalClockConfig,
                 defaultClockDomainFrequency    = globalFrequency,
                 targetDirectory="gen/src/verilog").generateVerilog(elaborate(mWidth)).printPruned()

  }

}
