package com.andersoncarlosfs.data.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.nio.file.Path;
import java.util.Map;
import org.apache.jena.riot.Lang;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataSource {

    private String name;
    private Map<Path, Lang> files;
    private Long freshness;
    private Float reliability;

    protected DataSource() {
    }

    public DataSource(String name, Map<Path, Lang> files) {
        this.name = name;
        this.files = files;
    }

    public DataSource(String name, Map<Path, Lang> files, Long freshness, Float reliability) {
        this.name = name;
        this.files = files;
        this.freshness = freshness;
        this.reliability = reliability;
    }

    /**
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name to set
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the files
     */
    public Map<Path, Lang> getFiles() {
        return files;
    }

    /**
     *
     * @param files the files to set
     */
    protected void setFiles(Map<Path, Lang> files) {
        this.files = files;
    }

    /**
     *
     * @return the freshness
     */
    public Long getFreshness() {
        return freshness;
    }

    /**
     *
     * @param freshness the freshness to set
     */
    protected void setFreshness(Long freshness) {
        this.freshness = freshness;
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
     * @param reliability the reliability to set
     */
    protected void setReliability(Float reliability) {
        this.reliability = reliability;
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
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
        if (!(object instanceof Rule)) {
            return false;
        }
        DataSource other = (DataSource) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

}
