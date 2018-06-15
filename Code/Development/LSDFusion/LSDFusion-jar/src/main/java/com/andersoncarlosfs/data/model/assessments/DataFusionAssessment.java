/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

import com.andersoncarlosfs.data.model.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
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
    public Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, Entry<DataQualityAssessment, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>>>>> getComputedDataQualityAssessment();

    /**
     *
     * @return a view of the duplicate statements in this data fusion processing
     */
    default Collection<Triple> getDuplicates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return the model
     */
    default Model getModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
