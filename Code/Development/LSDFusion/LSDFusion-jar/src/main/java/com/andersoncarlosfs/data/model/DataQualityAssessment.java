/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.validation.constraints.Size;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataQualityAssessment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(min = 1)
    private AtomicInteger frequency;
    @Size(min = 1)
    private AtomicInteger homogeneity;
    @Size(min = 0, max = 1)
    private Float reliability;
    @Size(min = 0, max = 1)
    private Float freshness;

    public DataQualityAssessment() {
        this.frequency = new AtomicInteger(0);
        this.homogeneity = new AtomicInteger(0);
    }

    public DataQualityAssessment(AtomicInteger frequency, AtomicInteger homogeneity) {
        this.frequency = frequency;
        this.homogeneity = homogeneity;
    }

    /**
     *
     * @return the frequency
     */
    public AtomicInteger getFrequency() {
        return frequency;
    }

    /**
     *
     * @param frequency the frequency to set
     */
    public void setFrequency(AtomicInteger frequency) {
        this.frequency = frequency;
    }

    /**
     *
     * @return the homogeneity
     */
    public AtomicInteger getHomogeneity() {
        return homogeneity;
    }

    /**
     *
     * @param homogeneity the homogeneity to set
     */
    public void setHomogeneity(AtomicInteger homogeneity) {
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
