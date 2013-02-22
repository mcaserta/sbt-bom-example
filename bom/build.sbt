sbtPlugin := true

name := "bom"

organization := "com.acme.sbt.bom"

organizationName := "ACME Inc."

organizationHomepage := Some(url("http://www.acme.com"))

version := "1.0.0-SNAPSHOT"

resolvers ++= Seq(
  "ACME Mirror" at "http://nexus.acme.com/content/groups/public",
  "ACME Releases" at "http://nexus.acme.com/content/repositories/acme-releases",
  "ACME 3rd Party" at "http://nexus.acme.com/content/repositories/acme-thirdparty",
  "ACME Snapshots" at "http://nexus.acme.com/content/repositories/acme-snapshots"
)

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "http://nexus.acme.com/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("ACME Snapshots" at nexus + "content/repositories/acme-snapshots")
  else
    Some("ACME Releases"  at nexus + "content/repositories/acme-releases")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".auth-acme")
