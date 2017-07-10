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

    public DataSource() {
    }

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

}
