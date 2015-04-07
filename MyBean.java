package org.json.junit;

public class MyBean {
    public int intKey;
    public double doubleKey;
    public String stringKey;
    public String complexStringKey;
    public boolean trueKey;
    public boolean falseKey;

    public MyBean() {
        intKey = 42;
        doubleKey = -23.45e7;
        stringKey = "hello world!";
        complexStringKey = "h\be\tllo w\u1234orld!";
        trueKey = true;
        falseKey = false;
    }
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
}
