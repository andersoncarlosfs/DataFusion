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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Property;
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

    /**
     *
     * @return the equivalence classes
     * @throws IOException
     */
    public Collection<EquivalenceClass> findEquivalenceClasses() throws IOException {
        if (links.isEmpty()) {
            return super.findEquivalenceClasses((DataSource[]) datasets.toArray());
        }
        return super.findEquivalenceClasses((DataSource[]) datasets.toArray());
    }

    /**
     *
     * @return the equivalence classes
     * @throws IOException
     */
    private Collection<Map<Collection<RDFNode>, Property>> calculateScore() throws IOException {
        Map<RDFNode, Integer> occurrenceFrequency = new HashMap<>();
        for (DataSource dataSource : datasets) {
            Location location = Location.create(getTemporaryDirectory().toString());
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
        return null;
    }

}
