resolvers ++= Seq(
  "ACME Mirror" at "http://nexus.acme.com/content/groups/public",
  "ACME Releases" at "http://nexus.acme.com/content/repositories/acme-releases",
  "ACME 3rd Party" at "http://nexus.acme.com/content/repositories/acme-thirdparty",
  "ACME Snapshots" at "http://nexus.acme.com/content/repositories/acme-snapshots"
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.4.0")

addSbtPlugin("com.acme.sbt.bom" % "bom" % "1.0.0-SNAPSHOT")

credentials += Credentials(Path.userHome / ".ivy2" / ".auth-acme")
