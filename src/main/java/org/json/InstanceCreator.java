package org.json;

/**
 * Interface defining a creator that produces new instances of type {@code T}.
 *
 * @param <T> the type of instances created
 */
interface InstanceCreator<T> {

  /**
   * Creates a new instance of type {@code T}.
   *
   * @return a new instance of {@code T}
   */
  T create();
}
