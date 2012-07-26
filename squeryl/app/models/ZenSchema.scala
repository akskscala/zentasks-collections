package models

import org.squeryl.Schema

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/07/26
 * Time: 20:41
 * To change this template use File | Settings | File Templates.
 */

object ZenSchema  extends Schema{

  val projects = table[Project]

  val projectMembers = table[ProjectMember]

  val users = table[User]

  val tasks = table[Task]

}
