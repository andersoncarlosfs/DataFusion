/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.Constants;
import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.DefaultUploadedFile;
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

        FacesContext.getCurrentInstance().getExternalContext().log(file.getFileName());
       
        FacesContext.getCurrentInstance().getExternalContext().log(System.getProperty("java.io.tmpdir"));
        
        String prefix = FilenameUtils.getBaseName(file.getFileName()); 
        String suffix = FilenameUtils.getExtension(file.getFileName());
        
        file.write(prefix);
        
        DefaultUploadedFile
        
        Path file = Files.createTempFile(prefix, suffix);
        
        try (InputStream input = uploadedFile.getInputStream()) {
    Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
}
        
        /*
        Path path = Files.createTempFile(System.getProperty("java.io.tmpdir"), file.getFileName(), (FileAttribute<?>) null);
/*
        file.write(path.toString());

        dataSource.setPath(path);
*/
    }

}
