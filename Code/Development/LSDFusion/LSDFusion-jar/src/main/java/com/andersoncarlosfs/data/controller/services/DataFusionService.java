/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.controller.services;

import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.DataQualityAssessment;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashSet;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.graph.Node;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusionService {

    /**
     *
     * @param dataSources
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public Collection<Collection<Node>> findEquivalenceClasses(Collection<DataSource> dataSources) throws IOException {
        return new DataFusion(dataSources).getDataQualityAssessment().keySet();
    }

    /**
     *
     * @param dataSources
     * @return
     * @throws IOException
     */
    public Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> getDataQualityAssessment(Collection<DataSource> dataSources) throws IOException {
        return new DataFusion(dataSources).getDataQualityAssessment();
    }

}
