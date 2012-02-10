/*
 * File:         SampleResourceBundle_fr.java
 * Author:       JSON.org
 */
package org.json.tests;

import java.util.*;

/**
 * The Class SampleResourceBundle_fr.
 */
public class SampleResourceBundle_fr extends ListResourceBundle {
    
    /* (non-Javadoc)
     * @see java.util.ListResourceBundle#getContents()
     */
    @Override
    public Object[][] getContents() {
        return contents;
    }
    
    /** The contents. */
    private Object[][] contents = {
            { "ASCII", "Number that represent chraracters" },
            { "JAVA", "The language you are running to see this" },
            { "JSON", "What are we testing?" },
        };
}