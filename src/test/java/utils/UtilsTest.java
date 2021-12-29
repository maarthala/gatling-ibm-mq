package utils;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
// import com.google.common.base.Charsets;
// import java.util.logging.*;
import org.json.*;
import org.w3c.dom.Document;
// import javax.xml.xpath.XPath;
// import javax.xml.xpath.XPathConstants;
// import javax.xml.xpath.XPathExpressionException;
// import javax.xml.xpath.XPathFactory;
// import java.util.List;
import java.util.HashMap;
// import java.util.Iterator;
import java.util.Map;
// import java.io.ByteArrayOutputStream;
// import java.io.PrintStream;
// import org.junit.contrib.java.lang.system.SystemOutRule;
// import org.junit.Assert;
// import org.junit.Rule;



public class UtilsTest {

    @Test 
    void readFile() throws Exception  {
            String fileContent = Utils.getFileFromResources("test.json");
            JSONObject jsonObject = new JSONObject(fileContent);
            Assertions.assertEquals(jsonObject.getString("name"),"hello");
    }


    @Test 
    void readXmlValue() throws Exception  {
            String fileContent = Utils.getFileFromResources("input.xml");
            String filePropertyFileContent = Utils.getFileFromResources("params.properties");
            Document document = Utils.loadXMLFrom(fileContent);
            Map<String,String> map = Utils.propToMap(filePropertyFileContent);
            Map<String,String> params = Utils.getXMLParams(document, map);
            System.out.println(params);
            String value = params.get("VAR1");
            Assertions.assertEquals(value,"33333");
    }

    @Test 
    void readXml2Document() throws Exception  {
        String fileContent = Utils.getFileFromResources("input.xml");
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

            String inputContent = Utils.getFileFromResources("input.xml");
            Document innputDocument = Utils.loadXMLFrom(inputContent);  
            
            String outputContent = Utils.getFileFromResources("output.xml");
            //System.out.println("Template->" + outputContent);

            String filePropertyFile = Utils.getFileFromResources("params.properties");
            System.out.println(filePropertyFile);

            Map<String,String> map = Utils.propToMap(filePropertyFile);
 
            Map<String,String> ouputParams = Utils.getXMLParams(innputDocument, map);
            //System.out.println("OutputMap->" + ouputParams);

            Map<String,String> mapGeneric = Parser.replacerMap;

            mapGeneric.forEach((k, v) -> ouputParams.merge(k, v, (oldValue, newValue) -> oldValue));
            System.out.println(ouputParams);


            String name = ouputParams.get("VAR1");
            Assertions.assertEquals(name,"33333");
   

            String content = Utils.parsePayload(outputContent, ouputParams);
            System.out.println("Output->" + content);
            Assertions.assertTrue(content.contains("33333"));

    }

}
