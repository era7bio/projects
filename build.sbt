name          := "projects"
organization  := "era7bio"
description   := "projects project"

bucketSuffix  := "era7.com"

libraryDependencies ++= Seq(
  "ohnosequences" %% "cosas"           % "0.8.0",
  "ohnosequences" %% "aws-scala-tools" % "0.18.1",
  "ohnosequences" %% "datasets"        % "0.4.1"
)

// NOTE should be reestablished
wartremoverErrors in (Test, compile) := Seq()
wartremoverErrors in (Compile, compile) := Seq()
