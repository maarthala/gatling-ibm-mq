package  mq.utils

import java.util.UUID.randomUUID
import java.text.SimpleDateFormat
import java.util.Date
import scala.util.Random
import mq.config._;

object Parser  extends  SimulationTraits {

        
    def replacer :Map[String, String] =
        Map[String, String] (
            "_UUID_" -> UUID,
            "_RAND_STR6_" -> randStr(6),
            "_RAND_STR8_" -> randStr(8),
            "_CURRENT_DT_ISO_" -> currentISODate,
            "_TS_" -> unixtimestamp,
            "_RUNID_" -> Config.RUNID,
            "_RAND_INT4_" -> randInt(4),
            "_RAND_INT6_" -> randInt(6),
            "_RAND_INT8_" -> randInt(8),
            "_DATE_" -> todayDate,
            "_TIME_" -> timeNow
        )


    def randStr(n:Int) = Random.alphanumeric.take(n).mkString.toUpperCase()
    
    def randInt(n: Int) = Random.alphanumeric.filter(_.isDigit).take(n).mkString
    
    def r = new scala.util.Random
    
    def roundUp (d: Double) = math.round(d).toInt
    
    def UUID =  randomUUID().toString().replace("-","").toUpperCase()

    def currentISODate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date())

    def unixtimestamp = (System.currentTimeMillis() / 1000L).toString();

    def todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date())

    def timeNow = new SimpleDateFormat("HH:mm:ss").format(new Date())

}