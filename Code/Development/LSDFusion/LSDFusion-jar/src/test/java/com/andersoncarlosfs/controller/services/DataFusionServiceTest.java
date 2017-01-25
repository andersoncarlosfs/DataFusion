/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.DataSource;
import java.io.File;
import java.io.IOException;
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
     * Test of getEquivalenceClasses method, of class DataFusion.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetEquivalenceClasses() throws IOException {
        System.out.println("begin test getEquivalentClasses");
        //https://www.w3.org/wiki/DataSetRDFDumps
        DataSource dataSource1 = new DataSource();
        dataSource1.setInputStream(new File("/home/lsdfusion/Desktop/LSDFusion/Datasets/MeSH/MeSH.ttl"));
        dataSource1.setSyntax(Lang.TURTLE);
        //http://data.bnf.fr/semanticweb
        DataSource dataSource2 = new DataSource();
        dataSource2.setInputStream(new File("/home/lsdfusion/Desktop/LSDFusion/Datasets/BNF/BNF.nt"));
        dataSource2.setSyntax(Lang.NT);
        DataFusionService service = new DataFusionService();
        Integer size = 0;
        Collection<Collection<RDFNode>> classes = service.findEquivalenceClasses(dataSource1, dataSource2);
        for (Collection<RDFNode> classe : classes) {
            size += classe.size();
            System.out.println("{");
            for (RDFNode node : classe) {
                System.out.println(node.toString());
            }
            System.out.println("}");
        }
        System.out.println("Total of equivalence classes: " + size);
        System.out.println("end test getEquivalentClasses");
    }

}
