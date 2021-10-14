package org.json.junit.data;

public class DependencyClassOuter {

    public DependencyClassInner getDependencyClassInner() {
        return new DependencyClassInner(this);
    }
}
