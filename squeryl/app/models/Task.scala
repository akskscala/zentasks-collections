package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import org.squeryl.KeyedEntity

case class Task(var id: Long,
                folder: String, projectId: Long,
                title: String,
                done: Boolean,
                dueDate: Option[Date],
                assignedTo: Option[String]) extends KeyedEntity[Long]{
  def this() = this(0,"",0,"",false,None,None)
}

object Task {

  import org.squeryl.PrimitiveTypeMode._
  import ZenSchema._
  
  // -- Queries
  
  /**
   * Retrieve a Task from the id.
   */
  def findById(id: Long): Option[Task] = {
    transaction{
      tasks.lookup(id)
    }
  }

  /**
   * Retrieve todo tasks for the user.
   */
  def findTodoInvolving(user: String): Seq[(Task,Project)] = {
    transaction{
      join(tasks,projects,projectMembers)( (t,p,m) => {
        where( m.userEmail === user).select(t,p).on( (t.projectId === p.id) , (p.id === m.projectId))
      }).toList
    }
  }
  
  /**
   * Find tasks related to a project
   */
  def findByProject(project: Long): Seq[Task] = {

    transaction{
      from(tasks)(t => {
        where(t.projectId === project).select(t)
      }).toList
    }
  }

  /**
   * Delete a task
   */
  def delete(id: Long) {
    transaction{
      tasks.delete(id)
    }
  }
  
  /**
   * Delete all task in a folder.
   */
  def deleteInFolder(projectId: Long, folder: String) {
    transaction{
      tasks.deleteWhere(t => (t.projectId === projectId) and (t.folder === folder))
    }

  }
  
  /**
   * Mark a task as done or not
   */
  def markAsDone(taskId: Long, done: Boolean) {
    transaction{
      update(tasks)(t => {
        where(t.id === taskId).set(t.done := true)
      })
    }

  }
  
  /**
   * Rename a folder.
   */
  def renameFolder(projectId: Long, folder: String, newName: String) {
    transaction{
      update(tasks)(t => {
        where( (t.projectId === projectId) and (t.folder === folder)).set(t.folder := newName)
      })
    }

  }
  
  /**
   * Check if a user is the owner of this task
   */
  def isOwner(task: Long, user: String): Boolean = {
    transaction{
      tasks.lookup(task) match{
        case Some(t) => {
          from(projectMembers)(t => {
            where(t.userEmail === user).select(t)
          }).headOption.isDefined
        }
        case None => false
      }
    }

  }

  /**
   * Create a Task.
   */
  def create(task: Task): Task = {
    transaction{
      tasks.insert(task)
    }

  }
  
}
