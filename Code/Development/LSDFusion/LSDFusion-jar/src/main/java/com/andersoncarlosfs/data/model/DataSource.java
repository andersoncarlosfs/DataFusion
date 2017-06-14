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

    private final Path path;
    private final Lang syntax;
    private final Long freshness;
    private final Float reliability;

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
     * @return the syntax
     */
    public Lang getSyntax() {
        return syntax;
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
     * @return the reliability
     */
    public Float getReliability() {
        return reliability;
    }

}
