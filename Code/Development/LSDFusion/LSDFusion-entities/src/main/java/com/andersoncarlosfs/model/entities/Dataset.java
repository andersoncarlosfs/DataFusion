/*
 * To change this license header, choose License Headers in Project Attributes.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.entities;

import com.andersoncarlosfs.model.AbstractEntity;
import java.io.File;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class Dataset extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private File file;

    public Dataset() {
    }

    public Dataset(File file) {
        this.file = file;
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
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     *
     * @see AbstractEntity#getPrimaryKey()
     * @return the idDataset
     */
    @Override
    public Comparable getPrimaryKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
