libraryDependencies ~= {l => l.filter(_.name != "jstools")}

lazy val utils = RootProject(file("../jstools"))

lazy val root = Project(id = "transformers", base = file(".")).dependsOn(utils)
