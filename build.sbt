import Dependencies._
import com.typesafe.sbt.SbtNativePackager.autoImport.packageName

lazy val Forecastify = Project(
  id = "forecastify",
  base = file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings ++ Seq(libraryDependencies ++= Dependencies.akkaDeps ++ akkaHttpDeps ++ commonDeps))

lazy val rootSettings = Seq(
  organization := "io.forecastify",
  parallelExecution in Test := true,
  mainClass in Compile := Some("io.forecastify.Main"),
  version := "0.0.1",
  publishArtifact := false,
  packageName in Docker := "forecastify",
  maintainer in Docker := "plavreshin",
  version in Docker := "master",
  dockerBaseImage := "anapsix/alpine-java",
  dockerExposedPorts := Seq(9000),
  dockerExposedVolumes in Docker := Seq("/opt/docker/logs"),
  dockerEntrypoint in Docker := Seq("./opt/docker/bin/forecastify"),
  dockerRepository := Some("plavreshin"),
  dockerUpdateLatest := true)

lazy val commonSettings = Seq(
  scalaVersion := "2.11.11",
  externalResolvers := Dependencies.libsResolvers,
  scalacOptions ++= Seq(
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Ywarn-dead-code",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding", "utf8"),
  fork in Test := false)
