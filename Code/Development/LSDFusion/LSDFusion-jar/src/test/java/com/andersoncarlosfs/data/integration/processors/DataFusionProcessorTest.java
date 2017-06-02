/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.util.DisjointSet;
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
public class DataFusionProcessorTest {
    
    public DataFusionProcessorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of fuse method, of class DataFusionProcessor.
     */
    @Test
    public void testFuse() throws Exception {
        System.out.println("fuse");
        DataSource[] dataSources = null;
        DisjointSet expResult = null;
        DisjointSet result = DataFusionProcessor.fuse(dataSources);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
