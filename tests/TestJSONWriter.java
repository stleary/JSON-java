/*
 * File: TestJSONWriter.java Author: JSON.org
 */
package org.json.tests;

import java.io.IOException;
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONStringer;
import org.json.JSONWriter;

import junit.framework.TestCase;

/**
 * The Class TestJSONWriter.
 */
public class TestJSONWriter extends TestCase
{
    
    /** The jsonwriter. */
    JSONWriter jsonwriter;

    /**
     * The Class BadWriterThrowsOnNonBrace.
     */
    class BadWriterThrowsOnNonBrace extends Writer
    {

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#write(char[], int, int)
         */
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            if (cbuf[0] != '{')
                throw new IOException("Test Message From Non-Brace");
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#flush()
         */
        @Override
        public void flush() throws IOException
        {
            //Do Nothing
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#close()
         */
        @Override
        public void close() throws IOException
        {
            //Do Nothing
        }

    }

    /**
     * The Class BadWriterThrowsOnLeftBrace.
     */
    class BadWriterThrowsOnLeftBrace extends Writer
    {

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#write(char[], int, int)
         */
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            if (cbuf[0] == '{')
                throw new IOException("Test Message From Left Brace");
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#flush()
         */
        @Override
        public void flush() throws IOException
        {
            //Do Nothing
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#close()
         */
        @Override
        public void close() throws IOException
        {
            //Do Nothing
        }

    }

    /**
     * The Class BadWriterThrowsOnRightBrace.
     */
    class BadWriterThrowsOnRightBrace extends Writer
    {

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#write(char[], int, int)
         */
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            if (cbuf[0] == '}')
                throw new IOException("Test Message From Right Brace");
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#flush()
         */
        @Override
        public void flush() throws IOException
        {
            //Do Nothing
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.Writer#close()
         */
        @Override
        public void close() throws IOException
        {
            //Do Nothing
        }

    }

    /**
     * The Class BadExtensionThatCausesNestingError.
     */
    class BadExtensionThatCausesNestingError extends JSONStringer
    {
        
        /**
         * Change mode.
         *
         * @param c the c
         * @return the jSON writer
         */
        public JSONWriter changeMode(char c)
        {
            mode = c;
            return this;
        }
    }
    
    /**
     * Tests the key method.
     */
    public void testKey()
    {
        try
        {
            jsonwriter = new JSONStringer();
            String result = jsonwriter.object().key("abc").value("123")
                    .key("abc2").value(60).key("abc3").value(20.98).key("abc4")
                    .value(true).endObject().toString();
            assertEquals(
                    "{\"abc\":\"123\",\"abc2\":60,\"abc3\":20.98,\"abc4\":true}",
                    result);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the value method.
     */
    public void testValue()
    {
        try
        {
            jsonwriter = new JSONStringer();
            String result = jsonwriter.array().value("123").value(10)
                    .value(30.45).value(false).endArray().toString();
            assertEquals("[\"123\",10,30.45,false]", result);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the object method using stops at max depth.
     */
    public void testObject_StopsAtMaxDepth()
    {
        try
        {
            jsonwriter = new JSONStringer();
            int i = 0;
            while (i < 201)
            {
                jsonwriter.object().key("123");
                i++;
            }
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Nesting too deep.", e.getMessage());
        }
    }

    /**
     * Tests the array method using stops at max depth.
     */
    public void testArray_StopsAtMaxDepth()
    {
        try
        {
            jsonwriter = new JSONStringer();
            int i = 0;
            while (i < 201)
            {
                jsonwriter.array();
                i++;
            }
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Nesting too deep.", e.getMessage());
        }
    }

    /**
     * Tests the value method using out of sequence.
     */
    public void testValue_OutOfSequence()
    {
        try
        {
            jsonwriter = new JSONStringer();
            jsonwriter.value(true);
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Value out of sequence.", e.getMessage());
        }
    }

    /**
     * Tests the object method using out of sequence.
     */
    public void testObject_OutOfSequence()
    {
        try
        {
            jsonwriter = new JSONStringer();
            jsonwriter.object().object();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misplaced object.", e.getMessage());
        }
    }

    /**
     * Tests the object method using two objects within array.
     */
    public void testObject_TwoObjectsWithinArray()
    {
        try
        {
            jsonwriter = new JSONStringer();
            String result = jsonwriter.array().object().endObject().object()
                    .endObject().endArray().toString();
            assertEquals("[{},{}]", result);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the object method using two strings and an int within object.
     */
    public void testObject_TwoStringsAndAnIntWithinObject()
    {
        try
        {
            jsonwriter = new JSONStringer();
            String result = jsonwriter.object().key("string1").value("abc")
                    .key("int").value(35).key("string2").value("123")
                    .endObject().toString();
            assertEquals(
                    "{\"string1\":\"abc\",\"int\":35,\"string2\":\"123\"}",
                    result);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the key method using misplaced key.
     */
    public void testKey_MisplacedKey()
    {
        try
        {
            jsonwriter = new JSONStringer();
            jsonwriter.key("123");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misplaced key.", e.getMessage());
        }
    }

    /**
     * Tests the key method using catches ioexception.
     */
    public void testKey_CatchesIoexception()
    {
        try
        {
            jsonwriter = new JSONWriter(new BadWriterThrowsOnNonBrace());
            jsonwriter.object().key("123");
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Test Message From Non-Brace", e.getMessage());
        }
    }

    /**
     * Tests the object method using catches ioexception.
     */
    public void testObject_CatchesIoexception()
    {
        try
        {
            jsonwriter = new JSONWriter(new BadWriterThrowsOnLeftBrace());
            jsonwriter.object();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Test Message From Left Brace", e.getMessage());
        }
    }

    /**
     * Tests the key method using null key.
     */
    public void testKey_NullKey()
    {
        try
        {
            jsonwriter = new JSONStringer();
            jsonwriter.key(null);
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Null key.", e.getMessage());
        }
    }

    /**
     * Tests the array method using two arrays within object.
     */
    public void testArray_TwoArraysWithinObject()
    {
        try
        {
            jsonwriter = new JSONStringer();
            String result = jsonwriter.object().key("123").array().endArray()
                    .key("1234").array().endArray().endObject().toString();
            assertEquals("{\"123\":[],\"1234\":[]}", result);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the object method using two objects within object.
     */
    public void testObject_TwoObjectsWithinObject()
    {
        try
        {
            jsonwriter = new JSONStringer();
            String result = jsonwriter.object().key("123").object().endObject()
                    .key("1234").object().endObject().endObject().toString();
            assertEquals("{\"123\":{},\"1234\":{}}", result);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the array method using two arrays within array.
     */
    public void testArray_TwoArraysWithinArray()
    {
        try
        {
            jsonwriter = new JSONStringer();
            String result = jsonwriter.array().array().endArray().array()
                    .endArray().endArray().toString();
            assertEquals("[[],[]]", result);
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the array method using misplaced array.
     */
    public void testArray_MisplacedArray()
    {
        try
        {
            jsonwriter = new JSONStringer();
            jsonwriter.object().array();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misplaced array.", e.getMessage());
        }
    }

    /**
     * Tests the endArray method using misplaced end array.
     */
    public void testEndArray_MisplacedEndArray()
    {
        try
        {
            jsonwriter = new JSONStringer();
            jsonwriter.endArray();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misplaced endArray.", e.getMessage());
        }
    }

    /**
     * Tests the endObject method using misplaced end object.
     */
    public void testEndObject_MisplacedEndObject()
    {
        try
        {
            jsonwriter = new JSONStringer();
            jsonwriter.endObject();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misplaced endObject.", e.getMessage());
        }
    }

    /**
     * Tests the endObject method using catches ioexception.
     */
    public void testEndObject_CatchesIoexception()
    {
        try
        {
            jsonwriter = new JSONWriter(new BadWriterThrowsOnRightBrace());
            jsonwriter.object().endObject();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Test Message From Right Brace", e.getMessage());
        }
    }

    /**
     * Tests the pop method using bad extension that causes nesting error1.
     */
    public void testPop_BadExtensionThatCausesNestingError1()
    {
        try
        {
            BadExtensionThatCausesNestingError betcnw = new BadExtensionThatCausesNestingError();
            betcnw.object().endObject();
            betcnw.changeMode('k').endObject();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Nesting error.", e.getMessage());
        }
    }

    /**
     * Tests the pop method using bad extension that causes nesting error2.
     */
    public void testPop_BadExtensionThatCausesNestingError2()
    {
        try
        {
            BadExtensionThatCausesNestingError betcnw = new BadExtensionThatCausesNestingError();
            betcnw.array();
            betcnw.changeMode('k').endObject();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Nesting error.", e.getMessage());
        }
    }

}