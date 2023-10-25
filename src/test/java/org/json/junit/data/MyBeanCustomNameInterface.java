package org.json.junit.data;

import org.rookout.json.JSONPropertyIgnore;
import org.rookout.json.JSONPropertyName;

public interface MyBeanCustomNameInterface {
    @JSONPropertyName("InterfaceField")
    float getSomeFloat();
    @JSONPropertyIgnore
    int getIgnoredInt();
}