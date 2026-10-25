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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
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
    public void objectConstructorAppliesExplicitStrictModeToValues() {
        JSONParserConfiguration configuration = new JSONParserConfiguration().withStrictMode();
        JSONParserConfiguration lenient = new JSONParserConfiguration().withStrictMode(false);
        for (String source : Arrays.asList(
                "[]",
                "{",
                "{} trailing",
                "{\"value\":'text'}",
                "{\"value\":[1,]}",
                "{\"value\":{\"nested\":1,}}")) {
            JSONTokener tokener = new JSONTokener(source, lenient);
            assertThrows(JSONException.class, () -> new JSONObject(tokener, configuration));
            assertSame(lenient, tokener.getJsonParserConfiguration());
        }
    }

    @Test
    public void arrayConstructorAppliesExplicitStrictModeToValues() {
        JSONParserConfiguration configuration = new JSONParserConfiguration().withStrictMode();
        JSONParserConfiguration lenient = new JSONParserConfiguration().withStrictMode(false);
        for (String source : Arrays.asList(
                "{}",
                "[",
                "[] trailing",
                "['text']",
                "[[1,]]",
                "[{\"nested\":1,}]")) {
            JSONTokener tokener = new JSONTokener(source, lenient);
            assertThrows(JSONException.class, () -> new JSONArray(tokener, configuration));
            assertSame(lenient, tokener.getJsonParserConfiguration());
        }
    }

    @Test
    public void explicitConfigurationOverridesTokenerStrictMode() {
        JSONParserConfiguration strict = new JSONParserConfiguration().withStrictMode();
        JSONParserConfiguration lenient = new JSONParserConfiguration().withStrictMode(false);
        JSONTokener objectTokener = new JSONTokener("{\"value\":[{'nested':'text',}], 'other':'text'} 'next'", strict);
        JSONTokener arrayTokener = new JSONTokener("[{\"value\":['text',]},'text'] 'next'", strict);

        JSONObject object = new JSONObject(objectTokener, lenient);
        JSONArray array = new JSONArray(arrayTokener, lenient);

        assertEquals("text", object.getJSONArray("value").getJSONObject(0).getString("nested"));
        assertEquals("text", array.getJSONObject(0).getJSONArray("value").getString(0));
        assertEquals("text", object.getString("other"));
        assertEquals("text", array.getString(1));
        assertSame(strict, objectTokener.getJsonParserConfiguration());
        assertSame(strict, arrayTokener.getJsonParserConfiguration());
        assertThrows(JSONException.class, () -> objectTokener.nextValue());
        assertThrows(JSONException.class, () -> arrayTokener.nextValue());
    }

    @Test
    public void objectConstructorRestoresTokenerConfiguration() {
        JSONParserConfiguration original = new JSONParserConfiguration().withStrictMode(false);
        JSONParserConfiguration strict = new JSONParserConfiguration().withStrictMode();
        for (String source : Arrays.asList("{}", "{\"value\":[{\"nested\":\"text\"}],\"other\":true}")) {
            JSONTokener tokener = new JSONTokener(source, original);
            new JSONObject(tokener, strict);
            assertSame(original, tokener.getJsonParserConfiguration());
        }
    }

    @Test
    public void arrayConstructorRestoresTokenerConfiguration() {
        JSONParserConfiguration original = new JSONParserConfiguration().withStrictMode(false);
        JSONParserConfiguration strict = new JSONParserConfiguration().withStrictMode();
        for (String source : Arrays.asList("[]", "[{\"value\":[\"text\"]},true]")) {
            JSONTokener tokener = new JSONTokener(source, original);
            new JSONArray(tokener, strict);
            assertSame(original, tokener.getJsonParserConfiguration());
        }
    }

    @Test
    public void explicitConfigurationAppliesToNestedDuplicateKeys() {
        JSONParserConfiguration configuration = new JSONParserConfiguration().withOverwriteDuplicateKey(true);
        JSONObject object = new JSONObject(new JSONTokener("{\"value\":[{\"key\":1,\"key\":2}]}"), configuration);
        JSONArray array = new JSONArray(new JSONTokener("[{\"value\":{\"key\":1,\"key\":2}}]"), configuration);

        assertEquals(2, object.getJSONArray("value").getJSONObject(0).getInt("key"));
        assertEquals(2, array.getJSONObject(0).getJSONObject("value").getInt("key"));
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

    private static final String SHARED_KEYS_SOURCE = "{\"rows\": ["
            + "{\"name\": \"a\", \"count\": 1, \"big\": 12345678901, \"ratio\": 1.5, \"flag\": true, \"missing\": null,"
            + " \"nested\": {\"name\": \"inner\", \"list\": [1, \"name\", null]}},"
            + "{\"name\": \"name\", \"count\": 2, \"nested\": {\"name\": \"inner2\", \"list\": []}},"
            + "{\"name\": \"third\", \"count\": -3}"
            + "]}";

    @Test
    public void testMaxSharedKeysConfig() {
        JSONParserConfiguration config = new JSONParserConfiguration();
        assertEquals(JSONParserConfiguration.DEFAULT_MAXIMUM_SHARED_KEYS, config.getMaxSharedKeys());
        assertEquals(1000, config.getMaxSharedKeys());
        assertEquals(0, config.withMaxSharedKeys(0).getMaxSharedKeys());
        assertEquals(JSONParserConfiguration.UNLIMITED_SHARED_KEYS, config.withMaxSharedKeys(-1).getMaxSharedKeys());
        assertEquals(JSONParserConfiguration.UNLIMITED_SHARED_KEYS, config.withMaxSharedKeys(-5).getMaxSharedKeys());

        JSONParserConfiguration chained = config.withMaxSharedKeys(7).withOverwriteDuplicateKey(true).withStrictMode(false);
        assertEquals(7, chained.getMaxSharedKeys());
        assertTrue(chained.isOverwriteDuplicateKey());
        assertEquals(1000, config.getMaxSharedKeys());
    }

    @Test
    public void testSharedKeysByDefault() {
        JSONArray rows = new JSONObject(SHARED_KEYS_SOURCE).getJSONArray("rows");
        String nameKey = key(rows.getJSONObject(0), "name");
        for (int i = 1; i < rows.length(); i++) {
            assertSame(nameKey, key(rows.getJSONObject(i), "name"));
        }
        assertSame(nameKey, key(rows.getJSONObject(0).getJSONObject("nested"), "name"));
        assertSame(key(rows.getJSONObject(0), "count"), key(rows.getJSONObject(2), "count"));
        assertSame(key(rows.getJSONObject(0).getJSONObject("nested"), "list"),
                key(rows.getJSONObject(1).getJSONObject("nested"), "list"));

        // string values equal to a key are not shared
        assertNotSame(nameKey, rows.getJSONObject(1).get("name"));
        assertNotSame(nameKey, rows.getJSONObject(0).getJSONObject("nested").getJSONArray("list").get(1));
    }

    @Test
    public void testSharedKeysDisabled() {
        JSONArray rows = new JSONObject(SHARED_KEYS_SOURCE, new JSONParserConfiguration().withMaxSharedKeys(0))
                .getJSONArray("rows");
        assertNotSame(key(rows.getJSONObject(0), "name"), key(rows.getJSONObject(1), "name"));
        assertNotSame(key(rows.getJSONObject(0), "count"), key(rows.getJSONObject(1), "count"));
    }

    @Test
    public void testSharedKeysLimit() {
        JSONArray rows = new JSONArray("[{\"a\": 1, \"b\": 2, \"c\": 3}, {\"a\": 4, \"b\": 5, \"c\": 6}]",
                new JSONParserConfiguration().withMaxSharedKeys(2));
        assertSame(key(rows.getJSONObject(0), "a"), key(rows.getJSONObject(1), "a"));
        assertSame(key(rows.getJSONObject(0), "b"), key(rows.getJSONObject(1), "b"));
        assertNotSame(key(rows.getJSONObject(0), "c"), key(rows.getJSONObject(1), "c"));
    }

    @Test
    public void testSharedKeysUnlimited() {
        int keyCount = JSONParserConfiguration.DEFAULT_MAXIMUM_SHARED_KEYS + 500;
        StringBuilder row = new StringBuilder("{");
        for (int i = 0; i < keyCount; i++) {
            row.append(i == 0 ? "" : ",").append("\"k").append(i).append("\":").append(i);
        }
        row.append('}');
        String source = "[" + row + "," + row + "]";

        JSONArray unlimited = new JSONArray(source,
                new JSONParserConfiguration().withMaxSharedKeys(JSONParserConfiguration.UNLIMITED_SHARED_KEYS));
        for (int i = 0; i < keyCount; i++) {
            assertSame(key(unlimited.getJSONObject(0), "k" + i), key(unlimited.getJSONObject(1), "k" + i));
        }

        JSONArray limited = new JSONArray(source);
        String lastKey = "k" + (keyCount - 1);
        assertSame(key(limited.getJSONObject(0), "k0"), key(limited.getJSONObject(1), "k0"));
        assertNotSame(key(limited.getJSONObject(0), lastKey), key(limited.getJSONObject(1), lastKey));
        assertTrue(unlimited.similar(limited));
    }

    @Test
    public void testSharedKeysNotSharedAcrossParses() {
        JSONObject first = new JSONObject("{\"name\": 1}");
        JSONObject second = new JSONObject("{\"name\": 2}");
        assertNotSame(key(first, "name"), key(second, "name"));
    }

    @Test
    public void testSharedKeysParseUnchanged() {
        JSONObject unshared = new JSONObject(SHARED_KEYS_SOURCE, new JSONParserConfiguration().withMaxSharedKeys(0));
        JSONObject shared = new JSONObject(SHARED_KEYS_SOURCE);
        JSONObject unlimited = new JSONObject(SHARED_KEYS_SOURCE,
                new JSONParserConfiguration().withMaxSharedKeys(JSONParserConfiguration.UNLIMITED_SHARED_KEYS));
        assertTrue(unshared.similar(shared));
        assertTrue(unshared.similar(unlimited));
        assertEquals(unshared.toString(), shared.toString());
        assertEquals(unshared.toString(), unlimited.toString());

        JSONObject row = shared.getJSONArray("rows").getJSONObject(0);
        assertEquals(Integer.class, row.get("count").getClass());
        assertEquals(Long.class, row.get("big").getClass());
        assertEquals(java.math.BigDecimal.class, row.get("ratio").getClass());
        assertEquals(Boolean.TRUE, row.get("flag"));
        assertSame(JSONObject.NULL, row.get("missing"));
        assertSame(JSONObject.NULL, row.getJSONObject("nested").getJSONArray("list").get(2));
        assertEquals("third", shared.getJSONArray("rows").getJSONObject(2).get("name"));
    }

    @Test
    public void testSharedKeysLenientSyntax() {
        JSONArray rows = new JSONArray("[{\"name\": 1, \"count\": 2}, {'name': 'single', count: 3}]",
                new JSONParserConfiguration().withStrictMode(false));
        assertSame(key(rows.getJSONObject(0), "name"), key(rows.getJSONObject(1), "name"));
        assertSame(key(rows.getJSONObject(0), "count"), key(rows.getJSONObject(1), "count"));
        assertEquals("single", rows.getJSONObject(1).get("name"));
    }

    @Test
    public void testSharedKeysStrictMode() {
        JSONArray rows = new JSONArray("[{\"a\": 1}, {\"a\": 2}]", new JSONParserConfiguration().withStrictMode());
        assertSame(key(rows.getJSONObject(0), "a"), key(rows.getJSONObject(1), "a"));
        assertFalse(rows.getJSONObject(0).similar(rows.getJSONObject(1)));
    }

    /**
     * Returns the key instance a JSONObject holds, rather than the caller's equal String.
     */
    private static String key(JSONObject jsonObject, String name) {
        for (String key : jsonObject.keySet()) {
            if (key.equals(name)) {
                return key;
            }
        }
        fail("missing key " + name);
        return null;
    }

}
