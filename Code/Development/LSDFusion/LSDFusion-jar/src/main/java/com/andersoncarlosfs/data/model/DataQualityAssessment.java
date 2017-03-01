/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import java.io.Serializable;
import javax.validation.constraints.Size;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataQualityAssessment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(min = 1)
    private Integer frequency;
    @Size(min = 1)
    private Integer homogeneity;
    @Size(min = 0, max = 1)
    private Float reliability;
    @Size(min = 0, max = 1)
    private Float freshness;

    public DataQualityAssessment() {
        this.frequency = 0;
        this.homogeneity = 0;
    }

    public DataQualityAssessment(Integer frequency, Integer homogeneity) {
        this.frequency = frequency;
        this.homogeneity = homogeneity;
    }

    /**
     *
     * @return the frequency
     */
    public Integer getFrequency() {
        return frequency;
    }

    /**
     *
     * @param frequency the frequency to set
     */
    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    /**
     *
     * @return the homogeneity
     */
    public Integer getHomogeneity() {
        return homogeneity;
    }

    /**
     *
     * @param homogeneity the homogeneity to set
     */
    public void setHomogeneity(Integer homogeneity) {
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
