package org.json;

import java.util.function.Consumer;

/**
 * Describes the "with" pattern of building a configuration.
 */
public abstract class AbstractConfiguration {

    /**
     * This clone should always ensure a deep copy of the object.
     *
     * @return a new deep copied object
     */
    @Override
    protected abstract Object clone();

    /**
     * Automates the "with" pattern by create a clone and apply the setter {@link Consumer} to the new instance.
     *
     * @param setter the setter for applying the "with" to the desiring property
     * @return the new instance with the property being set
     * @param <T> the configuration class
     */
    protected <T extends AbstractConfiguration> T with(Consumer<T> setter) {
        T instance = (T)clone();
        setter.accept(instance);
        return instance;
    }
}
