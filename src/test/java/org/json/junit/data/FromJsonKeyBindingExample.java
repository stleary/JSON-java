package org.json.junit.data;

import org.json.JSONPropertyIgnore;

/**
 * Bean used to pin down how {@code JSONObject#fromJson(Class)} binds JSON keys
 * to fields: {@code privateString} and {@code privateBoolean} are private,
 * {@code transientString} is transient, and {@code ignoredGetterBoolean} is
 * hidden behind a getter annotated with {@code @JSONPropertyIgnore} (which only
 * suppresses the bean to JSONObject direction).
 */
public class FromJsonKeyBindingExample {
  public String displayName;
  private boolean privateBoolean;
  private String privateString;
  private transient String transientString;
  private boolean ignoredGetterBoolean;

  public FromJsonKeyBindingExample() {
    // Required for JSONObject#fromJson(Class<T>) tests.
  }

  public boolean isPrivateBoolean() {
    return privateBoolean;
  }

  public String getPrivateString() {
    return privateString;
  }

  public String getTransientString() {
    return transientString;
  }

  @JSONPropertyIgnore
  public boolean isIgnoredGetterBoolean() {
    return ignoredGetterBoolean;
  }
}
