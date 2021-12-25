package mq.scenarios

import mq.config.Config._
import mq.config.Model._
import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import scala.io.Source._
import mq.utils.Utils
import javax.jms._

case class mqscn(name: String, mqmodel: MQ) extends BaseScenario {

    /*
    def scenariobase = exec(
        exec {
            session =>
            var bodystr = Utils.readFileFromResource(mqmodel.payload)
            bodystr =  Utils.setParams(bodystr)
            session.set("bodystr", s"${bodystr}")
        }.exec(
            jms(name).send
                .queue(mqmodel.queueName)
                //.replyQueue("DEV.QUEUE.2")
                .textMessage(StringBody("#{bodystr}"))
                //.check(simpleCheck(checkBodyTextCorrect)))
        )
    )
    */

      def scenariobase = exec(
        exec {
            session =>
            var bodystr = Utils.readFileFromResource(mqmodel.payload)
            bodystr =  Utils.setParams(bodystr)
            session.set("bodystr", s"${bodystr}")
        }.exec(
            jms(name).requestReply
                .queue(mqmodel.queueName)
                .replyQueue(mqmodel.replyQueue)
                .textMessage(StringBody("#{bodystr}"))
                .property("test_header", "test_value")
                .jmsType("test_jms_type")
                .check(simpleCheck(checkBodyTextCorrect)))
        )
        

    val mq = scenario (name)
    .during (MAX_DURATION_SEC){
        exec(scenariobase)
            .pause(mqmodel.thinktime)
    }

    def checkBodyTextCorrect(m: Message) = {
    print ("here")
    print (m);  
    // this assumes that the service just does an "uppercase" transform on the text
    m match {
      case tm: TextMessage => tm.getText == "hello"
      case _               => true
    }
  }
}