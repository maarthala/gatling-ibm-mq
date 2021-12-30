package utils;
import org.junit.jupiter.api.Test;

import java.util.Map;

//import org.junit.jupiter.api.Assertions;

public class ParserTest {

    @Test 
    void isoDate() {
        System.out.println(Parser.currentISODate);
    }

    @Test 
    void ts() {
        System.out.println(Parser.unixtimestamp);
        System.out.println(Parser.todayDate);
        System.out.println(Parser.timeNow);
    }

    @Test
    void randIntStr() {
        System.out.println(Parser.randStr(7));
        System.out.println(Parser.randInt(3));
    }

    @Test
    void replacerMap() {
        Map<String,String> m1 = Parser.replacerMap;
        m1.put("TEST", "value");

        System.out.println(m1);
    }

    
}
