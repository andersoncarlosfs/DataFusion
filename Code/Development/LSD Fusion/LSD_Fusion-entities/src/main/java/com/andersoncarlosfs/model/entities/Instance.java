/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.entities;

import java.net.URI;
import java.util.ArrayList;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class Instance {

    private URI uri;
    private Source source;
    private ArrayList<Property> properties = new ArrayList<Property>();

    public Instance() {
    }

    public Instance(URI uri, Source source) {
        this.uri = uri;
        this.source = source;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * @return the source
     */
    public Source getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * @return the properties
     */
    public ArrayList<Property> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }

}
