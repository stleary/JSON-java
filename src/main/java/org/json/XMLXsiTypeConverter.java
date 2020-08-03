package org.json;

public interface XMLXsiTypeConverter<T> {
    T convert(String value);
}
