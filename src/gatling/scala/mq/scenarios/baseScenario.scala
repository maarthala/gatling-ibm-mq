package mq.scenarios

import com.typesafe.scalalogging.StrictLogging
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import mq.config.Config._

trait BaseScenario extends StrictLogging   {

    def scenariobase: ChainBuilder
    //def scenarioSend: ChainBuilder

    var env: String = System.getProperty("env", "local")
}
