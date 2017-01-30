/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.dq;

import java.io.Serializable;
import javax.validation.constraints.Size;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataQualityAssessment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(min = 0)
    private Integer occurrenceFrequency;
    @Size(min = 0, max = 1)
    private Float homogeneity;
    @Size(min = 0, max = 1)
    private Float reliability;
    @Size(min = 0, max = 1)
    private Float freshness;

    public DataQualityAssessment() {
    }

    /**
     *
     * @return the occurrenceFrequency
     */
    public Integer getOccurrenceFrequency() {
        return occurrenceFrequency;
    }

    /**
     *
     * @param occurrenceFrequency the occurrenceFrequency to set
     */
    public void setOccurrenceFrequency(Integer occurrenceFrequency) {
        this.occurrenceFrequency = occurrenceFrequency;
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

}
