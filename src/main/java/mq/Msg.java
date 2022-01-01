package mq;


import java.util.logging.*;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSRuntimeException;
import javax.jms.DeliveryMode;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.MQException;
import com.ibm.msg.client.jms.DetailedInvalidDestinationException;
import utils.*;
import java.util.Map;
import  utils.Parser;
import config.Config;
import org.w3c.dom.Document;

public class Msg  {

    private static final Logger logger = Logger.getLogger("com.ibm.mq.samples.jms");
    
    public static void replyToMessage(JMSContext context, Message receivedMessage) {
        try {
            if (receivedMessage instanceof Message) {
                Destination destination = receivedMessage.getJMSReplyTo();
                String correlationID = receivedMessage.getJMSCorrelationID();
                logger.info("JMSCorrelationID: " + correlationID);

                String replyMsgBody = replayMessageBody(receivedMessage.getBody(String.class));
                logger.fine(replyMsgBody);
                TextMessage message = context.createTextMessage(replyMsgBody);

                message.setJMSCorrelationID(correlationID);
                JMSProducer producer = context.createProducer();

                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                producer.send(destination, message);
            }
        } catch (JMSException jmsex) {
            logger.info("******** JMS Exception*********************");

            if (null != jmsex.getCause() && jmsex.getCause() instanceof MQException) {
                MQException innerException = (MQException) jmsex.getCause();

                if (MQConstants.MQRC_UNKNOWN_OBJECT_NAME == innerException.getReason()) {
                    logger.info("Reply to Queue no longer exists, skipping request");
                    return;
                }
            }

            logger.warning("Unexpected Expection replying to message");
            jmsex.printStackTrace();

      } catch (JMSRuntimeException jmsex) {
          // Get this exception when the message does not have a reply to queue.
          if (null != jmsex.getCause()) {
              MQException e = findMQException(jmsex);
              if (null != e && e instanceof MQException) {
                  if (MQConstants.MQRC_UNKNOWN_OBJECT_NAME == e.getReason()) {
                      logger.info("Reply to Queue no longer exists, skipping request");
                      return;
                  }
              }
          }
          if (null != jmsex.getCause() && jmsex.getCause() instanceof DetailedInvalidDestinationException) {
            logger.info("Reply to destination is invalid");
            return;
          }

          logger.warning("Unexpected runtime error");
          jmsex.printStackTrace();
        }
    }

    

    // recurse on the inner exceptions looking for a MQException.
    public static MQException findMQException(Exception e) {
        Exception inner = (Exception) e.getCause();
        if (null != inner) {
            if (inner instanceof MQException) {
                logger.info("Found MQException");
                return (MQException) inner;
            } else {
                return findMQException(inner);
            }
        }
        return null;
    }


    public static String replayMessageBody(String receivedMessage) {

        String replyPayload  = Config.MQPARAMS.get("PAYLOAD").trim();

        if (replyPayload.length() != 0) {

            try {
                Document innputDocument = Utils.loadXMLFrom(receivedMessage); 
                String outputContent = Utils.getFileFromResources(Config.MQPARAMS.get("PAYLOAD"));
                String filePropertyFile = Utils.getFileFromResources("xpath.props");
                Map<String,String> map = Utils.propToMap(filePropertyFile);
                Map<String,String> ouputParams = Utils.getXMLParams(innputDocument, map);
                Map<String,String> mapGeneric = Parser.replacerMap;
                mapGeneric.forEach((k, v) -> ouputParams.merge(k, v, (oldValue, newValue) -> oldValue));
                String content = Utils.parsePayload(outputContent, ouputParams);
                return content;

            }  catch(Exception e) {

            }
        }
        return  receivedMessage;
    }

}