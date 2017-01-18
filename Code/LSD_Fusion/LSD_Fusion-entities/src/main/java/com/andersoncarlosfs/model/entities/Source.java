/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.entities;

/**
 *
 * @author Ioanna Giannopoulou and Anderson Carlos Ferreira da Silva
 */
public class Source {

    private String name;
    private Float reliability;
    private Float freshness;

    public Source() {
    }

    public Source(String name, Float reliability, Float freshness) {
        this.name = name;
        this.reliability = reliability;
        this.freshness = freshness;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the reliability
     */
    public Float getReliability() {
        return reliability;
    }

    /**
     * @param reliability the reliability to set
     */
    public void setReliability(Float reliability) {
        this.reliability = reliability;
    }

    /**
     * @return the freshness
     */
    public Float getFreshness() {
        return freshness;
    }

    /**
     * @param freshness the freshness to set
     */
    public void setFreshness(Float freshness) {
        this.freshness = freshness;
    }

}
