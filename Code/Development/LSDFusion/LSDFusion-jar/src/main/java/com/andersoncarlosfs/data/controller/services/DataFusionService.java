/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.controller.services;

import com.andersoncarlosfs.data.model.Dataset;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.DataQualityAssessment;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
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
     * @param datasets
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public Collection<Collection<Node>> findEquivalenceClasses(Collection<Dataset> datasets) throws IOException {
        return new DataFusion(datasets).getDataQualityAssessment().keySet();
    }

    /**
     *
     * @param datasets
     * @return
     * @throws IOException
     */
    public Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> getDataQualityAssessment(Collection<Dataset> datasets) throws IOException {
        return new DataFusion(datasets).getDataQualityAssessment();
    }

}
