/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

import java.util.Collection;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface DataQualityAssessment {

    static final long serialVersionUID = 1L;

    /**
     *
     * @return the frequency
     */
    public Float getFrequency();

    /**
     *
     * @return the homogeneity
     */
    public Float getHomogeneity();

    /**
     *
     * @return the reliability
     */
    public Float getReliability();

    /**
     * Returns the timestamp.
     *
     * @return the freshness
     */
    public Float getFreshness();

    /**
     *
     * @return the morePrecise
     */
    public Collection<RDFNode> getMorePrecise();

    /**
     *
     * @return the score
     */
    default Float getScore() {
        return (getFreshness() + getFrequency() + getHomogeneity() + getReliability() + (getMorePrecise().size())) / (4 + getMorePrecise().size());
    }

}
