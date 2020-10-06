/**
 * 
 */
package org.json.junit.data;

import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

/**
 * Test bean to verify that the {@link org.json.JSONPropertyName} annotation
 * is inherited.
 */
public class MyBeanCustomNameSubClass extends MyBeanCustomName {
    @Override
    @JSONPropertyName("forcedInt")
    public int getIgnoredInt() { return 42*42; }
    @Override
    @JSONPropertyName("newIntFieldName")
    public int getSomeInt() { return 43; }
    @Override
    public String getSomeString() { return "subClassString"; }
    @Override
    @JSONPropertyName("AMoreNormalName")
    public double getMyDouble() { return 1.0d; }
    @Override
    public float getSomeFloat() { return 3.0f; }
    @JSONPropertyIgnore
    @JSONPropertyName("ShouldBeIgnored")
    public boolean getShouldNotBeJSON() { return true; }
    @JSONPropertyName("Getable")
    public boolean getable() { return true; }
}
