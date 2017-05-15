resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC3")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M8")