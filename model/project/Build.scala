import sbt._
import Keys._

object BuildSettings {

  import com.acme.sbt.bom.Settings

  val buildOrganization = "com.acme.platform.model"
  val buildVersion = "1.0.0-SNAPSHOT"

  val buildSettings = Settings.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion
  )
}

object Dependencies {

  import com.acme.sbt.bom.Dependencies._

  val deps = Seq(nscalatime, specs2) ++ junit
}

object ModelBuild extends Build {

  import BuildSettings._
  import Dependencies._

  lazy val root = Project(id = "model",
    base = file("."),
    settings = buildSettings ++ Seq(libraryDependencies ++= deps))

}
