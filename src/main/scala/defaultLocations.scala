package era7.projects

import ohnosequences.datasets._
import ohnosequences.cosas._, types._, fns._, klists._

class defaultS3LocationForTask[T <: AnyTask](val task: T) extends DepFn1[
  AnyData,
  AnyDenotation { type Value = S3DataLocation}
]
{

  implicit def default[D <: AnyData]: AnyApp1At[this.type, D] { type Y = D := S3DataLocation } =
    App1 { d: D => d := S3DataLocation(task.s3Output / d.label) }
}
