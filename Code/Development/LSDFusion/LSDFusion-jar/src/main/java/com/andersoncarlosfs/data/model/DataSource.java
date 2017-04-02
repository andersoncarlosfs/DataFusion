package com.andersoncarlosfs.data.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.graph.FrontsNode;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.Lang;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataSource extends File {

    private static final long serialVersionUID = 1L;

    //# These assignments need to be removed
    private Lang syntax = Lang.RDFNULL;
    private LocalDate freshness = null;
    private Collection<Property> equivalenceProperties = new ArrayList<>();
    private Collection<Collection<LinkedHashSet<FrontsNode>>> mappedProperties = new HashSet<>();
    //#

    public DataSource(String path) {
        super(path);
    }

    /**
     *
     * @return the syntax
     */
    public Lang getSyntax() {
        return syntax;
    }

    /**
     *
     * @param syntax the syntax to set
     */
    public void setSyntax(Lang syntax) {
        this.syntax = syntax;
    }

    /**
     *
     * @return the freshness
     */
    public LocalDate getFreshness() {
        return freshness;
    }

    /**
     *
     * @param freshness the freshness to set
     */
    public void setFreshness(LocalDate freshness) {
        this.freshness = freshness;
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

    /**
     *
     * @return the mappedProperties
     */
    public Collection<Collection<LinkedHashSet<FrontsNode>>> getMappedProperties() {
        return mappedProperties;
    }

    /**
     *
     * @param mappedProperties the mappedProperties to set
     */
    public void setMappedProperties(Collection<Collection<LinkedHashSet<FrontsNode>>> mappedProperties) {
        this.mappedProperties = mappedProperties;
    }

}
