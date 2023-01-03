package org.json.junit.data;

import org.json.JSONNamingStrategy;
import org.json.PropertyNamingStrategies;

@JSONNamingStrategy(PropertyNamingStrategies.Strategies.SNAKE_CASE)
public interface MyBeanCustomNameInterfaceNamingStrategy {
    float getSomeFloat();
    int getIgnoredInt();
}