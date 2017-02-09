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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.base.block.FileMode;
import org.apache.jena.tdb.sys.SystemTDB;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 * 2,5s
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class DataIntegration_2M implements AutoCloseable {

    public static final Collection<Property> equivalenceProperties;
    private final Map<RDFNode, Collection<RDFNode>> quotientSet = new HashMap();

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
        model.read("../../../../Datasets/INA/links.n3");
        SimpleSelector selector = new SimpleSelector() {
            @Override
            public boolean test(Statement s) {
                return equivalenceProperties.contains(s.getPredicate());
            }
        };
        StmtIterator statements = model.listStatements(selector);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            RDFNode subject = statement.getSubject();
            RDFNode object = statement.getObject();
            unionEquivalenceClass(subject, object);
        }
        return quotientSet.values().stream().filter((Collection<RDFNode> c) -> c.size() > 1).collect(Collectors.toList());
    }

    /**
     * Find
     *
     * @param node
     * @return
     */
    private RDFNode findEquivalenceClass(RDFNode node) {
        if (quotientSet.containsKey(node)) {
            Collection nodeCollection = quotientSet.get(node);
            if (nodeCollection.size() > 1) {
                return node;
            }
            RDFNode parent = (RDFNode) nodeCollection.iterator().next();
            if (parent.equals(node)) {
                return parent;
            }
            return findEquivalenceClass(parent);
        }
        quotientSet.put(node, new HashSet<>(Arrays.asList(node)));
        return node;
    }

    /**
     * Union
     *
     * @param subject
     * @param object
     */
    private void unionEquivalenceClass(RDFNode subject, RDFNode object) {
        RDFNode subjectParent = findEquivalenceClass(subject);
        RDFNode objectParent = findEquivalenceClass(object);
        if (subjectParent.equals(objectParent)) {
            return;
        }
        Collection subjectCollection = quotientSet.get(subjectParent);
        Collection objectCollection = quotientSet.get(objectParent);
        if (subjectCollection.size() < objectCollection.size()) {
            objectCollection.addAll(subjectCollection);
            quotientSet.put(subjectParent, new HashSet<>(Arrays.asList(objectParent)));
        } else {
            subjectCollection.addAll(objectCollection);
            quotientSet.put(objectParent, new HashSet<>(Arrays.asList(subjectParent)));
        }
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
