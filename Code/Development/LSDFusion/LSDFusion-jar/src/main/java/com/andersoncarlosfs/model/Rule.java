/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import java.util.Collection;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class Rule {

    public enum Function {
        MIN, MAX, CONTAINS, UNION, CONSTRUCT
    }

    private final Function function;
    private final Collection<? extends RDFNode> properties;

    public Rule(Function function, Collection<? extends RDFNode> properties) {
        this.function = function;
        this.properties = properties;
    }

    /**
     *
     * @return the function
     */
    public Function getFunction() {
        return function;
    }

    /**
     *
     * @return the properties
     */
    public Collection<? extends RDFNode> getProperties() {
        return properties;
    }

}
