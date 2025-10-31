package org.json.junit.data;

import org.json.JSONObject;

public class CustomClassC {
  public String stringName;
  public Long longNumber;

  public CustomClassC() {}
  public CustomClassC(String stringName, Long longNumber) {
    this.stringName = stringName;
    this.longNumber = longNumber;
  }

  public JSONObject toJSON() {
    JSONObject object = new JSONObject();
    object.put("stringName", this.stringName);
    object.put("longNumber", this.longNumber);
    return object;
  }

  @Override
  public boolean equals(Object o) {
    CustomClassC classC = (CustomClassC) o;
    return this.stringName.equals(classC.stringName)
    && this.longNumber.equals(classC.longNumber);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(stringName, longNumber);
  }
}

