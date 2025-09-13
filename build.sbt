ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.6"

maintainer := "Oussama Elarbi Boudihir"

// Disable doc generation (no sources)
Compile / doc / sources := Seq.empty

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, OpenApiGeneratorPlugin)
  .settings(
    name := "Recipe Application - Givery",

    // Dependencies
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      ehcache,
      javaJpa,
      "org.postgresql" % "postgresql" % "42.7.7",
      "org.playframework" %% "play-jdbc-evolutions" % "3.0.9",
      "org.playframework" %% "play-logback" % "3.0.9",
      "javax.annotation" % "javax.annotation-api" % "1.3.2",
      "org.hibernate.orm" % "hibernate-core" % "7.1.0.Final"
    ),

    // OpenAPI code generation
    openApiGeneratorName := "scala-play-server",
    openApiInputSpec := "conf/openapi/recipes-api.yaml",
    openApiOutputDir := ((Compile / sourceManaged).value / "openapi").getPath,
    openApiApiPackage := "org.givery.recipe.api",
    openApiModelPackage := "org.givery.recipe.model",
    openApiInvokerPackage := "org.givery.recipe.invoker",
    openApiAdditionalProperties := Map(
      "modelPropertyNaming" -> "snake_case"
    ),
    Compile / sourceGenerators += Def.task {
      val outDir = (Compile / sourceManaged).value / "openapi"
      openApiGenerate.value
      (outDir ** "*.scala").get ++ (outDir ** "*.java").get
    }.taskValue,
    Compile / managedSourceDirectories ++= Seq(
      (Compile / sourceManaged).value / "openapi"
    )
  )

// Formatting on compile
ThisBuild / scalafmtOnCompile := true

// Resources
Compile / unmanagedResourceDirectories += baseDirectory.value / "conf"
