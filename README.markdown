# SBT Company Wide Settings Example

Company wide settings, a.k.a. Bill Of Materials (BOM from now on), can be implemented in [SBT](http://www.scala-sbt.org/) using [plugins](http://www.scala-sbt.org/release/docs/Getting-Started/Using-Plugins.html).

The example is split up in two separate SBT projects, each in its own directory:

* `bom`: the Bill Of Materials
* `model`: a dummy project depending on the BOM

Of course, in a real project, you would have one BOM and several projects depending on it. Changing a setting in the BOM would then propagate the modification to the dependent projects.

## Build and Run

To build and run the example:

```shell
$ git clone git://github.com/mcaserta/sbt-bom-example.git
$ cd sbt-bom-example/bom
$ sbt publish-local
$ cd ../model
$ sbt test
```

You should see that the `bom` plugin gets installed in the local Ivy cache and the tests in the dependent `model` project run successfully.

## Assumptions

The example assumes you have a company maven repository whose credentials are specified in the `$HOME/ivy2/.auth-acme` file like this:

  realm=Sonatype Nexus Repository Manager
	host=nexus.acme.com
	user=roadrunner
	password=beepbeep

**NB:** the `realm` key must match the http basic authentication realm in the http headers your company maven repository is advertising. If you're using [Nexus](http://www.sonatype.org/nexus/), chances are the above example will work out of the box.

You might not actually need a maven repository, although it certainly helps distributing artifacts company wide (and since you're looking into company wide settings, you might as well implement things *The Right Way™*)

## The Bill Of Materials

References to files and commands in this section are relative to the `bom` directory.

### build.sbt

First of all, in `build.sbt` we have `sbtPlugin := true`. This tells SBT this is actually a plugin. Nothing particularly mind blowing so far. 

The `resolvers` setting adds the company maven repositories to the resolvers. This is not really needed unless you have such repos. You might want to set up a mirror repository to save a few bucks on your company's Internet bill. Also, the releases, snapshots and 3rd party repositories are only really needed if you depend on a particular artifact in your plugin's sources that is not available in the default resolvers (Maven Central and Java.net actually).

The `publishMavenStyle`, `publishTo` and `credentials` settings are needed when publishing the plugin's artifacts to the company maven repository. To publish the plugin's artifacts, simply run:

	$ sbt publish

### plugins.sbt (in dir `project`)

This is only needed if you use [Intellij IDEA](http://www.jetbrains.com/idea/) and want to open the project in it. More info about this can be found [here](https://github.com/mpeltonen/sbt-idea).

### model.scala (in dir `src/main/scala/com/acme/sbt/bom`)

The objects defined in this scala source file are made available by SBT in the classpath of the project build where you have declared a dependency on this plugin. This allows for the company wide settings.

Let's look at the various objects in detail.

#### Resolvers

```scala
object Resolvers {
  val mirror = "ACME Mirror" at "http://nexus.acme.com/content/groups/public"
  val releases = "ACME Releases" at "http://nexus.acme.com/content/repositories/acme-releases"
  val snapshots = "ACME Snapshots" at "http://nexus.acme.com/content/repositories/acme-snapshots"
  val thirdparty = "ACME 3rd Party" at "http://nexus.acme.com/content/repositories/acme-thirdparty"
  val allResolvers = Seq(releases, snapshots, thirdparty, mirror)
}
```

The `Resolvers` object is a placeholder for [SBT resolvers](http://www.scala-sbt.org/release/docs/Detailed-Topics/Resolvers.html). We also declare an `allResolvers` value to bind them all.

#### Dependencies

```scala
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
```

Inside the `Dependencies` object we have a nested `v` object that is a placeholder for version numbers. This is useful when you have framework/library/platform dependencies with multiple artifacts and want to change the version number of a given framework's artifacts once and for all.

```scala
  // platform deps
  val dal = pdep("dal")
  val model = pdep("model")
  val multiplexerapi = pdepgav("multiplexer", "multiplexer-api")
  val multiplexerimpl = pdepgav("multiplexer", "multiplexer-impl")
  val normalizerapi = pdepgav("normalizer", "normalizer-api")
  val normalizerimpl = pdepgav("normalizer", "normalizer-impl")
```

I'm assuming the projects depending on this BOM are for a platform we're building which is made up of the following modules:

* `com.acme.platform.dal:dal:1.0.0-SNAPSHOT`
* `com.acme.platform.model:model:1.0.0-SNAPSHOT`
* `com.acme.platform.multiplexer:multiplexer-api:1.0.0-SNAPSHOT`
* `com.acme.platform.multiplexer:multiplexer-impl:1.0.0-SNAPSHOT`
* `com.acme.platform.normalizer:normalizer-api:1.0.0-SNAPSHOT`
* `com.acme.platform.normalizer:normalizer-impl:1.0.0-SNAPSHOT`

The `pdep` and `pdepgav` are utility methods defined at the bottom of the `Dependencies` object and allow for factorization of the common bits in the artifact coordinates.

```scala
// library/framework deps
val akkakernel = "com.typesafe.akka" %% "akka-kernel" % v.akka
val akkaremote = "com.typesafe.akka" %% "akka-remote" % v.akka
val akkaslf4j = "com.typesafe.akka" %% "akka-slf4j" % v.akka
val akkatestkit = "com.typesafe.akka" %% "akka-testkit" % v.akka % "test"
val hsqldb = "org.hsqldb" % "hsqldb" % "2.2.9" % "test"
...
```

These are all placeholders for framework/library dependencies.

```scala
val neo4j = Seq(janino, jcloverslf4j, jerseycore, neo4jgraphalgo, neo4jgraphmatching, neo4jkernel, neo4jlucene, neo4jserver)
```

Framework dependencies can be grouped together in a sequence for easier inclusion later.

#### Settings

The `defaultSettings` value augments SBT's `Defaults.defaultSettings` with lots of custom stuff. For instance, we declare the default scala version and configure the resolvers using the `allResolvers` value we saw above in the `Resolvers` object. This is where you want to make configuration changes that affect the whole platform.

## The dependent project part

Okay, we've factored out all the common bits in the plugin. Time to profit!

References to files and commands in this section are relative to the `model` directory.

### plugins.sbt (in dir `project`)

Here we have the usual sbt-idea stuff as above and, of course:

```scala
addSbtPlugin("com.acme.sbt.bom" % "bom" % "1.0.0-SNAPSHOT")
```

This makes the plugin classes available in SBT's build classpath.

### Build.scala (in dir `project`)

Let's take a look separately at the different objects we have in the build file.

#### BuildSettings

```scala
object BuildSettings {

  import com.acme.sbt.bom.Settings

  val buildOrganization = "com.acme.platform.model"
  val buildVersion = "1.0.0-SNAPSHOT"

  val buildSettings = Settings.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion
  )
}
```

We import `com.acme.sbt.bom.Settings` from the BOM plugin so, when later we reference `Settings.defaultSettings`, we are actually referencing the build settings defined globally in our plugin. Also, the `organization` and `version` properties are overridden with values specified in `buildOrganization` and `buildVersion`.

#### Dependencies

```scala
object Dependencies {
  
  import com.acme.sbt.bom.Dependencies._

  val deps = Seq(nscalatime, specs2) ++ junit
}
```

We import `com.acme.sbt.bom.Dependencies._` so, when later we reference `nscalatime`, `specs2` and `junit`, we are actually referencing the dependencies' values defined in our BOM plugin.

#### ModelBuild

```scala
object ModelBuild extends Build {

  import BuildSettings._
  import Dependencies._

  lazy val root = Project(id = "model",
    base = file("."),
    settings = buildSettings ++ Seq(libraryDependencies ++= deps))

}
```

We import `BuildSettings._` and `Dependencies._` so, when later we reference `buildSettings` and `deps`, we are actually referencing the values respectively defined in the `BuildSettings` and `Dependencies` objects in the same file.

## Conclusions

I hope you found this example clear and useful. 

You can reach me via

* [twitter](https://twitter.com/mirkocaserta)
* [mail](mailto:mirko.caserta@gmail.com)

