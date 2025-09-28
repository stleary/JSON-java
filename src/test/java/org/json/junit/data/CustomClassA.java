package org.json.junit.data;

public class CustomClassA {
  public java.time.LocalDateTime localDate;

  public CustomClassA() {}
  public CustomClassA(java.time.LocalDateTime localDate) {
    this.localDate = localDate;
  }

  @Override
  public boolean equals(Object o) {
    CustomClassA classA = (CustomClassA) o;
    return this.localDate.equals(classA.localDate);
  }
}

