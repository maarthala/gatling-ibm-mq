package mq.simulations

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.jms.Predef._

import mq.scenarios._
import mq.utils._
import mq.config.Model._
import mq.config.Config._
import io.gatling.core.structure.PopulationBuilder
import net.liftweb.json._
import scala.io.Source

class BasicSimulation extends Simulation with SimulationTraits {

    def scnList() = {
        var input = Utils.readScnConfig();
        var scnList= new Array[PopulationBuilder](input.size)
        var i = 0
        for ((k,v) <- input) {
            var conf = if (v.host.isEmpty) getJndiConf(v) else getJmsConf(v)
            var scen = mqscn(k, v).mq.inject (rampUsers(v.users) during (RAMP_UP_SEC)) .protocols(conf)
            scnList(i) = scen
            i=i+1
        }
        scnList
    }
    
    setUp (scnList().toSeq:_*).maxDuration(MAX_DURATION_SEC)
}