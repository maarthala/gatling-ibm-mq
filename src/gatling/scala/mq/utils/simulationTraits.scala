package mq.utils

import io.gatling.core.Predef._
import io.gatling.core.feeder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import com.typesafe.scalalogging.StrictLogging
import javax.jms._
import io.gatling.jms.protocol.JmsProtocolBuilder
import io.gatling.jms.Predef._

import com.ibm.mq.constants.CMQC
import com.ibm.mq.jms._
import com.ibm.msg.client.wmq.common.CommonConstants
import com.ibm.mq.{MQGetMessageOptions, MQMessage, MQPutMessageOptions, MQQueue, MQQueueManager}
import com.ibm.msg.client.jms.JmsConnectionFactory._
import com.ibm.msg.client.jms.JmsFactoryFactory._
import com.ibm.msg.client.wmq.WMQConstants._

import scala.concurrent.duration._

import mq.config.Model._
import mq.utils.Utils

trait SimulationTraits extends StrictLogging {
    
    def getHttpConf(): HttpProtocolBuilder = {
        http
    }

    def createFeeder(filenamePerfix: String) = {
        csv(filenamePerfix + ".csv").circular
    }

    def getJndiConf(mqmodel: MQ): JmsProtocolBuilder  = {

        var bindingFile = Utils.readBindingFileFromResource(mqmodel.bindingPath)
        val jndiBasedConnectionFactory = jmsJndiConnectionFactory
            .connectionFactoryName("qcf")
            .url(s"file://$bindingFile")
            .contextFactory("com.sun.jndi.fscontext.RefFSContextFactory")

        val jndiConfig = jms
        .connectionFactory(jndiBasedConnectionFactory)
        .usePersistentDeliveryMode

        return jndiConfig
    }

    def getJmsConf(mqmodel: MQ ): JmsProtocolBuilder  = {
        println(mqmodel)
        val connectionFactory = new MQConnectionFactory()
            connectionFactory.setHostName(mqmodel.host)
            connectionFactory.setPort(mqmodel.port)
            connectionFactory.setQueueManager(mqmodel.queueManager)
            connectionFactory.setChannel(mqmodel.channel)
            connectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT)

        val jmsConfig = jms
            .connectionFactory(connectionFactory)
            .credentials(mqmodel.authUser, mqmodel.authPassword)
            .usePersistentDeliveryMode
            .replyTimeout(100.millis)
            .matchByCorrelationId

        
        return jmsConfig
    }
}
