package utils;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Parser {


    public static String uuidgen() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString.replace("-","").toUpperCase();
    }


    public static String  currentISODate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());

    public static String unixtimestamp = Long.toString((System.currentTimeMillis() / 1000L));

    public static String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    public static String timeNow = new SimpleDateFormat("HH:mm:ss").format(new Date());

    public static String randStr(Integer targetStringLength)  {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
    
        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

          return generatedString;
    }

    public static String randInt(Integer targetStringLength)  {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 57; // numeral '10'
        Random random = new Random();
    
        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

          return generatedString;
    }


    public static  Map<String, String> replacerMap = new HashMap<String, String>()
        {
            {
                put("UUID", uuidgen());
                put("RAND_STR6", randStr(6));
                put("RAND_STR8", randStr(8));
                put("CURRENT_DT_ISO", currentISODate);
                put("TS", unixtimestamp);
                put("RUNID", "1234");
                put("RAND_INT4", randInt(4));
                put("RAND_INT6", randInt(6));
                put("RAND_INT8", randInt(8));
                put("DATE", todayDate);
                put("TIME", timeNow);

            };
        };

}
