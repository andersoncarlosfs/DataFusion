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
 * 2,627s
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class DataIntegration_1M implements AutoCloseable {

    public static final Collection<Property> equivalenceProperties;
    private final Map<RDFNode, RDFNode> quotientSet = new HashMap();
    private final Map<RDFNode, Integer> sizes = new HashMap();

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

        //
        Map<RDFNode, Collection<RDFNode>> set = new HashMap();
        for (Map.Entry<RDFNode, RDFNode> entry : quotientSet.entrySet()) {
            RDFNode key = entry.getKey();
            RDFNode value = entry.getValue();
            RDFNode root = findEquivalenceClass(value);
            if (set.containsKey(root)) {
                set.get(root).add(key);
            } else {
                set.put(root, new HashSet<>(Arrays.asList(key)));
            }
        }
        return set.values().stream().collect(Collectors.toList());
    }

    /**
     * Find
     *
     * @param node
     * @return
     */
    private RDFNode findEquivalenceClass(RDFNode node) {
        if (quotientSet.containsKey(node)) {
            RDFNode root = node;
            while (root != quotientSet.get(root)) {
                root = quotientSet.get(root);
            }
            while (node != root) {
                RDFNode parent = quotientSet.get(node);
                quotientSet.put(node, root);
                node = parent;
            }
            return root;
        }
        quotientSet.put(node, node);
        sizes.put(node, 1);
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
        if (subjectParent == objectParent) {
            return;
        }
        Integer subjectParentSize = sizes.get(subjectParent);
        Integer objectParentSize = sizes.get(objectParent);
        if (subjectParentSize < objectParentSize) {
            objectParentSize += subjectParentSize;
            quotientSet.put(subjectParent, objectParent);
        } else {
            subjectParentSize += objectParentSize;
            quotientSet.put(objectParent, subjectParent);
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
