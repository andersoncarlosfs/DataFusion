/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

import java.util.Collection;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface DataFusionAssessment {

    public Map<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment();

    /**
     *
     * @return a view of the duplicate statements in this data fusion processing
     */
    public Collection<Statement> getDuplicates();

    /**
     *
     * @return the model
     */
    public Model getModel();

}
