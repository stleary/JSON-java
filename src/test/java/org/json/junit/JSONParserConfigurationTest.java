package org.json.junit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONParserConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
    public void givenInvalidInputArrays_testStrictModeTrue_shouldThrowJsonException() {
        List<String> strictModeInputTestCases = getNonCompliantJSONList();
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
            .withStrictMode(true);

        strictModeInputTestCases.forEach(
            testCase -> assertThrows("expected non-compliant array but got instead: " + testCase, JSONException.class,
                () -> new JSONArray(testCase, jsonParserConfiguration)));
    }

    @Test
    public void givenInvalidInputArrays_testStrictModeFalse_shouldNotThrowAnyException() {
        List<String> strictModeInputTestCases = getNonCompliantJSONList();
        JSONParserConfiguration jsonParserConfiguration = new JSONParserConfiguration()
            .withStrictMode(false);

        strictModeInputTestCases.forEach(testCase -> new JSONArray(testCase, jsonParserConfiguration));
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

    private List<String> getNonCompliantJSONList() {
        return Arrays.asList(
            "[1,2];[3,4]",
            "[1, 2,3]:[4,5]",
            "[{test: implied}]",
            "[{\"test\": implied}]",
            "[{\"number\":\"7990154836330\",\"color\":'c'},{\"number\":8784148854580,\"color\":RosyBrown},{\"number\":\"5875770107113\",\"color\":\"DarkSeaGreen\"}]",
            "[{test: \"implied\"}]");
    }
}
