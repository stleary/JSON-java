package org.json.junit;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.core.StringContains;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JSONObjectRecursiveExceptionTest {
    public static class RecursiveList {
        public List<RecursiveList> getRecursiveList() {
            return Collections.singletonList(this);
        }
    }
    
    public static class RecursiveArray {
        public RecursiveArray[] getRecursiveArray() {
            return new RecursiveArray[] {this};
        }
    }
    
    public static class RecursiveMap {
        public Map<String, RecursiveMap> getRecursiveMap() {
            Map<String, RecursiveMap> map = new HashMap<String, RecursiveMap>();
            map.put("test", this);
            return map;
        }
    }
    
    public static class Recursive {
        public Recursive getSelf() {
            return this;
        }
    }
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    
    @Before
    public void before() {
        exceptionRule.expect(JSONException.class);
        exceptionRule.expectMessage(StringContains.containsString("JavaBean object contains recursively defined member variable of key"));
    }
   
    
    @Test
    public void testRecursiveList() {          
        new JSONObject(new RecursiveList());
    }
    
    @Ignore
    @Test
    public void testRecursiveArray() {        
        new JSONObject(new RecursiveArray());
    }
    
    @Ignore
    @Test
    public void testRecursiveMap() {        
        new JSONObject(new RecursiveMap());
    }
    
    @Test
    public void testRecursive() {
        new JSONObject(new Recursive());
    }
}
