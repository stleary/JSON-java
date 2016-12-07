package org.json.util;

/************************************************************************************************
 *                                                                                              *
 * This class contains static methods for reading and parsing http json response                *
 *                                                                                              *
 *                                                                                              *
 *                                                                                              *
 *                                 Copyright Gamal Shaban 2012.                                 *
 *                                     gemy21ce@gmail.com                                       *
 *                                                                                              *
 *                                                                                              *
 *                                                                                              *
 *                                                                                              *
 ************************************************************************************************/

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This provides reading json from http response,parse it into json object.
 *
 * @author Gamal Shaban
 */
public final class HttpJSONReader {

    /**
     * read from the Reader object.
     *
     * @param rd
     * @return
     * @throws IOException
     */
    protected static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * read the url and passes the response as String.
     *
     * @param url
     * @return
     */
    public static String readOnly(String url) throws MalformedURLException, IOException {
        
        InputStream is = new URL(url).openStream();
        //surround the statments with try and finally so make sure that inputstream will be closed.
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return jsonText;
        } finally {
            is.close();
        }


    }

    /**
     * reads the url and passes the response as json object.
     *
     * @param url
     * @return
     */
    public static JSONObject readAndParse(String url) throws JSONException, MalformedURLException, IOException  {
        //call the readOnly method and parse the json.
        JSONObject json = new JSONObject(readOnly(url));
        return json;
    }

    /**
     * parse a json string and returns the json object representation.
     *
     * @param jsonString
     * @return
     */
    public static JSONObject parse(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }
}
