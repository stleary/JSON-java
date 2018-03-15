package org.json.junit.data;

import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

public interface MyBeanCustomNameInterface {
    @JSONPropertyName("InterfaceField")
    float getSomeFloat();
    @JSONPropertyIgnore
    int getIgnoredInt();
}