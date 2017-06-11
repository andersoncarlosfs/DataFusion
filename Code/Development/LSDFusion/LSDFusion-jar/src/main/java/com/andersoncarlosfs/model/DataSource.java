package com.andersoncarlosfs.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.Lang;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataSource {

    private final Path path;
    private final Lang syntax;
    private final LocalDate freshness;
    private final Float reliability;
    private final Collection<Property> equivalenceProperties;
    private final Collection<Collection<Property>> mappedProperties;

    public DataSource(Path path, Lang syntax, LocalDate freshness, Float reliability, Collection<Property> equivalenceProperties, Collection<Collection<Property>> mappedProperties) {
        this.path = path;
        this.syntax = syntax;
        this.freshness = freshness;
        this.reliability = reliability;
        this.equivalenceProperties = equivalenceProperties;
        this.mappedProperties = mappedProperties;
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
     * @return the syntax
     */
    public Lang getSyntax() {
        return syntax;
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
     * @return the reliability
     */
    public Float getReliability() {
        return reliability;
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
     * @return the mappedProperties
     */
    public Collection<Collection<Property>> getMappedProperties() {
        return mappedProperties;
    }

}
