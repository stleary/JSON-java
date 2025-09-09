package org.json;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The {@code JSONBuilder} class provides a configurable mechanism for
 * defining how different Java types are handled during JSON serialization
 * or deserialization.
 *
 * <p>This class maintains two internal mappings:
 * <ul>
 *   <li>A {@code classMapping} which maps Java classes to functions that convert
 *   an input {@code Object} into an appropriate instance of that class.</li>
 *   <li>A {@code collectionMapping} which maps collection interfaces (like {@code List}, {@code Set}, {@code Map})
 *   to supplier functions that create new instances of concrete implementations (e.g., {@code ArrayList} for {@code List}).</li>
 * </ul>
 *
 * <p>The mappings are initialized with default values for common primitive wrapper types
 * and collection interfaces, but they can be modified at runtime using setter methods.
 *
 * <p>This class is useful in custom JSON serialization/deserialization frameworks where
 * type transformation and collection instantiation logic needs to be flexible and extensible.
 */
public class JSONBuilder {

  /**
   * A mapping from Java classes to functions that convert a generic {@code Object}
   * into an instance of the target class.
   *
   * <p>Examples of default mappings:
   * <ul>
   *   <li>{@code int.class} or {@code Integer.class} -> Converts a {@code Number} to {@code int}</li>
   *   <li>{@code boolean.class} or {@code Boolean.class} -> Identity function</li>
   *   <li>{@code String.class} -> Identity function</li>
   * </ul>
   */
  private static final Map<Class<?>, Function<Object, ?>> classMapping = new HashMap<>();

  /**
   * A mapping from collection interface types to suppliers that produce
   * instances of concrete collection implementations.
   *
   * <p>Examples of default mappings:
   * <ul>
   *   <li>{@code List.class} -> {@code ArrayList::new}</li>
   *   <li>{@code Set.class} -> {@code HashSet::new}</li>
   *   <li>{@code Map.class} -> {@code HashMap::new}</li>
   * </ul>
   */
  private static final Map<Class<?>, Supplier<?>> collectionMapping = new HashMap<>();
  
   // Static initializer block to populate default mappings
   static {
    classMapping.put(int.class, s -> ((Number) s).intValue());
    classMapping.put(Integer.class, s -> ((Number) s).intValue());
    classMapping.put(double.class, s -> ((Number) s).doubleValue());
    classMapping.put(Double.class, s -> ((Number) s).doubleValue());
    classMapping.put(float.class, s -> ((Number) s).floatValue());
    classMapping.put(Float.class, s -> ((Number) s).floatValue());
    classMapping.put(long.class, s -> ((Number) s).longValue());
    classMapping.put(Long.class, s -> ((Number) s).longValue());
    classMapping.put(boolean.class, s -> s);
    classMapping.put(Boolean.class, s -> s);
    classMapping.put(String.class, s -> s);

    collectionMapping.put(List.class, ArrayList::new);
    collectionMapping.put(Set.class, HashSet::new);
    collectionMapping.put(Map.class, HashMap::new);
   }

  /**
   * Returns the current class-to-function mapping used for type conversions.
   *
   * @return a map of classes to functions that convert an {@code Object} to that class
   */
   public Map<Class<?>, Function<Object, ?>> getClassMapping() {
     return this.classMapping;
   }

  /**
   * Returns the current collection-to-supplier mapping used for instantiating collections.
   *
   * @return a map of collection interface types to suppliers of concrete implementations
   */
   public Map<Class<?>, Supplier<?>> getCollectionMapping() {
     return this.collectionMapping;
   }

  /**
   * Adds or updates a type conversion function for a given class.
   *
   * <p>This allows users to customize how objects are converted into specific types
   * during processing (e.g., JSON deserialization).
   *
   * @param clazz the target class for which the conversion function is to be set
   * @param function a function that takes an {@code Object} and returns an instance of {@code clazz}
   */
   public void setClassMapping(Class<?> clazz, Function<Object, ?> function) {
     classMapping.put(clazz, function);
   }

  /**
   * Adds or updates a supplier function for instantiating a collection type.
   *
   * <p>This allows customization of which concrete implementation is used for
   * interface types like {@code List}, {@code Set}, or {@code Map}.
   *
   * @param clazz the collection interface class (e.g., {@code List.class})
   * @param function a supplier that creates a new instance of a concrete implementation
   */
   public void setCollectionMapping(Class<?> clazz, Supplier<?> function) {
     collectionMapping.put(clazz, function);
   }
}
