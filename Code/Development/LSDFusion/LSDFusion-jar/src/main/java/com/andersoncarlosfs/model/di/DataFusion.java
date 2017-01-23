/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.enterprise.context.RequestScoped;
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

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusion {

    private final Path temporaryDirectory;
    private final Collection<DataSource> dataSources;

    public DataFusion(Path temporaryDirectory, DataSource... dataSources) {
        this.temporaryDirectory = temporaryDirectory;
        this.dataSources = Collections.unmodifiableCollection(Arrays.asList(dataSources));
    }

    /**
     *
     * @return the dataSources
     */
    public Collection<DataSource> getDataSources() {
        return dataSources;
    }

    public Collection<Collection<RDFNode>> getEquivalentClasses() throws IOException {
        Collection<Collection<RDFNode>> equivalentClasses = new HashSet<>();
        for (DataSource dataSource : dataSources) {
            Location location = Location.create(temporaryDirectory.toString());
            DatasetGraph rdfDatasetGraph = TDBFactory.createDatasetGraph(location);
            DatasetGraphTDB rdfDatasetGraphTDB = TDBInternal.getBaseDatasetGraphTDB(rdfDatasetGraph);
            TDBLoader.load(rdfDatasetGraphTDB, dataSource.getInputStream(), false);
            org.apache.jena.query.Dataset rdfDataset = TDBFactory.createDataset(location);
            rdfDataset.begin(ReadWrite.READ);
            StmtIterator iterator = rdfDataset.getDefaultModel().listStatements();
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
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
            rdfDataset.end();
        }
        return equivalentClasses;
    }

    private void calculateScore() {

    }

}
