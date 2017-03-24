/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration;

import com.andersoncarlosfs.data.model.DataQualityAssessment;
import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.util.DisjointSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 * http://algs4.cs.princeton.edu/42digraph/
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
    public static enum ComparatorRules {

        NUMBER_MAX,
        NUMBER_MIN,
        TEXT_CONTAINS;

    }

    private final Collection<DataSource> dataSources;

    public DataFusion(Collection<DataSource> dataSources) throws IOException {
        this.dataSources = dataSources;
    }

    /**
     *
     * @return the data fusion assessment
     * @throws java.io.IOException
     */
    public Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> getDataQualityAssessment() throws IOException {
        DataQualityEvaluation stream = new DataQualityEvaluation();
        for (DataSource dataSource : dataSources) {
            stream.setParameters(dataSource);
            RDFDataMgr.parse(stream, dataSource.getCanonicalPath(), dataSource.getSyntax());
        }
        return stream.calculateDataQualityAssessment();
    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class DataQualityEvaluation extends StreamRDFBase {

        private Map<Node, Map<Node, Set<Node>>> statements;
        private Map<Node, AtomicInteger> frequencies;
        private DisjointSet<Node> equivalenceClasses;
        private Collection<Node> equivalenceProperties;
        private Map<Node, ComparatorRules> comparatorRules;

        public DataQualityEvaluation() {
            //
            this.frequencies = new HashMap();
            //
            this.statements = new HashMap();
            //            
            this.equivalenceClasses = new DisjointSet<>();
        }

        /**
         *
         * @param dataSource
         */
        public void setParameters(DataSource dataSource) {
            this.equivalenceProperties = dataSource.getEquivalenceProperties().stream().map(RDFNode::asNode).collect(Collectors.toList());
            this.comparatorRules = dataSource.getComparatorRules().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().asNode(), e -> e.getValue()));
        }

        /**
         *
         * @param subject
         * @param predicate
         * @param object
         */
        private void parse(Node subject, Node predicate, Node object) {

            // Map statements
            statements.putIfAbsent(subject, new HashMap<>());
            // 
            Map<Node, Set<Node>> properties = statements.get(subject);
            properties.putIfAbsent(predicate, new HashSet<>());
            //
            Set<Node> objects = properties.get(predicate);
            objects.add(object);

            // Compute frequencies
            frequencies.putIfAbsent(object, new AtomicInteger(0));
            frequencies.get(object).incrementAndGet();

            // Find equivalence classes
            if (equivalenceProperties.contains(predicate)) {
                equivalenceClasses.union(subject, object);
            }

        }

        /**
         *
         * @see StreamRDFBase#triple(org.apache.jena.graph.Triple)
         */
        @Override
        public void triple(Triple triple) {
            parse(triple.getSubject(), triple.getPredicate(), triple.getObject());
        }

        /**
         *
         * @see StreamRDFBase#quad(org.apache.jena.sparql.core.Quad)
         */
        @Override
        public void quad(Quad quad) {
            parse(quad.getSubject(), quad.getPredicate(), quad.getObject());
        }

        /**
         *
         * @return
         */
        public Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> calculateDataQualityAssessment() {

            // 
            Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> computedStatements = new HashMap<>();

            // Loop equivalence classes
            for (Collection<Node> equivalenceClass : equivalenceClasses.disjointValues()) {
                // Map equivalence classes of statements      
                computedStatements.putIfAbsent(equivalenceClass, new HashMap<>());
                // Get properties of equivalence class
                Map<Node, Map<Node, DataQualityAssessment>> computedProperties = computedStatements.get(equivalenceClass);
                // Loop subjects of an equivalence class
                for (Node subject : equivalenceClass) {
                    // Loop properties of a subject
                    for (Map.Entry<Node, Set<Node>> propertyEntry : statements.get(subject).entrySet()) {
                        //
                        Node property = propertyEntry.getKey();
                        Set<Node> objects = propertyEntry.getValue();
                        // Map properties of equivalence class  
                        computedProperties.putIfAbsent(property, new HashMap<>());
                        // Get objects of equivalence class
                        Map<Node, DataQualityAssessment> computedObjects = computedProperties.get(property);
                        // Loop objects of a property
                        for (Node object : objects) {
                            // Compute frequency
                            DataQualityAssessment assessment = new DataQualityAssessment();
                            assessment.setFrequency(frequencies.get(object));
                            assessment.setHomogeneity(new AtomicInteger(0));
                            // Map objects
                            computedObjects.putIfAbsent(object, assessment);
                            // Compute homogeneity                            
                            assessment = computedObjects.get(object);
                            assessment.getHomogeneity().incrementAndGet();
                            // Loop objects of a property
                            for (Node node : objects) {
                                //
                                if (object.equals(node)) {
                                    continue;
                                }
                                ComparatorRules comparatorRule = comparatorRules.get(property);
                                //
                                if (comparatorRule == null) {
                                    break;
                                }
                                //
                                try {
                                    switch (comparatorRule) {
                                        case NUMBER_MAX:
                                            break;
                                        case NUMBER_MIN:
                                            break;
                                        case TEXT_CONTAINS:
                                            break;
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            return computedStatements;

        }

    }

}
