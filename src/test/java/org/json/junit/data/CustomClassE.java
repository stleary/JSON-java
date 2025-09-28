package org.json.junit.data;

import java.util.List;

public class CustomClassE {
  public List<CustomClassC> listClassC;

  public CustomClassE() {}
  public CustomClassE(List<CustomClassC> listClassC) {
    this.listClassC = listClassC;
  }

  @Override
  public boolean equals(Object o) {
    CustomClassE classE = (CustomClassE) o;
    return this.listClassC.equals(classE.listClassC);
  }
}
