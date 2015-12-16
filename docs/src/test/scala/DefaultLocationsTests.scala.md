
```scala
package era7.projects.test

import org.scalatest.FunSuite

import ohnosequences.datasets._
import ohnosequences.cosas._, types._, klists._
import era7.projects._

case object example {

  case class Sample(id: String)

  case object buh extends era7.projects.Project("buh")

  case object doSomething extends Task(buh)("doSomething") {

    case object x extends FileData("x")(fileType.txt)

    case object Input extends DataSet(x :×: |[AnyData])
    type Input = Input.type
    val input = Input
    type Output = input.type
    val output = input
  }

  case class doSomethingParam(b: String) extends Task(buh)(s"doSomething.${b}") {

    case object x extends FileData("x")(fileType.txt)

    case object Input extends DataSet(x :×: |[AnyData])
    type Input = Input.type
    val input = Input
    type Output = input.type
    val output = input
  }

  def lll(x: String) = {

    val a = doSomethingParam(x); import a._

    a.input.keys.types map a.defaultS3Location
  }
}

class DefaultLocationsTest extends FunSuite {

  import example._

  test("default S3 locations") {

    assert {
      (doSomething.input.keys.types map doSomething.defaultS3Location) === (
        (doSomething.x := S3DataLocation(doSomething.s3Output / doSomething.x.label)) :: *[AnyDenotation]
      )
    }
  }

  test("default parametric S3 locations") {

    println { List("a", "b", "c") map lll _}
  }
}

```




[test/scala/DefaultLocationsTests.scala]: DefaultLocationsTests.scala.md
[main/scala/projects.scala]: ../../main/scala/projects.scala.md
[main/scala/defaultLocations.scala]: ../../main/scala/defaultLocations.scala.md