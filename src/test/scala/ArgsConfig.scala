/*
 * Author: Steffen Reith (steffen.reith@hs-rm.de)
 *
 * Creation Date:  Wed Mar 13 14:54:42 CET 2024
 * Module Name:    ArgConfig - Holds all information after parsing command line parameter s
 * Project Name:   PipelinedMultiply - A demo for SpinalHDL's pipeline framework
 *
 */

// Used by scopt to store information
case class ArgsConfig(noOfRandomTests : Option[Int],
                      mWidth          : Option[Int],
                      simSeed         : Option[Int])
