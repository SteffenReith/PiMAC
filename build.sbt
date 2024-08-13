name := "PiMAC"
version := "1.0"

scalaVersion := "2.12.18"

val spinalVersion    = "1.10.2a"
val spinalCore       = "com.github.spinalhdl"                %% "spinalhdl-core"        % spinalVersion 
val spinalLib        = "com.github.spinalhdl"                %% "spinalhdl-lib"         % spinalVersion
val spinalIdslPlugin = compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)
val spinalSim        = "com.github.spinalhdl"                %% "spinalhdl-sim"         % spinalVersion
val scopt            = "com.github.scopt"                    %% "scopt"                 % "4.1.0"

// Added the spinal libraries
libraryDependencies ++= Seq(spinalCore, spinalLib, spinalIdslPlugin, spinalSim, scopt)

fork := true

