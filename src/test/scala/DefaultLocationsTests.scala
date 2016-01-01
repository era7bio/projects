package era7.projects.test

import org.scalatest.FunSuite

import ohnosequences.datasets._
import ohnosequences.cosas._, types._, klists._
import era7.projects._

case object example {

  case class Sample(id: String)

  case object buh extends era7.projects.Project("buh")

  case object doSomething extends Task(buh)(new java.util.Date) {

    case object x extends FileData("x")("txt")

    case object Input extends DataSet(x :×: |[AnyData])
    type Input = Input.type
    val input = Input
    type Output = input.type
    val output = input
  }

  case class doSomethingParam(b: String) extends Task(buh)(new java.util.Date) {

    case object x extends FileData("x")("txt")

    case object Input extends DataSet(x :×: |[AnyData])
    type Input = Input.type
    val input = Input
    type Output = input.type
    val output = input
  }
}

class DefaultLocationsTest extends FunSuite {

  import example._

  test("default S3 locations") {

    assert {
      (doSomething.input.keys.types map doSomething.defaultS3Location) === (
        (doSomething.x := S3Resource(doSomething.s3Output / doSomething.x.label)) :: *[AnyDenotation]
      )
    }

    assert {
      (doSomething.input.keys.types map doSomething.defaultS3Location) === doSomething.defaultOutputS3Location
    }
  }

  test("can map over default locations") {

    // NOTE just something which serves as a classifier for different denotations of the same resource
    val samples = Set("hola", "scalac", "que tal")

    val s3PerSample = samples map {
      s => {
        
        val addSamplePrefix = doSomething.defaultLocationWithQualifier(s)
        import addSamplePrefix._

        doSomething.defaultOutputS3Location map addSamplePrefix
      }
    }

    println { s3PerSample }
  }
}
