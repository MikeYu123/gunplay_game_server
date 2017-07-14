name := "gunplay_from_scratch"

version := "1.0"

scalaVersion := "2.12.2"
lazy val gunplayPhysics = RootProject(uri("git://github.com/mikeyu123/gunplay_physics.git"))
lazy val root = (project in file(".")).dependsOn(gunplayPhysics)
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)


