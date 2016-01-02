package era7.projects.test

import java.time._
import era7.projects._
import ohnosequences.datasets._
import ohnosequences.cosas._, records._, types._, klists._

case object preparePaella extends era7.projects.Project("prepare_paella")

case object preparePaellaTasks extends ProjectTasks(preparePaella)(
  buyRice     :×:
  buySeafood  :×: |[AnyTask]
)

case object rice    extends Data("La Fallera")
case object seafood extends Data("Preparado de Paella Pescanova")

case object buyRice extends Task2(preparePaella)(LocalDate.of(2016,3,2)) {

  type Input  = NoData; val input = noData
  type Output = rice.type :×: |[AnyData]; val output = rice :×: |[AnyData]

  val outputLocations = List( rice(buyRice.s3Output / rice.label) :: *[AnyDenotation] )
}
case object buySeafood extends Task2(preparePaella)(LocalDate.of(2016,3,2)) {

  type Input  = NoData; val input = noData
  type Output = seafood.type :×: |[AnyData]; val output = seafood :×: |[AnyData]

  val outputLocations = List( seafood(buySeafood.s3Output / seafood.label) :: *[AnyDenotation] )
}

case object projectState {

  // Now imagine that I already got my rice, but no seafood yet
  val current = preparePaellaTasks := {
    buyRice(
      Completed                 ::
      List(*[AnyDenotation])    ::
      buyRice.outputLocations   :: *[Any]
    ) ::
    buySeafood(
      Specified                   ::
      List(*[AnyDenotation])      ::
      buySeafood.outputLocations  :: *[Any]
    ) ::
    *[AnyDenotation]
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

abstract class GenericTasksTests[
  P <: AnyProject,
  Ks <: AnyProductType { type Types <: AnyKList { type Bound <: AnyTask } },
  PT <: ProjectTasks[P, Ks]
](val pt: PT) extends org.scalatest.FunSuite {

  test("dummy test with tasks") {}
}


// class preparePaellaDefaultTests extends GenericProjectStateTest(preparePaella)(preparePaellaTasks.keys)(preparePaellaTasks)(projectState.current)

object uhoh {

  val uh = projectState.current.value.head.isDeadlineOK

  val aaaa = projectState.current.value map getState
}
