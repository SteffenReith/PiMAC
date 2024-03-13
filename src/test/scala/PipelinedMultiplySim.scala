/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Wed Feb 14 10:39:22 CET 2024
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 */
import scala.sys.exit
import scala.util.Random
import scala.collection.{mutable => mut}

import scopt.OptionParser

import spinal.lib._

import spinal.core._
import spinal.core.sim._

object PipelinedMultiplySim {

  // Gives the smallest positive remainder r == x % n with 0 <= r < n
  private def unsignedMod(x : Int, n : Int) = (((x % n) + n) % n)

  def main(args: Array[String]) : Unit = {

    // Number of random tests
    var noOfRandomTests : Option[Int] = None

    // Period used for the simulation
    val simPeriod = 10

    // Holds the seed used for random number generation
    var simSeed : Option[Int] = None

    // The width of the tested multiplier
    var mWidth : Option[Int] = None

    // Queue used to delay the arguments until the results leave the pipeline
    var argsQueue = mut.Queue[(Int, Int, Int)]()

    // Create a new scopt parser
    val parser = new OptionParser[ArgsConfig]("PipelinedMultiplySim") {

      // A simple header for the help text
      head("PipelinedMultiplySim - A gate-level simulation for a simple pipelined multiplier", "")

      // Option to set the seed used for random number generation
      opt[Int]("simSeed").action {(s,c) => c.copy(simSeed = Some(s)) }
                         .text("Set the seed used for for random number generation")

      // Option to specify the number of random test
      opt[Int]("noOfRandomTests").action {(s,c) => c.copy(noOfRandomTests = Some(s)) }
                                 .text("Specify the number of simulated random tests")

      // Option to specify the width of the multiplier 
      opt[Int]("mWidth").action {(s,c) => c.copy(mWidth = Some(s)) }
                        .text("Set the width of the multiplier")

      // Help option
      help("help").text("print this text")

    }
    parser.parse(args, ArgsConfig(noOfRandomTests = Some(100),
                                  mWidth          = Some(16),
                                  simSeed         = Some(Random.nextInt))).map {cfg => 

      // Update the seed (if option is not set then use a random seed)
      simSeed = cfg.simSeed

      // Update the number of test 
      noOfRandomTests = cfg.noOfRandomTests

      // Update the width of the multiplier
      mWidth = cfg.mWidth

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
             .compile(new PipelinedMultiply(mWidth.get)).doSim(seed = simSeed.get) { dut =>

      // Some special corner-cases
      val specialTests = Array[(Int, Int, Int)]((((1 << mWidth.get) - 1), (((1 << mWidth.get) - 1)), (((1 << mWidth.get) - 1))), 
                                                (0, 0, 0), (0, 0, 1), 
                                                (1, 0, 0), (0, 1, 0), (1, 1, 0), (1, 0, 1), (0, 1, 1), (1, 1, 1),
                                                (0, 2, 0), (2, 0, 0), (0, 2, 1),(2, 0, 1),
                                                ((1 << (mWidth.get - 1)) - 1, (1 << (mWidth.get - 1)) - 1, 0), 
                                                (1 << (mWidth.get - 1), (1 << (mWidth.get - 1)) - 1, 0), 
                                                ((1 << (mWidth.get - 1)) - 1, (1 << (mWidth.get - 1)) - 1, 1), 
                                                (1 << (mWidth.get - 1), (1 << (mWidth.get - 1)) - 1, 1),
                                                ((1 << (mWidth.get - 1)) - 1, (1 << (mWidth.get - 1)) - 1, ((1 << mWidth.get) - 1)), 
                                                (1 << (mWidth.get - 1), (1 << (mWidth.get - 1)) - 1, ((1 << mWidth.get) - 1)))
                                              
      // Give some general info about the simulation
      printf(s"INFO: Start simulation (Width of multiplier is ${mWidth.get})\n")
      printf(s"INFO: Latency of simulated pipeline is ${dut.getLatency()} cycles\n")

      // Create a clock
      dut.clockDomain.forkStimulus(simPeriod)

      // Count the spent cycles 
      var cycles = 0
      dut.clockDomain.onRisingEdges { cycles = cycles + 1 }

      // Create a thread for adding / creating test data 
      val feeder = fork {

        // Give some information
        printf("INFO: Started a thread to create random test data\n")

        // Feed random and special operands in the simulation
        for (i <- 0 until noOfRandomTests.get + specialTests.length) {
      
          // Check for random test mode 
          val (argA, argB, argC) = if (i < noOfRandomTests.get) { 

            // Generate test data randomly
            (unsignedMod(Random.nextInt(), 1 << mWidth.get), unsignedMod(Random.nextInt(), 1 << mWidth.get), unsignedMod(Random.nextInt(), 1 << mWidth.get))
          
          } else {

            // Use provided test cases
            specialTests(i - noOfRandomTests.get)

          }

          // Type the testcase to the console
          printf(f"INFO: Feed test case #${i}%4d with A=${argA}%5d, B=${argB}%5d and C=${argC}%5d\n")

          // Put the test data to the delay queue
          argsQueue.enqueue((argA, argB, argC))

          // Feed the data into the simulation of the multiplier
          dut.io.a #= argA
          dut.io.b #= argB
          dut.io.c #= argC
          dut.clockDomain.waitSampling()

        }

      }

      // Create a thread to check the result of the simulation
      val eater = fork {

        // Give some information
        printf("INFO: Started a thread to check the result of the simulation\n")

        // Wait for result from simulated pipeline (one more step for first fed data)
        for (i <- 0 until dut.getLatency()) dut.clockDomain.waitSampling()

        // Short remark about the kind of test cases to be checked
        printf("INFO: Check random testss\n")

        // Check all testcases 
        for (i <- 0 until noOfRandomTests.get + specialTests.length) {

          // Wait for the next clock cycle
          dut.clockDomain.waitSampling()

          // Get data out of the delay queue
          val (a,b,c) = argsQueue.dequeue

          // Short remark about the kind of test cases to be checked
          if (i == noOfRandomTests.get) printf("INFO: Check special tests\n")

          // Check the result
          assert(((a.toLong * b.toLong) + c.toLong) == dut.io.result.toLong, s"Got ${dut.io.result.toLong}, expected ${(a.toLong * b.toLong) + c.toLong}\n")

          // Give some info
          printf(f"INFO: Eat test case #${i}%4d with A: ${a}%5d, B: ${b}%5d, C: ${c}%5d and Pipe: ${dut.io.result.toLong}%11d (Check: ${(a.toLong * b.toLong) + c.toLong}%11d)\n")

        }

        // Give some information about the number of spent cycles
        printf(s"INFO: Spent ${cycles} cycles\n")

      }

      // Wait until the complete simulation is done
      feeder.join()
      eater.join()

      // Give some information about the ended simulation
      println("INFO: Simulation terminated successfully!")

    }

  }

}
