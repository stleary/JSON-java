package org.json;

public interface JSONable {
	default public JSONObject toJSONObject() {
		return JSONObject.createJSONObject(this);
	}
}