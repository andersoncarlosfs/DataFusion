/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.x.data.integration.DataFusion;
import com.andersoncarlosfs.x.model.DataSource;
import java.util.Collection;
import java.util.Map;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author lsdfusion
 */
public class DataFusionProcessorTest {

    private DataSource dataSet;
    private DataSource link;

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
        //
        dataSet = new DataSource("../../../../Datasets/Books/dataset.rdf");
        dataSet.setSyntax(Lang.N3);
        //
        link = new DataSource("../../../../Datasets/Books/links.rdf");
        link.setSyntax(Lang.N3);
        link.setEquivalenceProperties(DataFusion.EQUIVALENCE_PROPERTIES);
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

        for (Map<RDFNode, Map<Property, Collection<RDFNode>>> statements : DataFusionProcessor.process(dataSet, link)) {
            System.out.println("{");
            for (Map.Entry<RDFNode, Map<Property, Collection<RDFNode>>> resources : statements.entrySet()) {
                for (Map.Entry<Property, Collection<RDFNode>> properties : resources.getValue().entrySet()) {
                    for (RDFNode object : properties.getValue()) {
                        System.out.println(resources.getKey() + "\t" + properties.getKey() + "\t" + object);
                    }
                }
            }
            System.out.println("}");
        }

    }

}
