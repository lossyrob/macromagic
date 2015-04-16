import sbt._
import sbt.Keys._
import scala.util.Properties

// sbt-assembly
import sbtassembly.Plugin._
import AssemblyKeys._

object Version {
  def either(environmentVariable: String, default: String): String =
    Properties.envOrElse(environmentVariable, default)

  val scala       = "2.11.5"
}

object BenchmarkBuild extends Build {
  val benchmarkKey = AttributeKey[Boolean]("javaOptionsPatched")

  val resolutionRepos = Seq(
    "Local Maven Repository"  at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    "NL4J Repository"         at "http://nativelibs4java.sourceforge.net/maven/",
    "maven2 dev repository"   at "http://download.java.net/maven/2",
    "Typesafe Repo"           at "http://repo.typesafe.com/typesafe/releases/",
    "spray repo"              at "http://repo.spray.io/",
    "sonatypeSnapshots"       at "http://oss.sonatype.org/content/repositories/snapshots"
  )

  // Default settings
  override lazy val settings =
    super.settings ++
  Seq(
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
    version := "0.1.0",
    scalaVersion := Version.scala,
    crossScalaVersions := Seq("2.11.5", "2.10.4"),

    // disable annoying warnings about 2.10.x
    conflictWarning in ThisBuild := ConflictWarning.disable,
    scalacOptions ++=
      Seq("-deprecation",
        "-unchecked",
        "-Yinline-warnings",
        "-language:implicitConversions",
        "-language:reflectiveCalls",
        "-language:higherKinds",
        "-language:postfixOps",
        "-language:existentials",
        "-feature"),

    publishMavenStyle := true,

    publishArtifact in Test := false,

    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
  )

  val defaultAssemblySettings =
    assemblySettings ++
  Seq(
    test in assembly := {},
    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case "reference.conf" => MergeStrategy.concat
        case "application.conf" => MergeStrategy.concat
        case "META-INF/MANIFEST.MF" => MergeStrategy.discard
        case "META-INF\\MANIFEST.MF" => MergeStrategy.discard
        case _ => MergeStrategy.first
      }
    },
    resolvers ++= resolutionRepos
  )

  // Project: root
  lazy val root =
    Project("root", file("."))
      .aggregate(benchmark)
      .settings(
      initialCommands in console:=
        """
          import geotrellis.raster._
          import geotrellis.vector._
          import geotrellis.proj4._
          """
    )

  lazy val macros =
    Project("macros", file("macros"))
      .settings(macrosSettings: _*)

  lazy val macrosSettings = Seq(
    name := "macros",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full),
    libraryDependencies <++= scalaVersion {
      case "2.10.4" => Seq(
        "org.spire-math" %% "spire-macros" % "0.9.1",
        "org.scala-lang" %  "scala-reflect" % "2.10.4",
        "org.scalamacros" %% "quasiquotes" % "2.0.1")
      case "2.11.5" => Seq(
        "org.spire-math" %% "spire-macros" % "0.9.1",
        "org.scala-lang" %  "scala-reflect" % "2.11.5")
    },
    resolvers += Resolver.sonatypeRepo("snapshots")
  )

  lazy val benchmark: Project =
    Project("benchmark", file("benchmark"))
      .dependsOn(macros)
      .settings(benchmarkSettings:_*)

  lazy val benchmarkSettings =
    Seq(
      name := "benchmark",

      scalaVersion := Version.scala,
      // raise memory limits here if necessary
      javaOptions += "-Xmx2G",
      javaOptions += "-Djava.library.path=/usr/local/lib",

      libraryDependencies ++= Seq(
        "com.google.code.caliper" % "caliper" % "1.0-SNAPSHOT" from "http://plastic-idolatry.com/jars/caliper-1.0-SNAPSHOT.jar",
        "com.google.guava" % "guava" % "r09",
        "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % "2.0",
        "com.google.code.gson" % "gson" % "1.7.1"
      ),


      // enable forking in both run and test
      fork := true,
      // custom kludge to get caliper to see the right classpath

      // we need to add the runtime classpath as a "-cp" argument to the
      // `javaOptions in run`, otherwise caliper will not see the right classpath
      // and die with a ConfigurationException unfortunately `javaOptions` is a
      // SettingsKey and `fullClasspath in Runtime` is a TaskKey, so we need to
      // jump through these hoops here in order to feed the result of the latter
      // into the former
      onLoad in Global ~= { previous => state =>
        previous {
          state.get(benchmarkKey) match {
            case None =>
              // get the runtime classpath, turn into a colon-delimited string
              Project
                .runTask(fullClasspath in Runtime in benchmark, state)
                .get
                ._2
                .toEither match {
                case Right(x) =>
                  val classPath =
                    x.files
                      .mkString(":")
                  // return a state with javaOptionsPatched = true and javaOptions set correctly
                  Project
                    .extract(state)
                    .append(
                    Seq(javaOptions in (benchmark, run) ++= Seq("-Xmx8G", "-cp", classPath)),
                      state.put(benchmarkKey, true)
                  )
                case _ => state
              }
            case Some(_) =>
              state // the javaOptions are already patched
          }
        }
      }
    ) ++
  defaultAssemblySettings

}
