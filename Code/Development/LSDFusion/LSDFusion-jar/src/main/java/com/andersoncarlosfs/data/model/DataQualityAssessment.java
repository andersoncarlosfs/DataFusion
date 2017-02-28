/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import java.io.Serializable;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataQualityAssessment implements Serializable {

    private static final long serialVersionUID = 1L;
    private int occurrenceFrequency;
    private float homogeneity;    
    private float reliability;    
    private int freshness;

    public DataQualityAssessment() {
    }

    /**
     *
     * @return the occurrenceFrequency
     */
    public int getOccurrenceFrequency() {
        return occurrenceFrequency;
    }

    /**
     *
     * @param occurrenceFrequency the occurrenceFrequency to set
     */
    public void setOccurrenceFrequency(int occurrenceFrequency) {
        this.occurrenceFrequency = occurrenceFrequency;
    }

    /**
     *
     * @return the homogeneity
     */
    public float getHomogeneity() {
        return homogeneity;
    }

    /**
     *
     * @param homogeneity the homogeneity to set
     */
    public void setHomogeneity(float homogeneity) {
        this.homogeneity = homogeneity;
    }

    /**
     *
     * @return the reliability
     */
    public float getReliability() {
        return reliability;
    }

    /**
     *
     * @param reliability the reliability to set
     */
    public void setReliability(float reliability) {
        this.reliability = reliability;
    }

    /**
     *
     * @return the freshness
     */
    public float getFreshness() {
        return freshness;
    }

    /**
     *
     * @param freshness the freshness to set
     */
    public void setFreshness(float freshness) {
        this.freshness = freshness;
    }

}
