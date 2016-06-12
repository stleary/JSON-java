package org.json;
/**
 * The <code>JSONString</code> interface allows a <code>toJSON()</code>
 * method. 
 */
public interface JSONSerializable {
    /**
     * The <code>toJSON</code> method allows a class to produce its own JSON
     * object creation.
     *
     * @return A strictly syntactically correct JSON text.
     */
    public Object toJSON();
}
