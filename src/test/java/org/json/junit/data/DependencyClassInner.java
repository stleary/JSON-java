package org.json.junit.data;

public class DependencyClassInner {
    private final DependencyClassOuter dependencyClassOuter;

    DependencyClassInner(DependencyClassOuter dependencyClassOuter) {
        this.dependencyClassOuter = dependencyClassOuter;
    }

    public DependencyClassOuter getDependencyClassOuter() {
        return dependencyClassOuter;
    }
}
