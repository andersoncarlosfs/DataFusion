package com.andersoncarlosfs.data.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import javax.enterprise.context.RequestScoped;
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
    private Calendar freshness = null;
    private Collection<Property> equivalenceProperties = new ArrayList<>();
    private Collection<Collection<TreeSet<Property>>> mappedEntries = new HashSet<>();
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
    public Calendar getFreshness() {
        return freshness;
    }

    /**
     *
     * @param freshness the freshness to set
     */
    public void setFreshness(Calendar freshness) {
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
     * @return the mappedEntries
     */
    public Collection<Collection<TreeSet<Property>>> getMappedEntries() {
        return mappedEntries;
    }

    /**
     *
     * @param mappedEntries the mappedEntries to set
     */
    public void setMappedEntries(Collection<Collection<TreeSet<Property>>> mappedEntries) {
        this.mappedEntries = mappedEntries;
    }

}
