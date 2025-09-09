package org.json;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

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
  private static final Map<Class<?>,  TypeConverter<?>> classMapping = new HashMap<Class<?>,  TypeConverter<?>>();

  /**
   * A mapping from collection interface types to suppliers that produce
   * instances of concrete collection implementations.
   *
   */
  private static final Map<Class<?>, InstanceCreator<?>> collectionMapping = new HashMap<Class<?>, InstanceCreator<?>>();
  
   // Static initializer block to populate default mappings
   static {
    classMapping.put(int.class, new TypeConverter<Integer>() {
      public Integer convert(Object input) {
        return ((Number) input).intValue();
      }
    });
    classMapping.put(Integer.class, new TypeConverter<Integer>() {
      public Integer convert(Object input) {
        return ((Number) input).intValue();
      }
    });
    classMapping.put(double.class, new TypeConverter<Double>() {
      public Double convert(Object input) {
        return ((Number) input).doubleValue();
      }
    });
    classMapping.put(Double.class, new TypeConverter<Double>() {
      public Double convert(Object input) {
        return ((Number) input).doubleValue();
      }
    });
    classMapping.put(float.class, new TypeConverter<Float>() {
      public Float convert(Object input) {
        return ((Number) input).floatValue();
      }
    });
    classMapping.put(Float.class, new TypeConverter<Float>() {
      public Float convert(Object input) {
        return ((Number) input).floatValue();
      }
    });
    classMapping.put(long.class, new TypeConverter<Long>() {
      public Long convert(Object input) {
        return ((Number) input).longValue();
      }
    });
    classMapping.put(Long.class, new TypeConverter<Long>() {
      public Long convert(Object input) {
        return ((Number) input).longValue();
      }
    });
    classMapping.put(boolean.class, new TypeConverter<Boolean>() {
      public Boolean convert(Object input) {
        return (Boolean) input;
      }
    });
    classMapping.put(Boolean.class, new TypeConverter<Boolean>() {
      public Boolean convert(Object input) {
        return (Boolean) input;
      }
    });
    classMapping.put(String.class, new TypeConverter<String>() {
      public String convert(Object input) {
        return (String) input;
      }
    });

    collectionMapping.put(List.class, new InstanceCreator<List>() {
      public List create() {
        return new ArrayList();
      }
    });
    collectionMapping.put(Set.class, new InstanceCreator<Set>() {
      public Set create() {
        return new HashSet();
      }
    });
    collectionMapping.put(Map.class, new InstanceCreator<Map>() {
      public Map create() {
        return new HashMap(); 
      }
    });
   }

  /**
   * Returns the current class-to-function mapping used for type conversions.
   *
   * @return a map of classes to functions that convert an {@code Object} to that class
   */
   public Map<Class<?>, TypeConverter<?>> getClassMapping() {
     return this.classMapping;
   }

  /**
   * Returns the current collection-to-supplier mapping used for instantiating collections.
   *
   * @return a map of collection interface types to suppliers of concrete implementations
   */
   public Map<Class<?>, InstanceCreator<?>> getCollectionMapping() {
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
   public void setClassMapping(Class<?> clazz, TypeConverter<?> function) {
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
   public void setCollectionMapping(Class<?> clazz, InstanceCreator<?> function) {
     collectionMapping.put(clazz, function);
   }
}
