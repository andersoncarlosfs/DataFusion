/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.data.model.assessments.DataQualityInformation;
import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.util.Function;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
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
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionProcessorTest {

    private Map<DataSource, Map<Function, Collection<Property>>> dataSources = new HashMap<>();

    private DataSource dataSet = new DataSource(Paths.get("../../../../Datasets/Books/dataset.rdf"), Lang.N3, null, null);
    private DataSource link = new DataSource(Paths.get("../../../../Datasets/Books/links.rdf"), Lang.N3, null, null);

    private Map<Function, Collection<Property>> rules = new HashMap<>();

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
        rules.put(Function.CONSTRUCT, DataFusionProcessor.EQUIVALENCE_PROPERTIES);
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
        for (Map.Entry<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityInformation>>> statements : DataFusionProcessor.process(dataSet, link).entrySet()) {
            System.out.println("{");
            System.out.println("\t" + statements.getKey());
            for (Map.Entry<Property, Map<RDFNode, DataQualityInformation>> properties : statements.getValue().entrySet()) {
                System.out.println("\t\t" + properties.getKey());
                for (Map.Entry<RDFNode, DataQualityInformation> objects : properties.getValue().entrySet()) {
                    System.out.println("\t\t\t" + objects.getKey());
                    System.out.println("\t\t\t\t" + "F" + objects.getValue().getFrequency()
                            + " F" + objects.getValue().getFreshness() + " H" + objects.getValue().getHomogeneity()
                            + " R" + objects.getValue().getReliability() + " M" + objects.getValue().getMorePrecise());
                }
            }
            System.out.println("}");
        }
    }

}
