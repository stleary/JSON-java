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
 * The <code>JSONString</code> interface allows a <code>toJSONString()</code>
 * method so that a class can change the behavior of
 * <code>JSONObject.toString()</code>, <code>JSONArray.toString()</code>,
 * and <code>JSONWriter.value(</code>Object<code>)</code>. The
 * <code>toJSONString</code> method will be used instead of the default behavior
 * of using the Object's <code>toString()</code> method and quoting the result.
 * <p>
 * If <code>toJSONString()</code> returns <code>null</code>, the class's
 * <code>toString()</code> value will be quoted and used.</p>
 * 
 * @author json.org
 * @version 2016-07-28
 */
public interface JSONString {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON
     * serialization. If <code>null</code> is returned, the class's
     * <code>toString</code> method will be called, and the result will be
     * quoted and used.
     * <p>
     * If a runtime exception is thrown, this will be caught and propagated
     * as a <code>JSONException</code>.</p>
     *
     * @return A strictly syntactically correct JSON text, or <code>null</code>
     * to indicate the <code>toString</code> value should be used instead
     */
    public String toJSONString();
}
