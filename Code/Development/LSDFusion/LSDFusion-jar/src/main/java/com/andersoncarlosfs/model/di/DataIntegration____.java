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
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.base.block.FileMode;
import org.apache.jena.tdb.sys.SystemTDB;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 * 1
 * @author Anderson Carlos Ferreira da Silva
 */
//https://www.w3.org/wiki/LargeTripleStores
public abstract class DataIntegration____ implements AutoCloseable {

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
        Collection<Collection<RDFNode>> quotientSet = new HashSet<>();
        for (DataSource dataSource : dataSources) {
            Model model = ModelFactory.createDefaultModel();
            model.read("../../../../Datasets/INA/links.n3");
            SimpleSelector sN = new SimpleSelector() {
                @Override
                public boolean test(Statement s) {
                    return equivalenceProperties.contains(s.getPredicate());
                }
            };            
            StmtIterator statements = model.listStatements(sN);
            Set<Statement> visited = new HashSet<>();
            while (statements.hasNext()) {
                Statement statement = statements.next();
                if (!visited.contains(statement)) {
                    SimpleSelector sS = new SimpleSelector() {
                        @Override
                        public boolean test(Statement s) {
                            return (s.getSubject().equals(statement.getSubject())
                                    || s.getSubject().equals(statement.getObject())
                                    || s.getObject().equals(statement.getSubject())
                                    || s.getObject().equals(statement.getObject()))
                                    && equivalenceProperties.contains(s.getPredicate());
                        }
                    };
                    StmtIterator s = model.listStatements(sS);
                    Collection<RDFNode> equivalenceClass = new HashSet<>();
                    while (s.hasNext()) {
                        Statement d = s.next();
                        equivalenceClass.add(d.getSubject());
                        equivalenceClass.add(d.getObject());
                        visited.add(d);
                    }
                    quotientSet.add(equivalenceClass);
                    visited.add(statement);  
                }                              
            }
        }
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
