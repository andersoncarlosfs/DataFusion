/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.di.DataFusion;
import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.store.DatasetGraphTDB;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.vocabulary.OWL;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusionService {

    /**
     *
     * @param dataSources
     * @return the equivalent classes
     * @throws IOException
     */
    public Collection<Collection<RDFNode>> getEquivalentClasses(DataSource... dataSources) throws IOException {
        Path temporaryDirectory = Files.createTempDirectory(null);
        Collection<Collection<RDFNode>> equivalentClasses = new HashSet<>();
        for (DataSource dataSource : dataSources) {
            Location location = Location.create(Files.createTempDirectory(temporaryDirectory, null).toString());
            DatasetGraph datasetGraph = TDBFactory.createDatasetGraph(location);
            DatasetGraphTDB datasetGraphTDB = TDBInternal.getBaseDatasetGraphTDB(datasetGraph);
            TDBLoader.load(datasetGraphTDB, dataSource.getInputStream(), false);
            Dataset dataset = TDBFactory.createDataset(location);
            dataset.begin(ReadWrite.READ);
            StmtIterator iterator = dataset.getDefaultModel().listStatements();
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                RDFNode predicate = statement.getPredicate();
                if (predicate.equals(OWL.sameAs)) {
                    RDFNode subject = statement.getSubject();
                    RDFNode object = statement.getObject();
                    Collection<RDFNode> classe = null;
                    for (Collection<RDFNode> resources : equivalentClasses) {
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
            }
            dataset.end();
            dataset.close();
        }
        Files.createTempDirectory(null);
        return equivalentClasses;
    }

    public void calculateScore(DataSource... dataSources) throws IOException {
        Path temporaryDirectory = Files.createTempDirectory(null);
        Collection<Collection<RDFNode>> equivalentClasses = getEquivalentClasses(dataSources);
        Map<RDFNode, Integer> occurrenceFrequency = new Hashtable<>();
        for (DataSource dataSource : dataSources) {
            Location location = Location.create(Files.createTempDirectory(temporaryDirectory, null).toString());
            DatasetGraph datasetGraph = TDBFactory.createDatasetGraph(location);
            DatasetGraphTDB datasetGraphTDB = TDBInternal.getBaseDatasetGraphTDB(datasetGraph);
            TDBLoader.load(datasetGraphTDB, dataSource.getInputStream(), false);
            Dataset dataset = TDBFactory.createDataset(location);
            dataset.begin(ReadWrite.READ);
            StmtIterator iterator = dataset.getDefaultModel().listStatements();
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                RDFNode subject = statement.getSubject();
                RDFNode predicate = statement.getPredicate();
                RDFNode object = statement.getObject();
                
                
                
            }
            dataset.end();
            dataset.close();
        }
        Files.createTempDirectory(null);
    }

}
