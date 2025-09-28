package org.json.junit.data;

import java.util.List;

public class CustomClassD {
  public List<String> stringList;

  public CustomClassD() {}
  public CustomClassD(List<String> stringList) {
    this.stringList = stringList;
  }

  @Override
  public boolean equals(Object o) {
    CustomClassD classD = (CustomClassD) o;
    return this.stringList.equals(classD.stringList);
  }
}

