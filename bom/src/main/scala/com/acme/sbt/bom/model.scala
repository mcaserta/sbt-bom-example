package com.acme.sbt.bom

import sbt._
import Keys._

object Resolvers {
  val mirror = "ACME Mirror" at "http://nexus.acme.com/content/groups/public"
  val releases = "ACME Releases" at "http://nexus.acme.com/content/repositories/acme-releases"
  val snapshots = "ACME Snapshots" at "http://nexus.acme.com/content/repositories/acme-snapshots"
  val thirdparty = "ACME 3rd Party" at "http://nexus.acme.com/content/repositories/acme-thirdparty"
  val allResolvers = Seq(releases, snapshots, thirdparty, mirror)
}

object Dependencies {

  //Versions
  object v {
    val platform = "1.0.0-SNAPSHOT"

    val akka = "2.1.0"
    val logback = "1.0.7"
    val neo4j = "1.9.M04"
    val scala = "2.10.0"
    val slf4j = "1.7.2"
    val slick = "1.0.0"
  }

  // platform deps
  val dal = pdep("dal")
  val model = pdep("model")
  val multiplexerapi = pdepgav("multiplexer", "multiplexer-api")
  val multiplexerimpl = pdepgav("multiplexer", "multiplexer-impl")
  val normalizerapi = pdepgav("normalizer", "normalizer-api")
  val normalizerimpl = pdepgav("normalizer", "normalizer-impl")

  // library/framework deps
  val akkakernel = "com.typesafe.akka" %% "akka-kernel" % v.akka
  val akkaremote = "com.typesafe.akka" %% "akka-remote" % v.akka
  val akkaslf4j = "com.typesafe.akka" %% "akka-slf4j" % v.akka
  val akkatestkit = "com.typesafe.akka" %% "akka-testkit" % v.akka % "test"
  val hsqldb = "org.hsqldb" % "hsqldb" % "2.2.9" % "test"
  val janino = "org.codehaus.janino" % "janino" % "2.6.1"
  val jcloverslf4j = "org.slf4j" % "jcl-over-slf4j" % v.slf4j
  val jerseycore = "com.sun.jersey" % "jersey-core" % "1.9"
  val junitcore = "junit" % "junit" % "4.11" % "test"
  val junitinterface = "com.novocode" % "junit-interface" % "0.9" % "test->default"
  val logback = "ch.qos.logback" % "logback-classic" % v.logback
  val neo4jgraphalgo = "org.neo4j" % "neo4j-graph-algo" % v.neo4j
  val neo4jgraphmatching = "org.neo4j" % "neo4j-graph-matching" % v.neo4j
  val neo4jkernel = "org.neo4j" % "neo4j-kernel" % v.neo4j
  val neo4jkerneltest = "org.neo4j" % "neo4j-kernel" % v.neo4j % "test" classifier "tests"
  val neo4jlucene = "org.neo4j" % "neo4j-lucene-index" % v.neo4j
  val neo4jserver = "org.neo4j.app" % "neo4j-server" % v.neo4j classifier "static-web" classifier "nologbackxml" exclude("janino", "janino") exclude("commons-logging", "commons-logging")
  val nscalatime = "com.github.nscala-time" %% "nscala-time" % "0.2.0"
  val scalalogging = "com.typesafe" %% "scalalogging-slf4j" % "1.0.1"
  val scalareflect = "org.scala-lang" % "scala-reflect" % v.scala
  val slf4j = "org.slf4j" % "slf4j-api" % v.slf4j
  val slick = "com.typesafe.slick" %% "slick" % v.slick
  val specs2 = "org.specs2" %% "specs2" % "1.14" % "test"

  val akka = Seq(akkaremote, akkaslf4j, akkatestkit)
  val junit = Seq(junitcore, junitinterface)
  val log = Seq(logback, scalalogging, slf4j)
  val neo4j = Seq(janino, jcloverslf4j, jerseycore, neo4jgraphalgo, neo4jgraphmatching, neo4jkernel, neo4jlucene, neo4jserver)

  private[this] def pdep(commonBit: String, version: String = v.platform): ModuleID =
    pdepgav(commonBit, commonBit, version)

  private[this] def pdepgav(groupBit: String, artifactBit: String, version: String = v.platform): ModuleID =
    "com.acme.platform.".concat(groupBit) %% artifactBit % version

}

object Settings {

  import Resolvers._
  import Dependencies.v

  val defaultSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := v.scala,
    resolvers := allResolvers,
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature"),
    scalacOptions in(Compile, doc) ++= Seq("-external-urls:scala=http://www.scala-lang.org/api/current/", "-no-link-warnings"),
    javacOptions ++= Seq("-Xlint:all"),
    publishMavenStyle := true,
    publishTo <<= version {
      (v: String) =>
        if (v.trim.endsWith("SNAPSHOT"))
          Some(snapshots)
        else
          Some(releases)
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".auth-acme"),
    organizationName := "ACME Inc.",
    organizationHomepage := Some(url("http://www.acme.com"))
  )
}
