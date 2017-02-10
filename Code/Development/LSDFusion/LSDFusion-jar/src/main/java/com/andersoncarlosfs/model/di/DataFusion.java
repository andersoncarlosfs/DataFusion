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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;

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
        if (links.length > 0) {
            this.links = Arrays.asList(links);
        } else {
            this.links = this.datasets;
        }
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
     * @throws java.io.IOException
     */
    public QuotientSet findEquivalenceClasses() throws IOException {
        QuotientSet quotientSet = new QuotientSet();
        for (DataSource dataSource : links) {
            if (dataSource.getFile().length() <= Runtime.getRuntime().freeMemory()) {
                
            }          
            Location location = Location.create(Files.createTempDirectory(getTemporaryDirectory(), null).toString());
                Dataset dataset = TDBFactory.createDataset(location);
                RDFDataMgr.read(dataset, dataSource.getInputStream(), dataSource.getSyntax());
                Model model = dataset.getDefaultModel();
            StmtIterator statements = model.listStatements(EQUIVALENCE_SELECTOR);
            while (statements.hasNext()) {
                Statement statement = statements.next();
                RDFNode subject = statement.getSubject();
                RDFNode object = statement.getObject();
                quotientSet.union(subject, object);
            }
        }
        return quotientSet;
    }

    /**
     *
     * @return the data fusion assessment
     */
    public DataFusionAssessment calculateScore() {
        return null;
    }

    /**
     * Disjoint-set data structure to keep track of a set of equivalence class
     * partitioned into a number of disjoint subsets
     *
     * @see <a href="http://algs4.cs.princeton.edu/15uf/">1.5 Case Study:
     * Union-Find</a>
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    public class QuotientSet extends HashMap<RDFNode, Collection<RDFNode>> {

        /**
         * Find
         *
         * @param node
         * @return
         */
        private RDFNode find(RDFNode node) {
            Collection nodeCollection = get(node);
            if (nodeCollection.size() > 1) {
                return node;
            }
            RDFNode parent = (RDFNode) nodeCollection.iterator().next();
            if (parent.equals(node)) {
                return parent;
            }
            return find(parent);
        }

        /**
         * Search
         *
         * @param node
         * @return
         */
        private Collection<RDFNode> search(RDFNode node) {
            return getOrDefault(find(node), Collections.EMPTY_LIST);
        }

        /**
         * Union
         *
         * @param subject
         * @param object
         */
        private void union(RDFNode subject, RDFNode object) {
            RDFNode subjectParent = subject;
            RDFNode objectParent = object;
            if (putIfAbsent(subject, new HashSet<>(Arrays.asList(subject))) != putIfAbsent(object, new HashSet<>(Arrays.asList(object)))) {
                subjectParent = find(subject);
                objectParent = find(object);
            }
            if (subjectParent.equals(objectParent)) {
                return;
            }
            Collection subjectCollection = get(subjectParent);
            Collection objectCollection = get(objectParent);
            if (subjectCollection.size() < objectCollection.size()) {
                objectCollection.addAll(subjectCollection);
                put(subjectParent, new HashSet<>(Arrays.asList(objectParent)));
            } else {
                subjectCollection.addAll(objectCollection);
                put(objectParent, new HashSet<>(Arrays.asList(subjectParent)));
            }
        }

        /**
         * @see Map#values()
         *
         * @return a view of the equivalence classes contained in this quotient
         * set
         */
        @Override
        public Collection<Collection<RDFNode>> values() {
            return super.values().parallelStream().filter((Collection<RDFNode> c) -> c.size() > 1).collect(Collectors.toList());
        }

    }

}
