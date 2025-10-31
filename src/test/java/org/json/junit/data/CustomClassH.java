package org.json.junit.data;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class CustomClassH {
  public Map<String, List<Integer>> integerMap;

  public CustomClassH() {}
  public CustomClassH(Map<String, List<Integer>> integerMap) {
    this.integerMap = integerMap;
  }

  @Override
  public boolean equals(Object object) {
    CustomClassH classH = (CustomClassH) object;
    return this.integerMap.size() == classH.integerMap.size()
    && this.integerMap.keySet().equals(classH.integerMap.keySet())
    && new ArrayList<>(this.integerMap.values()).equals(new ArrayList<>(classH.integerMap.values()));
  }
}
