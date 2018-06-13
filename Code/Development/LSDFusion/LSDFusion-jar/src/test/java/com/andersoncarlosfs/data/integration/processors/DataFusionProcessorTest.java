/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.model.Rule;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import com.andersoncarlosfs.data.model.assessments.DataQualityAssessment;
import com.andersoncarlosfs.data.util.Function;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashSet;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionProcessorTest {

    private Collection<DataSource> dataSources = new HashSet<>();

    private Collection<Rule> rules = new HashSet<>();

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

        dataSources.add(new DataSource(Paths.get("../../../../Datasets/Books/dataset.rdf"), Lang.N3, null, null));
        dataSources.add(new DataSource(Paths.get("../../../../Datasets/Books/links.rdf"), Lang.N3, null, null));

        for (Property property : DataFusionProcessor.EQUIVALENCE_PROPERTIES) {
            rules.add(new Rule(Function.IDENTITY, property));
        }
        
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

        DataFusionAssessment assessment = DataFusionProcessor.process(dataSources, rules);

        Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Map<RDFNode, Collection<DataSource>>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>> values = assessment.getComputedDataQualityAssessment();

        for (Entry<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Map<RDFNode, Collection<DataSource>>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>> classes : values.entrySet()) {

            Collection<RDFNode> subjects = classes.getKey().keySet();

            System.out.println("{\n\t" + "Subjects=" + subjects);

            Map<Map<RDFNode, Map<RDFNode, Collection<DataSource>>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> complements = classes.getValue();

            for (Entry<Map<RDFNode, Map<RDFNode, Collection<DataSource>>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> complement : complements.entrySet()) {

                Collection<RDFNode> predicates = complement.getKey().keySet();

                System.out.println("\t\t" + "Predicates=" + predicates);

                Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> objects = complement.getValue();

                for (Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> object : objects.entrySet()) {

                    RDFNode value = object.getKey();

                    System.out.println("\t\t\t" + "Object=" + value);

                    DataQualityAssessment records = object.getValue().getKey();

                    System.out.println("\t\t\t\t" + "Frequency=" + records.getFrequency());
                    System.out.println("\t\t\t\t" + "Homogeneity=" + records.getHomogeneity());
                    System.out.println("\t\t\t\t" + "Freshness=" + records.getFreshness());
                    System.out.println("\t\t\t\t" + "Reliability=" + records.getReliability());
                    System.out.println("\t\t\t\t" + "Score=" + records.getScore());
                    System.out.println("\t\t\t\t" + "MorePrecise=" + records.getMorePrecise());                    

                }
            }

            System.out.println("}");
        }

    }

}
