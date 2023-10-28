
import java.util.*;

import org.json.*;

public class Main {

	/**
	 * Construction.
	 * 
	 * @param arguments command line arguments
	 * @throws Exception
	 */
	public Main(String[] arguments) throws Exception {

		test1();
		JSONObject.setOrdered(true);
		test1();
	}

	/**
	 * Main entry.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {

		try {
			System.out.println("JSON java starting ...\n");
			Main main = new Main(args);
		} catch (Exception e) {
			System.out.println("\n*****  Exception caught, exit: " + e);
			e.printStackTrace();
			System.exit(2); // needed for GUI
		}
	}
	
    public void test1() {
    	
        final String string1 = "HasSameRef";
        final String string2 = "HasDifferentRef";
        JSONObject obj1 = new JSONObject()
                .put("key11", "abc")
                .put("key2", 2)
                .put("key3", string1);
        
        JSONObject obj2 = new JSONObject()
                .put("key21", "abc")
                .put("key2", 3)
                .put("key3", string1);

        JSONObject obj3 = new JSONObject()
                .put("key31", "abc")
                .put("key2", 2)
                .put("key3", new String(string1));
        
        JSONObject obj4 = new JSONObject()
                .put("key41", "abc")
                .put("key2", 2.0)
                .put("key3", new String(string1));

        JSONObject obj5 = new JSONObject()
                .put("key51", "abc")
                .put("key2", 2.0)
                .put("key3", new String(string2));
        JSONObject first = new JSONObject("{\"a\": 1, \"b\": 2, \"c\": 3}");
        JSONObject second = new JSONObject("{\"a\": 1, \"b\": 2.0, \"c\": 4}");
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>(
                Arrays.asList(obj1, obj2, obj3, obj4, obj5)
        );
        JSONObject test = new JSONObject()
        		.put("obj1", obj1)
        		.put("obj2", obj2)
        		.put("obj3", obj3)
        		.put("first", first)
        		.put("second", second)
        		.put("jsonObjects", jsonObjects);
        
        System.out.println(test.toString(2));
    }
}
