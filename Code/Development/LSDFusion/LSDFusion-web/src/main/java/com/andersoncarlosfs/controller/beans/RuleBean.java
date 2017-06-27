/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.model.Rule;
import com.andersoncarlosfs.data.util.Function;
import java.util.Collection;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class RuleBean {

    private Collection<Function> functions;
    private Collection<Property> properties;

    public RuleBean() {
    }

    /**
     * @return the functions
     */
    public Collection<Function> getFunctions() {
        return functions;
    }

    /**
     * @param functions the functions to set
     */
    public void setFunctions(Collection<Function> functions) {
        this.functions = functions;
    }

    /**
     * @return the properties
     */
    public Collection<Property> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Collection<Property> properties) {
        this.properties = properties;
    }

    /**
     * @return the rule
     */
    public Rule getRule() {
        return new Rule(functions, properties);
    }

    /**
     * @param rule the rule to set
     */
    public void setRule(Rule rule) {
        functions = rule.getFunctions();
        properties = rule.getProperties();
    }

    /**
     *
     * @return
     */
    public String reset() {

        functions = null;
        properties = null;

        return "edit";

    }

}
