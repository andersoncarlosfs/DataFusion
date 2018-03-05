/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import com.andersoncarlosfs.data.util.Function;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class Rule {

    private Path path;
    private Collection<Function> functions;
    private Collection<Property> properties;

    public Rule() {
        this.functions = new HashSet<>();
        this.properties = new HashSet<>();
    }

    public Rule(Collection<Function> functions, Collection<Property> properties) {
        this.functions = functions;
        this.properties = properties;
    }

    public Rule(Path path, Collection<Function> functions, Collection<Property> properties) {
        this.path = path;
        this.functions = functions;
        this.properties = properties;
    }

    /**
     *
     * @return the path
     */
    public Path getPath() {
        return path;
    }

    /**
     *
     * @param path the path to set
     */
    protected void setPath(Path path) {
        this.path = path;
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
     * @param functions the functions to set
     */
    protected void setFunctions(Collection<Function> functions) {
        this.functions = functions;
    }

    /**
     *
     * @return the properties
     */
    public Collection<Property> getProperties() {
        return properties;
    }

    /**
     *
     * @param properties the properties to set
     */
    protected void setProperties(Collection<Property> properties) {
        this.properties = properties;
    }

}
