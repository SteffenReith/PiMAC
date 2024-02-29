/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Wed Feb 14 10:39:22 CET 2024
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 */
import scala.sys.exit
import scala.util.Random

import spinal.lib._

import spinal.core._
import spinal.core.sim._

import scopt.OptionParser

object PipelinedMultiplySim {

  // Some simple conversion functions (all periods / ticks in this simulation are given in picoseconds)
  def ns2ps(x : Int) = 1000 * x
  def us2ps(x : Int) = 1000 * ns2ps(x)
  def ms2ps(x : Int) = 1000 * us2ps(x)

  def main(args: Array[String]) : Unit = {

    // The width of the tested muliplier
    val mWidth = 16

     // Use 100Mhz for the simulation
    val spinalConfig = SpinalConfig(defaultClockDomainFrequency = FixedFrequency(100 MHz))

    // Create a simple simulation environment
    SimConfig.workspacePath("gen/sim")
             .allOptimisation
             .withConfig(spinalConfig)
             .withWave
             .compile(new PipelinedMultiply(mWidth)).doSim(seed = 2) { dut =>

      // Give some general info about the simulation
      printf(s"INFO: Start simulation (Width of multiplier is ${mWidth})\n")
     
      // Do the simulation for 100 iterations cycles
      SimTimeout(100 * ns2ps(20) + 1000)

      // Create a 100MHz clock
      dut.clockDomain.forkStimulus(period = 1000) //ns2ps(10))

      // Print some information every real second
      dut.clockDomain.forkSimSpeedPrinter(1.0)

      // Feed 100 operands in the simulation
      for (i <- 0 until 100) {
      
        // Give some information 
        printf(s"INFO: Simulation step $i\n")
      
        // Feed new data in the simulation
        dut.io.a #= 10
        dut.io.b #= 11
        sleep(10000)//ns2ps(10))

        dut.io.a #= 0
        dut.io.b #= 0
        sleep(10000)//ns2ps(10))

      }

      // Give some information about the ended simulation
      println("INFO: Simulation terminated")

    }

  }

}
