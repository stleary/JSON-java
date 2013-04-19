package org.json.zip;

import org.json.JSONException;

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
 * JSONzip is a compression scheme for JSON text.
 * @author JSON.org
 * @version 2013-04-18
 */

/**
 * A Huffman encoder/decoder. It operates over a domain of integers, which may
 * map to characters or other symbols. Symbols that are used frequently are
 * given shorter codes than symbols that are used infrequently. This usually
 * produces shorter messages.
 *
 * Initially, all of the symbols are given the same weight. The weight of a
 * symbol is incremented by the tick method. The generate method is used to
 * generate the encoding table. The table must be generated before encoding or
 * decoding. You may regenerate the table with the latest weights at any time.
 */
public class Huff implements None, PostMortem {

    /**
     * The number of symbols known to the encoder.
     */
    private final int domain;

    /**
     * An array that maps symbol values to symbols.
     */
    private final Symbol[] symbols;

    /**
     * The root of the decoding table, and the terminal of the encoding table.
     */
    private Symbol table;

    /**
     * Have any weights changed since the table was last generated?
     */
    private boolean upToDate = false;

    /**
     * The number of bits in the last symbol. This is used in tracing.
     */
    private int width;

    private static class Symbol implements PostMortem {
        public Symbol back;
        public Symbol next;
        public Symbol zero;
        public Symbol one;
        public final int integer;
        public long weight;

        /**
         * Make a symbol representing a character or other value.
         *
         * @param integer
         *            The symbol's number
         */
        public Symbol(int integer) {
            this.integer = integer;
            this.weight = 0;
            this.next = null;
            this.back = null;
            this.one = null;
            this.zero = null;
        }

        public boolean postMortem(PostMortem pm) {
            boolean result = true;
            Symbol that = (Symbol) pm;

            if (this.integer != that.integer || this.weight != that.weight) {
                return false;
            }
            if ((this.back != null) != (that.back != null)) {
                return false;
            }
            Symbol zero = this.zero;
            Symbol one = this.one;
            if (zero == null) {
                if (that.zero != null) {
                    return false;
                }
            } else {
                result = zero.postMortem(that.zero);
            }
            if (one == null) {
                if (that.one != null) {
                    return false;
                }
            } else {
                result = one.postMortem(that.one);
            }
            return result;
        }

    }

    /**
     * Construct a Huffman encoder/decoder.
     *
     * @param domain
     *            The number of values known to the object.
     */
    public Huff(int domain) {
        this.domain = domain;
        int length = domain * 2 - 1;
        this.symbols = new Symbol[length];

// Make the leaf symbols.

        for (int i = 0; i < domain; i += 1) {
            symbols[i] = new Symbol(i);
        }

// SMake the links.

        for (int i = domain; i < length; i += 1) {
            symbols[i] = new Symbol(none);
        }
    }

    /**
     * Generate the encoding/decoding table. The table determines the bit
     * sequences used by the read and write methods.
     *
     * @return this
     */
    public void generate() {
        if (!this.upToDate) {

// Phase One: Sort the symbols by weight into a linked list.

            Symbol head = this.symbols[0];
            Symbol next;
            Symbol previous = head;
            Symbol symbol;

            this.table = null;
            head.next = null;
            for (int i = 1; i < this.domain; i += 1) {
                symbol = symbols[i];

// If this symbol weights less than the head, then it becomes the new head.

                if (symbol.weight < head.weight) {
                    symbol.next = head;
                    head = symbol;
                } else {

// To save time, we will start the search from the previous symbol instead
// of the head unless the current symbol weights less than the previous symbol.

                    if (symbol.weight < previous.weight) {
                        previous = head;
                    }

// Find a connected pair (previous and next) where the symbol weighs the same
// or more than previous but less than the next. Link the symbol between them.

                    while (true) {
                        next = previous.next;
                        if (next == null || symbol.weight < next.weight) {
                            break;
                        }
                        previous = next;
                    }
                    symbol.next = next;
                    previous.next = symbol;
                    previous = symbol;
                }
            }

// Phase Two: Make new symbols from the two lightest symbols until only one
// symbol remains. The final symbol becomes the root of the table binary tree.

            int avail = this.domain;
            Symbol first;
            Symbol second;
            previous = head;
            while (true) {
                first = head;
                second = first.next;
                head = second.next;
                symbol = this.symbols[avail];
                avail += 1;
                symbol.weight = first.weight + second.weight;
                symbol.zero = first;
                symbol.one = second;
                symbol.back = null;
                first.back = symbol;
                second.back = symbol;
                if (head == null) {
                    break;
                }

// Insert the new symbol back into the sorted list.

                if (symbol.weight < head.weight) {
                    symbol.next = head;
                    head = symbol;
                    previous = head;
                } else {
                    while (true) {
                        next = previous.next;
                        if (next == null || symbol.weight < next.weight) {
                            break;
                        }
                        previous = next;
                    }
                    symbol.next = next;
                    previous.next = symbol;
                    previous = symbol;
                }

            }

// The last remaining symbol is the root of the table.

            this.table = symbol;
            this.upToDate = true;
        }
    }

    private boolean postMortem(int integer) {
        int[] bits = new int[this.domain];
        Symbol symbol = this.symbols[integer];
        if (symbol.integer != integer) {
            return false;
        }
        int i = 0;
        while (true) {
            Symbol back = symbol.back;
            if (back == null) {
                break;
            }
            if (back.zero == symbol) {
                bits[i] = 0;
            } else if (back.one == symbol) {
                bits[i] = 1;
            } else {
                return false;
            }
            i += 1;
            symbol = back;
        }
        if (symbol != this.table) {
            return false;
        }
        this.width = 0;
        symbol = this.table;
        while (symbol.integer == none) {
            i -= 1;
            symbol = bits[i] != 0 ? symbol.one : symbol.zero;
        }
        return symbol.integer == integer && i == 0;
    }

    /**
     * Compare two Huffman tables.
     */
    public boolean postMortem(PostMortem pm) {

// Go through every integer in the domain, generating its bit sequence, and
// then proving that that bit sequence produces the same integer.

        for (int integer = 0; integer < this.domain; integer += 1) {
            if (!postMortem(integer)) {
                JSONzip.log("\nBad huff ");
                JSONzip.logchar(integer, integer);
                return false;
            }
        }
        return this.table.postMortem(((Huff) pm).table);
    }

    /**
     * Read bits until a symbol can be identified. The weight of the read
     * symbol will be incremented.
     *
     * @param bitreader
     *            The source of bits.
     * @return The integer value of the symbol.
     * @throws JSONException
     */
    public int read(BitReader bitreader) throws JSONException {
        try {
            this.width = 0;
            Symbol symbol = this.table;
            while (symbol.integer == none) {
                this.width += 1;
                symbol = bitreader.bit() ? symbol.one : symbol.zero;
            }
            tick(symbol.integer);
            if (JSONzip.probe) {
                JSONzip.logchar(symbol.integer, this.width);
            }
            return symbol.integer;
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * Increase by 1 the weight associated with a value.
     *
     * @param value
     *            The number of the symbol to tick
     * @return this
     */
    public void tick(int value) {
        this.symbols[value].weight += 1;
        this.upToDate = false;
    }

    /**
     * Increase by 1 the weight associated with a range of values.
     *
     * @param from
     *            The first symbol to tick
     * @param to
     *            The last symbol to tick
     * @return this
     */
    public void tick(int from, int to) {
        for (int value = from; value <= to; value += 1) {
            tick(value);
        }
    }

    /**
     * Recur from a symbol back, emitting bits. We recur before emitting to
     * make the bits come out in the right order.
     *
     * @param symbol
     *            The symbol to write.
     * @param bitwriter
     *            The bitwriter to write it to.
     * @throws JSONException
     */
    private void write(Symbol symbol, BitWriter bitwriter)
            throws JSONException {
        try {
            Symbol back = symbol.back;
            if (back != null) {
                this.width += 1;
                write(back, bitwriter);
                if (back.zero == symbol) {
                    bitwriter.zero();
                } else {
                    bitwriter.one();
                }
            }
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * Write the bits corresponding to a symbol. The weight of the symbol will
     * be incremented.
     *
     * @param value
     *            The number of the symbol to write
     * @param bitwriter
     *            The destination of the bits.
     * @return this
     * @throws JSONException
     */
    public void write(int value, BitWriter bitwriter) throws JSONException {
        this.width = 0;
        write(this.symbols[value], bitwriter);
        tick(value);
        if (JSONzip.probe) {
            JSONzip.logchar(value, this.width);
        }
    }
}
