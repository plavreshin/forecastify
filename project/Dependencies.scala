import sbt._

object Dependencies {
  val libsResolvers = Seq(
    "JCenter artifactory" at "https://jcenter.bintray.com",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")

  private val mockito = "org.mockito" % "mockito-all" % Versions.mockito % "test"
  private val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
  private val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Versions.akka % "test"
  private val logBackClassic = "ch.qos.logback" % "logback-classic" % Versions.logback
  private val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
  private val typeSafeConfig = "com.typesafe" % "config" % Versions.config
  private val playJson = "com.typesafe.play" %% "play-json" % Versions.playJson
  private val akkaHttpPlay = "de.heikoseeberger" %% "akka-http-play-json" % Versions.akkaHttpPlay
  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-actor",
    "com.typesafe.akka" %% "akka-remote",
    "com.typesafe.akka" %% "akka-slf4j",
    "com.typesafe.akka" %% "akka-persistence",
    "com.typesafe.akka" %% "akka-persistence-query-experimental",
    "com.typesafe.akka" %% "akka-cluster",
    "com.typesafe.akka" %% "akka-cluster-sharding",
    "com.typesafe.akka" %% "akka-stream"
  ).map(_ % Versions.akka)

  val akkaHttpDeps = Seq(
    "com.typesafe.akka" %% "akka-http-core",
    "com.typesafe.akka" %% "akka-http").map(_ % Versions.akkaHttp)

  val commonDeps = Seq(
    mockito,
    scalaTest,
    logBackClassic,
    scalaLogging,
    typeSafeConfig,
    playJson,
    akkaHttpPlay,
    akkaTestkit)

  object Versions {
    val mockito = "1.10.19"
    val scalaTest = "3.0.3"
    val config = "1.3.0"
    val akka = "2.4.18"
    val akkaHttp = "10.0.6"
    val playJson = "2.5.14"
    val logback = "1.1.7"
    val scalaLogging = "3.4.0"
    val akkaHttpPlay = "1.16.0"
  }
}