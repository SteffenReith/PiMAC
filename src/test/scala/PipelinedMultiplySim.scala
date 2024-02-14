/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Wed Feb 14 10:39:22 CET 2024
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 */
import scala.sys.exit
import scala.util.Random._

import spinal.core._
import spinal.core.sim._
import spinal.lib.sim.{StreamMonitor, StreamDriver, StreamReadyRandomizer, ScoreboardInOrder}

import scopt.OptionParser

object PipelinedMultiplySim {

  def main(args: Array[String]): Unit = {

    // Create a simple simulation environment
    SimConfig.withFstWave.compile(new PipelinedMultiply(16)).doSim(seed = 2) { dut =>

      val a = MOps(16)
      a.opA := 1
      a.opB := 1

      dut

      dut.clockDomain.forkStimulus(10)
      dut.clockDomain.waitActiveEdgeWhere(scoreboard.matches == 100)

    }

  }

}

object Example extends App {
  val dut = SimConfig.withWave.compile(StreamFifo(Bits(8 bits), 2))

  dut.doSim("simple test") { dut =>
    SimTimeout(10000)

    val scoreboard = ScoreboardInOrder[Int]()

    dut.io.flush #= false

    // drive random data and add pushed data to scoreboard
    StreamDriver(dut.io.push, dut.clockDomain) { payload =>
      payload.randomize()
      true
    }
    StreamMonitor(dut.io.push, dut.clockDomain) { payload =>
      scoreboard.pushRef(payload.toInt)
    }

    // randmize ready on the output and add popped data to scoreboard
    StreamReadyRandomizer(dut.io.pop, dut.clockDomain)
    StreamMonitor(dut.io.pop, dut.clockDomain) { payload =>
      scoreboard.pushDut(payload.toInt)
    }

    dut.clockDomain.forkStimulus(10)
    dut.clockDomain.waitActiveEdgeWhere(scoreboard.matches == 100)
  }
}