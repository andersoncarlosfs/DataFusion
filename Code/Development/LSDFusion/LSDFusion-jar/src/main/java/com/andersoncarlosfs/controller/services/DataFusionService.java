/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.di.DataFusion;
import com.andersoncarlosfs.model.entities.Dataset;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusionService {

    public void fuseData(Dataset... Datasets) {
    }

    public Collection<Collection<RDFNode>> getEquivalentClasses(Dataset... datasets) throws IOException {
        Path temporaryDirectory = Files.createTempDirectory(null);
        DataFusion dataFusion = new DataFusion(temporaryDirectory, datasets);
        temporaryDirectory.toFile().delete();
        return dataFusion.getEquivalentClasses();
    }

}
