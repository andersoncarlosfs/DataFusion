/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lsdfusion
 */
public class DisjointSetTest {

    private static final DisjointSet<Object> instance = new DisjointSet();

    private static final Object representative = 7;

    private static final Object subject = 8;

    private static final Object object = 9;

    private static final Collection equivaleceClass = Arrays.asList(7, 8);

    private static final Collection equivaleceClasses = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6), Arrays.asList(7, 8), Arrays.asList(9, 0));

    public DisjointSetTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance.add(0);
        instance.add(1);
        instance.add(2);
        instance.add(3);
        instance.add(4);
        instance.add(5);
        instance.add(6);
        instance.add(7);
        instance.add(8);
        instance.add(9);
        /*
        instance.add(null);
        instance.add(10);
        instance.add(11);
         */
        instance.union(3, 4);
        instance.union(7, 8);
        instance.union(9, 0);
        /*
        instance.union(null, 10);
        instance.union(10, 11);
         */
    }

    @After
    public void tearDown() {
        instance.clear();
        assertTrue(instance.isEmpty());
    }

    /**
     * Test of representative method, of class DisjointSet.
     */
    @Test
    public void testRepresentative() {
        System.out.println("representative");
        assertEquals(null, instance.representative(15));
    }

    /**
     * Test of find method, of class DisjointSet.
     */
    @Test
    public void testFind() {
        System.out.println("find");
        assertArrayEquals(equivaleceClass.toArray(), instance.find(subject).toArray());
    }

    /**
     * Test of remove method, of class DisjointSet.
     */
    //@Test
    public void testRemove() {
        System.out.println("remove");
    }

    /**
     * Test of remove separate, of class DisjointSet.
     */
    @Test
    public void testSeparate() {
        System.out.println("separate");
        instance.union(subject, object);
        System.out.println(instance.disjointValues());
        assertTrue(instance.separate(representative));
        System.out.println(instance.disjointValues());

    }

    /**
     * Test of disjointValues method, of class DisjointSet.
     */
    //@Test
    public void testDisjointValues() {
        System.out.println("disjointValues");
        assertArrayEquals(equivaleceClasses.toArray(), instance.disjointValues().toArray());
    }

    /**
     *
     */
    //@Test
    public void testCustom() {

    }

}
