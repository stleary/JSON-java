package org.json.junit.data;

/**
 * test class for verifying if recursively defined bean can be correctly identified
 * @author Zetmas
 *
 */
public class RecursiveBean {
    private String name;
    private Object reference;
    public String getName() { return name; }
    public Object getRef() {return reference;}
    public void setRef(Object refObj) {reference = refObj;}

    public RecursiveBean(String name) {
        this.name = name;
        reference = null;
    }
    public RecursiveBean(String name, Object refObj) {
        this.name = name;
        reference = refObj;
    }
}