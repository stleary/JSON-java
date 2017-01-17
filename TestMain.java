package org.json;
/**
 * test JSONObjectKeyOrder
 * @author wangq
 *
 */
public class TestMain {
public static void main(String[] args) {
	JSONObject jsonObject = new JSONObject("{a:1,d:{z:2,a:2},b:3}");
	System.out.println(jsonObject);
	
	JSONObjectKeyOrder jsonObject1 = new JSONObjectKeyOrder("{a:1,d:{z:2,a:2},b:3}");
	print(jsonObject1,"");
}

/**
 *a
 *d
 *	z
 *	a
 *b
 * @param jsonObject
 * @param index
 */
static void print(JSONObjectKeyOrder jsonObject,String index){
	String[] names = JSONObjectKeyOrder.getNames(jsonObject);
	for (String string : names) {
		System.out.println(index+string);
		if(jsonObject.optJSONObject(string)!=null){
			print((JSONObjectKeyOrder) jsonObject.optJSONObject(string),index+"  ");
		}
		
	}
}
}
