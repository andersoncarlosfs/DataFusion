/*
 * To change this license header, choose License Headers in Project Property.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import com.andersoncarlosfs.data.util.Function;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class Rule {

    private Function function;
    private Property property;
    private Object value;

    public Rule(Function function, Property property) {
        this.function = function;
        this.property = property;
    }

    public Rule(Function function, Property property, Object value) {
        this.function = function;
        this.property = property;
        this.value = value;
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
     * @param function the function to set
     */
    protected void setFunction(Function function) {
        this.function = function;
    }

    /**
     *
     * @return the property
     */
    public Property getProperty() {
        return property;
    }

    /**
     *
     * @param property the property to set
     */
    protected void setProperty(Property property) {
        this.property = property;
    }

    /**
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     *
     * @param value the value to set
     */
    protected void setValue(Object value) {
        this.value = value;
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (function != null ? function.hashCode() : 0);
        hash += (property != null ? property.hashCode() : 0);
        hash += (value != null ? value.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rule)) {
            return false;
        }
        Rule other = (Rule) object;
        if ((this.function == null && other.function != null) || (this.function != null && !this.function.equals(other.function))) {
            return false;
        }
        if ((this.property == null && other.property != null) || (this.property != null && !this.property.equals(other.property))) {
            return false;
        }
        if ((this.value == null && other.value != null) || (this.value != null && !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }

}
