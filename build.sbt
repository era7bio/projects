Nice.scalaProject

name          := "projects"
organization  := "era7"
description   := "projects project"

scalaVersion := "2.11.8"

// the org name differs on github:
GithubRelease.repo := s"era7bio/${name.value}"

bucketSuffix  := "era7.com"

libraryDependencies ++= Seq(
  "ohnosequences" %% "cosas"           % "0.8.0",
  "ohnosequences" %% "datasets"        % "0.3.0",
  "ohnosequences" %% "aws-scala-tools" % "0.16.0",
  "org.scalatest" %% "scalatest"       % "2.2.6" % Test
)
