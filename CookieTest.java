package org.json.junit;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;


/**
 * Tests for JSON-Java Cookie.java
 */
public class CookieTest {

    String realWorldCookie = 
        "hpc=d=I.aZLE4l.8DeqRynle2fTnKxdAycw3CvCrzMNofhR9a5vYaU.XnHk6n3ZenMs6Xqq"+
        "3Mc5kMw.M1c.vR6zdxVMsfAQn75WNaFp8mY3UQgEw8lvIAbZvT_PiJofv7OMCbabUOe1Efd"+
        "i2M5.aVTX2bHB3EJPhNQNe0B5PL6mGbz7KYYyujkcn6hVS7U5d5OYv7L0GSAiKY-&v=2; y"+
        "wadp115488662=3370273056; AO=u=1&o=1; ywandp=10001806365479:1024785001;"+
        "10001576721379:3531995934; fpc=10001806365479:ZblWsSPj||;10001576721379"+
        ":ZY1jZhRq||; V=v=0.7&m=0&ccOptions={\"show\":false,\"lang\":\"en\",\"fo"+
        "ntSize\":24,\"fontName\":\"Helvetica Neue,Helvetica,Arial,_sans\",\"fon"+
        "tColor\":\"#ffffff\",\"fontOpacity\":1,\"fontEffect\":\"none\",\"bgColo"+
        "r\":\"#000000\",\"bgOpacity\":0.75}; yvap=193@yvap=193@cc=1@al=1@vl=0@r"+
        "vl=0@ac=1@rvl_NFL=0@session_NFL=0@lmsID=@rcc=0; YLS=v=1&p=1&n=1; ucs=tr"+
        "=1424831973913&sfcTs=1425971131&sfc=1; B=26tgei1adfl2v&b=4&d=j7.bbChrYH"+
        "1Ww.22z25N3S2YRsiX.e8VKSZpZdjeYXeN.w--&s=lr; F=a=MVvM8WsMvSxoU9K4FcyMxZ"+
        ".lwmw1yLWpNLOZbMVqjDB8d.bZm1C1JJVJFfCXcy3YfSZy47VAvKKSGZBmM1HQdIUWJA--&"+
        "b=PW8Y; YP=v=AwAAY&d=AEcAMEQCIHHEk.ugtA0iqWk_ctLMBWKG_gJfDzKX.tlKIIGBVH"+
        "cTAiBgmZUHV73V2i80FgqcVjQnvNTyor0rYBXsjhXBul2PzwA-; ypcdb=096e88ca6ff13"+
        "fee954ee414bb7b9362; Y=v=1&n=edbmi9njnt2h1&p=; CRZY={\"33935700511_2015"+
        "0317\":{\"expires\":1426808579870,\"data\":{\"nv\":1,\"bn\":1,\"collaps"+
        "ed\":0}},\"33726925511_20150318\":{\"expires\":1426859124988,\"data\":{"+
        "\"nv\":7,\"bn\":0,\"collapsed\":0}},\"33748770511_20150318\":{\"expires"+
        "\":1426911961098,\"data\":{\"nv\":2,\"bn\":0,\"collapsed\":0}}}; apeaf="+
        "td-applet-stream={\"tmpl\":\"items\",\"po\":{\"2409678.20150318\":{\"c"+
        "\":0,\"v\":2,\"ts\":1426719393315}}}";
    
    @Test(expected=NullPointerException.class)
    public void shouldHandleNullCookie() {
        String cookieStr = null;
        Cookie.toJSONObject(cookieStr);
    }

    @Test(expected=JSONException.class)
    public void shouldHandleEmptyStringCookie() {
        String cookieStr = "";
        Cookie.toJSONObject(cookieStr);
    }

    @Test
    public void shouldHandleSimpleCookie() {
        String cookieStr = "abc=def";
        JSONObject jsonObject = Cookie.toJSONObject(cookieStr);
        Set<String> keySet = jsonObject.keySet();
        assertTrue("Keyset should have exactly 2 keys", keySet.size() == 2);
        assertTrue("name should have expected value",
                jsonObject.getString("name").equals("abc"));
        assertTrue("Value should have expected value", 
                jsonObject.getString("value").equals("def"));
        
    }

}
