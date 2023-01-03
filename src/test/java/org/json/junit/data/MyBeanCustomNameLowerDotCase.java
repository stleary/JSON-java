package org.json.junit.data;

import org.json.JSONNamingStrategy;
import org.json.JSONPropertyName;
import org.json.PropertyNamingStrategies;

/**
 * Test bean for the {@link JSONPropertyName} annotation.
 */
@JSONNamingStrategy(PropertyNamingStrategies.Strategies.LOWER_DOT_CASE)
public class MyBeanCustomNameLowerDotCase implements MyBeanCustomNameInterfaceNamingStrategy {
    public int getSomeInt() { return 42; }
    public long getSomeLong() { return 42L; }
    public String getSomeString() { return "someStringValue"; }
    public double getMyDouble() { return 0.0d; }
    @Override
    public float getSomeFloat() { return 2.0f; }
    @Override
    public int getIgnoredInt() { return 40; }
}
