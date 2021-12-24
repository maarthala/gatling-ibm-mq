# About this project
This project allows to send message to IBM MQ as part of load test using JMS and JNDI connections.

# Features
> Allows to configure multi queue

> Templating messages

> Dynamic scenarion configuration using configuration file [mq.json](./src/gatling/resources/dev/mq.json)

> Helps shift-left methods to quickly run messages to IBM MQ


# Run IBM MQ in dockers
```
docker run --name ibmmq --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --publish 1414:1414 --publish 9443:9443  --env MQ_APP_PASSWORD=passw0rd ibmcom/mq:latest
```

# Connect to docker and run below commands to provide access to app user
```
docker exec ibmmq  dspmqaut -m QM1 -n SYSTEM.DEFAULT.MODEL.QUEUE -t queue -p app
docker exec ibmmq   setmqaut -m QM1 -n SYSTEM.DEFAULT.MODEL.QUEUE -t queue -p app +dsp +get +put
```

# Open IBM MQ 

https://localhost:9443/

```
username: admin 
password: passw0rd
````

Navigate to Manage -> Local Queue Manager -> QM1 -> DEV.QUEUE.1


# run test

1. Configure MQ details in  [mq.json](./src/gatling/resources/dev/mq.json)
2. [mq.json](./src/gatling/resources/dev/mq.json) allows multi queue configuration. You can configure as many queue as you like with respective [payload](./src/gatling/resources/dev/message.txt)
3. Place your message.txt under  /src/gatling/resources/dev/  . [Example](./src/gatling/resources/dev/message.txt)
4. Apply templating varaibles to your message. Example _UUID_ 
5. run test

- By default test runs for 1 min
```
./gradlew gatlingRun
```

- To run test for  1 hour
```
./gradlew gatlingRun -DmaxDuration=60

```

# Message Templating

Currenlty following placeholders are supported. Always check for the latest code for more placeholders. Templating can be extended using [utils](./src/gatling/scala/mq/utils/Utils.scala)

- [X] _UUID_ -> To get UUID in the format XXXXXXXXXXXXXXXXXXXXXXXXXXXX
- [X] _RANDSTR6_ -> Random string of 6 characters.
- [X] _RANDSTR8_ -> Random string of 8 characters.
- [ ] _CURRENT_DT_ISO_ -> Current Date time in ISO format
- [ ] _CURRENT_TS_ -> Current unix timestamp
- [ ] _RAND_INT_ -> Rand Interger in the range
- [ ] _RUNID_ -> unix timestamp only once