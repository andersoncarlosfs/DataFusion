/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.data.controller.services.DataFusionService;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.DataQualityAssessment;
import com.andersoncarlosfs.data.model.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.jena.graph.Node;
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
public class DataFusionServiceTest {

    public DataFusionServiceTest() {
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
     * Test of getEquivalenceClasses method, of class DataFusionService.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetEquivalenceClasses() throws IOException {
        System.out.println("begin test getEquivalentClasses");
        //https://www.w3.org/wiki/DataSetRDFDumps
        //http://data.bnf.fr/semanticweb        
        //DataSource dataSource = new DataSource("../../../../Datasets/BNF/dataset.tar.gz");
        //Dataset dataSource = new DataSource("../../../../Datasets/INA/dataset.ttl");
        //dataSource.setSyntax(Lang.TTL);
        DataSource dataSource = new DataSource("../../../../Datasets/Test/dataset.n3");
        dataSource.setSyntax(Lang.N3);
        dataSource.setEquivalenceProperties(Collections.EMPTY_LIST);
        //DataSource link = new DataSource("../../../../Datasets/BNF/links.nt");
        //link.setSyntax(Lang.N3);
        //Dataset link = new DataSource("../../../../Datasets/INA/links.n3");
        //link.setSyntax(Lang.N3);
        DataSource link = new DataSource("../../../../Datasets/Test/links.n3");
        link.setSyntax(Lang.N3);
        //DataSource link = new DataSource("../../../../Datasets/DBpedia/links/links.ttl");
        //link.setSyntax(Lang.TTL);  
        link.setEquivalenceProperties(DataFusion.EQUIVALENCE_PROPERTIES);
        DataFusionService service = new DataFusionService();
        Collection<Collection<Node>> equivalenceClasses = service.findEquivalenceClasses(Arrays.asList(dataSource, link));
        for (Collection<Node> classe : equivalenceClasses) {
            System.out.println("{");
            for (Node node : classe) {
                System.out.println(node.toString());
            }
            System.out.println("}");
        }
        System.out.println("Total of equivalence classes: " + equivalenceClasses.size());
        System.out.println("end test getEquivalentClasses");
    }

    /**
     * Test of getDataQualityAssessment method, of class DataFusionService.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetDataQualityAssessment() throws IOException {
        System.out.println("begin test getDataQualityAssessment");
        //https://www.w3.org/wiki/DataSetRDFDumps
        //http://data.bnf.fr/semanticweb        
        //DataSource dataSource = new DataSource("../../../../Datasets/BNF/dataset.tar.gz");
        //Dataset dataSource = new DataSource("../../../../Datasets/INA/dataset.ttl");
        //dataSource.setSyntax(Lang.TTL);
        DataSource dataSource = new DataSource("../../../../Datasets/Test/dataset.n3");
        dataSource.setSyntax(Lang.N3);
        //DataSource link = new DataSource("../../../../Datasets/BNF/links.nt");
        //link.setSyntax(Lang.N3);
        //Dataset link = new DataSource("../../../../Datasets/INA/links.n3");
        //link.setSyntax(Lang.N3);
        DataSource link = new DataSource("../../../../Datasets/Test/links.n3");
        link.setSyntax(Lang.N3);
        //DataSource link = new DataSource("../../../../Datasets/DBpedia/links/links.ttl");
        //link.setSyntax(Lang.TTL);  
        link.setEquivalenceProperties(DataFusion.EQUIVALENCE_PROPERTIES);
        DataFusionService service = new DataFusionService();
        Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> computedStatements = service.getDataQualityAssessment(Arrays.asList(dataSource, link));
        for (Map.Entry<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> computedStatement : computedStatements.entrySet()) {
            Collection<Node> equivalenceClasse = computedStatement.getKey();
            Map<Node, Map<Node, DataQualityAssessment>> computedProperties = computedStatement.getValue();
            System.out.println(equivalenceClasse.toString());
            for (Map.Entry<Node, Map<Node, DataQualityAssessment>> entry : computedProperties.entrySet()) {
                Node property = entry.getKey();
                Map<Node, DataQualityAssessment> value = entry.getValue();
                for (Map.Entry<Node, DataQualityAssessment> computedObjects : value.entrySet()) {
                    Node object = computedObjects.getKey();
                    DataQualityAssessment assessment = computedObjects.getValue();
                    System.out.println("    " + property + " " + object + "    " + assessment.getFrequency() + "   " + assessment.getHomogeneity() + " " + assessment.getMorePrecise().toString());
                }
            }
        }
        System.out.println("end test getDataQualityAssessment");
    }

}
