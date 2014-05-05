package org.json.zip;

import java.util.HashMap;

import org.json.Kim;

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
 * @version 2013-05-03
 */
class Keep implements None, PostMortem {
    private int capacity;
    protected int length;
    private Object[] list;
    private HashMap<Object, Integer> map;
    private int power;
    private long[] ticks;

    public Keep(int bits) {
        this.capacity = 1 << bits;
        this.length = 0;
        this.power = 0;
        this.ticks = new long[this.capacity];
        this.list = new Object[this.capacity];
        this.map = new HashMap<Object, Integer>(this.capacity);
   }

    /**
     * When an item ages, its use count is reduced by at least half.
     *
     * @param ticks
     *            The current use count of an item.
     * @return The new use count for that item.
     */
    public static long age(long ticks) {
        return ticks >= 32 ? 16 : ticks / 2;
    }

    /**
     * Return the number of bits required to contain an integer based on the
     * current length of the keep. As the keep fills up, the number of bits
     * required to identify one of its items goes up.
     */
    public int bitsize() {
        while (1 << this.power < this.length) {
            this.power += 1;
        }
        return this.power;
    }

    /**
     * Increase the usage count on an integer value.
     */
    public void tick(int integer) {
        this.ticks[integer] += 1;
    }

    /**
     * Compact the keep. A keep may contain at most this.capacity elements.
     * The keep contents can be reduced by deleting all elements with low use
     * counts, and by reducing the use counts of the survivors.
     */
    private void compact() {
        int from = 0;
        int to = 0;
        while (from < this.capacity) {
            Object key = this.list[from];
            long usage = age(this.ticks[from]);
            if (usage > 0) {
                this.ticks[to] = usage;
                this.list[to] = key;
                this.map.put(key, to);
                to += 1;
            } else {
                this.map.remove(key);
            }
            from += 1;
        }
        if (to < this.capacity) {
            this.length = to;
        } else {
            this.map.clear();
            this.length = 0;
        }
        this.power = 0;
    }

    /**
     * Find the integer value associated with this key, or nothing if this key
     * is not in the keep.
     *
     * @param key
     *            An object.
     * @return An integer
     */
    public int find(Object key) {
        Object o = this.map.get(key);
        return o instanceof Integer ? ((Integer) o).intValue() : none;
    }

    public boolean postMortem(PostMortem pm) {
        Keep that = (Keep) pm;
        if (this.length != that.length) {
            JSONzip.log(this.length + " <> " + that.length);
            return false;
        }
        for (int i = 0; i < this.length; i += 1) {
            boolean b;
            if (this.list[i] instanceof Kim) {
                b = this.list[i].equals(that.list[i]);
            } else {
                Object o = this.list[i];
                Object q = that.list[i];
                if (o instanceof Number) {
                    o = o.toString();
                }
                if (q instanceof Number) {
                    q = q.toString();
                }
                b = o.equals(q);
            }
            if (!b) {
                JSONzip.log("\n[" + i + "]\n " + this.list[i] + "\n "
                        + that.list[i] + "\n " + this.ticks[i] + "\n "
                        + that.ticks[i]);
                return false;
            }
        }
        return true;
    }

    /**
     * Register a value in the keep. Compact the keep if it is full. The next
     * time this value is encountered, its integer can be sent instead.
     * @param value A value.
     */
    public void register(Object value) {
        if (JSONzip.probe) {
            int integer = find(value);
            if (integer >= 0) {
                JSONzip.log("\nDuplicate key " + value);
            }
        }
        if (this.length >= this.capacity) {
            compact();
        }
        this.list[this.length] = value;
        this.map.put(value, this.length);
        this.ticks[this.length] = 1;
        if (JSONzip.probe) {
            JSONzip.log("<" + this.length + " " + value + "> ");
        }
        this.length += 1;
    }

    /**
     * Return the value associated with the integer.
     * @param integer The number of an item in the keep.
     * @return The value.
     */
    public Object value(int integer) {
        return this.list[integer];
    }
}
