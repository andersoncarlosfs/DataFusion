/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.enums;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public enum FilenameExtension {

    RDF("rdf");

    private final String value;

    private FilenameExtension(String value) {
        this.value = value;
    }

    /**
     *
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     *
     * @see Object#toString()
     * @return
     */
    @Override
    public String toString() {
        return getClass().getName() + "[" + name() + "=" + value + "]";
    }

}
