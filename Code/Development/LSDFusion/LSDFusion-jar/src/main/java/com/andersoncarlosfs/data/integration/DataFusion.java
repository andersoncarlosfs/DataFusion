/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration;

import com.andersoncarlosfs.data.model.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusion {

    /**
     * 
     */
    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    /**
     * 
     */
    public static final Selector EQUIVALENCE_SELECTOR = new EquivalenceSelector(EQUIVALENCE_PROPERTIES);

    private final Collection<DataSource> datasets;
    private final Collection<DataSource> links;

    public DataFusion(Collection<DataSource> datasets, DataSource... links) throws IOException {
        this.datasets = Collections.unmodifiableCollection(datasets);
        if (links.length > 0) {
            this.links = Arrays.asList(links);
        } else {
            this.links = this.datasets;
        }
    }

    /**
     *
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public QuotientSet findEquivalenceClasses() throws IOException {
        QuotientSet quotientSet = new QuotientSet();
        for (DataSource dataSource : links) {
            Model model = RDFDataMgr.loadModel(dataSource.getFile().getCanonicalPath(), dataSource.getSyntax());
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
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private static class EquivalenceSelector implements Selector {

        private final Collection<Property> properties;

        public EquivalenceSelector(Collection<Property> properties) {
            this.properties = properties;
        }

        /**
         *
         * @see Selector#isSimple()
         * @return
         */
        @Override
        public boolean isSimple() {
            return false;
        }

        /**
         *
         * @see Selector#getSubject()
         * @return
         */
        @Override
        public Resource getSubject() {
            return null;
        }

        /**
         *
         * @see Selector#getPredicate()
         * @return
         */
        @Override
        public Property getPredicate() {
            return null;
        }

        /**
         *
         * @see Selector#getObject()
         * @return
         */
        @Override
        public RDFNode getObject() {
            return null;
        }

        /**
         *
         * @see Selector#test(java.lang.Object)
         * @param s
         * @return
         */
        @Override
        public boolean test(Statement s) {
            return properties.contains(s.getPredicate());
        }

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
    public class QuotientSet {

        private HashMap<RDFNode, Collection<RDFNode>> map = new HashMap<>();

        private QuotientSet() {
        }

        /**
         * Find
         *
         * @param node
         * @return
         */
        private RDFNode find(RDFNode node) {
            Collection nodeCollection = map.get(node);
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
        public Collection<RDFNode> search(RDFNode node) {
            return map.getOrDefault(find(node), Collections.EMPTY_LIST);
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
            if (map.putIfAbsent(subject, new HashSet<>(Arrays.asList(subject))) != map.putIfAbsent(object, new HashSet<>(Arrays.asList(object)))) {
                subjectParent = find(subject);
                objectParent = find(object);
            }
            if (subjectParent.equals(objectParent)) {
                return;
            }
            Collection subjectCollection = map.get(subjectParent);
            Collection objectCollection = map.get(objectParent);
            if (subjectCollection.size() < objectCollection.size()) {
                objectCollection.addAll(subjectCollection);
                map.put(subjectParent, new HashSet<>(Arrays.asList(objectParent)));
            } else {
                subjectCollection.addAll(objectCollection);
                map.put(objectParent, new HashSet<>(Arrays.asList(subjectParent)));
            }
        }

        /**
         *
         * @return a view of the equivalence classes contained in this quotient
         * set
         */
        public Collection<Collection<RDFNode>> values() {
            return map.values().parallelStream().filter((Collection<RDFNode> c) -> c.size() > 1).collect(Collectors.toList());
        }

    }

}
