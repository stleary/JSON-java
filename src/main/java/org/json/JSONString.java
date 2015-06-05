package org.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The <code>JSONString</code> interface allows a <code>toJSONString()</code>
 * method so that a class can change the behavior of
 * <code>JSONObject.toString()</code>, <code>JSONArray.toString()</code>,
 * and <code>JSONWriter.value(</code>Object<code>)</code>. The
 * <code>toJSONString</code> method will be used instead of the default behavior
 * of using the Object's <code>toString()</code> method and quoting the result.
 */
public interface JSONString {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON
     * serialization.<br>
     * 
     * The default implementation will look for fields in the class and will check
     * if they extend <code>JSONString</code>. If they do the result of
     * <code>&lt;field&gt.toJSONString()</code> is taken as value. Otherwise it will
     * just use <code>&lt;field&gt.toString()</code>.
     *
     * @return A strictly syntactically correct JSON text or null if an exception
     * occurred
     * @throws JSONException if a field's type or value could not be retrieved 
     */
    public default String toJSONString() throws JSONException {
    	String ret = "{\n";
		for(Field f: this.getClass().getFields()){
			Object o = null;
			try {
				if(Modifier.isStatic(f.getModifiers())){
					o = f.get(null);
				} else {
					o = f.get(this);
				}
			} catch( IllegalAccessException | IllegalArgumentException e){
				throw new JSONException("could not create a JSON representation"
								+ "for " + this.getClass().getName(), e);
			}
			ret += "\"" + f.getName().replace("\"", "\\\"") + "\":";
			if(f.getType().isAssignableFrom(JSONString.class)){
				ret += ((JSONString) o).toJSONString();
			} else {
				ret += "\"" + o.toString().replace("\"", "\\\"") + "\"";
			}
			ret += "\n";
		}
		ret += "}\n";
		return ret;
	}
}
