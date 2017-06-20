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
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionProcessorTest {

    private Collection<DataSource> dataSources = new HashSet<>();

    private DataSource dataSet = new DataSource(Paths.get("../../../../Datasets/Books/dataset.rdf"), Lang.N3, null, null);

    private DataSource link = new DataSource(Paths.get("../../../../Datasets/Books/links.rdf"), Lang.N3, null, null);

    private Collection<Rule> rules = new HashSet<>();

    private Rule construct = new Rule(Arrays.asList(Function.CONSTRUCT), DataFusionProcessor.EQUIVALENCE_PROPERTIES);

    private boolean duplicatesAllowed = false;

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

        dataSources.add(dataSet);
        dataSources.add(link);

        rules.add(construct);

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

        DataFusionAssessment assessment = DataFusionProcessor.process(dataSources, rules, duplicatesAllowed);

        Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>>> values = assessment.getComputedDataQualityAssessment();

        for (Map.Entry<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>>> classes : values.entrySet()) {

            Collection<RDFNode> subjects = classes.getKey();

            System.out.println("{\n\t" + "Subjects=" + subjects);

            Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>> complements = classes.getValue();

            for (Map.Entry<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>> complement : complements.entrySet()) {

                Collection<RDFNode> predicates = complement.getKey();

                System.out.println("\t\t" + "Predicates=" + predicates);

                Map<RDFNode, DataQualityAssessment> objects = complement.getValue();

                for (Map.Entry<RDFNode, DataQualityAssessment> object : objects.entrySet()) {

                    RDFNode value = object.getKey();

                    System.out.println("\t\t\t" + "Object=" + value);

                    DataQualityAssessment records = object.getValue();

                    System.out.println("\t\t\t\t" + "Frequency=" + records.getFrequency());
                    System.out.println("\t\t\t\t" + "Homogeneity=" + records.getHomogeneity());
                    System.out.println("\t\t\t\t" + "Freshness=" + records.getFreshness());
                    System.out.println("\t\t\t\t" + "Reliability=" + records.getReliability());

                }
            }

            System.out.println("}");
        }

    }

}
