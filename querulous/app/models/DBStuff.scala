package models

import com.twitter.querulous.evaluator.QueryEvaluator

import play.api.Logger

/**
 * Author: chris
 * Created: 7/26/12
 */

object DBStuff {

  val log = Logger("DBStuff")

  val queryEvaluator = QueryEvaluator(
    dbhost = "localhost",
    dbname = "zentasks",
    username = "chris",
    password = "chris")

  def createDbTables() {
    try {
      log.info("Checking if DB tables exist")
      queryEvaluator.count("select count(*) from user")
      log.info("Looks like DB tables already exist")
    } catch { case e =>
      log.info("Looks like DB tables do not exist. Creating them.")

      queryEvaluator.execute(
        """
          |create table user (
          |  email varchar(255) not null primary key,
          |  name varchar(255) not null,
          |  password varchar(255) not null
          |)
        """.stripMargin
      )

      queryEvaluator.execute(
        """
          |create table project (
          |  id bigint not null primary key auto_increment,
          |  name varchar(255) not null,
          |  folder varchar(255) not null
          |)
        """.stripMargin
      )

      queryEvaluator.execute(
        """
          |create table project_member (
          |  project_id                bigint not null,
          |  user_email                varchar(255) not null,
          |  foreign key(project_id)   references project(id) on delete cascade,
          |  foreign key(user_email)   references user(email) on delete cascade
          |)
        """.stripMargin
      )

      queryEvaluator.execute(
        """
          |create table task (
          |  id                        bigint not null primary key auto_increment,
          |  title                     varchar(255) not null,
          |  done                      boolean,
          |  due_date                  timestamp,
          |  assigned_to               varchar(255),
          |  project                   bigint not null,
          |  folder                    varchar(255),
          |  foreign key(assigned_to)  references user(email) on delete set null,
          |  foreign key(project)      references project(id) on delete cascade
          |)
        """.stripMargin
      )

    }
  }
}
