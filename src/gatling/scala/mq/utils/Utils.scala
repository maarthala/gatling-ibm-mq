package mq.utils

import com.typesafe.scalalogging.StrictLogging

import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import io.gatling.core.body.StringBody

import mq.config.Config._
import mq.config.Model._
import scala.io.Source

import net.liftweb. json._
import scala.io.Codec
import mq.config.Model._
import mq.utils.Parser

//import ma.scenarios._

object Utils  extends  SimulationTraits {

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
        val re = Parser.replacer
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