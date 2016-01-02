package era7.projects

import java.net.URL
import java.time._
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

  type Raw = AnyTaskState :: List[Input#Raw] :: List[Output#Raw] :: *[Any]

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
  type Input <: AnyProductType { type Types <: AnyKList.Of[AnyData] }
  val input: Input

  type Output <: AnyProductType { type Types <: AnyKList.Of[AnyData] }
  val output: Output

  // NOTE in a future better world we could use this
  lazy val branch = name

  val deadline: LocalDate

  def deadlinePassed: Boolean = deadline.isAfter(LocalDate.now)
}

case object AnyTask {

  implicit def denotationSyntax[T <: AnyTask, V <: T#Raw](td: T := V): TaskDenotationSyntax[T,V] =
    TaskDenotationSyntax(td)
}
case class TaskDenotationSyntax[T <: AnyTask, V <: T#Raw](val td: T := V) {

  def isDeadlineOK: Boolean = td.value.head match {
    case Failed | Cancelled | Expired | Completed => true
    case Specified | InReview |Started            => td.tpe.deadlinePassed
  }
}

/*
  This is a helper constructor for doing  something like

  ``` scala
  case object doSomething extends Task(project)(input)(output)(date)`
  ```
*/
class Task[
  P <: AnyProject,
  I <: AnyProductType { type Types <: AnyKList.Of[AnyData] },
  O <: AnyProductType { type Types <: AnyKList.Of[AnyData] }
](
  val project: P
)(
  val inputData: I
)(
  val outputData: O
)(
  val deadline: LocalDate
)(
  implicit
    proof1: noDuplicates isTrueOn I#Types,
    proof2: noDuplicates isTrueOn O#Types
)
extends AnyTask {

  type Project = P

  type Input = I
  lazy val input: Input = inputData

  type Output = O
  lazy val output: Output = outputData

  lazy val name: String = toString
}


abstract class Task2[
  P <: AnyProject
](
  val project: P
)(
  val deadline: LocalDate
)
extends AnyTask {

  type Project = P

  lazy val name: String = toString
}


trait AnyTaskRaw {

  type T <: AnyTask
  type Inputs = List[Any]
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

case object getState extends DepFn1[AnyDenotation, (AnyTask, AnyTaskState)] {

  implicit def default[
    T <: AnyTask,
    V <: T#Raw
  ]
  : AnyApp1At[this.type, T := V] { type Y = (T,V#Head) } =
    this at { tv: T := V => (tv.tpe, tv.value.head) }
}

// TODO return the task if not or somethikn similar
case object taskDeadlineOK extends DepFn1[AnyDenotation, Boolean] {

  implicit def default[T <: AnyTask, V <: T#Raw]
  : AnyApp1At[taskDeadlineOK.type, T := V] { type Y = Boolean } =
    App1 {
      tv: T := V => tv.value.head match {
          case Failed | Cancelled | Expired | Completed => true
          case Specified | InReview |Started            => tv.tpe.deadlinePassed
        }
    }
}
