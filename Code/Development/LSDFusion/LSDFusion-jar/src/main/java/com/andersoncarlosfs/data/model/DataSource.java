package com.andersoncarlosfs.data.model;

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
    private File file;
    private Lang syntax = Lang.RDFNULL;
    private Calendar lastModified;

    public DataSource(File file) {
        this.file = file;
    }

    public DataSource(File file, Lang syntax) {
        this.file = file;
        this.syntax = syntax;
    }

    public DataSource(File file, Calendar lastModified) {
        this.file = file;
        this.lastModified = lastModified;
    }

    public DataSource(File file, Lang syntax, Calendar lastModified) {
        this.file = file;
        this.syntax = syntax;
        this.lastModified = lastModified;
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
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     *
     * @return the inputStream
     * @throws java.io.FileNotFoundException
     */
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
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
