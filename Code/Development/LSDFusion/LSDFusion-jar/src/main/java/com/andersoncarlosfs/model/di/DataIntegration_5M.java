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
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.base.block.FileMode;
import org.apache.jena.tdb.sys.SystemTDB;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 * 2,527s
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class DataIntegration_5M implements AutoCloseable {

    private static final Property equivalenceProperty = ResourceFactory.createProperty("http://www.andersoncarlosfs.com/df#equivalent");
    public static final Collection<Property> equivalenceProperties;
    private final Collection<Collection<RDFNode>> quotientSet = new HashSet<>();

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
        Model model = ModelFactory.createDefaultModel();
        Model m = ModelFactory.createDefaultModel();
        m.read("../../../../Datasets/INA/links.n3");
        SimpleSelector selector = new SimpleSelector() {
            @Override
            public boolean test(Statement s) {
                return equivalenceProperties.contains(s.getPredicate());
            }
        };
        m.listStatements().
                filterKeep((Statement t) -> equivalenceProperties.contains(t.getPredicate()))
                .forEachRemaining((Statement t) -> {
                    Statement statement = ResourceFactory.createStatement(t.getSubject(), equivalenceProperty, t.getObject());
                    if (!model.contains(statement)) {
                        model.add(statement);
                    }
                });
        model.listSubjects().forEachRemaining((Resource subject) -> {
            Collection<RDFNode> collection = new HashSet<>();
            model.listObjectsOfProperty(subject, equivalenceProperty)
                    .forEachRemaining((RDFNode object) -> {
                        model.listResourcesWithProperty(equivalenceProperty, object).forEachRemaining((Resource subject1) -> {
                            model.remove(subject1, equivalenceProperty, object);
                            collection.add(subject1);
                        });
                        collection.add(object);
                    });
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

}
