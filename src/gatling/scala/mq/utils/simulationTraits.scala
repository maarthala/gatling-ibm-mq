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
import com.ibm.msg.client.jms.JmsConnectionFactory
import com.ibm.msg.client.jms.JmsFactoryFactory
//import com.ibm.msg.client.wmq.WMQConstants._
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.jms._;

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
   
        /*
        val cf = new MQConnectionFactory()
            cf.setHostName(mqmodel.host)
            cf.setPort(mqmodel.port)
            cf.setQueueManager(mqmodel.queueManager)
            cf.setChannel(mqmodel.channel)
            cf.setTransportType(CommonConstants.WMQ_CM_CLIENT)
        */
        
        val ff: JmsFactoryFactory = JmsFactoryFactory.getInstance(JmsConstants.WMQ_PROVIDER)
        val cf: JmsConnectionFactory = ff.createConnectionFactory

        cf.setStringProperty(CommonConstants.WMQ_HOST_NAME, mqmodel.host)
        cf.setIntProperty(CommonConstants.WMQ_PORT, mqmodel.port)
        cf.setStringProperty(CommonConstants.WMQ_CHANNEL, mqmodel.channel)
        cf.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT)
        cf.setStringProperty(CommonConstants.WMQ_QUEUE_MANAGER, mqmodel.queueManager)
        cf.setStringProperty(CommonConstants.WMQ_APPLICATIONNAME, "Gatling")
        cf.setBooleanProperty(JmsConstants.USER_AUTHENTICATION_MQCSP, true)
        cf.setStringProperty(JmsConstants.USERID, mqmodel.authUser)
        cf.setStringProperty(JmsConstants.PASSWORD, mqmodel.authPassword)
        

        val jmsConfig = jms
            .connectionFactory(cf)
            .credentials(mqmodel.authUser, mqmodel.authPassword)
            .usePersistentDeliveryMode
            .replyTimeout(1000.millis)
            .matchByCorrelationId
        return jmsConfig
    }
}
