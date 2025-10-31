package org.json.junit.data;

import java.util.List;

public class CustomClassF {
  public List<List<String>> listOfString;

  public CustomClassF() {}
  public CustomClassF(List<List<String>> listOfString) {
    this.listOfString = listOfString;
  }

  @Override
  public boolean equals(Object o) {
    CustomClassF classF = (CustomClassF) o;
    return this.listOfString.equals(classF.listOfString);
  }
}

