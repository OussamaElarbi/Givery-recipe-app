ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "Recipe Application - Givery",
    idePackagePrefix := Some("org.givery.recipe")
  ).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  jdbc,
  ehcache
)
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.7.0"
enablePlugins(OpenApiGeneratorPlugin)