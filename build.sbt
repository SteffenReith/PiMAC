name := "PipelinedMultiply"
version := "1.0"

scalaVersion := "2.12.18"
val spinalVersion = "1.10.1"

// Added the spinal libraries
libraryDependencies ++= Seq("com.github.spinalhdl" %% "spinalhdl-core"        % spinalVersion,
                            "com.github.spinalhdl" %% "spinalhdl-lib"         % spinalVersion,
                            "com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion,
                            "com.github.spinalhdl" %% "spinalhdl-sim"         % spinalVersion)

fork := true

