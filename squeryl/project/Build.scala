import sbt._
import Keys._

import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "zentask"
    val appVersion      = "1.0"

  val appDependencies = Seq(
    "org.squeryl" %% "squeryl" % "0.9.5-2",
    "mysql" % "mysql-connector-java" % "5.1.21"
  )

  val main = PlayProject(appName, appVersion,appDependencies, mainLang = SCALA)
}
            
