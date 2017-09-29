package org.json;

public interface JSONable {
	default public JSONObject toJSONObject() {
		return new JSONObject(JSONObject.createJSONObject(this));
	}
}