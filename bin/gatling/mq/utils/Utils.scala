package mq.utils

import com.typesafe.scalalogging.StrictLogging

import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import io.gatling.core.body.StringBody
import scala.util.Random
import mq.config.Config._
import mq.config.Model._
import scala.io.Source
import java.util.UUID.randomUUID
import net.liftweb. json._
import scala.io.Codec
import mq.config.Model._
//import ma.scenarios._

object Utils  extends  SimulationTraits {

    def replacer :Map[String, String] =
        Map[String, String] (
            "_UUID_" -> UUID,
            "_RANDSTR6_" -> randStr(6),
            "_RANDINT8_" -> randInt(8)
        )

    def randStr(n:Int) = Random.alphanumeric.take(n).mkString.toUpperCase()
    
    def randInt(n: Int) = Random.alphanumeric.filter(_.isDigit).take(n).mkString
    
    def r = new scala.util.Random
    
    def roundUp (d: Double) = math.round(d).toInt
    
    def UUID =  randomUUID().toString().replace("-","").toUpperCase()

    def readFileFromResource (filename :String) : String = {
        val file = s"${BASE_ENV}/$filename"
        val content = Source.fromResource(file)(Codec.ISO8859).getLines().mkString
        return content
    }


    def readBindingFileFromResource(filename :String) : String = {
        val file = s"${BASE_ENV}/bindings/$filename"
        val path = getClass.getClassLoader.getResource(file).getPath
        return path
    }

    def setParams (content :String) : String = {
        var tmp = content
        val re = replacer
        for((k, v) <- re) {
            tmp = tmp.replace(k,v)
        }
        return tmp
    }

    def readScnConfig() : Map[String,MQ] = {
        val content = Utils.readFileFromResource("mq.json")
        implicit val formats = DefaultFormats
        var input = parse(content) . extract[Map[String, MQ]]
        input
    }
}