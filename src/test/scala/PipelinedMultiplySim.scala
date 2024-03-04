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

object PipelinedMultiplySim {

  // Gives the smallest positive remainder r == x % n with 0 <= r < n
  def unsignedMod(x : Int, n : Int) = (((x % n) + n) % n)

  def main(args: Array[String]) : Unit = {

    // Number of simulations steps 
    val simNum = 1000

    // Period used for the simulation
    val simPeriod = 10

    // The width of the tested multiplier
    val mWidth = 16

    // Queue used to delay the arguments until the results leave the pipeline
    var argsQueue = mut.Queue[(Int,Int)]()

    // Make a synchronous reset and use the rising edge for the clock
    val globalClockConfig = ClockDomainConfig(clockEdge        = RISING,
                                              resetKind        = SYNC,
                                              resetActiveLevel = HIGH)

     // Use 100 MHz for the simulation
    val spinalConfig = SpinalConfig(defaultClockDomainFrequency  = FixedFrequency(100 MHz),
                                    defaultConfigForClockDomains = globalClockConfig)

    // Create a simple simulation environment
    SimConfig.workspacePath("gen/sim")
             .allOptimisation
             .withConfig(spinalConfig)
             .withWave
             .withTimeScale(1 ns)
             .withTimePrecision(100 ps)
             .compile(new PipelinedMultiply(mWidth)).doSim(seed = 2) { dut =>

      // Give some general info about the simulation
      printf(s"INFO: Start simulation (Width of multiplier is ${mWidth})\n")
      printf(s"INFO: Latency of simulated pipeline is ${dut.getLatency()} cycles\n")

      // Create a clock
      dut.clockDomain.forkStimulus(simPeriod)

      // Do the simulation for as much iterations cycles a test cases (plus some spare cycles)
      SimTimeout(simPeriod * (simNum + 2 * dut.getLatency() + 10))

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
      println("INFO: Simulation terminated successfully!")

    }

  }

}
