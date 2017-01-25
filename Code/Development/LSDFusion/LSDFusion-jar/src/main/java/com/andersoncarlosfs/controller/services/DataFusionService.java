/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.DataSource;
import com.andersoncarlosfs.model.di.DataFusion;
import java.io.IOException;
import java.util.Arrays;
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
     * @param dataSources
     * @return the equivalent classes
     * @throws java.io.IOException
     */
    public Collection<Collection<RDFNode>> findEquivalenceClasses(DataSource... dataSources) throws IOException {
        return new DataFusion(Arrays.asList(dataSources)).findEquivalenceClasses();
    }

}
