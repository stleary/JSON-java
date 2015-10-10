package org.json;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class JSONArrayTest {

	@Test
	public void emptyStringCollection() {
		final Collection<String> strings = new ArrayList<String>();
		final JSONArray array = new JSONArray(strings);
		assertEquals("The array is not empty", 0, array.length());
	}
}
