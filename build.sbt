name := "PipeMult"
version := "1.0"
scalaVersion := "2.12.16"

// Added the spinal libraries
libraryDependencies ++= Seq(
  "cc.redberry" %% "rings.scaladsl" % "2.5.7",
  "com.github.spinalhdl" %% "spinalhdl-core" % latest.release,
  "com.github.spinalhdl" %% "spinalhdl-lib"  % latest.release
  "com.github.spinalhdl" %% "spinalhdl-sim"  % latest.release)

fork := true

