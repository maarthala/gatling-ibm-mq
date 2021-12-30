package mq. config

import com.typesafe.scalalogging.StrictLogging

object Model extends StrictLogging {
    case class MQ(
    host: String,
    port: Int,
    queueManager: String,
    channel: String,
    queueName: String,
    replyQueue: String,
    replyValidator: String,
    authUser: String,
    authPassword: String,
    bindingPath: String,
    payload: String,
    users: Int,
    thinktime: Int)
}