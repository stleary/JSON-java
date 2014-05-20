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
 * JSONzip is a binary-encoded JSON dialect. It is designed to compress the
 * messages in a session in bandwidth constrained applications, such as mobile.
 *
 * JSONzip is adaptive, so with each message seen, it should improve its
 * compression. It minimizes JSON's overhead, reducing punctuation
 * to a small number of bits. It uses Huffman encoding to reduce the average
 * size of characters. It uses caches (or Keeps) to keep recently seen strings
 * and values, so repetitive content (such as object keys) can be
 * substantially reduced. It uses a character encoding called Kim (Keep it
 * minimal) that is smaller than UTF-8 for most East European, African, and
 * Asian scripts.
 *
 * JSONzip tends to reduce most content by about half. If there is a lot of
 * recurring information, the reduction can be much more dramatic.
 *
 * FOR EVALUATION PURPOSES ONLY. THIS PACKAGE HAS NOT YET BEEN TESTED
 * ADEQUATELY FOR PRODUCTION USE.
 *
 * @author JSON.org
 * @version 2014-05-20
 */
public abstract class JSONzip implements None, PostMortem {
    /**
     * The characters in JSON numbers can be reduced to 4 bits each.
     */
    public static final byte[] bcd = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-', '+', 'E'
    };

    /**
     * The end of string code.
     */
    public static final int end = 256;

    /**
     * The end of number code.
     */
    public static final int endOfNumber = bcd.length;

    /**
     * The first positive integer that cannot be encoded in 4 bits.
     */
    public static final long int4 = 16;

    /**
     * The first positive integer that cannot be encoded in 7 bits.
     */
    public static final long int7 = 144;

    /**
     * The first positive integer that cannot be encoded in 14 bits.
     */
    public static final long int14 = 16528;

    /**
     * The package supports tracing for debugging.
     */
    public static final boolean probe = false;

    /**
     * The value code for an empty object.
     */
    public static final int zipEmptyObject = 0;

    /**
     * The value code for an empty array.
     */
    public static final int zipEmptyArray = 1;

    /**
     * The value code for true.
     */
    public static final int zipTrue = 2;

    /**
     * The value code for false.
     */
    public static final int zipFalse = 3;

    /**
     * The value code for null.
     */
    public static final int zipNull = 4;

    /**
     * The value code for a non-empty object.
     */
    public static final int zipObject = 5;

    /**
     * The value code for an array with a string as its first element.
     */
    public static final int zipArrayString = 6;

    /**
     * The value code for an array with a non-string value as its first element.
     */
    public static final int zipArrayValue = 7;

    /**
     * A Huffman encoder for names.
     */
    protected final Huff namehuff;

    /**
     * A Huffman encoder for names extended bytes.
     */
    protected final Huff namehuffext;

    /**
     * A place to keep the names (keys).
     */
    protected final Keep namekeep;

    /**
     * A Huffman encoder for string values.
     */
    protected final Huff stringhuff;

    /**
     * A Huffman encoder for string values extended bytes.
     */
    protected final Huff stringhuffext;

    /**
     * A place to keep the strings.
     */
    protected final Keep stringkeep;

    /**
     * A place to keep the values.
     */
    protected final Keep valuekeep;

    /**
     * Initialize the data structures.
     */
    protected JSONzip() {
        this.namehuff = new Huff(end + 1);
        this.namehuffext = new Huff(end + 1);
        this.namekeep = new Keep(9);
        this.stringhuff = new Huff(end + 1);
        this.stringhuffext = new Huff(end + 1);
        this.stringkeep = new Keep(11);
        this.valuekeep = new Keep(10);
    }

    /**
     * Generate the Huffman tables.
     */
    protected void generate() {
        this.namehuff.generate();
        this.namehuffext.generate();
        this.stringhuff.generate();
        this.stringhuffext.generate();
    }

    /**
     * Write an end-of-line to the console.
     */
    static void log() {
        log("\n");
    }

    /**
     * Write an integer to the console.
     *
     * @param integer The integer to write to the log.
     */
    static void log(int integer) {
        log(integer + " ");
    }

    /**
     * Write two integers, separated by ':' to the console.
     * The second integer is suppressed if it is 1.
     *
     * @param integer The integer to write to the log.
     * @param width The width of the integer in bits.
     */
    static void log(int integer, int width) {
        if (width == 1) {
            log(integer);
        } else {
            log(integer + ":" + width + " ");
        }
    }

    /**
     * Write a string to the console.
     *
     * @param string The string to be written to the log.
     */
    static void log(String string) {
        System.out.print(string);
    }

    /**
     * Write a character or its code to the console.
     *
     * @param integer The charcode to be written to the log.
     * @param width The width of the charcode in bits.
     */
    static void logchar(int integer, int width) {
        if (integer > ' ' && integer <= '}') {
            log("'" + (char) integer + "':" + width + " ");
        } else {
            log(integer, width);
        }
    }

    /**
     * This method is used for testing the implementation of JSONzip. It is not
     * suitable for any other purpose. It is used to compare a Compressor and a
     * Decompressor, verifying that the data structures that were built during
     * zipping and unzipping were the same.
     *
     * @return true if the structures match.
     */
    public boolean postMortem(PostMortem pm) {
        JSONzip that = (JSONzip) pm;
        return this.namehuff.postMortem(that.namehuff)
                && this.namekeep.postMortem(that.namekeep)
                && this.stringkeep.postMortem(that.stringkeep)
                && this.stringhuff.postMortem(that.stringhuff)
                && this.valuekeep.postMortem(that.valuekeep);
    }
}
