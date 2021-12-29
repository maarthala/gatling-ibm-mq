package mq.scenarios

import mq.config.Config._
import mq.config.Model._
import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import scala.io.Source._
import mq.utils.Utils
import javax.jms._


case class mqscn(name: String, mqmodel: MQ) extends BaseScenario {

    def scenariobase = exec(
        exec {
            session =>
            var bodystr = Utils.readFileFromResource(mqmodel.payload)
            bodystr =  Utils.setParams(bodystr)
            var replyQflag = "1"
            if ( mqmodel.replyQueue.length() == 0 ) {
                replyQflag = "0"
            }
            session.set("bodystr", s"${bodystr}").set("replyQflag" , s"${replyQflag}")
        }.doIfEqualsOrElse( "#{replyQflag}", "0")  { 
        exec(
           jms(name).send
                .queue(mqmodel.queueName)
                .textMessage(StringBody("#{bodystr}"))
        )
        } {
            exec(
            jms(name).requestReply
                .queue(mqmodel.queueName)
                .replyQueue(mqmodel.replyQueue)                
                .textMessage(StringBody("#{bodystr}"))
                .check(simpleCheck(checkBodyTextCorrect))
            )

        }
    )
    

    val mq = scenario (name)
    .during (MAX_DURATION_SEC){
        exec(scenariobase)
            .pause(mqmodel.thinktime)
    }

    def checkBodyTextCorrect(m: Message) = {
    print("CorrelationID : " + m.getJMSCorrelationID());  
    m match {
      case tm: TextMessage => tm.getText.contains(mqmodel.replyValidator)
      case _               => false
    }
  }
}