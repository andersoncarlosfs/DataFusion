package com.andersoncarlosfs.data.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.andersoncarlosfs.data.integration.DataFusion;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
    private final UUID uuid = UUID.randomUUID();
    private Lang syntax = Lang.RDFNULL;
    private Calendar freshness = null;
    private Collection<Property> equivalenceProperties = new ArrayList<>();
    private Map<Property, DataFusion.ComparatorRules> comparatorRules = new HashMap<>();

    public DataSource(String path) {
        super(path);
    }

    /**
     *
     * @return the uuid
     */
    public UUID getUUID() {
        return uuid;
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
     * @return the comparatorRules
     */
    public Map<Property, DataFusion.ComparatorRules> getComparatorRules() {
        return comparatorRules;
    }

    /**
     *
     * @param comparatorRules the comparatorRules to set
     */
    public void setComparatorRules(Map<Property, DataFusion.ComparatorRules> comparatorRules) {
        this.comparatorRules = comparatorRules;
    }

}
