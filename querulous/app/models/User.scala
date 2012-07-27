package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.sql.ResultSet
import DBStuff.queryEvaluator

case class User(email: String, name: String, password: String)

object User {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */

  val simpleQ = { row: ResultSet =>
    User(row.getString("user.email"), row.getString("user.name"), row.getString("user.password"))
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    queryEvaluator.selectOne("select * from user where email = ?", email)(simpleQ)
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    queryEvaluator.select("select * from user")(simpleQ)
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    queryEvaluator.selectOne("select * from user where email = ? and password = ?", email, password)(simpleQ)
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    queryEvaluator.insert("insert into user values (?, ?, ?)", user.email, user.name, user.password)
    user
  }
  
}
