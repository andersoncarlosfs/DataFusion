/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

import java.util.Collection;
import org.apache.jena.graph.Node;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface DataQualityInformation {

    static final long serialVersionUID = 1L;

    /**
     *
     * @return the frequency
     */
    public Number getFrequency();

    /**
     *
     * @return the homogeneity
     */
    public Number getHomogeneity();

    /**
     *
     * @return the reliability
     */
    public Number getReliability();

    /**
     * Returns the timestamp.
     *
     * @return the freshness
     */
    public Number getFreshness();

    /**
     *
     * @return the morePrecise
     */
    public Collection<Node> getMorePrecise();

}
