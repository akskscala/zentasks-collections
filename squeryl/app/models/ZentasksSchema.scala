package models

import org.squeryl.Schema

object ZentasksSchema extends Schema {

  val projects = table[Project]

  val projectMembers = table[ProjectMember]

  val users = table[User]

  val tasks = table[Task]

}
