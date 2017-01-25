/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusion extends DataIntegration {

    private final Collection<DataSource> datasets;
    private final Collection<DataSource> links;
    private final Path temporaryDirectory;    

    public DataFusion(Collection<DataSource> datasets, DataSource... links) throws IOException {
        this.datasets = Collections.unmodifiableCollection(datasets);
        this.links = Collections.unmodifiableCollection(Arrays.asList(links));
        this.temporaryDirectory = Files.createTempDirectory(null);
    }

    /**
     *
     * @return the temporaryDirectory
     */
    @Override
    protected Path getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public Collection<Collection<RDFNode>> findEquivalentClasses() throws IOException {
        if (links.isEmpty()) {
            return findEquivalentClasses(datasets);
        }
        return findEquivalentClasses(links);
    }

    private void calculateScore() {

    }

}
