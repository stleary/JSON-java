package org.json.junit.data;

import java.math.BigInteger;

public class CustomClassA {
  public BigInteger largeInt;

  public CustomClassA() {}
  public CustomClassA(BigInteger largeInt) {
    this.largeInt = largeInt;
  }

  @Override
  public boolean equals(Object o) {
    CustomClassA classA = (CustomClassA) o;
    return this.largeInt.equals(classA.largeInt);
  }
}

