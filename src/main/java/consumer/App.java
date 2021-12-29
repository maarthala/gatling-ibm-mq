/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package consumer;

import java.util.logging.*;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSRuntimeException;
import javax.jms.DeliveryMode;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.MQException;
import com.ibm.msg.client.jms.DetailedInvalidDestinationException;
import utils.*;
import java.util.Map;
import  utils.Parser;
import org.w3c.dom.Document;


public class App {

    private static final Level LOGLEVEL = Level.ALL;
    private static final Logger logger = Logger.getLogger("com.ibm.mq.samples.jms");

    private static String CIPHER_SUITE;
    private static String CCDTURL;
    private static Map<String,String> MQPARAMS;

    public static void main(String[] args) {

        initialiseLogging();
        String x = Parser.uuidgen();

        String mqParamsContent = "";
        try {
            mqParamsContent = Utils.getFileFromResources("mq.properties");
        }   catch(Exception e) {

        }
        logger.info(mqParamsContent);
        MQPARAMS = Utils.getXpathProperties(mqParamsContent);
        logger.info("Put application is starting" + x);

        JMSContext context;
        Destination destination;
        JMSConsumer consumer;

        JmsConnectionFactory connectionFactory = createJMSConnectionFactory();
        setJMSProperties(connectionFactory);
        logger.info("created connection factory");

        context = connectionFactory.createContext();
        logger.info("context created");
        destination = context.createQueue("queue:///" + MQPARAMS.get("READ_QUEUE"));
        logger.info("destination created");
        consumer = context.createConsumer(destination);
        logger.info("consumer created");

        while (true) {
            try {
                Message receivedMessage = consumer.receive();
                //long extractedValue = getAndDisplayMessageBody(receivedMessage);
                replyToMessage(context, receivedMessage);
            } catch (JMSRuntimeException jmsex) {

                jmsex.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static long getAndDisplayMessageBody(Message receivedMessage) {
        long responseValue = 0;
        if (receivedMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) receivedMessage;
            try {
                logger.info("Request message was :" + textMessage.getText());
                responseValue = 10;
            } catch (JMSException jmsex) {
                recordFailure(jmsex);
            }
        } else if (receivedMessage instanceof Message) {
            logger.info("Message received was not of type TextMessage.\n");
        } else {
            logger.info("Received object not of JMS Message type!\n");
        }
        return responseValue;
    }

    private static void replyToMessage(JMSContext context, Message receivedMessage) {
        try {
            if (receivedMessage instanceof Message) {
                Destination destination = receivedMessage.getJMSReplyTo();
                
                // System.out.println(destination);
                // if (MQPARAMS.get("WRITE_QUEUE").trim().length() != 0) {
                //     destination = context.createQueue("queue:///" + MQPARAMS.get("WRITE_QUEUE"));
                // }

                String correlationID = receivedMessage.getJMSCorrelationID();
                logger.info("JMSCorrelationID: " + correlationID);

                String replyMsgBody = replayMessageBody(receivedMessage.getBody(String.class));
                System.out.println(replyMsgBody);
                TextMessage message = context.createTextMessage(replyMsgBody);

                message.setJMSCorrelationID(correlationID);
                JMSProducer producer = context.createProducer();
                
                // Make sure message put on a reply queue is non-persistent so non XMS/JMS apps
                // can get the message off the temp reply queue
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

          // Get this exception when the reply to queue is no longer valid.
          // eg. When app that posted the message is no longer running.
          if (null != jmsex.getCause() && jmsex.getCause() instanceof DetailedInvalidDestinationException) {
            logger.info("Reply to destination is invalid");
            return;
          }

          logger.warning("Unexpected runtime error");
          jmsex.printStackTrace();
        }
    }

    // recurse on the inner exceptions looking for a MQException.
    private static MQException findMQException(Exception e) {
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


    private static JmsConnectionFactory createJMSConnectionFactory() {
        JmsFactoryFactory ff;
        JmsConnectionFactory cf;
        try {
            ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            cf = ff.createConnectionFactory();
        } catch (JMSException jmsex) {
            recordFailure(jmsex);
            cf = null;
        }
        return cf;
    }

    private static void setJMSProperties(JmsConnectionFactory cf) {
        
        try {
            if (null == CCDTURL) {
                cf.setStringProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, MQPARAMS.get("MQ_HOST"));
                cf.setStringProperty(WMQConstants.WMQ_CHANNEL, MQPARAMS.get("CHANNEL"));
            } else {
                logger.info("Will be making use of CCDT File " + CCDTURL);
                cf.setStringProperty(WMQConstants.WMQ_CCDTURL, CCDTURL);
            }
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, MQPARAMS.get("QMGR"));
            cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsBasicResponse (JMS)");
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            cf.setStringProperty(WMQConstants.USERID, MQPARAMS.get("APP_USER"));
            cf.setStringProperty(WMQConstants.PASSWORD, MQPARAMS.get("APP_PASSWORD"));
            if (CIPHER_SUITE != null && !CIPHER_SUITE.isEmpty()) {
                cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, CIPHER_SUITE);
            }
        } catch (JMSException jmsex) {
            recordFailure(jmsex);
        }
        return;
    }

    private static void recordFailure(Exception ex) {
        if (ex != null) {
            if (ex instanceof JMSException) {
                processJMSException((JMSException) ex);
            } else {
                logger.warning(ex.getMessage());
            }
        }
        logger.info("FAILURE");
        return;
    }

    private static void processJMSException(JMSException jmsex) {
        logger.info(jmsex.getMessage());
        Throwable innerException = jmsex.getLinkedException();
        logger.info("Exception is: " + jmsex);
        if (innerException != null) {
            logger.info("Inner exception(s):");
        }
        while (innerException != null) {
            logger.warning(innerException.getMessage());
            innerException = innerException.getCause();
        }
        return;
    }

    private static void initialiseLogging() {
        Logger defaultLogger = Logger.getLogger("");
        Handler[] handlers = defaultLogger.getHandlers();
        if (handlers != null && handlers.length > 0) {
            defaultLogger.removeHandler(handlers[0]);
        }

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(LOGLEVEL);
        logger.addHandler(consoleHandler);

        logger.setLevel(LOGLEVEL);
        logger.finest("Logging initialised");
    }

    private static String replayMessageBody(String receivedMessage) {

        String replyPayload  = MQPARAMS.get("PAYLOAD").trim();

        if (replyPayload.length() != 0) {

            try {
                Document innputDocument = Utils.loadXMLFrom(receivedMessage); 
                String outputContent = Utils.getFileFromResources(MQPARAMS.get("PAYLOAD"));
                String filePropertyFile = Utils.getFileFromResources("params.properties");
                Map<String,String> map = Utils.getXpathProperties(filePropertyFile);
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