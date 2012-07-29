package models

import java.sql.Timestamp

import play.api.db._
import play.api.Play.current

import org.scalaquery.ql.ForeignKeyAction.{Cascade, SetNull}
import org.scalaquery.ql.{Query, Sequence}
import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session.Database.threadLocalSession

case class Task(
  id: Long,
  title: String,
  done: Boolean,
  dueDate: Option[Timestamp],
  assignedTo: Option[String],
  project: Long,
  folder: String
)

object Task extends Table[Task]("task") {
  lazy val db = Database.forDataSource(DB.getDataSource())

  def id         = column[Long]     ("id",          O NotNull, O PrimaryKey)
  def title      = column[String]   ("title",       O NotNull, O DBType("varchar(255)"))
  def done       = column[Boolean]  ("done",        O Nullable)
  def dueDate    = column[Timestamp]("due_date",    O Nullable)
  def assignedTo = column[String]   ("assigned_to", O Nullable, O DBType("varchar(255)"))
  def project    = column[Long]     ("project",     O NotNull)
  def folder     = column[String]   ("folder",      O DBType("varchar(255)"))

  def assignedToFk = foreignKey("assigned_to", assignedTo, User)   (_.email, onDelete = SetNull)
  def projectFk    = foreignKey("project",     project,    Project)(_.id,    onDelete = Cascade)

  def * = id ~ title ~ done ~ dueDate.? ~ assignedTo.? ~ project ~ folder <> (Task.apply _, Task.unapply _)

  val seq = Sequence[Long]("task_seq") start 1000

  /**
   * Retrieve a Task from the id.
   */
  def findById(id: Long): Option[Task] = {
    db.withSession {
      val q = for {
        t <- Task
        if t.id === id
      } yield t
      Option(q first)
    }
  }

  /**
   * Retrieve todo tasks for the user.
   */
  def findTodoInvolving(user: String): Seq[(Task,Project)] = {
    db.withSession {
      val q = for {
        t <- Task
        pm <- ProjectMember
        if pm.projectId === t.project
        p <- Project
        if p.id === pm.projectId
        if t.done === false
        if pm.userEmail === user
      } yield t -> p
      q list
    }
  }

  /**
   * Find tasks related to a project
   */
  def findByProject(project: Long): Seq[Task] = {
    db.withSession {
      val q = for {
        t <- Task
        if t.project === project
      } yield t
      q list
    }
  }

  /**
   * Delete a task
   */
  def delete(id: Long) {
    db.withSession {
      val q = for {
        t <- Task
        if t.id === id
      } yield t
      q delete
    }
  }

  /**
   * Delete all task in a folder.
   */
  def deleteInFolder(projectId: Long, folder: String) {
    db.withSession {
      val q = for {
        t <- Task
        if t.project === projectId
        if t.folder === folder
      } yield t
      q delete
    }
  }

  /**
   * Mark a task as done or not
   */
  def markAsDone(taskId: Long, done: Boolean) {
    db.withSession {
      val q = for (t <- Task if t.id === taskId) yield t.done
      q update done
    }
  }

  /**
   * Rename a folder.
   */
  def renameFolder(projectId: Long, folder: String, newName: String) {
    db.withSession {
      val q = for {
        t <- Task
        if t.project === projectId
        if t.folder === folder
      } yield t.folder
      q update folder
    }
  }

  /**
   * Check if a user is the owner of this task
   */
  def isOwner(task: Long, user: String): Boolean = {
    db.withSession {
      val q = for {
        t <- Task
        p <- Project if t.project === p.id
        pm <- ProjectMember if pm.projectId === p.id
        if pm.userEmail === user
        if t.id === task
      } yield pm.userEmail.count == 1
      q first
    }
  }

  /**
   * Create a Task.
   */
  def create(task: Task): Task = {
    db.withSession {
      val id = if (task.id == 0) {
        Query(seq.next) first
      } else {
        task.id
      }
      val t = Task(id, task.title, task.done, task.dueDate, task.assignedTo, task.project, task.folder)
      Task insert t
      t
    }
  }
}
