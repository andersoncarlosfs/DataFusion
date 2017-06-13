/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.model.DataSource;
import com.andersoncarlosfs.model.Rule;
import java.nio.file.Paths;
import org.apache.jena.riot.Lang;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionProcessorTest {

    private DataSource dataSet;
    private DataSource link;

    private Rule construct = new Rule(Rule.Function.CONSTRUCT, DataFusionProcessor.EQUIVALENCE_PROPERTIES);

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
        link = new DataSource(Paths.get("../../../../Datasets/Books/links.rdf"), Lang.N3, null, null, construct);
        dataSet = new DataSource(Paths.get("../../../../Datasets/Books/dataset.rdf"), Lang.N3, null, null);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of process method, of class DataFusionProcessor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
    }

}
