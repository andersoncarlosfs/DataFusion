/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import com.andersoncarlosfs.model.entities.Dataset;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

/**
 * Abstract class to manipulate DAOs
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class AbstractDataIntegration implements Serializable {

    private final Path temporaryDirectory;
    private final Collection<Dataset> datasets;

    public AbstractDataIntegration(Path temporaryDirectory, Dataset... datasets) throws IOException {
        this.temporaryDirectory = temporaryDirectory;
        this.datasets = Arrays.asList(datasets);
    }

    /**
     * @return the temporaryDirectory
     * @throws java.io.IOException
     */
    protected final Path getTemporaryDirectory() throws IOException {
        return temporaryDirectory;
    }

    /**
     * @return the datasets
     */
    public Collection<Dataset> getDatasets() {
        return datasets;
    }

}
