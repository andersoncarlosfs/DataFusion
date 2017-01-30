/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di.enums;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public enum EquivalentProperty {

    sameAs(OWL.sameAs),
    exactMatch(SKOS.exactMatch);
    
    private final Property value;

    private EquivalentProperty(Property value) {
        this.value = value;
    }

    /**
     *
     * @return the value
     */
    public Property value() {
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
