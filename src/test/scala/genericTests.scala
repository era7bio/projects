package era7.projects.test

import era7.projects._
import ohnosequences.cosas._, types._, klists._, fns._

abstract class GenericProjectStateTest[
  P <: AnyProject,
  Ks <: AnyProductType { type Types <: AnyKList { type Bound <: AnyTask } },
  PT <: ProjectTasks[P, Ks],
  PTS <: PT#Raw,
  BL <: AnyKList { type Bound = Boolean }
](val p: P)(val pks: Ks)(val pt: PT)(val state: PT := PTS)(implicit
  mapper: AnyApp2At[
    mapKList[taskDeadlineOK.type, Boolean],
    taskDeadlineOK.type,
    PTS
  ] {
    type Y = BL
  }
) extends org.scalatest.FunSuite {

  test("check deadlines")
  {

    // println { mapper(taskDeadlineOK, state.value) }
  }
}
