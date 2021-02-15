/**
 * 
 */
package org.json.junit;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONUtils;
import org.junit.Test;

/**
 * Tests for org.json.JSONUtils.java
 * 
 * @author shashank
 *
 */
public class JSONUtilsTest {

	/**
	 * Test method for
	 * {@link org.json.JSONUtils#getExpandedJSON(org.json.JSONObject)}.
	 */
	@Test
	public void testGetExpandedJSONList() {
		List<JSONObject> originalJSONList = new ArrayList<JSONObject>();
		InputStream jsonStream;
		JSONObject originalJSON;

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestIn2_1.json");
		originalJSON = new JSONObject(new JSONTokener(jsonStream));
		originalJSONList.add(originalJSON);

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestIn2_2.json");
		originalJSON = new JSONObject(new JSONTokener(jsonStream));
		originalJSONList.add(originalJSON);

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestIn2_3.json");
		originalJSON = new JSONObject(new JSONTokener(jsonStream));
		originalJSONList.add(originalJSON);

		List<JSONObject> expandedJSONList = JSONUtils.getExpandedJSON(originalJSONList);

		// JSONUtilsTestIn2_1
		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2_1_1.json");
		JSONObject expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(0)));

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2_1_2.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(1)));

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2_1_3.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(2)));

		// JSONUtilsTestIn2_2
		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2_2_1.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(3)));

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2_2_2.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(4)));

		// JSONUtilsTestIn2_3
		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2_3_1.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(5)));

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2_3_2.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(6)));
	}

	/**
	 * Test method for
	 * {@link org.json.JSONUtils#getExpandedJSON(org.json.JSONObject)}.
	 */
	@Test
	public void testGetExpandedJSON() {
		InputStream jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestIn1.json");
		JSONObject originalJSON = new JSONObject(new JSONTokener(jsonStream));

		List<JSONObject> expandedJSONList = JSONUtils.getExpandedJSON(originalJSON);

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut1.json");
		JSONObject expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(0)));

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut2.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(1)));

		jsonStream = JSONUtilsTest.class.getClassLoader().getResourceAsStream("JSONUtilsTestOut3.json");
		expandedJSON = new JSONObject(new JSONTokener(jsonStream));
		assertTrue(expandedJSON.similar(expandedJSONList.get(2)));
	}

}
