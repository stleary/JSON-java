package org.json.junit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONParserConfiguration;
import org.json.JSONTokener;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JSONParserConfigurationTest {
    private static final String TEST_SOURCE = "{\"key\": \"value1\", \"key\": \"value2\"}";

    @Test(expected = JSONException.class)
    public void testThrowException() {
        new JSONObject(TEST_SOURCE);
    }

    @Test
    public void testOverwrite() {
        JSONObject jsonObject = new JSONObject(TEST_SOURCE,
                new JSONParserConfiguration().withOverwriteDuplicateKey(true));

        assertEquals("duplicate key should be overwritten", "value2", jsonObject.getString("key"));
    }

    @Test
    public void strictModeIsCloned(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true)
                .withMaxNestingDepth(12);

        assertTrue(jsonParserConfiguration.isStrictMode());
    }

    @Test
    public void maxNestingDepthIsCloned(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .<JSONParserConfiguration>withKeepStrings(true)
                .withStrictMode(true);

        assertTrue(jsonParserConfiguration.isKeepStrings());
    }
    
    @Test
    public void useNativeNullsIsCloned() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withUseNativeNulls(true)
                .withStrictMode(true);
        assertTrue(jsonParserConfiguration.isUseNativeNulls());
    }

    @Test
    public void verifyDuplicateKeyThenMaxDepth() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withOverwriteDuplicateKey(true)
                .withMaxNestingDepth(42);

        assertEquals(42, jsonParserConfiguration.getMaxNestingDepth());
        assertTrue(jsonParserConfiguration.isOverwriteDuplicateKey());
    }

    @Test
    public void verifyMaxDepthThenDuplicateKey() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withMaxNestingDepth(42)
                .withOverwriteDuplicateKey(true);

        assertTrue(jsonParserConfiguration.isOverwriteDuplicateKey());
        assertEquals(42, jsonParserConfiguration.getMaxNestingDepth());
    }

    @Test
    public void givenInvalidInput_testStrictModeTrue_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        List<String> strictModeInputTestCases = getNonCompliantJSONArrayList();
        // this is a lot easier to debug when things stop working
        for (int i = 0; i < strictModeInputTestCases.size(); ++i) {
            String testCase = strictModeInputTestCases.get(i);
            try {
                JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
                String s = jsonArray.toString();
                String msg = "Expected an exception, but got: " + s + " Noncompliant Array index: " + i;
                fail(msg);
            } catch (Exception e) {
                // its all good
            }
        }
    }

    @Test
    public void givenInvalidInputObjects_testStrictModeTrue_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        List<String> strictModeInputTestCases = getNonCompliantJSONObjectList();
        // this is a lot easier to debug when things stop working
        for (int i = 0; i < strictModeInputTestCases.size(); ++i) {
            String testCase = strictModeInputTestCases.get(i);
            try {
                JSONObject jsonObject = new JSONObject(testCase, jsonParserConfiguration);
                String s = jsonObject.toString();
                String msg = "Expected an exception, but got: " + s + " Noncompliant Array index: " + i;
                fail(msg);
            } catch (Exception e) {
                // its all good
            }
        }
    }

    @Test
    public void givenEmptyArray_testStrictModeTrue_shouldNotThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "[]";
        JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
        assertEquals(testCase, jsonArray.toString());
    }

    @Test
    public void givenEmptyObject_testStrictModeTrue_shouldNotThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "{}";
        JSONObject jsonObject = new JSONObject(testCase, jsonParserConfiguration);
        assertEquals(testCase, jsonObject.toString());
    }

    @Test
    public void givenValidNestedArray_testStrictModeTrue_shouldNotThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[[\"c\"], [10.2], [true, false, true]]";

        JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
        JSONArray arrayShouldContainStringAt0 = jsonArray.getJSONArray(0);
        JSONArray arrayShouldContainNumberAt0 = jsonArray.getJSONArray(1);
        JSONArray arrayShouldContainBooleanAt0 = jsonArray.getJSONArray(2);

        assertTrue(arrayShouldContainStringAt0.get(0) instanceof String);
        assertTrue(arrayShouldContainNumberAt0.get(0) instanceof Number);
        assertTrue(arrayShouldContainBooleanAt0.get(0) instanceof Boolean);
    }

    @Test
    public void givenValidNestedObject_testStrictModeTrue_shouldNotThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "{\"a0\":[\"c\"], \"a1\":[10.2], \"a2\":[true, false, true]}";

        JSONObject jsonObject = new JSONObject(testCase, jsonParserConfiguration);
        JSONArray arrayShouldContainStringAt0 = jsonObject.getJSONArray("a0");
        JSONArray arrayShouldContainNumberAt0 = jsonObject.getJSONArray("a1");
        JSONArray arrayShouldContainBooleanAt0 = jsonObject.getJSONArray("a2");

        assertTrue(arrayShouldContainStringAt0.get(0) instanceof String);
        assertTrue(arrayShouldContainNumberAt0.get(0) instanceof Number);
        assertTrue(arrayShouldContainBooleanAt0.get(0) instanceof Boolean);
    }

    @Test
    public void givenValidEmptyArrayInsideArray_testStrictModeTrue_shouldNotThrowJsonException(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "[[]]";
        JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
        assertEquals(testCase, jsonArray.toString());
    }

    @Test
    public void givenValidEmptyArrayInsideObject_testStrictModeTrue_shouldNotThrowJsonException(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "{\"a0\":[]}";
        JSONObject jsonObject = new JSONObject(testCase, jsonParserConfiguration);
        assertEquals(testCase, jsonObject.toString());
    }

    @Test
    public void givenValidEmptyArrayInsideArray_testStrictModeFalse_shouldNotThrowJsonException(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);
        String testCase = "[[]]";
        JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
        assertEquals(testCase, jsonArray.toString());
    }

    @Test
    public void givenValidEmptyArrayInsideObject_testStrictModeFalse_shouldNotThrowJsonException(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);
        String testCase = "{\"a0\":[]}";
        JSONObject jsonObject = new JSONObject(testCase, jsonParserConfiguration);
        assertEquals(testCase, jsonObject.toString());
    }

    @Test
    public void givenInvalidStringArray_testStrictModeTrue_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "[badString]";
        JSONException je = assertThrows(JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Value 'badString' is not surrounded by quotes at 10 [character 11 line 1]",
                je.getMessage());
    }

    @Test
    public void givenInvalidStringObject_testStrictModeTrue_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "{\"a0\":badString}";
        JSONException je = assertThrows(JSONException.class, () -> new JSONObject(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Value 'badString' is not surrounded by quotes at 15 [character 16 line 1]",
                je.getMessage());
    }

    @Test
    public void allowNullArrayInStrictMode() {
        String expected = "[null]";
        JSONArray jsonArray = new JSONArray(expected, new JSONParserConfiguration().withStrictMode(true));
        assertEquals(expected, jsonArray.toString());
    }

    @Test
    public void allowNullObjectInStrictMode() {
        String expected = "{\"a0\":null}";
        JSONObject jsonObject = new JSONObject(expected, new JSONParserConfiguration().withStrictMode(true));
        assertEquals(expected, jsonObject.toString());
    }

    @Test
    public void shouldHandleNumericArray() {
        String expected = "[10]";
        JSONArray jsonArray = new JSONArray(expected, new JSONParserConfiguration().withStrictMode(true));
        assertEquals(expected, jsonArray.toString());
    }

    @Test
    public void shouldHandleNumericObject() {
        String expected = "{\"a0\":10}";
        JSONObject jsonObject = new JSONObject(expected, new JSONParserConfiguration().withStrictMode(true));
        assertEquals(expected, jsonObject.toString());
    }
    @Test
    public void givenCompliantJSONArrayFile_testStrictModeTrue_shouldNotThrowAnyException() throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/compliantJsonArray.json"))) {
            String compliantJsonArrayAsString = lines.collect(Collectors.joining());
            JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                    .withStrictMode(true);
            new JSONArray(compliantJsonArrayAsString, jsonParserConfiguration);
        }
    }

    @Test
    public void givenCompliantJSONObjectFile_testStrictModeTrue_shouldNotThrowAnyException() throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/compliantJsonObject.json"))) {
            String compliantJsonObjectAsString = lines.collect(Collectors.joining());
            JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                    .withStrictMode(true);

            new JSONObject(compliantJsonObjectAsString, jsonParserConfiguration);
        }
    }

    @Test
    public void givenInvalidInputArrays_testStrictModeFalse_shouldNotThrowAnyException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);

        List<String> strictModeInputTestCases = getNonCompliantJSONArrayList();

        // this is a lot easier to debug when things stop working
        for (int i = 0; i < strictModeInputTestCases.size(); ++i) {
            String testCase = strictModeInputTestCases.get(i);
            try {
                JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
            } catch (Exception e) {
                System.out.println("Unexpected exception: " + e.getMessage() + " Noncompliant Array index: " + i);
                fail(String.format("Noncompliant array index: %d", i));
            }
        }
    }

    @Test
    public void givenInvalidInputObjects_testStrictModeFalse_shouldNotThrowAnyException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);

        List<String> strictModeInputTestCases = getNonCompliantJSONObjectList();

        // this is a lot easier to debug when things stop working
        for (int i = 0; i < strictModeInputTestCases.size(); ++i) {
            String testCase = strictModeInputTestCases.get(i);
            try {
                JSONObject jsonObject = new JSONObject(testCase, jsonParserConfiguration);
            } catch (Exception e) {
                System.out.println("Unexpected exception: " + e.getMessage() + " Noncompliant Array index: " + i);
                fail(String.format("Noncompliant array index: %d", i));
            }
        }
    }

    @Test
    public void givenInvalidInputArray_testStrictModeTrue_shouldThrowInvalidCharacterErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "[1,2];[3,4]";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Unparsed characters found at end of input text at 6 [character 7 line 1]",
                je.getMessage());
    }

    @Test
    public void givenInvalidInputObject_testStrictModeTrue_shouldThrowInvalidCharacterErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "{\"a0\":[1,2];\"a1\":[3,4]}";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONObject(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Invalid character ';' found at 12 [character 13 line 1]", je.getMessage());
    }

    @Test
    public void givenInvalidInputArrayWithNumericStrings_testStrictModeTrue_shouldThrowInvalidCharacterErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "[\"1\",\"2\"];[3,4]";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Unparsed characters found at end of input text at 10 [character 11 line 1]",
                je.getMessage());
    }

    @Test
    public void givenInvalidInputObjectWithNumericStrings_testStrictModeTrue_shouldThrowInvalidCharacterErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "{\"a0\":[\"1\",\"2\"];\"a1\":[3,4]}";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONObject(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Invalid character ';' found at 16 [character 17 line 1]", je.getMessage());
    }

    @Test
    public void givenInvalidInputArray_testStrictModeTrue_shouldThrowValueNotSurroundedByQuotesErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "[{\"test\": implied}]";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Value 'implied' is not surrounded by quotes at 17 [character 18 line 1]",
                je.getMessage());
    }

    @Test
    public void givenInvalidInputObject_testStrictModeTrue_shouldThrowValueNotSurroundedByQuotesErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);
        String testCase = "{\"a0\":{\"test\": implied}]}";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONObject(testCase, jsonParserConfiguration));
        assertEquals("Strict mode error: Value 'implied' is not surrounded by quotes at 22 [character 23 line 1]",
                je.getMessage());
    }

    @Test
    public void givenInvalidInputArray_testStrictModeFalse_shouldNotThrowAnyException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);
        String testCase = "[{\"test\": implied}]";
        new JSONArray(testCase, jsonParserConfiguration);
    }

    @Test
    public void givenInvalidInputObject_testStrictModeFalse_shouldNotThrowAnyException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);
        String testCase = "{\"a0\":{\"test\": implied}}";
        new JSONObject(testCase, jsonParserConfiguration);
    }

    @Test
    public void givenNonCompliantQuotesArray_testStrictModeTrue_shouldThrowJsonExceptionWithConcreteErrorDescription() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCaseOne = "[\"abc', \"test\"]";
        String testCaseTwo = "['abc\", \"test\"]";
        String testCaseThree = "['abc']";
        String testCaseFour = "[{'testField': \"testValue\"}]";

        JSONException jeOne = assertThrows(JSONException.class,
                () -> new JSONArray(testCaseOne, jsonParserConfiguration));
        JSONException jeTwo = assertThrows(JSONException.class,
                () -> new JSONArray(testCaseTwo, jsonParserConfiguration));
        JSONException jeThree = assertThrows(JSONException.class,
                () -> new JSONArray(testCaseThree, jsonParserConfiguration));
        JSONException jeFour = assertThrows(JSONException.class,
                () -> new JSONArray(testCaseFour, jsonParserConfiguration));

        assertEquals(
                "Expected a ',' or ']' at 10 [character 11 line 1]",
                jeOne.getMessage());
        assertEquals(
                "Strict mode error: Single quoted strings are not allowed at 2 [character 3 line 1]",
                jeTwo.getMessage());
        assertEquals(
                "Strict mode error: Single quoted strings are not allowed at 2 [character 3 line 1]",
                jeThree.getMessage());
        assertEquals(
                "Strict mode error: Single quoted strings are not allowed at 3 [character 4 line 1]",
                jeFour.getMessage());
    }

    @Test
    public void givenNonCompliantQuotesObject_testStrictModeTrue_shouldThrowJsonExceptionWithConcreteErrorDescription() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCaseOne = "{\"abc': \"test\"}";
        String testCaseTwo = "{'abc\": \"test\"}";
        String testCaseThree = "{\"a\":'abc'}";
        String testCaseFour = "{'testField': \"testValue\"}";

        JSONException jeOne = assertThrows(JSONException.class,
                () -> new JSONObject(testCaseOne, jsonParserConfiguration));
        JSONException jeTwo = assertThrows(JSONException.class,
                () -> new JSONObject(testCaseTwo, jsonParserConfiguration));
        JSONException jeThree = assertThrows(JSONException.class,
                () -> new JSONObject(testCaseThree, jsonParserConfiguration));
        JSONException jeFour = assertThrows(JSONException.class,
                () -> new JSONObject(testCaseFour, jsonParserConfiguration));

        assertEquals(
                "Expected a ':' after a key at 10 [character 11 line 1]",
                jeOne.getMessage());
        assertEquals(
                "Strict mode error: Single quoted strings are not allowed at 2 [character 3 line 1]",
                jeTwo.getMessage());
        assertEquals(
                "Strict mode error: Single quoted strings are not allowed at 6 [character 7 line 1]",
                jeThree.getMessage());
        assertEquals(
                "Strict mode error: Single quoted strings are not allowed at 2 [character 3 line 1]",
                jeFour.getMessage());
    }

    @Test
    public void givenUnbalancedQuotesArray_testStrictModeFalse_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);

        String testCaseOne = "[\"abc', \"test\"]";
        String testCaseTwo = "['abc\", \"test\"]";

        JSONException jeOne = assertThrows(JSONException.class,
                () -> new JSONArray(testCaseOne, jsonParserConfiguration));
        JSONException jeTwo = assertThrows(JSONException.class,
                () -> new JSONArray(testCaseTwo, jsonParserConfiguration));

        assertEquals("Expected a ',' or ']' at 10 [character 11 line 1]", jeOne.getMessage());
        assertEquals("Unterminated string. Character with int code 0 is not allowed within a quoted string. at 15 [character 16 line 1]", jeTwo.getMessage());
    }

    @Test
    public void givenUnbalancedQuotesObject_testStrictModeFalse_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);

        String testCaseOne = "{\"abc': \"test\"}";
        String testCaseTwo = "{'abc\": \"test\"}";

        JSONException jeOne = assertThrows(JSONException.class,
                () -> new JSONObject(testCaseOne, jsonParserConfiguration));
        JSONException jeTwo = assertThrows(JSONException.class,
                () -> new JSONObject(testCaseTwo, jsonParserConfiguration));

        assertEquals("Expected a ':' after a key at 10 [character 11 line 1]", jeOne.getMessage());
        assertEquals("Unterminated string. Character with int code 0 is not allowed within a quoted string. at 15 [character 16 line 1]", jeTwo.getMessage());
    }

    @Test
    public void givenInvalidInputArray_testStrictModeTrue_shouldThrowKeyNotSurroundedByQuotesErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[{test: implied}]";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));

        assertEquals("Strict mode error: Value 'test' is not surrounded by quotes at 6 [character 7 line 1]",
                je.getMessage());
    }

    @Test
    public void givenInvalidInputObject_testStrictModeTrue_shouldThrowKeyNotSurroundedByQuotesErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "{test: implied}";
        JSONException je = assertThrows("expected non-compliant json but got instead: " + testCase,
                JSONException.class, () -> new JSONObject(testCase, jsonParserConfiguration));

        assertEquals("Strict mode error: Value 'test' is not surrounded by quotes at 5 [character 6 line 1]",
                je.getMessage());
    }

    @Test
    public void givenInvalidInputObject_testStrictModeTrue_JSONObjectUsingJSONTokener_shouldThrowJSONException() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            new JSONObject(new JSONTokener("{\"key\":\"value\"} invalid trailing text"), new JSONParserConfiguration().withStrictMode(true));
        });

        assertEquals("Strict mode error: Unparsed characters found at end of input text at 17 [character 18 line 1]", exception.getMessage());
    }

    @Test
    public void givenInvalidInputObject_testStrictModeTrue_JSONObjectUsingString_shouldThrowJSONException() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            new JSONObject("{\"key\":\"value\"} invalid trailing text", new JSONParserConfiguration().withStrictMode(true));
        });
        assertEquals("Strict mode error: Unparsed characters found at end of input text at 17 [character 18 line 1]", exception.getMessage());
    }

    @Test
    public void givenInvalidInputObject_testStrictModeTrue_JSONArrayUsingJSONTokener_shouldThrowJSONException() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            new JSONArray(new JSONTokener("[\"value\"] invalid trailing text"), new JSONParserConfiguration().withStrictMode(true));
        });

        assertEquals("Strict mode error: Unparsed characters found at end of input text at 11 [character 12 line 1]", exception.getMessage());
    }

    @Test
    public void givenInvalidInputObject_testStrictModeTrue_JSONArrayUsingString_shouldThrowJSONException() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            new JSONArray("[\"value\"] invalid trailing text", new JSONParserConfiguration().withStrictMode(true));
        });
        assertEquals("Strict mode error: Unparsed characters found at end of input text at 11 [character 12 line 1]", exception.getMessage());
    }

    /**
     * This method contains short but focused use-case samples and is exclusively used to test strictMode unit tests in
     * this class.
     *
     * @return List with JSON strings.
     */
    private List<String> getNonCompliantJSONArrayList() {
        return Arrays.asList(
                "[1],",
                "[1,]",
                "[,]",
                "[,,]",
                "[[1],\"sa\",[2]]a",
                "[1],\"dsa\": \"test\"",
                "[[a]]",
                "[]asdf",
                "[]]",
                "[]}",
                "[][",
                "[]{",
                "[],",
                "[]:",
                "[],[",
                "[],{",
                "[1,2];[3,4]",
                "[test]",
                "[{'testSingleQuote': 'testSingleQuote'}]",
                "[1, 2,3]:[4,5]",
                "[{test: implied}]",
                "[{\"test\": implied}]",
                "[{\"number\":\"7990154836330\",\"color\":'c'},{\"number\":8784148854580,\"color\":RosyBrown},{\"number\":\"5875770107113\",\"color\":\"DarkSeaGreen\"}]",
                "[{test: \"implied\"}]");
    }

    /**
     * This method contains short but focused use-case samples and is exclusively used to test strictMode unit tests in
     * this class.
     *
     * @return List with JSON strings.
     */
    private List<String> getNonCompliantJSONObjectList() {
        return Arrays.asList(
                "{\"a\":1},",
                "{\"a\":1,}",
                "{\"a0\":[1],\"a1\":\"sa\",\"a2\":[2]}a",
                "{\"a\":1},\"dsa\": \"test\"",
                "{\"a\":[a]}",
                "{}asdf",
                "{}}",
                "{}]",
                "{}{",
                "{}[",
                "{},",
                "{}:",
                "{},{",
                "{},[",
                "{\"a0\":[1,2];\"a1\":[3,4]}",
                "{\"a\":test}",
                "{a:{'testSingleQuote': 'testSingleQuote'}}",
                "{\"a0\":1, \"a1\":2,\"a2\":3}:{\"a3\":4,\"a4\":5}",
                "{\"a\":{test: implied}}",
                "{a:{\"test\": implied}}",
                "{a:[{\"number\":\"7990154836330\",\"color\":'c'},{\"number\":8784148854580,\"color\":RosyBrown},{\"number\":\"5875770107113\",\"color\":\"DarkSeaGreen\"}]}",
                "{a:{test: \"implied\"}}"
        );
    }

}
