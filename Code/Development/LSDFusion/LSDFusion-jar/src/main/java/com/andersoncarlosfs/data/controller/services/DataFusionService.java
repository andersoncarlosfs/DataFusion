/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.controller.services;

import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.integration.DataFusion;
import java.io.IOException;
import java.util.Collection;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusionService {

    /**
     *
     * @param datasets
     * @param links
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public Collection<Collection<RDFNode>> findEquivalenceClasses(Collection<DataSource> datasets, DataSource... links) throws IOException {
        return new DataFusion(datasets, links).findEquivalenceClasses().values();
    }

    public void X(Collection<DataSource> datasets, DataSource... links) throws Exception {
        new DataFusion(datasets, links).calculateScore();
    }

}
