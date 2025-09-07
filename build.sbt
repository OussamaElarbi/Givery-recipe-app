ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.6"

maintainer := "Oussama Elarbi Boudihir"

Compile / doc / sources := Seq.empty

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "Recipe Application - Givery",
    idePackagePrefix := Some("org.givery.recipe"),
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      ehcache,
      "org.postgresql" % "postgresql" % "42.7.7",
      "org.playframework" %% "play-jdbc-evolutions" % "3.0.8"
    )
  )