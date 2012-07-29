package models

import play.api.db._
import play.api.Play.current

import org.scalaquery.ql.ForeignKeyAction.Cascade
import org.scalaquery.ql.{Query, Sequence}
import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session.Database.threadLocalSession

case class Project(id: Long, name: String, folder: String)

object Project extends Table[Project]("project") {
  lazy val db = Database.forDataSource(DB.getDataSource())

  def id     = column[Long]  ("id",     O NotNull, O PrimaryKey)
  def name   = column[String]("name",   O NotNull, O DBType("varchar(255)"))
  def folder = column[String]("folder", O NotNull, O DBType("varchar(255)"))

  def * = id ~ name ~ folder <> (Project.apply _, Project.unapply _)

  val seq = Sequence[Long]("project_seq") start 1000

  /**
   * Retrieve a Project from id.
   */
  def findById(id: Long): Option[Project] = {
    db.withSession {
      val q = for {
        p <- Project
        if p.id === id
      } yield p
      Option(q first)
    }
  }

  /**
   * Retrieve project for user
   */
  def findInvolving(user: String): Seq[Project] = {
    db.withSession {
      val q = for {
        p <- Project
        pm <- ProjectMember
        if p.id === pm.projectId
        if pm.userEmail === user
      } yield p
      q list
    }
  }

  /**
   * Update a project.
   */
  def rename(id: Long, newName: String) {
    db.withSession {
      val q = for {
        p <- Project
        if p.id === id
      } yield p.name
      q update newName
    }
  }

  /**
   * Delete a project.
   */
  def delete(id: Long) {
    db.withSession {
      val q = for {
        p <- Project
        if p.id === id
      } yield p
      q delete
    }
  }

  /**
   * Delete all project in a folder
   */
  def deleteInFolder(folder: String) {
    db.withSession {
      val q = for {
        p <- Project
        if p.folder === folder
      } yield p
      q delete
    }
  }

  /**
   * Rename a folder
   */
  def renameFolder(folder: String, newName: String) {
    db.withSession {
      val q = for {
        p <- Project
        if p.folder === folder
      } yield p.name
      q update newName
    }
  }

  /**
   * Retrieve project member
   */
  def membersOf(project: Long): Seq[User] = {
    db.withSession {
      val q = for {
        u <- User
        pm <- ProjectMember
        if u.email === pm.userEmail
        if pm.projectId === project
      } yield u
      q list
    }
  }

  /**
   * Add a member to the project team.
   */
  def addMember(project: Long, user: String) {
    db.withSession {
      ProjectMember insert ProjectMember(project, user)
    }
  }

  /**
   * Remove a member from the project team.
   */
  def removeMember(project: Long, user: String) {
    db.withSession {
      val q = for {
        pm <- ProjectMember
        if pm.projectId === project
        if pm.userEmail === user
      } yield pm
      q delete
    }
  }

  /**
   * Check if a user is a member of this project
   */
  def isMember(project: Long, user: String): Boolean = {
    db.withSession {
      val q = for {
        u <- User
        pm <- ProjectMember
        if u.email === pm.userEmail
        if pm.projectId === project
        if pm.userEmail === user
      } yield u.email.count
      q.first == 1
    }
  }

  /**
   * Create a Project.
   */
  def create(project: Project, members: Seq[String]): Project = {
    db.withSession {
      val id = if (project.id == 0) {
        Query(seq.next) first
      } else {
        project.id
      }
      val p = Project(id, project.name, project.folder)
      Project insert p
      members foreach (ProjectMember insert ProjectMember(id, _))
      p
    }
  }
}

case class ProjectMember(projectId: Long, userEmail: String)

object ProjectMember extends Table[ProjectMember]("project_member") {
  def projectId = column[Long]  ("project_id", O NotNull)
  def userEmail = column[String]("user_email", O NotNull, O DBType("varchar(255)"))

  def projectIdFk = foreignKey("project_id", projectId, Project)(_.id,    onDelete = Cascade)
  def userEmailFk = foreignKey("user_email", userEmail, User)   (_.email, onDelete = Cascade)

  def * = projectId ~ userEmail <> (ProjectMember.apply _, ProjectMember.unapply _)
}
