package org.json.junit;

import java.io.*;

// bean must be serializable
public class MyBean implements Serializable {
    // bean properties should be private
    private static final long serialVersionUID = 1L;
    private int intKey;
    private double doubleKey;
    private String stringKey;
    private String complexStringKey;
    private boolean trueKey;
    private boolean falseKey;

    /**
     * Throw in a few public properties in order to test building
     * from an Object.
     */
     public String publicStr;
     public int publicInt;

     // bean needs a default ctor
    public MyBean() {
        intKey = 42;
        doubleKey = -23.45e7;
        stringKey = "hello world!";
        complexStringKey = "h\be\tllo w\u1234orld!";
        trueKey = true;
        falseKey = false;

        publicStr = "abc";
        publicInt = 42;
    }

    // need getters, but don't need setters
    public int getIntKey() {
        return intKey;
    }
    public double getDoubleKey() {
        return doubleKey;
    }
    public String getStringKey() {
        return stringKey;
    }
    public String getComplexStringKey() {
        return complexStringKey;
    }
    public boolean isTrueKey() {
        return trueKey;
    }
    public boolean isFalseKey() {
        return falseKey;
    }

    /**
     * Just a random invalid JSON getter, not even backed up by a property 
     */
    public StringReader getStringReaderKey() {
        return (new StringReader("") {
            /**
             * TODO: Need to understand why returning a string
             * turns this into an empty JSONObject, 
             * but not overriding turns this into a string.
             */
            @Override
            public String toString(){
                return "Whatever";
            } 
        });
    }
    // bean hashcode is recommended
    public int hashCode() {
        return super.hashCode();
    }
    // bean equals is recommended
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
