resolvers ++= Seq(
  "ACME Mirror" at "http://nexus.acme.com/content/groups/public",
  "ACME Releases" at "http://nexus.acme.com/content/repositories/acme-releases",
  "ACME 3rd Party" at "http://nexus.acme.com/content/repositories/acme-thirdparty",
  "ACME Snapshots" at "http://nexus.acme.com/content/repositories/acme-snapshots",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.3.0-SNAPSHOT")

addSbtPlugin("com.acme.sbt.bom" % "bom" % "1.0.0-SNAPSHOT")

credentials += Credentials(Path.userHome / ".ivy2" / ".auth-acme")
