/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Data Fusion Ontology Language
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DFOL {

    /**
     * Returns a <tt>Resource</tt> by a given URI without a specific model.
     * These will use ResourceFactory to create a <tt>Resource</tt> by a given
     * URI without a specific model.
     *
     * @param identifier to be used to create the <tt>Resource</tt>
     * @return a <tt>Resource</tt>
     */
    protected static final Resource createResource(String identifier) {
        return ResourceFactory.createResource(NAMESPACE + identifier);
    }

    /**
     * Returns a <tt>Property</tt> by a given URI without a specific model.
     * These will use ResourceFactory to create a <tt>Property</tt> by a given
     * URI without a specific model.
     *
     * @param identifier to be used to create the <tt>Property</tt>
     * @return a <tt>Property</tt>
     */
    protected static final Property createProperty(String identifier) {
        return ResourceFactory.createProperty(NAMESPACE + identifier);
    }

    /**
     * The namespace of the vocabulary as a string ({@value}).
     */
    public static final String NAMESPACE = "http://www.andersoncarlosfs.com/dfol#";

    /**
     * Returns the namespace of the vocabulary as a string.
     *
     * @see DFOL#NAMESPACE
     * @return the namespace
     */
    public static String getURI() {
        return NAMESPACE;
    }

    /**
     * The namespace of the vocabulary as a resource.
     *
     * @see DFOL#NAMESPACE
     * @return the namespace
     */
    public static Resource getResource() {
        return createResource(null);
    }

    /**
     * Properties
     */
    public static final Property isEquivalent = createProperty("isEquivalent");
    public static final Property hasValue = createProperty("hasValue");
    public static final Property hasHomogeneity = createProperty("hasHomogeneity");
    public static final Property hasFrequency = createProperty("hasFrequency");
    public static final Property hasReliability = createProperty("hasReliability");
    public static final Property isMorePrecise = createProperty("isMorePrecise");
    public static final Property isSynonym = createProperty("isSynonym");
    public static final Property hasTrustiness = createProperty("hasTrustiness");

}
