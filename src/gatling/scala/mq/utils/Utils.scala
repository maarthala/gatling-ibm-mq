package mq.utils

import com.typesafe.scalalogging.StrictLogging

import io.gatling.core.Predef._
import io.gatling.jms.Predef._
import io.gatling.core.body.StringBody

import mq.config.Config._
import mq.config.Model._
import scala.io.Source
import scala.util.Random

import net.liftweb. json._
import scala.io.Codec
import mq.config.Model._
import mq.utils.Parser


object Utils  extends  SimulationTraits {

    

    def getPath(path : String) : String = {

        if ( TESTDATA_FOLDER.trim.length() != 0 ) {
            return  s"${TESTDATA_FOLDER}/$path"
        } 
        return getClass.getClassLoader.getResource(path).getPath
    }

    def readFileFromResource (filename :String) : String = {
        var file = getPath(filename)
        return  Source.fromFile(file)(Codec.ISO8859).getLines().mkString
    }

    def setParams (content :String, file : Seq[Map[String, Any]]) : String = {
        var tmp = content
        val random = new Random
        
        if (file.toList.length != 0) { 
            var rec = file.toList(random.nextInt(file.toList.length))
            for((k, v) <- rec) {
                tmp = tmp.replace(k,v.toString())
            }
        }
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