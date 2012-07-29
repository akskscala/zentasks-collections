import sbt._
import Keys._

import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "zentask"
    val appVersion      = "1.0"

    val appDependencies = Seq("org.scalaquery" %% "scalaquery" % "0.10.0-M1")

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA)

}
