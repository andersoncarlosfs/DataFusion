/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class LinkedDataset extends Dataset {

    private Collection<Property> equivalenceProperties;

    public LinkedDataset(String path) {
        super(path);
        equivalenceProperties = new HashSet<>();
    }

    public LinkedDataset(String path, Collection<Property> equivalenceProperties) {
        super(path);
        this.equivalenceProperties = equivalenceProperties;
    }

    public LinkedDataset(String path, Property... equivalenceProperties) {
        super(path);
        this.equivalenceProperties = new HashSet<>(Arrays.asList(equivalenceProperties));
    }

    /**
     *
     * @return the equivalenceProperties
     */
    public Collection<Property> getEquivalenceProperties() {
        return equivalenceProperties;
    }

    /**
     *
     * @param equivalenceProperties the equivalenceProperties to set
     */
    public void setEquivalenceProperties(Collection<Property> equivalenceProperties) {
        this.equivalenceProperties = equivalenceProperties;
    }

}
