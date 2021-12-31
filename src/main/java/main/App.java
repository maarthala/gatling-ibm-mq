/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package main;

import java.util.logging.*;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.JMSRuntimeException;

import com.ibm.msg.client.jms.JmsConnectionFactory;

import  utils.Parser;
import logging.*;
import mq.*;
import jms.*;
import config.Config;

import java.util.Properties;

import org.apache.commons.cli.*;


public class App {

    private static final Logger logger = Logger.getLogger("com.ibm.mq.samples.jms");
    
    public static void main(String[] parameter) {

        Logging.initialiseLogging();
        String x = Parser.uuidgen();


        Config.Config();

        logger.info("Put application is starting : " + x);

        JMSContext context;
        Destination destination;
        JMSConsumer consumer;

        JmsConnectionFactory connectionFactory = Jms.createJMSConnectionFactory();
        Jms.setJMSProperties(connectionFactory);
        logger.info("created connection factory");

        context = connectionFactory.createContext();
        logger.info("context created");
        destination = context.createQueue("queue:///" + Config.MQPARAMS.get("READ_QUEUE"));
        logger.info("destination created");
        consumer = context.createConsumer(destination);
        logger.info("consumer created");

        while (true) {
            try {
                Message receivedMessage = consumer.receive();
                Msg.replyToMessage(context, receivedMessage);
            } catch (JMSRuntimeException jmsex) {

                jmsex.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

  

}