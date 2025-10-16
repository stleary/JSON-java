package org.json;

/**
 * Interface defining a converter that converts an input {@code Object} 
 * into an instance of a specific type {@code T}.
 *
 * @param <T> the target type to convert to
 */
interface TypeConverter<T> {

  /**
   * Converts the given input object to an instance of type {@code T}.
   *
   * @param input the object to convert
   * @return the converted instance of type {@code T}
   */
  T convert(Object input);
}
