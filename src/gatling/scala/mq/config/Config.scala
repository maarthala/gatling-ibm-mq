package mq.config

import com.typesafe.scalalogging.StrictLogging

object Config extends StrictLogging {

    val ENV_DEV: String = "dev"
    val ENV_PERF: String = "perf"

    val BASE_ENV = System.getProperty("env","dev")

    val MAX_DURATION_SEC = System.getProperty("maxDuration" , "1").toInt * 60

    val THINK_TIME_SEC = System.getProperty("thinktime" , "1").toInt

    val RAMP_UP_SEC = System.getProperty("rampUp", "1").toInt
}
    