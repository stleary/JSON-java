package org.json.junit.data;

/**
 * this is simply a class that contains some enum instances
 */
public class MyEnumClass {
    private MyEnum myEnum;
    private MyEnumField myEnumField;

    public MyEnum getMyEnum() {
        return this.myEnum;
    }
    public void setMyEnum(MyEnum myEnum) {
        this.myEnum = myEnum;
    }
    public MyEnumField getMyEnumField() {
        return this.myEnumField;
    }
    public void setMyEnumField(MyEnumField myEnumField) {
        this.myEnumField = myEnumField;
    }
}
