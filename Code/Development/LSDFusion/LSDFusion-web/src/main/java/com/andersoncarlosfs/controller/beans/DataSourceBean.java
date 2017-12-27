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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
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

        file = null;

        return "edit?faces-redirect=true";

    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void upload() throws Exception {

        if (file.getSize() > 0) {

            Path path = Files.createTempDirectory(null).resolve(file.getFileName());

            Files.copy(file.getInputstream(), path, StandardCopyOption.REPLACE_EXISTING);

            dataSource.setPath(path);

        } else {

            file = null;

        }

    }

    /**
     *
     */
    @FacesConverter(value = "reliabilityConverter")
    public static class ReliabilityConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {            
            return new Float(value) / 100;
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            return Float.toString((Float) value * 100);
        }

    }

}
