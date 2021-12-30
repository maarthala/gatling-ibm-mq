package jms;


import java.util.logging.*;
import javax.jms.JMSException;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import config.Config;

public class Jms {
    
    private static final Logger logger = Logger.getLogger("com.ibm.mq.samples.jms");

    private static String CIPHER_SUITE;
    private static String CCDTURL;

    public static JmsConnectionFactory createJMSConnectionFactory() {
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

    public static void setJMSProperties(JmsConnectionFactory cf) {
        
        try {
            if (null == CCDTURL) {
                cf.setStringProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, Config.MQPARAMS.get("MQ_HOST"));
                cf.setStringProperty(WMQConstants.WMQ_CHANNEL, Config.MQPARAMS.get("CHANNEL"));
            } else {
                logger.info("Will be making use of CCDT File " + CCDTURL);
                cf.setStringProperty(WMQConstants.WMQ_CCDTURL, CCDTURL);
            }
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, Config.MQPARAMS.get("QMGR"));
            cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsBasicResponse (JMS)");
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            cf.setStringProperty(WMQConstants.USERID, Config.MQPARAMS.get("APP_USER"));
            cf.setStringProperty(WMQConstants.PASSWORD, Config.MQPARAMS.get("APP_PASSWORD"));
            if (CIPHER_SUITE != null && !CIPHER_SUITE.isEmpty()) {
                cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, CIPHER_SUITE);
            }
        } catch (JMSException jmsex) {
            recordFailure(jmsex);
        }
        return;
    }

    public static void recordFailure(Exception ex) {
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

    public static void processJMSException(JMSException jmsex) {
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


}
