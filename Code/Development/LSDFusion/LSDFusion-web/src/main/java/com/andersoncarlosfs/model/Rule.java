/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import com.andersoncarlosfs.data.util.Function;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author anderson
 */
public class Rule extends com.andersoncarlosfs.data.model.Rule {

    public Rule() {
        super();
    }

    /**
     * 
     * @param path the path to set
     */
    @Override
    public void setPath(Path path) {
        super.setPath(path);
    }
    
    /**
     *
     * @return the functions
     */
    @Override
    public Collection<Function> getFunctions() {
        return super.getFunctions();
    }

    /**
     * 
     * @param functions the functions to set
     */
    @Override
    public void setFunctions(Collection<Function> functions) {
        super.setFunctions(functions);
    }

    /**
     *
     * @return the properties
     */
    @Override
    public Collection<Property> getProperties() {
        return super.getProperties();
    }

    /**
     *
     * @param properties the properties to set
     */
    @Override
    public void setProperties(Collection<Property> properties) {
        super.setProperties(properties);
    }

}
