/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface DataQualityAssessment extends DataQualityInformation {

    /**
     *
     * @return the frequency
     */
    @Override
    public Float getFrequency();

    /**
     *
     * @return the homogeneity
     */
    @Override
    public Float getHomogeneity();

    /**
     *
     * @return the reliability
     */
    @Override
    public Float getReliability();

    /**
     *
     * @return the freshness
     */
    @Override
    public Float getFreshness();

    /**
     *
     * @return the score
     */
    default Float getScore() {
        return (getFreshness() + getFrequency() + getHomogeneity() + getReliability() + (getMorePrecise().size())) / (4 + getMorePrecise().size());
    }

    /**
     *
     * @return the trustiness
     */
    public Float getTrustiness();

}
