package mq.scenarios

import mq.config.Config._
import mq.config.Model._
import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import scala.io.Source._
import mq.utils.Utils

case class mqscn(name: String, mqmodel: MQ) extends BaseScenario {

    def scenariobase = exec(
        exec {
            session =>
            var bodystr = Utils.readFileFromResource(mqmodel.payload)
            bodystr =  Utils.setParams(bodystr)
            session.set("bodystr", s"${bodystr}")
        }.exec(
            jms(name).send
                .queue(mqmodel.queueName)
                .textMessage(StringBody("#{bodystr}"))
            )
    )

    val mq = scenario (name)
    .during (MAX_DURATION_SEC){
        exec(scenariobase)
            .pause(mqmodel.thinktime)
    }
}