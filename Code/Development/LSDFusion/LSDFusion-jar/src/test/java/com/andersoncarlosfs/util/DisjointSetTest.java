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

    private static final DisjointSet_<Object> instance = new DisjointSet_();

    private static final Object representative = 7;
  
    private static final Object subject = 8;
    
    private static final Object object = 9;
    
    private static final Collection equivaleceClass = Arrays.asList(7, 8);
    
    private static final Collection equivaleceClasses = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6), Arrays.asList(7, 8), Arrays.asList(9 , 10));

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
        instance.add(1);
        instance.add(2);
        instance.unionIfAbsent(3, 4);
        instance.add(5);
        instance.add(6);
        instance.unionIfAbsent(7, 8);
        instance.unionIfAbsent(9, 10);
    }

    @After
    public void tearDown() {
        instance.clear();
        assertTrue(instance.isEmpty());
    }

    /**
     * Test of representative method, of class DisjointSet_.
     */
    @Test
    public void testRepresentative() {
        System.out.println("representative");
        assertEquals(representative, instance.representative(subject));
    }

    /**
     * Test of find method, of class DisjointSet_.
     */
    @Test
    public void testFind() {
        System.out.println("find");
        assertArrayEquals(equivaleceClass.toArray(), instance.find(subject).toArray());
    }

    /**
     * Test of union method, of class DisjointSet_.
     */
    @Test
    public void testUnionIfAbsent() {
        System.out.println("unionIfAbsent");
        instance.unionIfAbsent(subject, object);
        assertEquals(instance.representative(subject), instance.representative(object));
    }

    /**
     * Test of disjointValues method, of class DisjointSet_.
     */
    //@Test
    public void testDisjointValues() {
        System.out.println("disjointValues");        
        assertArrayEquals(equivaleceClasses.toArray(), instance.disjointValues().toArray());
    }
    
    /**
     * 
     */
    @Test
    public void testCustom() {
        instance.union(subject, null);      
        System.out.println(instance.disjointValues());
    }

}
