
name := "lepemail"

lazy val akkaHttpVersion = "10.2.6"
lazy val akkaVersion = "2.6.16"
lazy val courierVersion = "3.0.1"

lazy val sharedSettings = Seq(
  organization := "com.lepltd",
  version := "1.0.0",
  scalaVersion := "2.13.6",
  trapExit := false,
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked"
  )
)

lazy val lepemail = (project in file("."))
  .aggregate(core, web)
  .settings(
    assembly / mainClass := Some("com.lepltd.web.Main")
  )

lazy val web = (project in file("web"))
  .dependsOn(core)
  .settings(
    sharedSettings,
    assembly / assemblyJarName := "lepemail-1.0.0"
  )

lazy val core = (project in file("core")).settings(
  sharedSettings,
  libraryDependencies ++= Seq(
    "com.github.daddykotex" %% "courier" % courierVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.5",
    "io.spray" %% "spray-json" % "1.3.6",

  )
)

resolvers += "Sonatype release repository" at "https://oss.sonatype.org/content/repositories/releases/"

Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")

// show full stack traces and test case durations
Test / testOptions += Tests.Argument("-oDF")
Test / logBuffered := false

ThisBuild / assemblyMergeStrategy := {
  case PathList("module-info.class") =>
    MergeStrategy.discard
  case PathList("io.netty.versions.properties") =>
    MergeStrategy.discard
  case x if x.endsWith("/module-info.class") || x.endsWith("/io.netty.versions.properties") =>
    MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)

}

