package org.json.junit.data;

public class CustomClassB {
  public int number;
  public CustomClassC classC;

  public CustomClassB() {}
  public CustomClassB(int number, CustomClassC classC) {
    this.number = number;
    this.classC = classC;
  }

  @Override
  public boolean equals(Object o) {
    CustomClassB classB = (CustomClassB) o;
    return this.number == classB.number
    && this.classC.equals(classB.classC);
  }
}

