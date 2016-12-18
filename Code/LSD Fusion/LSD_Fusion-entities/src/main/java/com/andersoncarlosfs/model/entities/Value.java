/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.entities;

import java.util.ArrayList;

/**
 *
 * @author Ioanna Giannopoulou and Anderson Carlos Ferreira da Silva
 */
public class Value {

    private Source source;
    private String value;
    private String uri;
    private Float homogeneity;
    private Float occurrenceFrequency;
    private Boolean implausible;
    private String violatedRules;
    private Float qualityScore;
    private String qualityValue;
    private ArrayList<Value> isMorePreciseThan = new ArrayList<>();
    private ArrayList<String> isMorePreciseThanStr = new ArrayList<>();
    private ArrayList<Value> isSynonym = new ArrayList<>();

    public Value(String value) {
        this.value = value;
    }

    public Value(Source source, String value) {
        this.source = source;
        this.value = value;
    }

    /**
     * @return the source
     */
    public Source getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the homogeneity
     */
    public Float getHomogeneity() {
        return homogeneity;
    }

    /**
     * @param homogeneity the homogeneity to set
     */
    public void setHomogeneity(Float homogeneity) {
        this.homogeneity = homogeneity;
    }

    /**
     * @return the occurrenceFrequency
     */
    public Float getOccurrenceFrequency() {
        return occurrenceFrequency;
    }

    /**
     * @param occurrenceFrequency the occurrenceFrequency to set
     */
    public void setOccurrenceFrequency(Float occurrenceFrequency) {
        this.occurrenceFrequency = occurrenceFrequency;
    }

    /**
     * @return the implausible
     */
    public Boolean getImplausible() {
        return implausible;
    }

    /**
     * @param implausible the implausible to set
     */
    public void setImplausible(Boolean implausible) {
        this.implausible = implausible;
    }

    /**
     * @return the violatedRules
     */
    public String getViolatedRules() {
        return violatedRules;
    }

    /**
     * @param violatedRules the violatedRules to set
     */
    public void setViolatedRules(String violatedRules) {
        this.violatedRules = violatedRules;
    }

    /**
     * @return the qualityScore
     */
    public Float getQualityScore() {
        return qualityScore;
    }

    /**
     * @param qualityScore the qualityScore to set
     */
    public void setQualityScore(Float qualityScore) {
        this.qualityScore = qualityScore;
    }

    /**
     * @return the qualityValue
     */
    public String getQualityValue() {
        return qualityValue;
    }

    /**
     * @param qualityValue the qualityValue to set
     */
    public void setQualityValue(String qualityValue) {
        this.qualityValue = qualityValue;
    }

    /**
     * @return the isMorePreciseThan
     */
    public ArrayList<Value> getIsMorePreciseThan() {
        return isMorePreciseThan;
    }

    /**
     * @param isMorePreciseThan the isMorePreciseThan to set
     */
    public void setIsMorePreciseThan(ArrayList<Value> isMorePreciseThan) {
        this.isMorePreciseThan = isMorePreciseThan;
    }

    /**
     * @return the isMorePreciseThanStr
     */
    public ArrayList<String> getIsMorePreciseThanStr() {
        return isMorePreciseThanStr;
    }

    /**
     * @param isMorePreciseThanStr the isMorePreciseThanStr to set
     */
    public void setIsMorePreciseThanStr(ArrayList<String> isMorePreciseThanStr) {
        this.isMorePreciseThanStr = isMorePreciseThanStr;
    }

    /**
     * @return the isSynonym
     */
    public ArrayList<Value> getIsSynonym() {
        return isSynonym;
    }

    /**
     * @param isSynonym the isSynonym to set
     */
    public void setIsSynonym(ArrayList<Value> isSynonym) {
        this.isSynonym = isSynonym;
    }

}
