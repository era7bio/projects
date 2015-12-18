package era7.projects

import java.net.URL
import ohnosequences.awstools.s3._
import ohnosequences.datasets._
import ohnosequences.cosas._, types._, klists._, records._, fns._

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
trait AnyTask extends AnyType {

  type Raw = AnyTaskState

  type Project <: AnyProject
  val project: Project

  val name: String
  lazy val fullName : String  = s"${project.name}.${name}"
  lazy val label    : String  = fullName

  lazy val s3Output: S3Folder = project.s3Output / name /

  /*
    ### Input and output

    Both inputs and outputs are records of `AnyData`. Normally you would define a task as an `object`, with nested `object`s for the input and output. Then you just need to set the corresponding types and values.
  */
  type Input <: AnyDataSet
  val input: Input

  type Output <: AnyDataSet
  val output: Output

  // NOTE in a future better world we could use this
  lazy val branch = name

  /*
    This is a depfn which when applied on data: `task.defaultS3Location(d)` yields the default S3 location for `d`. You can use it for building data Loquat data mappings, for example, by mapping over the types of the input/output records.
  */
  case object defaultS3Location extends defaultS3LocationForTask(this)

  def defaultOutputS3Location[
    O <: AnyKList { type Bound = AnyDenotation { type Value = S3Resource } }
  ](implicit
    mapper: AnyApp2At[
      mapKList[defaultS3Location.type, AnyDenotation { type Value = S3Resource }],
      defaultS3Location.type,
      Input#Keys#Types
    ] { type Y = O }
  )
  : O =
    mapper(defaultS3Location, input.keys.types)
    // (input.keys.types: Input#Keys#Types) map defaultS3Location

  val deadline: java.util.Date
}
/*
  This is a helper constructor for doing `case object doSometing extends Task(project)(name)`
*/

abstract class Task[P <: AnyProject](val project: P)(val deadline: java.util.Date) extends AnyTask {

  type Project = P

  lazy val name: String = toString
}

sealed trait AnyTaskState
case object Specified extends AnyTaskState {

  def start   : Started.type    = Started
  def cancel  : Cancelled.type  = Cancelled
  def expire  : Expired.type    = Expired
}
// TODO assigned? to someone?
case object Started   extends AnyTaskState {

  def fail      : Failed.type     = Failed
  def expire    : Expired.type    = Expired
  def review    : InReview.type   = InReview
  // TODO without review? kinda weird
  def complete  : Completed.type  = Completed
}
case object Failed    extends AnyTaskState
case object Cancelled extends AnyTaskState
case object Expired   extends AnyTaskState
case object InReview  extends AnyTaskState {

  def complete: Completed.type = Completed
}
case object Completed extends AnyTaskState

abstract class ProjectTasks[P <: AnyProject, Ks <: AnyProductType { type Types <: AnyKList { type Bound <: AnyTask } }](
  val project: P
)
(
  val tasks: Ks
)(implicit
  noDuplicates: noDuplicates isTrueOn Ks#Types
)
extends RecordType(tasks)
