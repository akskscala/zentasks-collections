import sbt._
import Keys._

import play.Project._

object ApplicationBuild extends Build {

    val appName         = "zentask"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.h2database"     %  "h2"                        % "[1.3,)",
      "com.github.seratch" %% "scalikejdbc"               % "[1.6,)",
      "com.github.seratch" %% "scalikejdbc-interpolation" % "[1.6,)",
      "com.github.seratch" %% "scalikejdbc-play-plugin"   % "[1.6,)"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
    )

}
            
