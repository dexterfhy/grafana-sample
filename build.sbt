name := "grafana-sample"
organization := "grafana-sample"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.8"

libraryDependencies += guice

libraryDependencies ++= Seq(
  "io.github.cdimascio" % "java-dotenv" % "5.2.2",
  "org.projectlombok" % "lombok" % "1.18.20" % Provided,
  "com.oath.cyclops" % "cyclops" % "10.4.0",
  "org.apache.commons" % "commons-text" % "1.9",
  "org.apache.commons" % "commons-collections4" % "4.4",
  "org.apache.commons" % "commons-math3" % "3.6.1",
  "commons-io" % "commons-io" % "2.11.0",
  "com.google.guava" % "guava" % "r05",
  "org.apache.kafka" % "kafka-clients" % "3.2.0"
)