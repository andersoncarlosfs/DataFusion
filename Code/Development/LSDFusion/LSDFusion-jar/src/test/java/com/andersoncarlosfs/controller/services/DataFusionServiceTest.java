/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.data.controller.services.DataFusionService;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.Dataset;
import com.andersoncarlosfs.data.model.LinkedDataset;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
        //DataSource dataset = new DataSource("../../../../Datasets/BNF/dataset.tar.gz");
        Dataset dataset = new Dataset("../../../../Datasets/INA/dataset.ttl");
        dataset.setSyntax(Lang.TURTLE);
        //DataSource link = new LinkedDataset("../../../../Datasets/BNF/links.nt", DataFusion.EQUIVALENCE_PROPERTIES);
        Dataset link = new LinkedDataset("../../../../Datasets/INA/links.n3", DataFusion.EQUIVALENCE_PROPERTIES);
        //DataSource link = new LinkedDataset("../../../../Datasets/DBpedia/links/links.ttl", DataFusion.EQUIVALENCE_PROPERTIES);
        //link.setSyntax(Lang.NT);       
        link.setSyntax(Lang.N3);
        //link.setSyntax(Lang.TTL);    
        DataFusionService service = new DataFusionService();
        Collection<Collection<Node>> equivalenceClasses = service.findEquivalenceClasses(Arrays.asList(dataset, link));
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
     * Test of X method, of class DataFusionService.
     *
     * @throws java.io.IOException
     */
    //@Test
    public void testX() throws IOException {

    }

}
