/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
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
public abstract class DataIntegration implements AutoCloseable {

    /**
     *
     * @return the temporaryDirectory
     */
    protected abstract Path getTemporaryDirectory();

    /**
     *
     * @param dataSources
     * @return the equivalent classes
     * @throws IOException
     */
    public Collection<Collection<RDFNode>> findEquivalentClasses(Collection<DataSource> dataSources) throws IOException {
        Collection<Collection<RDFNode>> equivalentClasses = new HashSet<>();
        for (DataSource dataSource : dataSources) {
            Location location = Location.create(getTemporaryDirectory().toString());
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
        return equivalentClasses;
    }

    /**
     *
     * @see AutoCloseable#close()
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        getTemporaryDirectory().toFile().delete();
    }

    /**
     * @see Object#finalize()
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

}
