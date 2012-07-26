package models

import org.squeryl._

object ZentasksDb extends Schema {

  val projects = table[Project]("project")
  val tasks = table[Task]("task")
  val users = table[User]("user")

}
