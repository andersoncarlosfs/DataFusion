/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.store.DatasetGraphTDB;
import org.apache.jena.tdb.sys.TDBInternal;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataExtractionService {

    public Collection<Model> extractDataModel(File... files) throws IOException {
        Collection<Model> models = new HashSet<>();
        for (File file : files) {
            //models.add(RDFDataMgr.loadModel(file.getPath()));
        }
        return models;
    }

    public Collection<Collection<String>> getEquivalentClasses(File... files) throws IOException {
        Collection<Collection<String>> equivalentClasses = new HashSet<>();
        Path tempDirectory = Files.createTempDirectory(null);
        for (File file : files) {
            Location location = Location.create(tempDirectory.toString());
            DatasetGraph datasetGraph = TDBFactory.createDatasetGraph(location);
            DatasetGraphTDB datasetGraphTDB = TDBInternal.getBaseDatasetGraphTDB(datasetGraph);
            TDBLoader.load(datasetGraphTDB, new FileInputStream(file), false);
            Dataset dataset = TDBFactory.createDataset(location);
            dataset.begin(ReadWrite.READ);
            StmtIterator iterator = dataset.getDefaultModel().listStatements();
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                String subject = statement.getSubject().toString();
                String object = statement.getObject().toString();
                Collection<String> classe = null;
                for (Collection<String> resources : equivalentClasses) {
                    if (resources.contains(subject)) {
                        classe = resources;
                        break;
                    }
                }
                if (classe == null) {
                    classe = new HashSet<>();
                    classe.add(subject);
                    equivalentClasses.add(classe);
                }
                if (!classe.contains(object)) {
                    classe.add(object);
                }
            }
            dataset.end();
        }
        tempDirectory.toFile().delete();
        return equivalentClasses;
    }

}
