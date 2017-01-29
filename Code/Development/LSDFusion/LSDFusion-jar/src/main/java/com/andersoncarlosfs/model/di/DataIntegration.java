/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import com.andersoncarlosfs.model.EquivalenceClass;
import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
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
     * @return the equivalence classes
     * @throws IOException
     */
    public Collection<EquivalenceClass> findEquivalenceClasses(DataSource... dataSources) throws IOException {
        Collection<EquivalenceClass> equivalenceClasses = new HashSet<>();
        for (DataSource dataSource : dataSources) {
            Location location = Location.create(Files.createTempDirectory(getTemporaryDirectory(), null).toString());
            Dataset dataset = TDBFactory.createDataset(location);
            RDFDataMgr.read(dataset, dataSource.getInputStream(), dataSource.getSyntax());
            StmtIterator iterator = dataset.getDefaultModel().listStatements();
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                RDFNode predicate = statement.getPredicate();
                if (predicate.equals(OWL.sameAs)) {
                    RDFNode subject = statement.getSubject();
                    RDFNode object = statement.getObject();
                    EquivalenceClass classe = null;
                    for (EquivalenceClass resources : equivalenceClasses) {
                        if (resources.contains(subject)) {
                            classe = resources;
                            break;
                        }
                    }
                    if (classe == null) {
                        classe = new EquivalenceClass(OWL.sameAs);
                        classe.add(subject);
                        equivalenceClasses.add(classe);
                    }
                    if (!classe.contains(object)) {
                        classe.add(object);
                    }
                }
            }
        }
        return equivalenceClasses;
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
