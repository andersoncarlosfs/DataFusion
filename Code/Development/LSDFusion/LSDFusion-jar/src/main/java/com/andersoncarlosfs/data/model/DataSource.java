package com.andersoncarlosfs.data.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.Lang;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataSource {

    private static final long serialVersionUID = 1L;

    //# These assignments need to be removed
    private String path = "";
    private Lang syntax = Lang.RDFNULL;
    private LocalDate freshness = null;
    private Collection<Property> equivalenceProperties = new ArrayList<>();
    private Collection<Collection<LinkedHashSet<Property>>> mappedProperties = new HashSet<>();
    //#

    public DataSource() {
    }

    public DataSource(String path) {
        this.path = path;
    }

    /**
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
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
     * @param freshness the freshness to set
     */
    public void setFreshness(Date freshness) {
        Instant instant = freshness.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        this.freshness = zonedDateTime.toLocalDate();
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
    public Collection<Collection<LinkedHashSet<Property>>> getMappedProperties() {
        return mappedProperties;
    }

    /**
     *
     * @param mappedProperties the mappedProperties to set
     */
    public void setMappedProperties(Collection<Collection<LinkedHashSet<Property>>> mappedProperties) {
        this.mappedProperties = mappedProperties;
    }

}
