package models

import org.squeryl.dsl.CompositeKey2
import org.squeryl.KeyedEntity

case class Project(var id: Long, var folder: String, var name: String) extends KeyedEntity[Long] {

  def this() = this(0, "", "")
}

case class ProjectMember(var projectId: Long, var userEmail: String) extends KeyedEntity[CompositeKey2[Long, String]] {

  def this() = this(0, "")

  def id = new CompositeKey2[Long, String](projectId, userEmail)
}


object Project {

  import org.squeryl.PrimitiveTypeMode._

  val projects = ZentasksSchema.projects
  val members = ZentasksSchema.projectMembers
  val users = ZentasksSchema.users


  // -- Queries

  /**
   * Retrieve a Project from id.
   */
  def findById(id: Long): Option[Project] = {

    transaction {
      projects.lookup(id)
    }
  }

  /**
   * Retrieve project for user
   */
  def findInvolving(user: String): Seq[Project] = {
    transaction {
      val projectIds = from(members)(t => {
        where(t.userEmail === user).select(t)
      }).toList map (_.projectId)

      from(projects)(t => {
        where(t.id in projectIds).select(t)
      }).toList
    }
  }

  /**
   * Update a project.
   */
  def rename(id: Long, newName: String) {
    transaction {
      update(projects)(t => {
        where(t.id === id).set(t.name := newName)
      })
    }
  }

  /**
   * Delete a project.
   */
  def delete(id: Long) {
    transaction {
      projects.delete(id)
    }

  }

  /**
   * Delete all project in a folder
   */
  def deleteInFolder(folder: String) {
    transaction {
      projects.deleteWhere(t => t.folder === folder)
    }

  }

  /**
   * Rename a folder
   */
  def renameFolder(folder: String, newName: String) {
    transaction {
      update(projects)(t => {
        where(t.folder === folder).set(t.folder := newName)
      })

    }

  }

  /**
   * Retrieve project member
   */
  def membersOf(project: Long): Seq[User] = {
    transaction {
      val ms = from(members)(t => {
        where(t.projectId === project).select(t)
      }).toList.map(_.userEmail)

      from(users)(t => {
        where(t.email in ms).select(t)
      }).toList

    }

  }

  /**
   * Add a member to the project team.
   */
  def addMember(project: Long, user: String) {
    val m = new ProjectMember(project, user)
    transaction {
      members.insert(m)
    }

  }

  /**
   * Remove a member from the project team.
   */
  def removeMember(project: Long, user: String) {
    transaction {
      members.deleteWhere(t => {
        (t.projectId === project) and (t.userEmail === user)
      })
    }
  }

  /**
   * Check if a user is a member of this project
   */
  def isMember(project: Long, user: String): Boolean = {

    transaction {
      from(members)(t => {
        where((t.projectId === project) and (t.userEmail === user)).select(t)
      }).headOption.isDefined
    }

  }

  /**
   * Create a Project.
   */
  def create(project: Project, members: Seq[String]): Project = {

    transaction {
      projects.insert(project)

      val ms = members.map(email => ProjectMember(project.id, email))
      this.members.insert(ms)
    }
    project
  }

}
