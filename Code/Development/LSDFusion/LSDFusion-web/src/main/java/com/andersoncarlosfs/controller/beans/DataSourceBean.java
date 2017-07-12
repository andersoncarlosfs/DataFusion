/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.faces.context.FacesContext;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataSourceBean {

    private DataSource dataSource;
    private UploadedFile file;

    public DataSourceBean() {
    }

    /**
     *
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     *
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     *
     * @return
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     *
     * @param file
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     *
     * @return
     */
    public String reset() {

        dataSource = new DataSource();

        return "edit";

    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void upload() throws Exception {
        
        Path path = Files.createTempFile("-", file.getFileName());
        
        Files.copy(file.getInputstream(), path, StandardCopyOption.REPLACE_EXISTING);
    
        dataSource.setPath(path);

    }

}
