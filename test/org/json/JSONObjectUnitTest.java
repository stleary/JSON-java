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
}
