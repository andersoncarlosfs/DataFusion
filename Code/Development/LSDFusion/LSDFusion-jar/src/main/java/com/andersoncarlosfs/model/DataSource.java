package com.andersoncarlosfs.model;

/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.riot.Lang;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataSource implements Serializable {

    private static final long serialVersionUID = 1L;
    private final UUID uuid = UUID.randomUUID();
    private InputStream inputStream;
    private Lang syntax = Lang.RDFNULL;
    private Calendar lastModified;

    public DataSource() {
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
     * @return the inputStream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     *
     * @param inputStream the inputStream to set
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     *
     * @param file the file to set
     * @throws java.io.FileNotFoundException
     */
    public void setInputStream(File file) throws FileNotFoundException {
        this.inputStream = new FileInputStream(file);
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
     * @return the lastModified
     */
    public Calendar getLastModified() {
        return lastModified;
    }

    /**
     *
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
    }

}
