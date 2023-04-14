#JSONDivider Usage Manual

You can divide your JSON string with only one line bellow with JSONDivider.

#### List<String> result = JSONDivider.disassembleJsonStr(jsonStr, 100000, true);

- p1 : jsonStr - First parameter is your original JSON string.
- p2 : 100000 - Second parameter is result divided JSON string's MAX_SIZE bytes length.
- p3 : true - Third parameter is whether you get logs or not.
- p4 : List<String> result - It returns one or more strings divided from your original JSON string.


#### There are three requirements to use JSONDivider

- First, the JSONArray's key must be a singular noun.  
- Second, JSONArray must have its upper JSONObject which key is a plural noun of JSONArray's key.
- Third, the outermost JSONArray's key must be a plural noun.

For example,
These JSONObject datasets are would be allowed.
- {"items":{"Products":{"Product":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}}}
- {"items":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}

These JSONObject datasets are would not be allowed.
- {"items":{"Products":{"Products":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}}}
- {"items":{"Product":{"Product":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}}}
- {"items":{"Products":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}}
- {"items":{"Product":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}}
- {"item":[{"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}, {"key1":"value1", "key2":"value2"}]}

If you follow three requirements above in your JSON structure,
you can get List of divided JSON string under MAX_SIZE you want.

You can get JSON string List of original structure but contents inside are divided.

### Please test with sample json dataset below.
- #### docs/JSONDivider/AItems.json
- #### docs/JSONDivider/BItems.json
