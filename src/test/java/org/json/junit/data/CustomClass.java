package org.json.junit.data;

public class CustomClass {
  public int number;
  public String name;
  public Long longNumber;

  public CustomClass() {}
  public CustomClass (int number, String name, Long longNumber) {
    this.number = number;
    this.name = name;
    this.longNumber = longNumber;
  }
  @Override
  public boolean equals(Object o) {
    CustomClass customClass = (CustomClass) o;

    return (this.number == customClass.number
    && this.name.equals(customClass.name)
    && this.longNumber.equals(customClass.longNumber));
  }
}

