package org.json;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class JSONArrayTest {

    @Test
    public void testIterable() throws Exception {
        String sampleArray = "[0,1,2,3,4,5,6,7,8,9,10]";
        JSONArray jArr = new JSONArray(sampleArray);
        int count = 0;
        for (Integer obj : (Iterable<Integer>)jArr) {
            assertTrue(count == obj);
            count++;
        }

    }
}
