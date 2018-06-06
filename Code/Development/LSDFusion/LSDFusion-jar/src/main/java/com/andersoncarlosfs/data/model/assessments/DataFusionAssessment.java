/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

import com.andersoncarlosfs.data.model.DataSource;
import java.util.Collection;
import java.util.Map;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface DataFusionAssessment {

    /**
     *
     * @return
     */
    public Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment();

    /**
     *
     * @return a view of the duplicate statements in this data fusion processing
     */
    public Collection<Triple> getDuplicates();

    /**
     *
     * @return the model
     */
    public Model getModel();

}
