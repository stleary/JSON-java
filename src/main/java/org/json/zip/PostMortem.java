package org.json.zip;

/*
 Copyright (c) 2013 JSON.org

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
 * The PostMortem interface allows for testing the internal state of JSONzip
 * processors. Testing that JSONzip can compress an object and reproduce a
 * corresponding object is not sufficient. Complete testing requires that the
 * same internal data structures were constructed on both ends. If those
 * structures are not exactly equivalent, then it is likely that the
 * implementations are not correct, even if conventional tests are passed.
 *
 * PostMortem allows for testing of deep structures without breaking
 * encapsulation.
 */
public interface PostMortem {
    /**
     * Determine if two objects are equivalent.
     *
     * @param pm
     *            Another object of the same type.
     * @return true if they match.
     */
    public boolean postMortem(PostMortem pm);
}
