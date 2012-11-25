package org.json;
/*
By: Alexander Forselius <drsounds@gmail.com>
Copyright (c) 2002-2012 JSON.org (contribution by Alexander Forselius)

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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/***
 * A simple JSON serializer
 * @author drsou_000
 *
 */
public class JSONSerializer {
    /**
    Strip away get from accessor
    **/
    private String stripAccessor(String name) {
        return name.substring(3).toLowerCase(Locale.US);
    }
    /**
    Make the first letters uppercase. from {@link http://stackoverflow.com/questions/1149855/how-to-upper-case-every-first-letter-of-word-in-a-string}
    **/
    private String radiate(String s) {
        final StringBuilder result = new StringBuilder(s.length());
        String[] words = s.split("\\s");
        for(int i=0,l=words.length;i<l;++i) {
          if(i>0) result.append(" ");      
          result.append(Character.toUpperCase(words[i].charAt(0)))
                .append(words[i].substring(1));

        }
        return result.toString();
    }
	/***
    Serialize Any class into a JSON object
    ***/
    public JSONObject deserialize(Object object) {
        JSONObject newObject = new JSONObject();
        Field[] fields = object.getClass().getDeclaredFields();
        for(int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            try {
                String name = field.getName();
                if(name.startsWith("this"))
                    continue;
                String newName = radiate(field.getName());
                //newObject.put(method.getName(),  method.invoke(object));
                Class<?> _class = field.getType();
                Method method = object.getClass().getMethod("get" + newName);
                Object member = method.invoke(object);
                if(member.getClass().isArray()) {
                    JSONArray array = new JSONArray();
                    Object[] collection = (Object[])member;
                    for(Object _object: collection) {
                        if(_object instanceof String || _object instanceof Float || _object instanceof Float || _object instanceof Integer) {
                            array.put(object);
                        } else if(_object instanceof Object) {
                            array.put(this.deserialize(_object));
                        } else {
                            array.put(_object);
                        }
                    } 
                    newObject.put(name, array);
                } else if(member instanceof String || member instanceof Float || member instanceof Float || member instanceof Integer) {
                    newObject.put(name, member);
                } else if(member instanceof Object) {
                    newObject.put(name, this.deserialize(member));
                } else {
                    newObject.put(name, member);
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return newObject;
    }
}
