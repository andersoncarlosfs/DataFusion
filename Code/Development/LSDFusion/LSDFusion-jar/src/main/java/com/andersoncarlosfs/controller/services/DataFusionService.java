/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.DataSource;
import com.andersoncarlosfs.model.di.DataFusion;
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
     * @throws java.lang.Exception
     */
    public Collection<Collection<RDFNode>> findEquivalenceClasses(Collection<DataSource> datasets, DataSource... links) throws Exception {
        return new DataFusion(datasets, links).findEquivalenceClasses();
    }

    public void X(Collection<DataSource> datasets, DataSource... links) throws Exception {
        new DataFusion(datasets, links).calculateScore();
    }

}
