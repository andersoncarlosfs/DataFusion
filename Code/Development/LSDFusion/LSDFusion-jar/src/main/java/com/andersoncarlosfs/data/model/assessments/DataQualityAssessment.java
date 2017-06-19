/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model.assessments;

import com.andersoncarlosfs.data.model.control.DataQualityControl;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface DataQualityAssessment extends DataQualityControl {

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
