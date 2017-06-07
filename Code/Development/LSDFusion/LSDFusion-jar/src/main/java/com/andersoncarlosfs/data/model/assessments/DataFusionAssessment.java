/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

import com.andersoncarlosfs.x.model.DataSource;
import java.util.Collection;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface DataFusionAssessment {

    /**
     *
     * @return the data sources
     */
    public Collection<DataSource> getDataSources();

    public Map<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityInformation>>> getComputedDataQualityInformation();

    public Map<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment();

    /**
     *
     * @return the model
     */
    public Model getModel();

}
