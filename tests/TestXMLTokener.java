/*
 * File:         TestXMLTokener.java
 * Author:       JSON.org
 */
package org.json.tests;

import org.json.JSONException;
import org.json.XMLTokener;

import junit.framework.TestCase;

/**
 * The Class TestXMLTokener.
 */
public class TestXMLTokener extends TestCase
{

    /** The xmltokener. */
    private XMLTokener xmltokener;

    /**
     * Tests the nextContent method.
     */
    public void testNextContent()
    {
        try
        {
            xmltokener = new XMLTokener("< abc><de&nbsp;f/></abc>");
            assertEquals('<', xmltokener.nextContent());
            assertEquals("abc>", xmltokener.nextContent());
            assertEquals('<', xmltokener.nextContent());
            assertEquals("de&nbsp;f/>", xmltokener.nextContent());
            assertEquals('<', xmltokener.nextContent());
            assertEquals("/abc>", xmltokener.nextContent());
            assertEquals(null, xmltokener.nextContent());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextCdata method.
     */
    public void testNextCdata()
    {
        try
        {
            xmltokener = new XMLTokener("<[CDATA[<abc/>]]>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('[', xmltokener.next('['));
            assertEquals("CDATA", xmltokener.nextToken());
            assertEquals('[', xmltokener.next('['));
            assertEquals("<abc/>", xmltokener.nextCDATA());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextCdata method using broken cdata.
     */
    public void testNextCdata_BrokenCdata1()
    {
        try
        {
            xmltokener = new XMLTokener("<[CDATA[<abc/>]><abc>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('[', xmltokener.next('['));
            assertEquals("CDATA", xmltokener.nextToken());
            assertEquals('[', xmltokener.next('['));
            xmltokener.nextCDATA();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Unclosed CDATA at 22 [character 23 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextCdata method using broken cdata.
     */
    public void testNextCdata_BrokenCdata2()
    {
        try
        {
            xmltokener = new XMLTokener("<[CDATA[<abc/>]]<abc>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('[', xmltokener.next('['));
            assertEquals("CDATA", xmltokener.nextToken());
            assertEquals('[', xmltokener.next('['));
            xmltokener.nextCDATA();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Unclosed CDATA at 22 [character 23 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextCdata method using broken cdata.
     */
    public void testNextCdata_BrokenCdata3()
    {
        try
        {
            xmltokener = new XMLTokener("<[CDATA[<abc/>]]<abc>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('[', xmltokener.next('['));
            assertEquals("CDATA", xmltokener.nextToken());
            assertEquals('[', xmltokener.next('['));
            xmltokener.nextCDATA();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Unclosed CDATA at 22 [character 23 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextCdata method using broken cdata.
     */
    public void testNextCdata_BrokenCdata4()
    {
        try
        {
            xmltokener = new XMLTokener("<[CDATA[<abc/>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('[', xmltokener.next('['));
            assertEquals("CDATA", xmltokener.nextToken());
            assertEquals('[', xmltokener.next('['));
            xmltokener.nextCDATA();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Unclosed CDATA at 15 [character 16 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextEntity method using ampersand.
     */
    public void testNextEntity_Ampersand()
    {
        try
        {
            xmltokener = new XMLTokener("<&amp;>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('&', xmltokener.next('&'));
            assertEquals('&', xmltokener.nextEntity('&'));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextEntity method using number entity.
     */
    public void testNextEntity_NumberEntity()
    {
        try
        {
            xmltokener = new XMLTokener("<&#60;>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('&', xmltokener.next('&'));
            assertEquals("&#60;", xmltokener.nextEntity('&'));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the nextEntity method using broken entity.
     */
    public void testNextEntity_BrokenEntity()
    {
        try
        {
            xmltokener = new XMLTokener("<&nbsp");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('&', xmltokener.next('&'));
            assertEquals("&#60;", xmltokener.nextEntity('&'));
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Missing ';' in XML entity: &nbsp at 7 [character 8 line 1]", e.getMessage());
        }
    }

    /**
     * Tests the nextMeta method using string.
     */
    public void testNextMeta_String()
    {
        try
        {
            xmltokener = new XMLTokener("<! \"metaString\">");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('!', xmltokener.next('!'));
            assertEquals(true, xmltokener.nextMeta());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the nextMeta method using open string.
     */
    public void testNextMeta_OpenString()
    {
        try
        {
            xmltokener = new XMLTokener("<! \"metaString>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('!', xmltokener.next('!'));
            xmltokener.nextMeta();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Unterminated string at 16 [character 17 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextMeta method using symbols.
     */
    public void testNextMeta_Symbols()
    {
        try
        {
            xmltokener = new XMLTokener("<! <>/=!?>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('!', xmltokener.next('!'));
            assertEquals('<', xmltokener.nextMeta());
            assertEquals('>', xmltokener.nextMeta());
            assertEquals('/', xmltokener.nextMeta());
            assertEquals('=', xmltokener.nextMeta());
            assertEquals('!', xmltokener.nextMeta());
            assertEquals('?', xmltokener.nextMeta());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextMeta method using misshaped.
     */
    public void testNextMeta_Misshaped()
    {
        try
        {
            xmltokener = new XMLTokener("<!");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('!', xmltokener.next('!'));
            xmltokener.nextMeta();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misshaped meta tag at 3 [character 4 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextMeta method using ending with bang.
     */
    public void testNextMeta_EndingWithBang()
    {
        try
        {
            xmltokener = new XMLTokener("<!data!");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('!', xmltokener.next('!'));
            assertEquals(true, xmltokener.nextMeta());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextMeta method using ending with space.
     */
    public void testNextMeta_EndingWithSpace()
    {
        try
        {
            xmltokener = new XMLTokener("<!data ");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('!', xmltokener.next('!'));
            assertEquals(true, xmltokener.nextMeta());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using normal tag.
     */
    public void testNextToken_NormalTag()
    {
        try
        {
            xmltokener = new XMLTokener("<da ta>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals("da", xmltokener.nextToken());
            assertEquals("ta", xmltokener.nextToken());
            assertEquals('>', xmltokener.nextToken());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using tag with bad character.
     */
    public void testNextToken_TagWithBadCharacter()
    {
        try
        {
            xmltokener = new XMLTokener("<da<ta>");
            assertEquals('<', xmltokener.next('<'));
            xmltokener.nextToken();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Bad character in a name at 4 [character 5 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using tag with misplaced less than.
     */
    public void testNextToken_TagWithMisplacedLessThan()
    {
        try
        {
            xmltokener = new XMLTokener("<<data>");
            assertEquals('<', xmltokener.next('<'));
            xmltokener.nextToken();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misplaced '<' at 2 [character 3 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using misshaped element.
     */
    public void testNextToken_MisshapedElement()
    {
        try
        {
            xmltokener = new XMLTokener("<");
            assertEquals('<', xmltokener.next('<'));
            xmltokener.nextToken();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Misshaped element at 2 [character 3 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using symbols.
     */
    public void testNextToken_Symbols()
    {
        try
        {
            xmltokener = new XMLTokener("< /=!?");
            assertEquals('<', xmltokener.next('<'));
            assertEquals('/', xmltokener.nextToken());
            assertEquals('=', xmltokener.nextToken());
            assertEquals('!', xmltokener.nextToken());
            assertEquals('?', xmltokener.nextToken());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using string.
     */
    public void testNextToken_String()
    {
        try
        {
            xmltokener = new XMLTokener("<\"abc&amp;123\">");
            assertEquals('<', xmltokener.next('<'));
            assertEquals("abc&123", xmltokener.nextToken());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using no greater than.
     */
    public void testNextToken_NoGreaterThan()
    {
        try
        {
            xmltokener = new XMLTokener("<abc123");
            assertEquals('<', xmltokener.next('<'));
            assertEquals("abc123", xmltokener.nextToken());
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the nextToken method using unterminated string.
     */
    public void testNextToken_UnterminatedString()
    {
        try
        {
            xmltokener = new XMLTokener("<\"abc123>");
            assertEquals('<', xmltokener.next('<'));
            xmltokener.nextToken();
            fail("Should have thrown exception.");
        } catch (JSONException e)
        {
            assertEquals("Unterminated string at 10 [character 11 line 1]", e.getMessage());
        }
    }
    
    /**
     * Tests the skipTo method.
     */
    public void testSkipTo()
    {
        try
        {
            xmltokener = new XMLTokener("<abc123>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals(true, xmltokener.skipPast("c1"));
            assertEquals('2', xmltokener.next('2'));
            assertEquals(false, xmltokener.skipPast("b1"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }
    
    /**
     * Tests the skipTo method using long parameter.
     */
    public void testSkipTo_LongParameter()
    {
        try
        {
            xmltokener = new XMLTokener("<abc>");
            assertEquals('<', xmltokener.next('<'));
            assertEquals(false, xmltokener.skipPast("abcdefghi"));
        } catch (JSONException e)
        {
            fail(e.getMessage());
        }
    }

}