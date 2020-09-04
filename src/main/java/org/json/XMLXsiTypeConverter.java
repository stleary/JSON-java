package org.json;
/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**
 * Type conversion configuration interface to be used with xsi:type attributes.
 * <pre>
 * <b>XML Sample</b>
 * {@code
 *      <root>
 *          <asString xsi:type="string">12345</asString>
 *          <asInt xsi:type="integer">54321</asInt>
 *      </root>
 * }
 * <b>JSON Output</b>
 * {@code
 *     {
 *         "root" : {
 *             "asString" : "12345",
 *             "asInt": 54321
 *         }
 *     }
 * }
 *
 * <b>Usage</b>
 * {@code
 *      Map<String, XMLXsiTypeConverter<?>> xsiTypeMap = new HashMap<String, XMLXsiTypeConverter<?>>();
 *      xsiTypeMap.put("string", new XMLXsiTypeConverter<String>() {
 *          &#64;Override public String convert(final String value) {
 *              return value;
 *          }
 *      });
 *      xsiTypeMap.put("integer", new XMLXsiTypeConverter<Integer>() {
 *          &#64;Override public Integer convert(final String value) {
 *              return Integer.valueOf(value);
 *          }
 *      });
 * }
 * </pre>
 * @author kumar529
 * @param <T> return type of convert method
 */
public interface XMLXsiTypeConverter<T> {
    T convert(String value);
}
