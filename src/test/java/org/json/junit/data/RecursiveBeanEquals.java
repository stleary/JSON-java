package org.json.junit.data;

/** test class for verifying if recursively defined bean can be correctly identified */
public class RecursiveBeanEquals {
  private final String name;
  private Object reference;

  public RecursiveBeanEquals(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Object getRef() {
    return reference;
  }

  public void setRef(Object refObj) {
    reference = refObj;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof RecursiveBeanEquals && name.equals(((RecursiveBeanEquals) other).name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
