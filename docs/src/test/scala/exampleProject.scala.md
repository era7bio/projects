
```scala
package era7bio.projects.test

import java.time._
import era7bio.projects._
import ohnosequences.datasets._
import ohnosequences.cosas._, records._, types._, klists._

case object preparePaella extends era7bio.projects.Project("prepare_paella")

case object preparePaellaTasks extends ProjectTasks(preparePaella)(
  buyRice     :×:
  buySeafood  :×: |[AnyTask]
)

case object rice    extends Data("La Fallera")
case object seafood extends Data("Preparado de Paella Pescanova")

case object buyRice extends Task(preparePaella)(noData)(rice :×: |[AnyData])(LocalDate.of(2016,3,2))
case object buySeafood extends Task(preparePaella)(noData)(seafood :×: |[AnyData])(LocalDate.of(2016,3,2))

case object projectState {

  // Now imagine that I already got my rice, but no seafood yet
  val current = preparePaellaTasks := {
    buyRice( Completed :: List(*[AnyDenotation]) :: List(buyRice.defaultOutputS3Location) :: *[Any] )         ::
    buySeafood( Specified :: List(*[AnyDenotation]) :: List(buySeafood.defaultOutputS3Location) :: *[Any] )   ::
    *[AnyDenotation]
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

abstract class GenericTasksTests[
  P <: AnyProject,
  Ks <: AnyProductType { type Types <: AnyKList { type Bound <: AnyTask } },
  PT <: ProjectTasks[P, Ks]
](val pt: PT) extends org.scalatest.FunSuite {

  test("dummy test with tasks") {

    // println { pt.keys.types.asList toString }
  }
}

class buh extends GenericTasksTests[
  preparePaella.type,
  buyRice.type     :×:
  buySeafood.type  :×: |[AnyTask],
  preparePaellaTasks.type
](preparePaellaTasks)

```




[main/scala/defaultLocations.scala]: ../../main/scala/defaultLocations.scala.md
[main/scala/package.scala]: ../../main/scala/package.scala.md
[main/scala/projects.scala]: ../../main/scala/projects.scala.md
[test/scala/DefaultLocationsTests.scala]: DefaultLocationsTests.scala.md
[test/scala/exampleProject.scala]: exampleProject.scala.md