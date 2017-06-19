/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import com.andersoncarlosfs.data.util.Function;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class Rule {

    private final Collection<Function> functions;
    private final Collection<Property> properties;

    public Rule() {
        this.functions = new HashSet<>();
        this.properties = new HashSet<>();
    }

    public Rule(Collection<Function> functions, Collection<Property> properties) {
        this.functions = functions;
        this.properties = properties;
    }

    /**
     *
     * @return the functions
     */
    public Collection<Function> getFunctions() {
        return functions;
    }

    /**
     *
     * @return the properties
     */
    public Collection<? extends RDFNode> getProperties() {
        return properties;
    }

}
