/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.model.DataSource;
import java.nio.file.Path;
import org.apache.jena.riot.Lang;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataSourceBean {

    private Path path;
    private Lang syntax;
    private Long freshness;
    private Float reliability;

    public DataSourceBean() {
    }

    /**
     * @return the path
     */
    public Path getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * @return the syntax
     */
    public Lang getSyntax() {
        return syntax;
    }

    /**
     * @param syntax the syntax to set
     */
    public void setSyntax(Lang syntax) {
        this.syntax = syntax;
    }

    /**
     * @return the freshness
     */
    public Long getFreshness() {
        return freshness;
    }

    /**
     * @param freshness the freshness to set
     */
    public void setFreshness(Long freshness) {
        this.freshness = freshness;
    }

    /**
     * @return the reliability
     */
    public Float getReliability() {
        return reliability;
    }

    /**
     * @param reliability the reliability to set
     */
    public void setReliability(Float reliability) {
        this.reliability = reliability;
    }

    /**
     * @return the rule
     */
    public DataSource getDataSource() {
        return new DataSource(path, syntax, freshness, reliability);
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        path = dataSource.getPath();
        syntax = dataSource.getSyntax();
        freshness = dataSource.getFreshness();
        reliability = dataSource.getReliability();
    }

    /**
     *
     * @return
     */
    public String reset() {

        path = null;
        syntax = null;
        freshness = null;
        reliability = null;

        return "edit";

    }

}
