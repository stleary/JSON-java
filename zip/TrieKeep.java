package org.json.zip;

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
 * A TrieKeep is a Keep that implements a Trie.
 */
class TrieKeep extends Keep {

    /**
     * The trie is made of nodes.
     */
    class Node implements PostMortem {
        private int integer;
        private Node[] next;

        /**
         * Each non-leaf node contains links to up to 256 next nodes. Each node
         * has an integer value.
         */
        public Node() {
            this.integer = none;
            this.next = null;
        }

        /**
         * Get one of a node's 256 links. If it is a leaf node, it returns
         * null.
         *
         * @param cell
         *            A integer between 0 and 255.
         * @return
         */
        public Node get(int cell) {
            return this.next == null ? null : this.next[cell];
        }

        /**
         * Get one of a node's 256 links. If it is a leap node, it returns
         * null. The argument is treated as an unsigned integer.
         *
         * @param cell
         *            A byte.
         * @return
         */
        public Node get(byte cell) {
            return get(((int) cell) & 0xFF);
        }

        /**
         * Compare two nodes. Their lengths must be equal. Their links must
         * also compare.
         */
        public boolean postMortem(PostMortem pm) {
            Node that = (Node) pm;
            if (that == null) {
                JSONzip.log("\nMisalign");
                return false;
            }
            if (this.integer != that.integer) {
                JSONzip.log("\nInteger " + this.integer + " <> " +
                        that.integer);
                return false;
            }
            if (this.next == null) {
                if (that.next == null) {
                    return true;
                }
                JSONzip.log("\nNext is null " + this.integer);
                return false;
            }
            for (int i = 0; i < 256; i += 1) {
                Node node = this.next[i];
                if (node != null) {
                    if (!node.postMortem(that.next[i])) {
                        return false;
                    }
                } else if (that.next[i] != null) {
                    JSONzip.log("\nMisalign " + i);
                    return false;
                }
            }
            return true;
        }

        /**
         * Set a node's link to another node.
         *
         * @param cell
         *            An integer between 0 and 255.
         * @param node
         *            The new value for the cell.
         */
        public void set(int cell, Node node) {
            if (this.next == null) {
                this.next = new Node[256];
            }
            if (JSONzip.probe) {
                if (node == null || this.next[cell] != null) {
                    JSONzip.log("\nUnexpected set.\n");
                }
            }
            this.next[cell] = node;
        }

        /**
         * Set a node's link to another node.
         *
         * @param cell
         *            A byte.
         * @param node
         *            The new value for the cell.
         */
        public void set(byte cell, Node node) {
            set(((int) cell) & 0xFF, node);
        }

        /**
         * Get one of a node's 256 links. It will not return null. If there is
         * no link, then a link is manufactured.
         *
         * @param cell
         *            A integer between 0 and 255.
         * @return
         */
        public Node vet(int cell) {
            Node node = get(cell);
            if (node == null) {
                node = new Node();
                set(cell, node);
            }
            return node;
        }

        /**
         * Get one of a node's 256 links. It will not return null. If there is
         * no link, then a link is manufactured.
         *
         * @param cell
         *            A byte.
         * @return
         */
        public Node vet(byte cell) {
            return vet(((int) cell) & 0xFF);
        }
    }

    private int[] froms;
    private int[] thrus;
    private Node root;
    private Kim[] kims;

    /**
     * Create a new Keep of kims.
     *
     * @param bits
     *            The log2 of the capacity of the Keep. For example, if bits is
     *            12, then the keep's capacity will be 4096.
     */
    public TrieKeep(int bits) {
        super(bits);
        this.froms = new int[this.capacity];
        this.thrus = new int[this.capacity];
        this.kims = new Kim[this.capacity];
        this.root = new Node();
    }

    /**
     * Get the kim associated with an integer.
     *
     * @param integer
     * @return
     */
    public Kim kim(int integer) {
        Kim kim = this.kims[integer];
        int from = this.froms[integer];
        int thru = this.thrus[integer];
        if (from != 0 || thru != kim.length) {
            kim = new Kim(kim, from, thru);
            this.froms[integer] = 0;
            this.thrus[integer] = kim.length;
            this.kims[integer] = kim;
        }
        return kim;
    }

    /**
     * Get the length of the Kim associated with an integer. This is sometimes
     * much faster than get(integer).length.
     *
     * @param integer
     * @return
     */
    public int length(int integer) {
        return this.thrus[integer] - this.froms[integer];
    }

    /**
     * Find the integer value associated with this key, or nothing if this key
     * is not in the keep.
     *
     * @param key
     *            An object.
     * @return An integer
     */
    public int match(Kim kim, int from, int thru) {
        Node node = this.root;
        int best = none;
        for (int at = from; at < thru; at += 1) {
            node = node.get(kim.get(at));
            if (node == null) {
                break;
            }
            if (node.integer != none) {
                best = node.integer;
            }
            from += 1;
        }
        return best;
    }

    public boolean postMortem(PostMortem pm) {
        boolean result = true;
        TrieKeep that = (TrieKeep) pm;
        if (this.length != that.length) {
            JSONzip.log("\nLength " + this.length + " <> " + that.length);
            return false;
        }
        if (this.capacity != that.capacity) {
            JSONzip.log("\nCapacity " + this.capacity + " <> " +
                    that.capacity);
            return false;
        }
        for (int i = 0; i < this.length; i += 1) {
            Kim thiskim = this.kim(i);
            Kim thatkim = that.kim(i);
            if (!thiskim.equals(thatkim)) {
                JSONzip.log("\n[" + i + "] " + thiskim + " <> " + thatkim);
                result = false;
            }
        }
        return result && this.root.postMortem(that.root);
    }

    public void registerMany(Kim kim) {
        int length = kim.length;
        int limit = this.capacity - this.length;
        if (limit > JSONzip.substringLimit) {
            limit = JSONzip.substringLimit;
        }
        int until = length - (JSONzip.minSubstringLength - 1);
        for (int from = 0; from < until; from += 1) {
            int len = length - from;
            if (len > JSONzip.maxSubstringLength) {
                len = JSONzip.maxSubstringLength;
            }
            len += from;
            Node node = this.root;
            for (int at = from; at < len; at += 1) {
                Node next = node.vet(kim.get(at));
                if (next.integer == none
                        && at - from >= (JSONzip.minSubstringLength - 1)) {
                    next.integer = this.length;
                    this.uses[this.length] = 1;
                    this.kims[this.length] = kim;
                    this.froms[this.length] = from;
                    this.thrus[this.length] = at + 1;
                    if (JSONzip.probe) {
                        try {
                            JSONzip.log("<<" + this.length + " "
                                    + new Kim(kim, from, at + 1) + ">> ");
                        } catch (Throwable ignore) {
                        }
                    }
                    this.length += 1;
                    limit -= 1;
                    if (limit <= 0) {
                        return;
                    }
                }
                node = next;
            }
        }
    }

    public void registerOne(Kim kim) {
        int integer = registerOne(kim, 0, kim.length);
        if (integer != none) {
            this.kims[integer] = kim;
        }
    }

    public int registerOne(Kim kim, int from, int thru) {
        if (this.length < this.capacity) {
            Node node = this.root;
            for (int at = from; at < thru; at += 1) {
                node = node.vet(kim.get(at));
            }
            if (node.integer == none) {
                int integer = this.length;
                node.integer = integer;
                this.uses[integer] = 1;
                this.kims[integer] = kim;
                this.froms[integer] = from;
                this.thrus[integer] = thru;
                if (JSONzip.probe) {
                    try {
                        JSONzip.log("<<" + integer + " " + new Kim(kim, from, thru) + ">> ");
                    } catch (Throwable ignore) {
                    }
                }
                this.length += 1;
                return integer;
            }
        }
        return none;
    }

    /**
     * Reserve space in the keep, compacting if necessary. A keep may contain
     * at most -capacity- elements. The keep contents can be reduced by
     * deleting all elements with low use counts, rebuilding the trie with the
     * survivors.
     */
    public void reserve() {
        if (this.capacity - this.length < JSONzip.substringLimit) {
            int from = 0;
            int to = 0;
            this.root = new Node();
            while (from < this.capacity) {
                if (this.uses[from] > 1) {
                    Kim kim = this.kims[from];
                    int thru = this.thrus[from];
                    Node node = this.root;
                    for (int at = this.froms[from]; at < thru; at += 1) {
                        Node next = node.vet(kim.get(at));
                        node = next;
                    }
                    node.integer = to;
                    this.uses[to] = age(this.uses[from]);
                    this.froms[to] = this.froms[from];
                    this.thrus[to] = thru;
                    this.kims[to] = kim;
                    to += 1;
                }
                from += 1;
            }

// It is possible, but highly unlikely, that too many items survive.
// If that happens, clear the keep.

            if (this.capacity - to < JSONzip.substringLimit) {
                this.power = 0;
                this.root = new Node();
                to = 0;
            }
            this.length = to;
            while (to < this.capacity) {
                this.uses[to] = 0;
                this.kims[to] = null;
                this.froms[to] = 0;
                this.thrus[to] = 0;
                to += 1;

            }
        }
    }

    public Object value(int integer) {
        return kim(integer);
    }
}
