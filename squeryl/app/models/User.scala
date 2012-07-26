package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import org.squeryl.KeyedEntity

case class User(var email : String, var name : String , var password : String) extends KeyedEntity[String]{

  def this() = this("","","")

  def id = email
}


object User {

  import org.squeryl.PrimitiveTypeMode._

  val users = ZenSchema.users

  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    transaction{
      from(users)(t => {
        where(t.email === email).select(t)
      }).headOption
    }

  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    transaction{
      from(users)(t => {
        where(true === true).select(t)
      }).toList
    }

  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    transaction{
      from(users)(t => {
        where((t.email === email) and (t.password === password)).select(t)
      }).headOption
    }

  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    transaction{
      users.insert(user)
    }
    user

  }
  
}
