package utils;

import java.io.StringReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.util.HashMap;
import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;


public  class Utils {


    public static String getFileFromResources(String fileName) throws Exception {

        String resourcePath = System.getProperty("resource");
        
        ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(fileName);
        
        if (resourcePath.length() > 0) {
            fileName = resourcePath + "/" + fileName;
            File initialFile = new File(fileName);
            stream = new FileInputStream(initialFile);
        }
        
        String text = null;
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

    public static Document loadXMLFrom(String xml) throws TransformerException {
        System.out.println(xml);
        Source source = new StreamSource(new StringReader(xml));
        DOMResult result = new DOMResult();
        TransformerFactory.newInstance().newTransformer().transform(source , result);
        return (Document) result.getNode();
    } 


    public static String evaluateXPath(Document document, String xpathExpression) throws Exception 
    {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
 
        List<String> values = new ArrayList<>();
        try
        {
            XPathExpression expr = xpath.compile(xpathExpression);
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
             
            for (int i = 0; i < nodes.getLength(); i++) {
                values.add(nodes.item(i).getNodeValue());
            }
                 
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return values.get(0);
    }

    // extract values from XML based on properties file map
    public static Map<String,String> getXMLParams(Document document , Map<String, String> paramList) throws Exception  {
        Map<String, String> mapr = new HashMap<String, String>();
        System.out.println(paramList);
        for (String name : paramList.keySet())
        {
            String xpath = paramList.get(name);
            try {
                String value =  evaluateXPath(document, xpath);
                mapr.put(name, value);
            }  catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            
        }
        return mapr;
    }

    // Convert Properties file content to map
    public static Map<String, String> propToMap(String filePropertyFileContent){
        Properties getProperties = new Properties();
        
        Map<String, String> propertyMap = new HashMap<String, String>();
        try {
            InputStream inputStream = new ByteArrayInputStream(filePropertyFileContent.getBytes(StandardCharsets.UTF_8));
            getProperties.load(inputStream);
            propertyMap.putAll((Map) getProperties);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertyMap;
    }


    public static String  parsePayload(String payloadFile , Map<String,String> params){
        for (Map.Entry<String, String> entry : params.entrySet()) {
            payloadFile = payloadFile.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return payloadFile;
    }

}
