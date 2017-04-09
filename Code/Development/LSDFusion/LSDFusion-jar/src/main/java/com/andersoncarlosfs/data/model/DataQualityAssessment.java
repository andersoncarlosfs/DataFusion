/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import java.io.Serializable;
import java.util.Collection;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.Size;
import org.apache.jena.graph.Node;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataQualityAssessment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(min = 0, max = 1)
    private Float frequency;
    @Size(min = 0, max = 1)
    private Float homogeneity;
    @Size(min = 0, max = 1)
    private Float reliability;
    @Size(min = 0, max = 1)
    private Float freshness;
    private Collection<Node> morePrecise;

    public DataQualityAssessment() {
    }

    /**
     *
     * @return the frequency
     */
    public Float getFrequency() {
        return frequency;
    }

    /**
     *
     * @param frequency the frequency to set
     */
    public void setFrequency(Float frequency) {
        this.frequency = frequency;
    }

    /**
     *
     * @return the homogeneity
     */
    public Float getHomogeneity() {
        return homogeneity;
    }

    /**
     *
     * @param homogeneity the homogeneity to set
     */
    public void setHomogeneity(Float homogeneity) {
        this.homogeneity = homogeneity;
    }

    /**
     *
     * @return the reliability
     */
    public Float getReliability() {
        return reliability;
    }

    /**
     *
     * @param reliability the reliability to set
     */
    public void setReliability(Float reliability) {
        this.reliability = reliability;
    }

    /**
     *
     * @return the freshness
     */
    public Float getFreshness() {
        return freshness;
    }

    /**
     *
     * @param freshness the freshness to set
     */
    public void setFreshness(Float freshness) {
        this.freshness = freshness;
    }

    /**
     *
     * @return the morePrecise
     */
    public Collection<Node> getMorePrecise() {
        return morePrecise;
    }

    /**
     *
     * @param morePrecise the morePrecise to set
     */
    public void setMorePrecise(Collection<Node> morePrecise) {
        this.morePrecise = morePrecise;
    }

}
