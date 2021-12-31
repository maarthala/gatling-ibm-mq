package utils;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import org.json.*;
import org.w3c.dom.Document;
import java.util.HashMap;
import java.util.Map;

public class UtilsTest {

    @Test 
    void readFile() throws Exception  {
            String fileContent = Utils.getFileFromResources("payload/test.json");
            JSONObject jsonObject = new JSONObject(fileContent);
            Assertions.assertEquals(jsonObject.getString("name"),"hello");
    }


    @Test 
    void readXmlValue() throws Exception  {
            String fileContent = Utils.getFileFromResources("/payload/input.xml");
            String filePropertyFileContent = Utils.getFileFromResources("xpath.props");
            Document document = Utils.loadXMLFrom(fileContent);
            Map<String,String> map = Utils.propToMap(filePropertyFileContent);
            Map<String,String> params = Utils.getXMLParams(document, map);
            System.out.println(params);
            String value = params.get("VAR1");
            Assertions.assertEquals(value,"33333");
    }

    @Test 
    void readXml2Document() throws Exception  {
        String fileContent = Utils.getFileFromResources("payload/input.xml");
        Document document = Utils.loadXMLFrom(fileContent);
        System.out.println(document);
    }


    @Test 
    void preparePayload() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("UUID" , "123456");
        map.put("DT" , "helo");
        System.out.println(map);
        String content = "hello ${UUID} , date ${DT}";
        content = Utils.parsePayload(content, map);
        Assertions.assertTrue(content.contains("123456"));
    }

    @Test
    void testOutputXml() throws Exception {

            String inputContent = Utils.getFileFromResources("payload/input.xml");
            Document innputDocument = Utils.loadXMLFrom(inputContent);  
            
            String outputContent = Utils.getFileFromResources("payload/output.xml");

            String filePropertyFile = Utils.getFileFromResources("xpath.props");
            System.out.println(filePropertyFile);

            Map<String,String> map = Utils.propToMap(filePropertyFile);
 
            Map<String,String> ouputParams = Utils.getXMLParams(innputDocument, map);

            Map<String,String> mapGeneric = Parser.replacerMap;

            mapGeneric.forEach((k, v) -> ouputParams.merge(k, v, (oldValue, newValue) -> oldValue));
  
            String name = ouputParams.get("VAR1");
            Assertions.assertEquals(name,"33333");
   
            String content = Utils.parsePayload(outputContent, ouputParams);
            System.out.println("Output->" + content);
            Assertions.assertTrue(content.contains("33333"));

    }

}
