package models

import com.twitter.querulous.evaluator.QueryEvaluator

/**
 * Author: chris
 * Created: 7/26/12
 */

object DBStuff {

  val queryEvaluator = QueryEvaluator(
    dbhost = "localhost",
    dbname = "zentasks",
    username = "chris",
    password = "chris")

}
