package mq.config

import com.typesafe.scalalogging.StrictLogging
import mq.utils._;

object Config extends StrictLogging {

    val rundiLocal = (System.currentTimeMillis() / 1000L).toString();

    val MAX_DURATION_SEC = System.getProperty("maxDuration" , "1").toInt * 60

    val THINK_TIME_SEC = System.getProperty("thinktime" , "1").toInt

    val RAMP_UP_SEC = System.getProperty("rampUp", "1").toInt

    val RUNID = System.getProperty("runid" ,rundiLocal)

    val TESTDATA_FOLDER  = System.getProperty("resource" , "")

    val SCN = Utils.readScnConfig();

    println( "***********  TEST DETAILS **********" )
    println("Runid :" + RUNID)
    println("Resource Folder :" + TESTDATA_FOLDER)
    
    println( "***********  END **********" )

}
    