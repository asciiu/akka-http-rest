name := "akka-http-rest"
organization := "me.archdev"
version := "1.0.0"
scalaVersion := "2.12.2"

libraryDependencies ++= {
  val akkaV = "10.0.10"
  val scalaTestV = "3.0.4"
  val slickVersion = "3.2.1"
  val circeV = "0.8.0"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaV,

    "com.typesafe.akka" %% "akka-stream" % "2.5.4",
    "com.typesafe.akka" %% "akka-actor"  % "2.5.4",

    "de.heikoseeberger" %% "akka-http-circe" % "1.18.0",

    "com.typesafe.slick" %% "slick" % slickVersion,
    "org.postgresql" % "postgresql" % "42.1.4",
    "org.flywaydb" % "flyway-core" % "4.2.0",

    "com.zaxxer" % "HikariCP" % "2.7.0",
    "org.slf4j" % "slf4j-nop" % "1.7.25",

    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-parser" % circeV,

    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV % "test",
    "ru.yandex.qatools.embed" % "postgresql-embedded" % "2.4" % "test",

    "com.softwaremill.akka-http-session" %% "core" % "0.5.2",
    "com.softwaremill.akka-http-session" %% "jwt"  % "0.5.2",
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
  )
}

Revolver.settings
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerExposedPorts := Seq(9000)
dockerEntrypoint := Seq("bin/%s" format executableScriptName.value, "-Dconfig.resource=docker.conf")
