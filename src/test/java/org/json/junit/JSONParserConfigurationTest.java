package org.json.junit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONParserConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

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
    public void givenInvalidInputArrays_testStrictModeTrue_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        List<String> strictModeInputTestCases = getNonCompliantJSONList();

        // this is a lot easier to debug when things stop working
        for (int i = 0; i < strictModeInputTestCases.size(); ++i) {
            String testCase = strictModeInputTestCases.get(i);
            try {
                JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
                String s = jsonArray.toString();
                System.out.println("Expected an exception, but got: " + s + " Noncompliant Array index: " + i);
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
    public void givenValidDoubleArray_testStrictModeTrue_shouldNotThrowJsonException() {
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
    public void givenValidEmptyArrayInsideArray_testStrictModeTrue_shouldNotThrowJsonException(){
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[[]]";

        JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);

        assertEquals(testCase, jsonArray.toString());
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
    public void givenInvalidString_testStrictModeTrue_shouldThrowJsonException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[badString]";

        JSONException je = assertThrows(JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));

        assertEquals("Value 'badString' is not surrounded by quotes at 10 [character 11 line 1]", je.getMessage());
    }

    @Test
    public void allowNullInStrictMode() {
        String expected = "[null]";
        JSONArray jsonArray = new JSONArray(expected, new JSONParserConfiguration().withStrictMode(true));
        assertEquals(expected, jsonArray.toString());
    }

    @Test
    public void shouldHandleNumericArray() {
        String expected = "[10]";
        JSONArray jsonArray = new JSONArray(expected, new JSONParserConfiguration().withStrictMode(true));
        assertEquals(expected, jsonArray.toString());
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
    public void givenInvalidInputArrays_testStrictModeFalse_shouldNotThrowAnyException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);

        List<String> strictModeInputTestCases = getNonCompliantJSONList();

        // this is a lot easier to debug when things stop working
        for (int i = 0; i < strictModeInputTestCases.size(); ++i) {
            String testCase = strictModeInputTestCases.get(i);
            try {
                JSONArray jsonArray = new JSONArray(testCase, jsonParserConfiguration);
            } catch (Exception e) {
                System.out.println("Unexpected exception: " + e.getMessage() + " Noncompliant Array index: " + i);
            }
        }
        strictModeInputTestCases.forEach(testCase -> new JSONArray(testCase, jsonParserConfiguration));
    }

    @Test
    public void givenInvalidInputArray_testStrictModeTrue_shouldThrowInvalidCharacterErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[1,2];[3,4]";

        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));

        assertEquals("invalid character ';' found after end of array at 6 [character 7 line 1]", je.getMessage());
    }

    @Test
    public void givenInvalidInputArrayWithNumericStrings_testStrictModeTrue_shouldThrowInvalidCharacterErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[\"1\",\"2\"];[3,4]";

        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));

        assertEquals("invalid character ';' found after end of array at 10 [character 11 line 1]", je.getMessage());
    }

    @Test
    public void givenInvalidInputArray_testStrictModeTrue_shouldThrowValueNotSurroundedByQuotesErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[{\"test\": implied}]";

        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));

        assertEquals("Value 'implied' is not surrounded by quotes at 17 [character 18 line 1]", je.getMessage());
    }

    @Test
    public void givenInvalidInputArray_testStrictModeFalse_shouldNotThrowAnyException() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(false);

        String testCase = "[{\"test\": implied}]";

        new JSONArray(testCase, jsonParserConfiguration);
    }

    @Test
    public void givenNonCompliantQuotes_testStrictModeTrue_shouldThrowJsonExceptionWithConcreteErrorDescription() {
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
                "Single quote wrap not allowed in strict mode at 2 [character 3 line 1]",
                jeTwo.getMessage());
        assertEquals(
                "Single quote wrap not allowed in strict mode at 2 [character 3 line 1]",
                jeThree.getMessage());
        assertEquals(
                "Single quote wrap not allowed in strict mode at 3 [character 4 line 1]",
                jeFour.getMessage());
    }

    @Test
    public void givenUnbalancedQuotes_testStrictModeFalse_shouldThrowJsonException() {
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
    public void givenInvalidInputArray_testStrictModeTrue_shouldThrowKeyNotSurroundedByQuotesErrorMessage() {
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
                .withStrictMode(true);

        String testCase = "[{test: implied}]";
        JSONException je = assertThrows("expected non-compliant array but got instead: " + testCase,
                JSONException.class, () -> new JSONArray(testCase, jsonParserConfiguration));

        assertEquals("Value 'test' is not surrounded by quotes at 6 [character 7 line 1]", je.getMessage());
    }

    /**
     * This method contains short but focused use-case samples and is exclusively used to test strictMode unit tests in
     * this class.
     *
     * @return List with JSON strings.
     */
    private List<String> getNonCompliantJSONList() {
        return Arrays.asList(
                "[1],",
                "[1,]",
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
}
