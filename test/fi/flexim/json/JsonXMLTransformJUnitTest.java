/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.flexim.json;

import org.json.JSONObject;
import org.json.JSONML;
import org.json.JSONString;
import org.json.XML;
import org.json.XMLTokener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kaustni
 */
public class JsonXMLTransformJUnitTest {
    
    public JsonXMLTransformJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void transformXMLtoJSON() {
        String xmlStr = "<ws><daily-schema data=\"http://schemas.flexim.fi/flexim6/2012/data\"  xmlns:ref=\"http://schemas.flexim.fi/flexim6/2012/ref\" ref:id=\"00000000000w_DSC\"   in-use=\"restricted\"><period start=\"06:00:00\" end=\"18:00:00\"></period></daily-schema></ws>";
        String jsonStr = org.json.XML.toJSONObject(xmlStr).toString();        
        System.out.println("json:"+jsonStr);
        
        JSONObject json = new JSONObject(jsonStr);
        System.out.println("jsonObject:"+json);
        String xml = XML.toString(json);
        //String xml = XML.toString((Object)json);
        System.out.println("xml:"+xml);
        assertEquals(xmlStr,xml);
    }
}
