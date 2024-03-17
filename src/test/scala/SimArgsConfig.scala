/*
 * Author: Steffen Reith (steffen.reith@hs-rm.de)
 *
 * Creation Date:  Wed Mar 13 14:54:42 CET 2024
 * Module Name:    SimArgConfig - Holds command line parameters for the simulation
 * Project Name:   PiMAC- A demo for SpinalHDL's pipeline framework
 *
 */

// Used by scopt to store information
case class SimArgsConfig(noOfRandomTestsArg : Option[Int],
                         mWidthArg          : Option[Int],
                         simSeedArg         : Option[Int],
                         verboseArg         : Option[Boolean])
