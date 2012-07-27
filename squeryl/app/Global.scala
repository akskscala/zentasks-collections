import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter}
import org.squeryl.{PrimitiveTypeMode, SessionFactory, Session}
import play.api._

import db.DB
import models._
import anorm._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    import play.api.Play.current
    SessionFactory.concreteFactory = Some(() => Session.create(DB.getConnection("default", true), new H2Adapter))

    PrimitiveTypeMode.transaction{
      ZentasksSchema.create
      InitialData.insert()
    }
  }
  
}

/**
 * Initial set of data to be imported 
 * in the sample application.
 */
object InitialData {
  
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
  
  def insert() = {
    
    if(User.findAll.isEmpty) {
      
      Seq(
        User("guillaume@sample.com", "Guillaume Bort", "secret"),
        User("maxime@sample.com", "Maxime Dantec", "secret"),
        User("sadek@sample.com", "Sadek Drobi", "secret"),
        User("erwan@sample.com", "Erwan Loisant", "secret")
      ).foreach(User.create)
      
      Seq(
        Project(1, "Play framework", "Play 2.0") -> Seq("guillaume@sample.com", "maxime@sample.com", "sadek@sample.com", "erwan@sample.com"),
        Project(2, "Play framework", "Play 1.2.4") -> Seq("guillaume@sample.com", "erwan@sample.com"),
        Project(3, "Play framework", "Website") -> Seq("guillaume@sample.com", "maxime@sample.com"),
        Project(4, "Zenexity", "Secret project") -> Seq("guillaume@sample.com", "maxime@sample.com", "sadek@sample.com", "erwan@sample.com"),
        Project(5, "Zenexity", "Playmate") -> Seq("maxime@sample.com"),
        Project(6, "Personal", "Things to do") -> Seq("guillaume@sample.com"),
        Project(7, "Zenexity", "Play samples") -> Seq("guillaume@sample.com", "maxime@sample.com"),
        Project(8, "Personal", "Private") -> Seq("maxime@sample.com"),
        Project(9, "Personal", "Private") -> Seq("guillaume@sample.com"),
        Project(10, "Personal", "Private") -> Seq("erwan@sample.com"),
        Project(11, "Personal", "Private") -> Seq("sadek@sample.com")
      ).foreach {
        case (project,members) => Project.create(project, members)
      }
      
      Seq(
        Task(0, "Todo", 1, "Fix the documentation", false, None, Some("guillaume@sample.com")),
        Task(0, "Urgent", 1, "Prepare the beta release", false, Some(date("2011-11-15")), None),
        Task(0, "Todo", 9, "Buy some milk", false, None, None),
        Task(0, "Todo", 2, "Check 1.2.4-RC2", false, Some(date("2011-11-18")), Some("guillaume@sample.com")),
        Task(0, "Todo", 7, "Finish zentask integration", true, Some(date("2011-11-15")), Some("maxime@sample.com")),
        Task(0, "Todo", 4, "Release the secret project", false, Some(date("2012-01-01")), Some("sadek@sample.com"))
      ).foreach(Task.create)
      
    }
    
  }
  
}
