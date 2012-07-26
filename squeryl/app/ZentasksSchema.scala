import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

import models._

object ZentasksSchema extends Schema {

  val projects = table[Project]("project")
  val tasks = table[Task]("task")
  val users = table[User]("user")

}
