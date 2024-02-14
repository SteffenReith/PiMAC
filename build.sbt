name := "PipeMult"
version := "1.0"
scalaVersion := "2.11.12"

// Added the spinal libraries
libraryDependencies ++= Seq("com.github.spinalhdl" %% "spinalhdl-core_2.11" % latest.release,
                            "com.github.spinalhdl" %% "spinalhdl-lib_2.11"  % latest.release
                            "com.github.spinalhdl" %% "spinalhdl-sim_2.11"  % latest.release)

fork := true

