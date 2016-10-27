import sbt._
import sbt.{Build => SbtBuild}
import sbt.Keys._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._
import org.scalatra.sbt._
import com.earldouglas.xsbtwebplugin.PluginKeys._
import com.earldouglas.xsbtwebplugin.WebPlugin._
import templemore.sbt.cucumber.CucumberPlugin

object Build extends SbtBuild {
  val Organization = "gray"
  val Name = "note-worthy"
  val Version = Option(System.getenv("BUILD_VERSION")) getOrElse "DEV"
  val ScalaVersion = "2.11.8"
  val ScalatraVersion = "2.4.1"

  val JettyPort = sys.props.get("jetty.port") map (_.toInt) getOrElse 8080

  val dependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.0" % "test",
    "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
    "info.cukes" %% "cucumber-scala" % "1.2.4" % "test",

    "org.scala-lang.modules" %% "scala-swing" % "1.0.1",

    "org.scala-lang.modules" % "scala-jline" % "2.12.1"
  )

  lazy val project = Project(
    Name,
    file("."),
    settings =
      Defaults.coreDefaultSettings ++
        assemblySettings ++
        Seq(CucumberPlugin.cucumberSettings: _*) ++
        Seq(
          CucumberPlugin.cucumberFeaturesLocation := "cucumber",
          CucumberPlugin.cucumberJsonReport := true,
          CucumberPlugin.cucumberStepsBasePackage := "cucumber.steps",
          unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "test/fixtures")
        ) ++
        Seq(scalacOptions ++= Seq("-feature", "-target:jvm-1.7", "-language:postfixOps")) ++
        Seq(
          organization := Organization,
          name := Name,
          version := Version,
          scalaVersion := ScalaVersion,
          resolvers += Classpaths.typesafeReleases,
          jarName in assembly := s"$Name.jar",
          mainClass in assembly := Some("Main"),
          mergeStrategy in assembly := {
            case PathList("mime.types") => MergeStrategy.first
            case x =>
              val oldStrategy = (mergeStrategy in assembly).value
              oldStrategy(x)
          },
          libraryDependencies ++= dependencies
        )
//        ++
//        Seq(
//          resolvers += "Local Ivy repository" at "file://" + Path.userHome + "/.ivy2/local",
//          resolvers += "BBC Forge Maven Releases" at "https://dev.bbc.co.uk/maven2/releases/",
//          resolvers += "BBC Forge Maven Snapshots" at "https://dev.bbc.co.uk/maven2/snapshots",
//          resolvers += "BBC Forge Artifactory" at "https://dev.bbc.co.uk/artifactory/repo",
//          resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
//        )
  )

}
