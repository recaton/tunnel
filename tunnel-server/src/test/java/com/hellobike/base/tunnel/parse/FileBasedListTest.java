package com.hellobike.base.tunnel.parse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

public class FileBasedListTest {

    FileBasedList<Integer> list;

    @Before
    public void before() {
        list = new FileBasedList<>(2);
        list.addAll(Arrays.asList(101, 202, 303));
    }

    @Test
    public void testAddAll() {
        Iterator<Integer> it = list.iterator();
        int seq = 0;
        while (it.hasNext()) {
            int v = it.next();
            switch (seq) {
                case 0: Assert.assertEquals(101, v); break;
                case 1: Assert.assertEquals(202, v); break;
                case 2: Assert.assertEquals(303, v); break;
            }
            seq++;
        }
    }

    @Test
    public void testIterator() {
        list = new FileBasedList<>(2);
        Assert.assertFalse(list.iterator().hasNext());
        list.add(7);
        Assert.assertTrue(list.iterator().hasNext());
    }

    @Test
    public void testAdd() {
        list.add(404);
        list.addAll(Arrays.asList(505, 606));
        list.add(707);
        Iterator<Integer> it = list.iterator();
        int seq = 0;
        while (it.hasNext()) {
            int v = it.next();
            switch (seq) {
                case 3: Assert.assertEquals(404, v); break;
                case 5: Assert.assertEquals(606, v); break;
                case 6: Assert.assertEquals(707, v); break;
            }
            seq++;
        }
    }

    @Test
    public void testContains() {
        Assert.assertTrue(list.contains(101));
        Assert.assertTrue(list.contains(303));
        Assert.assertFalse(list.contains(404));
    }


    @Test
    public void testClear() {
        list.clear();
        Assert.assertEquals(0, list.size());
        Assert.assertTrue(list.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testContainsAll() {
        FileBasedList<Integer> collection = new FileBasedList<>();
        collection.containsAll(Arrays.asList(202, 101));
    }


}
