package era7bio.projects

import ohnosequences.datasets._
import ohnosequences.cosas._, types._, fns._, klists._
import ohnosequences.awstools.s3._

class defaultS3LocationForTask[T <: AnyTask](val task: T) extends DepFn1[
  AnyData,
  AnyDenotation { type Value = S3Resource }
]
{

  implicit def default[D <: AnyData]: AnyApp1At[this.type, D] { type Y = D := S3Resource } =
    App1 { d: D => d := S3Resource(task.s3Output / d.label) }
}

// TODO should fragment be a S3 folder?
case class insertAfter(val prefix: String, val fragment: String) extends DepFn1[
  AnyDenotation { type Value = S3Resource },
  AnyDenotation { type Value = S3Resource }
]
{

  implicit def default[D <: AnyData]
  : AnyApp1At[insertAfter, D := S3Resource] { type Y = D := S3Resource } =
    App1 { d: D := S3Resource => {

        val rhs = d.value.resource.key.stripPrefix(prefix)
        d.tpe := S3Resource(S3Object(d.value.resource.bucket, s"${prefix}${fragment}/${rhs}"))
      }
    }
}
