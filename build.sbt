name          := "projects"
organization  := "era7bio"
description   := "projects project"
bucketSuffix  := "era7.com"

crossScalaVersions := Seq("2.11.11", "2.12.3")
scalaVersion := crossScalaVersions.value.last

libraryDependencies ++= Seq(
  "ohnosequences" %% "cosas"           % "0.10.1",
  "ohnosequences" %% "aws-scala-tools" % "0.20.0",
  "ohnosequences" %% "datasets"        % "0.5.2"
)

// wartremoverErrors in (Test, compile) := Seq()
// wartremoverErrors in (Compile, compile) := Seq()
