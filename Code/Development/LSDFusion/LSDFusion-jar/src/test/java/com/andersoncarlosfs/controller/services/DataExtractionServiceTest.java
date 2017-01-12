/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author AndersonCarlos
 */
public class DataExtractionServiceTest {

    public DataExtractionServiceTest() {
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
     * Test of extractDataModel method, of class DataExtractionService.
     *
     * @throws IOException
     */
    @Test
    public void testExtractDataModel() throws IOException {
        System.out.println("extractDataModel");
        File file = new File("C:\\Users\\AndersonCarlos\\Desktop\\LSD_Fusion\\Datasets\\DBpedia\\datasets\\onlyLitterals\\mappingbased_literals_de.ttl.bz2");
        DataExtractionService service = new DataExtractionService();
        service.extractDataModel(file);
    }

    /**
     * Test of getEquivalentClasses method, of class DataExtractionService.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetEquivalentClasses() throws IOException {
        System.out.println("getEquivalentClasses");
        File file = new File("C:\\Users\\AndersonCarlos\\Desktop\\LSD_Fusion\\Datasets\\DBpedia\\links\\sample.ttl");
        DataExtractionService service = new DataExtractionService();
        Collection<Collection<String>> classes = service.getEquivalentClasses(file);
        for (Collection<String> classe : classes) {
            System.out.println("{");
            for (String string : classe) {
               System.out.println(string); 
            }
            System.out.println("}");
        }
    }

}
