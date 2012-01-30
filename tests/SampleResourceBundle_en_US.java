/*
 * File:         SampleResourceBundle_en_US.java
 * Author:       JSON.org
 */
package org.json.tests;

import java.util.*;

/**
 * The Class SampleResourceBundle_en_US.
 */
public class SampleResourceBundle_en_US extends ListResourceBundle {
    
    /* (non-Javadoc)
     * @see java.util.ListResourceBundle#getContents()
     */
    @Override
    public Object[][] getContents() {
        return contents;
    }
    
    /** The contents. */
    private Object[][] contents = {
        { "ASCII", "American Standard Code for Information Interchange" },
        { "JAVA.desc", "Just Another Vague Acronym" },
        { "JAVA.data", "Sweet language" },
        { "JSON", "JavaScript Object Notation" },
    };
}