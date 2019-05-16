package com.hellobike.base.tunnel.parse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

public class FileBasedCollectionTest {

    FileBasedList<Integer> collection;

    @Before
    public void before() {
        collection = new FileBasedList<>(2);
        collection.addAll(Arrays.asList(101, 202, 303));
    }

    @Test
    public void testAddAll() {
        Iterator<Integer> it = collection.iterator();
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
        collection = new FileBasedList<>(2);
        Assert.assertFalse(collection.iterator().hasNext());
        collection.add(7);
        Assert.assertTrue(collection.iterator().hasNext());
    }

    @Test
    public void testAdd() {
        collection.add(404);
        collection.addAll(Arrays.asList(505, 606));
        collection.add(707);
        Iterator<Integer> it = collection.iterator();
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
        Assert.assertTrue(collection.contains(101));
        Assert.assertTrue(collection.contains(303));
        Assert.assertFalse(collection.contains(404));
    }


    @Test
    public void testClear() {
        collection.clear();
        Assert.assertEquals(0, collection.size());
        Assert.assertTrue(collection.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testContainsAll() {
        FileBasedList<Integer> collection = new FileBasedList<>();
        collection.containsAll(Arrays.asList(202, 101));
    }


}
