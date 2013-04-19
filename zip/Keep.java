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
 * A keep is a data structure that associates strings (or substrings) with
 * numbers. This allows the sending of small integers instead of strings.
 *
 * @author JSON.org
 * @version 2013-04-18
 */
abstract class Keep implements None, PostMortem {
    protected int capacity;
    protected int length;
    protected int power;
    protected long[] uses;

    public Keep(int bits) {
        this.capacity = JSONzip.twos[bits];
        this.length = 0;
        this.power = 0;
        this.uses = new long[this.capacity];
    }

    /**
     * When an item ages, its use count is reduced by at least half.
     *
     * @param use
     *            The current use count of an item.
     * @return The new use count for that item.
     */
    public static long age(long use) {
        return use >= 32 ? 16 : use / 2;
    }

    /**
     * Return the number of bits required to contain an integer based on the
     * current length of the keep. As the keep fills up, the number of bits
     * required to identify one of its items goes up.
     */
    public int bitsize() {
        while (JSONzip.twos[this.power] < this.length) {
            this.power += 1;
        }
        return this.power;
    }

    /**
     * Increase the usage count on an integer value.
     */
    public void tick(int integer) {
        this.uses[integer] += 1;
    }

    /**
     * Get the value associated with an integer.
     * @param integer The number of an item in the keep.
     * @return The value.
     */
    abstract public Object value(int integer);
}
