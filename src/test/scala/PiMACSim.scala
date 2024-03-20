/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Wed Feb 14 10:39:22 CET 2024
 * Module Name:    PiMACSim - A simple testbench for PiMAC
 * Project Name:   PiMAC - A demo for SpinalHDL's pipeline framework
 */

import scala.sys.exit
import scala.util.Random
import scala.collection.{mutable => mut}

import scopt.OptionParser

import spinal.lib._

import spinal.core._
import spinal.core.sim._

import Reporter._

object PiMACSim {

  // Gives the smallest positive remainder r == x % n with 0 <= r < n
  private def unsignedMod(x : Int, n : Int) = (((x % n) + n) % n)

  def main(args: Array[String]) : Unit = {

    // Period used for the simulation
    val simPeriod = 10

    // Queue used to delay the arguments until the results leave the pipeline
    var argsQueue = mut.Queue[(Int, Int, Int)]()

    // Create a new scopt parser
    val parser = new OptionParser[SimArgsConfig]("PipelinedMultiplySim") {

      // A simple header for the help text
      head("PipelinedMultiplySim - A gate-level simulation for a simple pipelined multiplier", "")

      // Option to set the seed used for random number generation
      opt[Int]("simSeed").action {(s,c) => c.copy(simSeedArg = Some(s)) }
                         .text("Set the seed used for for random number generation")

      // Option to specify the number of random test
      opt[Int]("noOfRandomTests").action {(s,c) => c.copy(noOfRandomTestsArg = Some(s)) }
                                 .text("Specify the number of simulated random tests")

      // Option to specify the width of the multiplier 
      opt[Int]("mWidth").action {(s,c) => c.copy(mWidthArg = Some(s)) }
                        .text("Set the width of the multiplier")

      // Option to specify the verboseness
      opt[Boolean]("verbose").action {(v,c) => c.copy(verboseArg = Some(v)) }
                             .text("Give detailed information about performed tests")                

      // Help option
      help("help").text("print this text")

    }

    // Set the number of tests, the seed for random number and the width of the multiplier
    val (noOfRandomTests, simSeed, mWidth) = parser.parse(args, SimArgsConfig(noOfRandomTestsArg = Some(100),
                                                                              mWidthArg          = Some(4),
                                                                              simSeedArg         = Some(Random.nextInt),
                                                                              verboseArg         = Some(true))).map {cfg => 

      // Update the level of verboseness
      setVerboseness(cfg.verboseArg.get)

      // Return the calculated values
      (cfg.noOfRandomTestsArg.get, cfg.simSeedArg.get, cfg.mWidthArg.get)

    } getOrElse {

      // Terminate program with error-code (wrong argument / option)
      exit(1)

    }

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
             .compile(new PiMAC(mWidth)).doSim(seed = simSeed) { dut =>

      // Some special corner-cases
      val specialTests = Array[(Int, Int, Int)]((((1 << mWidth) - 1), (((1 << mWidth) - 1)), (((1 << mWidth) - 1))), 
                                                (0, 0, 0), (0, 0, 1), 
                                                (1, 0, 0), (0, 1, 0), (1, 0, 1), (0, 1, 1), (1, 1, 0), (1, 1, 1),
                                                (0, 2, 0), (2, 0, 0), (0, 2, 1), (2, 0, 1), (2, 2, 0), (2, 2, 1),
                                                ((1 << (mWidth - 1)) - 1, (1 << (mWidth - 1)) - 1, 0), 
                                                ( 1 << (mWidth - 1),      (1 << (mWidth - 1)) - 1, 0), 
                                                ((1 << (mWidth - 1)) - 1, (1 << (mWidth - 1)) - 1, 1), 
                                                ( 1 << (mWidth - 1),      (1 << (mWidth - 1)) - 1, 1),
                                                ((1 << (mWidth - 1)) - 1, (1 << (mWidth - 1)) - 1, ((1 << mWidth) - 1)), 
                                                ( 1 << (mWidth - 1),      (1 << (mWidth - 1)) - 1, ((1 << mWidth) - 1)))
                                              
      // Give some general info about the simulation
      printReport(s"Start simulation (Width of multiplier is ${mWidth})\n")
      printReport(s"Latency of simulated pipeline is ${dut.getLatency()} cycles\n")

      // Create a clock
      dut.clockDomain.forkStimulus(simPeriod)

      // Count the spent cycles 
      var cycles = 0
      dut.clockDomain.onRisingEdges { cycles = cycles + 1 }

      // Create a thread for adding / creating test data 
      val feeder = fork {

        // Give some information
        printReport("Started a thread to create random test data\n")

        // Feed random and special operands in the simulation
        for (i <- 0 until noOfRandomTests + specialTests.length) {
      
          // Check for random test mode 
          val (argA, argB, argC) = if (i < noOfRandomTests) { 

            // Generate test data randomly
            (unsignedMod(Random.nextInt(), 1 << mWidth), unsignedMod(Random.nextInt(), 1 << mWidth), unsignedMod(Random.nextInt(), 1 << mWidth))
          
          } else {

            // Use provided special test cases
            specialTests(i - noOfRandomTests)

          }

          // Type the testcase to the console
          printReport(f"Feed test case #${i}%4d with A=${argA}%5d, B=${argB}%5d and C=${argC}%5d\n")

          // Put the test data to the delay queue
          argsQueue.enqueue((argA, argB, argC))

          // Feed the data into the simulation of the multiplier and wait for the rising edge
          dut.io.a #= argA
          dut.io.b #= argB
          dut.io.c #= argC
          dut.clockDomain.waitSampling()

        }

      }

      // Create a thread to check the result of the simulation
      val eater = fork {

        // Give some information
        printReport("Started a thread to check the result of the simulation\n")

        // Wait for result from simulated pipeline (one more step for first fed data)
        for (i <- 0 until dut.getLatency()) dut.clockDomain.waitSampling()

        // Short remark about the kind of test cases to be checked
        printReport("Check random testss\n")

        // Check all testcases 
        for (i <- 0 until noOfRandomTests + specialTests.length) {

          // Wait for the next clock cycle
          dut.clockDomain.waitSampling()

          // Get data out of the delay queue
          val (a,b,c) = argsQueue.dequeue

          // Short remark about the kind of test cases to be checked
          if (i == noOfRandomTests) printReport("Check special tests\n")

          // Check the result (result must be a * b + c)
          assert(((a.toLong * b.toLong) + c.toLong) == dut.io.result.toLong, s"Got ${dut.io.result.toLong}, expected ${(a.toLong * b.toLong) + c.toLong}\n")

          // Give some info
          printReport(f"Eat test case #${i}%4d with A: ${a}%5d, B: ${b}%5d, C: ${c}%5d and Pipe: ${dut.io.result.toLong}%11d (Check: ${(a.toLong * b.toLong) + c.toLong}%11d)\n")

        }

        // Give some information about the number of spent cycles
        printReport(s"Spent ${cycles} cycles\n")

      }

      // Wait until the complete simulation is done
      feeder.join()
      eater.join()

      // Give some information about the ended simulation
      setVerboseness(true)
      printReport("Simulation terminated successfully!\n")

    }

  }

}
