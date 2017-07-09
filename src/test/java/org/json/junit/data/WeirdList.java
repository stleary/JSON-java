/**
 * 
 */
package org.json.junit.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author John Aylward
 */
public class WeirdList {
    /** */
    private final List<Integer> list = new ArrayList<>();

    public WeirdList(Integer... vals) {
        this.list.addAll(Arrays.asList(vals));
    }

    /** gets a copy of the list */
    public List<Integer> get() {
        return new ArrayList<>(this.list);
    }

    /** gets a copy of the list */
    public List<Integer> getALL() {
        return new ArrayList<>(this.list);
    }

    /** get an index */
    public Integer get(int i) {
        return this.list.get(i);
    }

    /** get an index */
    public int getInt(int i) {
        return this.list.get(i);
    }

    /** adds a new value to the end of the list */
    public void add(Integer value) {
        this.list.add(value);
    }
}