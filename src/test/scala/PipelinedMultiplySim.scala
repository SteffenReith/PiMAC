/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Wed Feb 14 10:39:22 CET 2024
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 */
import scala.sys.exit
import scala.util.Random

import spinal.core._
import spinal.core.sim._

import scopt.OptionParser

object PipelinedMultiplySim {

  def main(args: Array[String]) : Unit = {

    // The width of the tested muliplier
    val mWidth = 16

    printf(s"INFO: Start simulation (Width of multiplier is $mWidth)")

    // Create a simple simulation environment
    SimConfig.withFstWave.compile(new PipelinedMultiply(mWidth)).doSim(seed = 2) { dut =>

      val operands = MOps(width = mWidth)

      // Do the simulation for 10k cycles
      SimTimeout(10000)

      // Create a 100MHz clock
      dut.clockDomain.forkStimulus(10)

      // Indicate invalid operands
      dut.io.ops.valid #= false

      // Feed 100 operands in the simulation
      for (i <- 0 until 100) {
      
        // Give some information 
        printf(s"INFO: Simulation step $i")

        // Set random values 
        operands.opA #= Random.nextInt(1 << mWidth)
        operands.opB #= Random.nextInt(1 << mWidth) 
      
        // Wait a random time (maximal 1000 - 1 cycles)
        sleep(Random.nextInt(1000))

        // Wait until the input stream is ready to receive data
        waitUntil(dut.io.ops.ready.toBoolean)

        // Feed new data in the simulation
        dut.io.ops.payload := operands
        dut.io.ops.valid #= true
        sleep(1)
        dut.io.ops.valid #= false

      }

      // Give some information about the ended simulation
      println("INFO: Simulation terminated")

    }

  }

}
