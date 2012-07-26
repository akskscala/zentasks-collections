import sbt._
import Keys._

import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "zentask"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      //"com.twitter" % "querulous" % "2.7.6"
      //"com.twitter" % "querulous_2.9.1" % "2.7.0"
      "com.twitter" % "querulous-core_2.9.1" % "2.7.0",
      "mysql" % "mysql-connector-java" % "5.1.21"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Maven" at "http://maven.twttr.com/"
    )

}
            
