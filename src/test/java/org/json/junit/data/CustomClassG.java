package org.json.junit.data;

import java.util.Map;

public class CustomClassG {
  public Map<String, String> dataList;

  public CustomClassG () {}
  public CustomClassG (Map<String, String> dataList) {
    this.dataList = dataList;
  }

  @Override
  public boolean equals(Object object) {
    CustomClassG classG = (CustomClassG) object;
    return this.dataList.equals(classG.dataList);
  }
}
