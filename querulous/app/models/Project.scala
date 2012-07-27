package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.sql.ResultSet
import DBStuff.queryEvaluator

case class Project(id: Pk[Long], folder: String, name: String)

object Project {


  // -- Parsers
  
  /**
   * Parse a Project from a ResultSet
   */

  val simpleQ = { row: ResultSet =>
    Project(Id(row.getLong("project.id")), row.getString("project.folder"), row.getString("project.name"))
  }
  
  // -- Queries
    
  /**
   * Retrieve a Project from id.
   */
  def findById(id: Long): Option[Project] = {
    queryEvaluator.selectOne("select * from project where id = ?", id)(simpleQ)
  }
  
  /**
   * Retrieve project for user
   */
  def findInvolving(user: String): Seq[Project] = {
    queryEvaluator.select(
      """
        select * from project
        join project_member on project.id = project_member.project_id
        where project_member.user_email = ?
      """,
      user
    )(simpleQ)
  }
  
  /**
   * Update a project.
   */
  def rename(id: Long, newName: String) {
    queryEvaluator.execute("update project set name = ? where id = ?", newName, id)
  }
  
  /**
   * Delete a project.
   */
  def delete(id: Long) {
    queryEvaluator.execute("delete from project where id = ?", id)
  }
  
  /**
   * Delete all project in a folder
   */
  def deleteInFolder(folder: String) {
    queryEvaluator.execute("delete from project where folder = ?", folder)
  }
  
  /**
   * Rename a folder
   */
  def renameFolder(folder: String, newName: String) {
    queryEvaluator.execute("update project set folder = ? where folder = ?", newName, folder)
  }
  
  /**
   * Retrieve project member
   */
  def membersOf(project: Long): Seq[User] = {
    queryEvaluator.select(
      """
          select user.* from user
          join project_member on project_member.user_email = user.email
          where project_member.project_id = ?
      """,
      project
    )(User.simpleQ)
  }
  
  /**
   * Add a member to the project team.
   */
  def addMember(project: Long, user: String) {
    queryEvaluator.insert("insert into project_member values(?, ?)", project, user)
  }
  
  /**
   * Remove a member from the project team.
   */
  def removeMember(project: Long, user: String) {
    queryEvaluator.execute("delete from project_member where project_id = ? and user_email = ?", project, user)
  }
  
  /**
   * Check if a user is a member of this project
   */
  def isMember(project: Long, user: String): Boolean = {
    queryEvaluator.select(
      """
          select count(user.email) = 1 from user
          join project_member on project_member.user_email = user.email
          where project_member.project_id = ? and user.email = ?
      """,
      project, user
    ) { row =>
      row.getBoolean(1) // columns are 1-indexed
    }.head
  }
   
  /**
   * Create a Project.
   */
  def create(project: Project, members: Seq[String]): Project = {
    queryEvaluator.transaction { t =>
      val id = t.insert("insert into project (name, folder) values (?, ?)", project.name, project.folder)

      members.foreach { email =>
        t.insert("insert into project_member values (?, ?)", id, email)
      }

      project.copy(id = Id(id))
    }
  }
  
}
