/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.json;

import java.util.List;
import java.util.Map;
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
public class JSONObjectUnitTest {
    
    String jsonTxtWithAttributes  = "{\"@ref:id\":\"00000000000w_DSC\",\n" +
        "\"@data\":\"http://schemas.flexim.fi/flexim6/2012/data\",\n" +
        "\"@xmlns:ref\":\"http://schemas.flexim.fi/flexim6/2012/ref\",\n" +
        "\"period\":\n" +
        "	{\"@end\":\"18:00:00\",\"@start\":\"06:00:00\"}\n" +
        ",\"@in-use\":\"restricted\"}";
    String jsonStandard = "{\"ID\": \"SGML\",\n" +
        "\"SortAs\": \"SGML\",\n" +
        "\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
        "\"Acronym\": \"SGML\",\n" +
        "\"Abbrev\": \"ISO 8879:1986\",\n" +
        "\"GlossDef\": {\n" +
        "	\"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
        "	\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
        "}}";
    
    JSONObject jsonObjWithAttributes = null;
    JSONObject jsonObjStandard = null;
    public JSONObjectUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        jsonObjStandard = new JSONObject(jsonStandard);
        jsonObjWithAttributes = new JSONObject(jsonTxtWithAttributes);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test hasAttributeKeys
     */
    @Test
    public void testHasAttributeKeys() {

        assertFalse(jsonObjStandard.hasAttributeKeys());
        assertTrue(jsonObjWithAttributes.hasAttributeKeys());
    }
    
    @Test
    public void testGetAttributeKeysNoAttributes() {
        Map<String,String> keys = jsonObjStandard.removeAttributeKeys();        
        assertNotNull(keys);
        assertEquals(0,keys.size());
    }
    @Test
    public void testGetAttributeKeysAttributesPresent(){
        Map<String,String> keys = jsonObjWithAttributes.removeAttributeKeys();
        assertNotNull(keys);
        assertEquals(4,keys.size());
    }
    @Test
    public void transformXmlAtrributeString(){
        String standardStr = jsonObjStandard.transformXmlAtrributeString();
        assertEquals("",standardStr);
        String attributeStr = jsonObjWithAttributes.transformXmlAtrributeString();
        assertEquals("ref:id=\"00000000000w_DSC\" data=\"http://schemas.flexim.fi/flexim6/2012/data\" in-use=\"restricted\" xmlns:ref=\"http://schemas.flexim.fi/flexim6/2012/ref\" ",attributeStr);
    
    }
    @Test
    public void transformXmlAttributeStringIntegerValue(){
    //    jsonObject:{"ws:create-entity":{"@xmlns:ws":"http://flexim.fi/flexim6/2013/ws","@to":"terminalCreate","@gid":"suuryritys.com/","ws:create":{"actor":{"@ref:owner":"actor#valk|Valkeakoski","terminal":{"relays":{"lock-relay":{"@delay":4}},"control":{"mothercard_loop-control":{"mothercard_loop-control-4":{"control-line":{"@io-number":4,"@mode":"E","@id":"Lukko"}}}},"application-settings":{"work":{"@enabled":"Y"}},"@device-type":4,"inside":{"@ref:ref":"location#4","@reader-type":"M"},"outside":{"@ref:ref":"location#5","@reader-type":"A"},"expansion-cards":{"expansion-card":{"extracard_loop-control":{"control-line":{"@io-number":1,"time-group":{"@ref:ref":"group#kv01_TG"},"@mode":"E","@id":"koe","daily-schema":{"@ref:ref":"0000000000002_DSC"}}}}},"network":{"@ref:ref":"actor#valk|Valkeakoski"},"info":{"@address":103220,"@sw-version":"v2.22","@is-activated":"N"}},"@ref:id":"ta11","identification":"","roles":"","@ref:name":"Microteam TA-3011","@xmlns":"http://schemas.flexim.fi/flexim6/2012/data","groups":"","@xmlns:ref":"http://schemas.flexim.fi/flexim6/2012/ref"}}}}

        String addressAttributes = "{\"@address\":103220,\"@sw-version\":\"v2.22\",\"@is-activated\":\"N\"}";
        JSONObject jsonObjWithAddress= new JSONObject(addressAttributes);
        String attributesForXml = jsonObjWithAddress.transformXmlAtrributeString();
        assertEquals("address=\"103220\" sw-version=\"v2.22\" is-activated=\"N\" ",attributesForXml);
    }
}
