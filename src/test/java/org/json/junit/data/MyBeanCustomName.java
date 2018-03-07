package org.json.junit.data;

import org.json.JSONPropertyName;

/**
 * Test bean for the {@link JSONPropertyName} annotation.
 */
public class MyBeanCustomName implements MyBeanCustomNameInterface {
    public int getSomeInt() { return 42; }
    @JSONPropertyName("")
    public long getSomeLong() { return 42L; }
    @JSONPropertyName("myStringField")
    public String getSomeString() { return "someStringValue"; }
    @JSONPropertyName("Some Weird NAme that Normally Wouldn't be possible!")
    public double getMyDouble() { return 0.0d; }
    @Override
    public float getSomeFloat() { return 2.0f; }
    @Override
    public int getIgnoredInt() { return 40; }
}
