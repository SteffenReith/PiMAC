/*
 * Author: Steffen Reith (Steffen.Reith@hs-rm.de)
 *
 * Create Date:    Wed Mar 13 18:49:06 CET 2024
 * Module Name:    Reporter - A simple report function
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 */

object Reporter {

    // Store the verbose mode
    private var level : Boolean = false

    // Print a simple string as report
    def printReport(s : String) : Unit = {if (level) printf(s"INFO: ${s}")}

    // The the verboseness mode 
    def setVerboseness(m : Boolean) : Unit = {level = m}

}
