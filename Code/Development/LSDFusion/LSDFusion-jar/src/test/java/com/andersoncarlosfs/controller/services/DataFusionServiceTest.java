/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
        //DataSource dataSource = new DataSource(new File("../../../../Datasets/BNF/dataset.tar.gz"));
        DataSource dataSource = new DataSource(new File("../../../../Datasets/INA/dataset.ttl"));
        dataSource.setSyntax(Lang.TURTLE);
        //DataSource link = new DataSource(new File("../../../../Datasets/BNF/links.nt"));
        DataSource link = new DataSource(new File("../../../../Datasets/INA/links.n3"));
        //DataSource link = new DataSource(new File("../../../../Datasets/DBpedia/links/links.ttl"));
        //link.setSyntax(Lang.NT);       
        link.setSyntax(Lang.N3);
        //link.setSyntax(Lang.TTL);    
        DataFusionService service = new DataFusionService();
        Collection<Collection<RDFNode>> equivalenceClasses = service.findEquivalenceClasses(Arrays.asList(dataSource), link);
        for (Collection<RDFNode> classe : equivalenceClasses) {
            System.out.println("{");
            for (RDFNode node : classe) {
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
        System.out.println("begin test getEquivalentClasses");
        //https://www.w3.org/wiki/DataSetRDFDumps
        //http://data.bnf.fr/semanticweb        
        //DataSource dataSource = new DataSource(new File("../../../../Datasets/BNF/dataset.tar.gz"));
        DataSource dataSource = new DataSource(new File("../../../../Datasets/INA/dataset.ttl"));
        dataSource.setSyntax(Lang.TURTLE);
        //DataSource link = new DataSource(new File("../../../../Datasets/BNF/links.nt"));
        DataSource link = new DataSource(new File("../../../../Datasets/INA/links.n3"));
        //DataSource link = new DataSource(new File("../../../../Datasets/DBpedia/links/links.ttl"));
        //link.setSyntax(Lang.NT);       
        link.setSyntax(Lang.N3);
        //link.setSyntax(Lang.TTL);    
        DataFusionService service = new DataFusionService();
        Collection<Collection<RDFNode>> equivalenceClasses = service.findEquivalenceClasses(Arrays.asList(link), link);
        for (Collection<RDFNode> classe : equivalenceClasses) {
            System.out.println("{");
            for (RDFNode node : classe) {
                //System.out.println(node.toString());
            }
            System.out.println("}");
        }
        System.out.println("Total of equivalence classes: " + equivalenceClasses.size());
        System.out.println("end test getEquivalentClasses");
    }

}
