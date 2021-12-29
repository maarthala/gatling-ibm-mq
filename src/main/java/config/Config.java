package config;

import java.util.Map;
import utils.*;

public class Config {

    public static Map<String,String> MQPARAMS;

    public static void  Config() {

        String mqParamsContent = "";
        try {
            mqParamsContent = Utils.getFileFromResources("mq.properties");
        }   catch(Exception e) {

        }

        MQPARAMS = Utils.propToMap(mqParamsContent);
    }

}
