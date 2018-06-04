package com.andersoncarlosfs.data.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.nio.file.Path;
import org.apache.jena.riot.Lang;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataSource {

    private Path path;
    private Lang syntax;
    private Long freshness;
    private Float reliability;

    public DataSource(Path path, Lang syntax) {
        this.path = path;
        this.syntax = syntax;
        this.freshness = null;
        this.reliability = null;

    }

    public DataSource(Path path, Lang syntax, Long freshness, Float reliability) {
        this.path = path;
        this.syntax = syntax;
        this.freshness = freshness;
        this.reliability = reliability;
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
     * @param path the path to set
     */
    protected void setPath(Path path) {
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
    protected void setSyntax(Lang syntax) {
        this.syntax = syntax;
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
        hash += (path != null ? path.hashCode() : 0);
        hash += (syntax != null ? syntax.hashCode() : 0);
        hash += (freshness != null ? freshness.hashCode() : 0);
        hash += (reliability != null ? reliability.hashCode() : 0);
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
        DataSource other = (DataSource) object;
        if ((this.path == null && other.path != null) || (this.path != null && !this.path.equals(other.path))) {
            return false;
        }
        if ((this.syntax == null && other.syntax != null) || (this.syntax != null && !this.syntax.equals(other.syntax))) {
            return false;
        }
        if ((this.freshness == null && other.freshness != null) || (this.freshness != null && !this.freshness.equals(other.freshness))) {
            return false;
        }
        if ((this.reliability == null && other.reliability != null) || (this.reliability != null && !this.reliability.equals(other.reliability))) {
            return false;
        }
        return true;
    }

}
