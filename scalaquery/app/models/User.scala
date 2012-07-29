package models

import play.api.db._
import play.api.Play.current

import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session.Database.threadLocalSession

case class User(email: String, name: String, password: String)

object User extends Table[User]("user") {
  lazy val db = Database.forDataSource(DB.getDataSource())

  def email    = column[String]("email",    O NotNull, O DBType("varchar(255)"), O PrimaryKey)
  def name     = column[String]("name",     O NotNull, O DBType("varchar(255)"))
  def password = column[String]("password", O NotNull, O DBType("varchar(255)"))

  def * = email ~ name ~ password <> (User.apply _, User.unapply _)

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    db.withSession {
      val q = for {
        u <- User
        if u.email === email
      } yield u
      Option(q first)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    db.withSession {
      val q = for (u <- User) yield u
      q list
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    db.withSession {
      val q = for {
        u <- User
        if u.email === email
        if u.password === password
      } yield u
      Option(q first)
    }
  }

  /**
   * Create a User.
   */
  def create(user: User): User = {
    db.withSession {
      User insert user
      user
    }
  }
}
