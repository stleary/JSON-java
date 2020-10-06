package org.json.junit.data;

/**
 * An enum that contains getters and some internal fields
 */
@SuppressWarnings("boxing")
public enum MyEnumField {
    VAL1(1, "val 1"),
    VAL2(2, "val 2"),
    VAL3(3, "val 3");

    private String value;
    private Integer intVal;
    private MyEnumField(Integer intVal, String value) {
        this.value = value;
        this.intVal = intVal;
    }
    public String getValue() {
        return this.value;
    }
    public Integer getIntVal() {
        return this.intVal;
    }
    @Override
    public String toString(){
        return this.value;
    }
}
