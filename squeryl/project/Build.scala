import sbt._
import Keys._

import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "zentask"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "org.squeryl" %% "squeryl" % "0.9.5-2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Sonatype OSS" at "http://oss.sonatype.org/content/repositories/releases",
      externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository"))
    )

}
            
