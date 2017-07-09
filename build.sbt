enablePlugins(JavaAppPackaging)

name := "jms-consumer"
organization := "org.jms.consumer"
version := "1.0.0"

scalaVersion := "2.12.2"
	
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xfatal-warnings",
  "-Ywarn-unused-import",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen")

libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter" % "1.5.4.RELEASE",
  "javax.jms" % "javax.jms-api" % "2.0",
  "org.apache.activemq" % "activemq-broker" % "5.14.5",
  "org.scalaz" %% "scalaz-effect" % "7.2.13",
  "io.reactivex.rxjava2" % "rxjava" % "2.1.1",
  "org.springframework.boot" % "spring-boot-starter-test" % "1.5.4.RELEASE" % "test",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "org.apache.activemq.tooling" % "activemq-junit" % "5.14.5" % "test",
  "org.awaitility" % "awaitility" % "3.0.0" % "test")

scalacOptions in (Compile, doc) := Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")
scalacOptions in (Test, doc) := Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")
apiMappings += (
  scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/"))
autoAPIMappings := true
apiURL := Some(url("http://consumer.jms.org/api/"))

scalastyleConfig := file("jms_consumer-style-config.xml")
scalastyleFailOnError := true

jacoco.settings

jacoco.excludes in jacoco.Config := Seq(
  "*DemoMsgConsumerConfig*",
  "*DemoMsgConsumerApplication*")

jacoco.thresholds in jacoco.Config := de.johoop.jacoco4sbt.Thresholds(
  instruction = 51.00,
  method = 95.00,
  branch = 51.00,
  complexity = 51.00,
  line = 95.0,
  clazz = 95.0)

mappings in (Compile, packageDoc) := Seq()
