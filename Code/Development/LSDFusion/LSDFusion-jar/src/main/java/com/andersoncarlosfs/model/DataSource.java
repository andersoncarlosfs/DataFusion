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
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataSource implements Serializable {

    private static final long serialVersionUID = 1L;
    private InputStream inputStream;

    public DataSource() {
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

}
