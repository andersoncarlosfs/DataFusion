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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.block.FileMode;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.SystemTDB;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class DataIntegration___ implements AutoCloseable {

    private static final Property equivalenceProperty = ResourceFactory.createProperty("http://www.andersoncarlosfs.com/df#equivalent");

    public static final Collection<Property> equivalenceProperties;

    static {

        //
        SystemTDB.setFileMode(FileMode.direct);

        //
        equivalenceProperties = new HashSet<>();
        equivalenceProperties.add(OWL.sameAs);
        equivalenceProperties.add(SKOS.exactMatch);

    }

    /**
     *
     * @return the temporaryDirectory
     */
    protected abstract Path getTemporaryDirectory();

    /**
     *
     * @param dataSources
     * @param equivalenceProperties
     * @return the equivalence classes
     * @throws IOException
     */
    public Collection<Collection<RDFNode>> findEquivalenceClasses(Collection<DataSource> dataSources, Collection<Property> equivalenceProperties) throws IOException {
        //RedBlackBalancedSearchTree quotientSet = new RedBlackBalancedSearchTree();        
        Model model = ModelFactory.createDefaultModel();
        for (DataSource dataSource : dataSources) {
            Location location = Location.create(Files.createTempDirectory(getTemporaryDirectory(), dataSource.getUUID().toString()).toString());
            Dataset dataset = TDBFactory.createDataset(location);
            RDFDataMgr.read(dataset, dataSource.getInputStream(), dataSource.getSyntax());
            dataset.getDefaultModel().listStatements().
                    filterKeep((Statement t) -> equivalenceProperties.contains(t.getPredicate()))
                    .forEachRemaining((Statement t) -> {
                        Statement statement = ResourceFactory.createStatement(t.getSubject(), equivalenceProperty, t.getObject());
                        if (!model.contains(statement)) {
                            model.add(statement);
                        }
                    });
        }
        Reasoner reasoner = ReasonerRegistry.getTransitiveReasoner();
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);
        Collection<Collection<RDFNode>> quotientSet = new HashSet<>();
        infModel.listSubjects().forEachRemaining((Resource r) -> {
            Collection<RDFNode> collection = new HashSet<>();
            infModel.listObjectsOfProperty(r, equivalenceProperty)
                    .forEachRemaining((RDFNode object) -> {
                        infModel.remove(r, equivalenceProperty, object);
                        infModel.remove(object.asResource(), equivalenceProperty, r);
                        infModel.listResourcesWithProperty(equivalenceProperty, object).forEachRemaining((Resource subject) -> {
                            infModel.remove(subject, equivalenceProperty, object);
                            infModel.remove(object.asResource(), equivalenceProperty, subject);
                            collection.add(subject);
                        });
                        collection.add(object);
                    });
            collection.add(r);
            quotientSet.add(collection);
        });
        quotientSet.removeIf(Collection::isEmpty);
        return quotientSet;
    }

    /**
     *
     * @see AutoCloseable#close()
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

        //
        TDB.closedown();
        // Apache Commons IO
        FileUtils.forceDelete(getTemporaryDirectory().toFile());
        // 
        //Files.walk(getTemporaryDirectory()).map(Path::toFile).sorted(Comparator.comparing(File::isDirectory)).forEach(File::delete);
        //getTemporaryDirectory().toFile().delete();

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

    private class EquivalenceClassTask extends RecursiveTask<Collection<RDFNode>> {

        private Model model;
        private final RDFNode node;

        public EquivalenceClassTask(Model model, RDFNode node) {
            this.model = model;
            this.node = node;
        }

        @Override
        protected Collection<RDFNode> compute() {
            Collection<ForkJoinTask> tasks = new ArrayList<>();
            Collection<RDFNode> equivalenceClass = new HashSet<>();
            model.listObjectsOfProperty(node.asResource(), equivalenceProperty).forEachRemaining((object) -> {
                model.remove(object.asResource(), equivalenceProperty, node);
                model.remove(node.asResource(), equivalenceProperty, object);
                //tasks.add((new EquivalenceClassTask(model, object)).fork());
                equivalenceClass.add(object);
                model.listResourcesWithProperty(equivalenceProperty, object).forEachRemaining((resource) -> {
                    model.remove(node.asResource(), equivalenceProperty, resource);
                    model.remove(resource, equivalenceProperty, node);
                    model.remove(object.asResource(), equivalenceProperty, resource);
                    model.remove(resource, equivalenceProperty, object);
                    //tasks.add((new EquivalenceClassTask(model, resource)).fork());
                    equivalenceClass.add(resource);
                });
            });
            //Collection<RDFNode> equivalenceClass = new HashSet<>();
            for (ForkJoinTask task : tasks) {
                equivalenceClass.addAll((Collection<RDFNode>) task.join());
            }
            equivalenceClass.add(node);
            return equivalenceClass;
        }

    }

}
