/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Tue Feb 13 22:29:34 CET 2024 
 * Project Name:   PipeMult - A demo for SpinalHDL's pipeline framework
 */
import spinal.core._
import spinal.lib._

class PipeMult extends Component {

 val io = new Bundle {
   val enable = in Bool()
   val value  = out UInt(8 bits)
 }

}

object PipeMult {

  def main(args: Array[String]) {
    SpinalVhdl(new PipeMult)
    SpinalVerilog(new PipeMult)

  }

}

