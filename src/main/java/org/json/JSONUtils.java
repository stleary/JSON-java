package org.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JSONUtils {

	/**
	 * A collection of static methods spanning JSONObjects and JSONArrays. We don't
	 * expect this to class be instantiated
	 * 
	 * @throws IllegalAccessException
	 */
	private JSONUtils() throws IllegalAccessException {
		throw new IllegalAccessException("Utilty class. Not meant to be instantiated");
	}

	/**
	 * Use this method to expand(<i>split</i>) each item in a List of JSONObjects
	 * with JSONArrays inside them into a List of multiple JSONObjects where each
	 * entry is a simple JSON. <i>Replicas</i> appear immediately next to their <i>
	 * originals</i> (i.e.) the order is maintained
	 * <p>
	 * If the original JSON contains a JSONArray for its <i>value</i>, in the
	 * expanded version, the i<sup>th</sup> JSONObject holds the element from
	 * i<sup>th</sup> position of original array
	 * </p>
	 * <p>
	 * If the original JSON contains an array but does not have a value at
	 * i<sup>th</sup> position, a <i>null</i> is placed
	 * </p>
	 * <p>
	 * If the original JSON does not contain an array, simply the original
	 * <i>scalar</i> is copied
	 * </p>
	 * 
	 * For example,
	 *
	 * <pre>
	 * {
	 *   a: b,
	 *   c: [d1, d2, d3],
	 *   e: [f1, f2]
	 * }
	 * </pre>
	 *
	 * results in the List of JSONObjects
	 * 
	 * <pre>
	 * {		{		{
	 *   a: b,	  a: b,	  	  a: b,
	 *   c: d1,	  c: d2,  	  c: d3,
	 *   e: f1	  e: f2	  	  e: null
	 * }		}		}
	 * </pre>
	 */
	public static List<JSONObject> getExpandedJSON(List<JSONObject> incomingJSONList) {
		List<JSONObject> outgoingJSONObjectList = new ArrayList<JSONObject>();

		for (JSONObject incomingJSON : incomingJSONList) {
			outgoingJSONObjectList.addAll(getExpandedJSON(incomingJSON));
		}
		return outgoingJSONObjectList;
	}

	/**
	 * Use this method to expand(<i>split</i>) a single JSONObject with JSONArrays
	 * inside it into a List of multiple JSONObjects where each entry is a simple
	 * JSON
	 * <p>
	 * If the original JSON contains a JSONArray for its <i>value</i>, in the
	 * expanded version, the i<sup>th</sup> JSONObject holds the element from
	 * i<sup>th</sup> position of original array
	 * </p>
	 * <p>
	 * If the original JSON contains an array but does not have a value at
	 * i<sup>th</sup> position, a <i>null</i> is placed
	 * </p>
	 * <p>
	 * If the original JSON does not contain an array, simply the original
	 * <i>scalar</i> is copied
	 * </p>
	 * 
	 * For example,
	 *
	 * <pre>
	 * {
	 *   a: b,
	 *   c: [d1, d2, d3],
	 *   e: [f1, f2]
	 * }
	 * </pre>
	 *
	 * results in the List of JSONObjects
	 * 
	 * <pre>
	 * {		{		{
	 *   a: b,	  a: b,	  	  a: b,
	 *   c: d1,	  c: d2,  	  c: d3,
	 *   e: f1	  e: f2	  	  e: null
	 * }		}		}
	 * </pre>
	 */
	public static List<JSONObject> getExpandedJSON(JSONObject incomingJSON) {
		List<JSONObject> outgoingJSONObjectList = new ArrayList<JSONObject>();

		List<Integer> arrayLengthCounts = new ArrayList<Integer>();
		List<String> arrayKeyNames = new ArrayList<String>();

		arrayLengthCounts.add(0);

		determineArrayDetails(incomingJSON, arrayLengthCounts, arrayKeyNames);

		int entryReplicationFactor = Collections.max(arrayLengthCounts);

		if (entryReplicationFactor == 0) {
			// Add once
			outgoingJSONObjectList.add(incomingJSON);
		} else {
			Set<String> allKeys = new HashSet<String>(incomingJSON.keySet());
			allKeys.removeAll(arrayKeyNames);

			String[] keysToCopy = allKeys.toArray(new String[0]);

			replicateAndFill(outgoingJSONObjectList, incomingJSON, arrayKeyNames, entryReplicationFactor, keysToCopy);
		}

		return outgoingJSONObjectList;
	}

	/**
	 * Evaluate specifics of JSONArray entries such as their names and lengths
	 * 
	 * @param incomingJSON
	 * @param arrayLengthCounts
	 * @param arrayKeyNames
	 */
	private static void determineArrayDetails(JSONObject incomingJSON, List<Integer> arrayLengthCounts,
			List<String> arrayKeyNames) {
		Iterator<String> keyIterator = incomingJSON.keys();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			if (incomingJSON.optJSONArray(key) != null) {
				arrayKeyNames.add(key);
				arrayLengthCounts.add(incomingJSON.getJSONArray(key).length());
			}
		}
	}

	/**
	 * Fill the given list with entries replicated as required
	 * 
	 * @param outgoingJSONObjectList
	 * @param incomingJSON
	 * @param arrayKeyNames
	 * @param entryReplicationFactor
	 * @param keysToCopy
	 */
	private static void replicateAndFill(List<JSONObject> outgoingJSONObjectList, JSONObject incomingJSON,
			List<String> arrayKeyNames, int entryReplicationFactor, String[] keysToCopy) {
		for (int i = 0; i < entryReplicationFactor; i++) {
			JSONObject outgoingJSONObject = new JSONObject(incomingJSON, keysToCopy);

			for (String key : arrayKeyNames) {
				outgoingJSONObject.put(key, incomingJSON.getJSONArray(key).opt(i));
			}

			outgoingJSONObjectList.add(outgoingJSONObject);
		}

	}

}
