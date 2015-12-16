package era7.projects

import java.net.URL
import ohnosequences.awstools.s3._
import ohnosequences.datasets._

/*
  This namespace contains github-related constants.
*/
case object github {

  lazy val org: String = "era7bio"
}
/*
  This namespace contains S3-related constants.
*/
case object s3 {

  lazy val bucket: String = "era7p"
}

trait AnyProject {

  val name: String

  /*
    ### GitHub data

    convenience fields for the different GitHub URLs associated with this project.
  */
  lazy val repo     : String  = s"${github.org}/${name}"
  lazy val repoURL  : URL     = new URL(s"https://github.com/${repo}")
  lazy val repoGit  : String  = s"git@github.com:${repo}.git"

  /*
    ### S3 data

    The S3 namespaces for the project and its input and output.
  */
  lazy val s3       : S3Folder = S3Folder(era7.projects.s3.bucket, name)
  lazy val s3Input  : S3Folder = s3 / "data" / "in"  /
  lazy val s3Output : S3Folder = s3 / "data" / "out" /

  // TODO one role per project, created when initialized
  // lazy val role: Role = projects
}
abstract class Project(val name: String) extends AnyProject

/*
  ## Tasks

  A task is part of a project, and has as input and output a set of data.
*/
trait AnyTask {

  type Project <: AnyProject
  val project: Project

  val name: String
  lazy val fullName: String   = s"${project.name}.${name}"
  lazy val s3Output: S3Folder = project.s3Output / name /

  /*
    ### Input and output

    Both inputs and outputs are records of `AnyData`. Normally you would define a task as an `object`, with nested `object`s for the input and output. Then you just need to set `type Input = input.type`, `type Output = output.type`.
  */
  type Input <: AnyDataSet
  val input: Input

  type Output <: AnyDataSet
  val output: Output

  // NOTE in a future better world we could use this
  lazy val branch = name

  case object defaultS3Location extends defaultS3LocationForTask(this)
}
/*
  This is a helper constructor for doing `case object doSometing extends Task(project)`. The name of the task will be that of the object.
*/
abstract class Task[P <: AnyProject](val project: P)(val name: String) extends AnyTask {

  type Project = P

  // lazy val name: String = toString


}
