# About this project
This project allows to send message to IBM MQ as part of load test using JMS and JNDI connections. Example configuration in this project use IBM MQ running in dockers to send message.

# Features
> Allows to configure multi queue

> Supports replyTo

> Templating messages

> Dynamic scenarion configuration using configuration file [mq.json](./src/gatling/resources/dev/mq.json)

> Helps shift-left methods to quickly run messages to IBM MQ

# Manual Execution

# Run IBM MQ in dockers
To run this project locally, you need IBM MQ running. Run IBM docker using below command.
```
docker run --name ibmmq --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --publish 1414:1414 --publish 9443:9443  --env MQ_APP_PASSWORD=passw0rd ibmcom/mq:latest
```


# Open IBM MQ 

https://localhost:9443/

```
username: admin 
password: passw0rd
````

Navigate to Manage -> Local Queue Manager -> QM1 -> DEV.QUEUE.1

## Configuration
[mq.json](./src/gatling/resources/dev/mq.json) contains MQ configration. Note. If replyQueue is defined, gatling will automatically add replyQueue and wait for the response otherwise gatling send message without wait.  

# run test

1. Configure MQ details in  [mq.json](./src/gatling/resources/dev/mq.json)
2. [mq.json](./src/gatling/resources/dev/mq.json) allows multi queue configuration. You can configure as many queue as you like with respective [payload](./src/gatling/resources/dev/payload/sample_message1.txt)
3. Place your message.txt under  /src/gatling/resources/dev/payload  . [Example](./src/gatling/resources/dev/payload/sample_message1.txt)
4. Apply templating varaibles to your message. Example _UUID_ 
5. run test

This project was buid using gradle. You might need to install gradle and java.

- By default test runs for 1 min
```
./gradlew gatlingRun
```

- To run test for  1 hour
```
./gradlew gatlingRun -DmaxDuration=60

```

# Producer support respond to replayQueue configured in Gatling Test using dynamic payload , including correlation from received message (XML only)

## How to use producer
Producer has 3 configuration files based on the requirement

- /src/main/resources/mq.properties >  MQ Details (Note: if WRITE_QUEUE has value, producer overwrite replyQueue from gatling )
- /src/main/resources/params.propertie > Properties file for xpath correlation in XML files. Variables are placeholders in payload (example : output.xml)
- payload (example : /src/main/resources/output.xml) > Producer send this output.xml content as a response to the message received in READ_QUEUE

## Templating
Similar to gatling, producer also supports templating. Gatling and Producer support same placeholders. To use placeholders, define placeholder in this format ${}  Example: ${UUID} or correlation from params.properties  Example ${VAR1}


- To run producer
```
./gradlew run

```

# Message Templating

Currenlty following placeholders are supported. Always check for the latest code for more placeholders. Templating can be extended using [utils](./src/gatling/scala/mq/utils/parser.scala)

- [X] _UUID_ -> To get UUID  Example: 9802139069434EE2B1B36266FD1BCF59
- [X] _RAND_STR6_ -> Random string of 6 characters. Example :eredce
- [X] _RAND_STR8_ -> Random string of 8 characters. Example :eredceww
- [X] _CURRENT_DT_ISO_ -> Current Date time in ISO format  Example: 2021-12-24T22:26:54
- [X] _CURRENT_TS_ -> Current unix timestamp  Example : 1640378409
- [X] _RAND_INT4_ -> Rand Interger in the range  Example: 3492
- [X] _RAND_INT6_ -> Rand Interger in the range  Example: 209223
- [X] _RAND_INT8_ -> Rand Interger in the range  Example: 20922322
- [X] _RUNID_ -> unix timestamp. Once Only per test.   Example : 1640378409
- [X] _DATE_ -> Only today date in YYYY-MM-DD.   Example : 2021-12-24
- [X] _TIME_ -> Only current time in HH:MM:SS.   Example : 12:12:12


# References
https://javadoc.io/doc/io.gatling/gatling-jms/latest/io/gatling/jms/action/index.html
