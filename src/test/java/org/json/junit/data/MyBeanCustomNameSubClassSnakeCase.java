/**
 * 
 */
package org.json.junit.data;

import org.json.JSONNamingStrategy;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;
import org.json.PropertyNamingStrategies;

/**
 * Test bean to verify that the {@link JSONPropertyName} annotation
 * is inherited.
 */
@JSONNamingStrategy(PropertyNamingStrategies.Strategies.SNAKE_CASE)
public class MyBeanCustomNameSubClassSnakeCase extends MyBeanCustomNameLowerDotCase {
    @Override
    public int getIgnoredInt() { return 42*42; }
    @Override
    public int getSomeInt() { return 43; }
    @Override
    public String getSomeString() { return "subClassString"; }
    @Override
    public double getMyDouble() { return 1.0d; }
    @Override
    public float getSomeFloat() { return 3.0f; }
    @JSONPropertyIgnore
    public boolean getShouldNotBeJSON() { return true; }
}
