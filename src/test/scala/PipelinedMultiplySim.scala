/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Wed Feb 14 10:39:22 CET 2024
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 */
import scala.sys.exit
import scala.util.Random
import scala.collection.{mutable => mut}

import spinal.lib._

import spinal.core._
import spinal.core.sim._

import scopt.OptionParser
import scala.collection.immutable.Queue

object PipelinedMultiplySim {

  // Some simple conversion functions (all periods / ticks in this simulation are given in picoseconds)
  def ns2ps(x : Int) = 1000 * x
  def us2ps(x : Int) = 1000 * ns2ps(x)
  def ms2ps(x : Int) = 1000 * us2ps(x)

  // Gives a remainder r == x % n with 0 <= r < n
  def unsignedMod(x : Int, n : Int) = (((x % n) + n) % n)

  def main(args: Array[String]) : Unit = {

    // Number of single simulations 
    val simNum = 100

    // The width of the tested muliplier
    val mWidth = 16

    // Queue used to delay the arguments until the results leave the pipeline
    var argsQueue = mut.Queue[(Int,Int)]()

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
      printf(s"INFO: Latency of simulated pipline is ${dut.getLatency()} cycles\n")

      // Do the simulation for 100 iterations cycles
      SimTimeout(simNum * ns2ps(20) + 1000)

      // Create a 100MHz clock
      dut.clockDomain.forkStimulus(period = 1000) //ns2ps(10))

      // Print some information every real second
      dut.clockDomain.forkSimSpeedPrinter(1.0)

      // Create a thread for adding / creating test data 
      val feeder = fork {

        // Give some information
        printf("INFO: Started a thread to create random test data\n")

        // Feed 100 operands in the simulation
        for (i <- 0 until simNum) {
      
          // Generate test data randomly
          val argA = unsignedMod(Random.nextInt(), 1 << mWidth)
          val argB = unsignedMod(Random.nextInt(), 1 << mWidth)
          
          // Type the testcase to the console
          printf(f"INFO: Feed test case #${i}%3d with A=${argA}%5d and B=${argB}%5d\n")

          // Put the test data to the delay queue
          argsQueue.enqueue((argA, argB))

          // Feed the data into the simulation of the multiplier
          dut.io.a #= argA
          dut.io.b #= argB
          dut.clockDomain.waitSampling()

        }

      }

      // Create a thread to check the result of the simulation
      val eater = fork {

        // Give some information
        printf("INFO: Started a thread to check the result of the simulation\n")

        // Wait for result from simulated pipeline (one more step for first fed data)
        for (i <- 0 until dut.getLatency()) dut.clockDomain.waitSampling()

        // Check all testcases 
        for (i <- 0 until simNum) {

          // Wait for the next clock cycle
          dut.clockDomain.waitSampling()

          // Get data out of the delay queue
          val (a,b) = argsQueue.dequeue

          // Check the result
          assert((a.toLong * b.toLong) == dut.io.result.toLong, s"Got ${dut.io.result.toLong}, expected ${a.toLong * b.toLong}\n")

          // Give some info
          printf(f"INFO: Eat test case ${i} with A: ${a}, B: ${b} Pipe: ${dut.io.result.toLong} (Check: ${a.toLong * b.toLong})\n")

        }

      }

      // Wait until the complete simulation is done
      feeder.join()
      eater.join()

      // Give some information about the ended simulation
      println("INFO: Simulation terminated")

    }

  }

}
