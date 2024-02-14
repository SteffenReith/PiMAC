/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Tue Feb 13 22:29:34 CET 2024 
 * Project Name:   PipeMult - A demo for SpinalHDL's pipeline framework
 */
import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.misc.pipeline._

// A container for the two arguments of an multiplication
case class MultOps(width : Int) extends Bundle {

   val opA = UInt(width bits)
   val opB = UInt(width bits)

}

class PipeMult (width : Int) extends Component {

  // Check for correct width
  assert(width > 1, message = "ERROR: Width of PipeMult has to be at least 2!")

  val io = new Bundle {
   
    // Start the pipeline
    val start = in Bool()

    // Indicates a completed multiplication 
    val ready = in Bool()

    // The arguments
    val ops = slave Stream(MultOps(width))

    // Gives the multiplied number
    val result  = master Stream(UInt(2 * width bits))
 
  }

  // Create the init stage of our pipeline
  val initStage = Node()

  // Create all other stages for the multiplication
  val multStages = for (i <- 0 until width) yield Node()

  // Connect the stages by simple pipeline-registers
  val stageRegisters = for (i <- 0 until (width - 1)) yield StageLink(multStages[i], multStages[i + 1])

  // Connect the init stage with the first stage for multiplication
  StageLink(initStage, multStages[0])

  // Create payloads 
  val OPS = Payload(MultOps(width))
  val ACC = Payload(UInt(2 * width bits))

  // Bind the init stage to in circuits interface 
  io.dataA.ready  := initStage.ready
  initStage.valid := io.ops.valid
  initStage(OPS)  := io.ops.payload

  // Init the accumulator in init stage
  initStage(ACC) := 0

  // Create the computation in each stage
  for(i <- 1 until width - 1) yield {
    
    multStage[i + 1](OPS) := multStage[i](OPS)
    multStage[i + 1](ACC) := multStage[i](ACC)
  
  }

  // Connect the end of the pipline to the output stream
  multStage[width - 1].ready := io.result.ready
  io.result.valid            := multStage[width - 1].valid
  io.result.payload          := multStage[width - 1](ACC)

  // Build the complete pipeline
  Builder(multStages)

}

object PipeMult {

  // Make a synchronous reset and use the rising edge for the clock
  val globalClockConfig = ClockDomainConfig(clockEdge        = RISING,
                                            resetKind        = SYNC,
                                            resetActiveLevel = HIGH)

  def main(args: Array[String]) = {

    def elaborate : PipeMult = {

      // Create a multiplier instance
      new PipeMult(16)

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
rintPruned()

  }

}
