import java.sql.Timestamp

import play.api._
import play.api.db._
import play.api.Play.current

import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session.Database.threadLocalSession

import models._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.createTables()
    InitialData.insert()
  }
  
}

/**
 * Initial set of data to be imported 
 * in the sample application.
 */
object InitialData {
  
  lazy val db = Database.forDataSource(DB.getDataSource())

  def date(str: String) = {
    val d = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
    new Timestamp(d.getTime)
  }

  def createTables() = db.withSession {
    (
      User.ddl ++
      Project.ddl ++
      Project.seq.ddl ++
      ProjectMember.ddl ++
      Task.ddl ++
      Task.seq.ddl
    ) create
  }

  def insert() = db.withSession {
    
    if(User.findAll.isEmpty) {
      
      User insertAll (
        User("guillaume@sample.com", "Guillaume Bort", "secret"),
        User("maxime@sample.com", "Maxime Dantec", "secret"),
        User("sadek@sample.com", "Sadek Drobi", "secret"),
        User("erwan@sample.com", "Erwan Loisant", "secret")
      )
      
      Seq(
        Project(1, "Play 2.0", "Play framework") -> Seq("guillaume@sample.com", "maxime@sample.com", "sadek@sample.com", "erwan@sample.com"),
        Project(2, "Play 1.2.4", "Play framework") -> Seq("guillaume@sample.com", "erwan@sample.com"),
        Project(3, "Website", "Play framework") -> Seq("guillaume@sample.com", "maxime@sample.com"),
        Project(4, "Secret project", "Zenexity") -> Seq("guillaume@sample.com", "maxime@sample.com", "sadek@sample.com", "erwan@sample.com"),
        Project(5, "Zenexity", "Playmate") -> Seq("maxime@sample.com"),
        Project(6, "Things to do", "Personal") -> Seq("guillaume@sample.com"),
        Project(7, "Play samples", "Zenexity") -> Seq("guillaume@sample.com", "maxime@sample.com"),
        Project(8, "Private", "Personal") -> Seq("maxime@sample.com"),
        Project(9, "Private", "Personal") -> Seq("guillaume@sample.com"),
        Project(10, "Private", "Personal") -> Seq("erwan@sample.com"),
        Project(11, "Private", "Personal") -> Seq("sadek@sample.com")
      ).foreach {
        case (project,members) => Project.create(project, members)
      }
      
      Seq(
        Task(0, "Fix the documentation", false, None, Some("guillaume@sample.com"), 1, "Todo"),
        Task(0, "Prepare the beta release", false, Some(date("2011-11-15")), None, 1, "Urgent"),
        Task(0, "Buy some milk", false, None, None, 9, "Todo"),
        Task(0, "Check 1.2.4-RC2", false, Some(date("2011-11-18")), Some("guillaume@sample.com"), 2, "Todo"),
        Task(0, "Finish zentask integration", true, Some(date("2011-11-15")), Some("maxime@sample.com"), 7, "Todo"),
        Task(0, "Release the secret project", false, Some(date("2012-01-01")), Some("sadek@sample.com"), 4, "Todo")
      ).foreach(Task.create)
      
    }
    
  }
  
}
