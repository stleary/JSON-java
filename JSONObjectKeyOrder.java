package org.json;

import java.util.ArrayList;
import java.util.List;

/**
 * sometimes we need key order in input,hashmap`s keyset is chaos.
 * @author wangq
 *
 */
public class JSONObjectKeyOrder extends JSONObject {
	// store the name in order
	private List<String> names;
	
	public JSONObjectKeyOrder(String source) throws JSONException {

		this(new JSONTokenerKeyOrder(source));
	}

	public JSONObjectKeyOrder(JSONTokenerKeyOrder jsonTokenerKeyOrder) {
		super(jsonTokenerKeyOrder);

	}

	public JSONObjectKeyOrder putOnce(String key, Object value) throws JSONException {
		super.putOnce(key, value);
		if (names == null) {
			names = new ArrayList<>();
		}
		names.add(key);
		return this;
	}
	/**
	 * null is ugly,and evil
	 * @param jo
	 * @return
	 */
	public static String[] getNames(JSONObjectKeyOrder jo) {
		if (jo.names == null) {
			return new String[0];
		}
		return jo.names.toArray(new String[0]);
	}
	/**
	 * i do`t like the opt** methods,it`s hard to get type,such the everything can be cast to String
	 * 
	 * @param key
	 * @return
	 */
	public boolean isInt(String key) {
		try {
			Integer.parseInt(optString(key));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
