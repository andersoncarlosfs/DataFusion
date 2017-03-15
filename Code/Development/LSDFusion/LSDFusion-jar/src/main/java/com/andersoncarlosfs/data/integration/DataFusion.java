/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration;

import com.andersoncarlosfs.data.model.DataQualityAssessment;
import com.andersoncarlosfs.data.model.Dataset;
import com.andersoncarlosfs.util.DisjointSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.atlas.lib.Sink;
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

    private final Collection<Dataset> datasets;

    public DataFusion(Collection<Dataset> datasets) throws IOException {
        this.datasets = datasets;
    }

    /**
     *
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public Collection<Collection<Node>> findEquivalenceClasses() throws IOException {
        DataQualityEvaluation stream = new DataQualityEvaluation();
        for (Dataset dataset : datasets) {
            stream.setEquivalenceProperties(dataset.getEquivalenceProperties());
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        return stream.equivalenceClasses.disjointValues();
    }

    /**
     *
     * @return the data fusion assessment
     * @throws java.io.IOException
     */
    public Sink calculateScore() throws IOException {
        DataQualityEvaluation stream = new DataQualityEvaluation();
        for (Dataset dataset : datasets) {
            stream.setEquivalenceProperties(dataset.getEquivalenceProperties());
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        stream.X();
        return null;
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
         * @param equivalenceProperties the equivalenceProperties to set
         */
        public void setEquivalenceProperties(Collection<Property> equivalenceProperties) {
            if (equivalenceProperties != null) {
                this.equivalenceProperties = equivalenceProperties.stream().map(RDFNode::asNode).collect(Collectors.toList());
            } else {
                this.equivalenceProperties = Collections.EMPTY_LIST;
            }
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

            // Calculate frequencies
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
        public void X() {

            //
            Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> computedStatements = new HashMap<>();

            //
            for (Collection<Node> equivalenceClasse : equivalenceClasses.disjointValues()) {
                //        
                computedStatements.putIfAbsent(equivalenceClasse, new HashMap<>());
                //
                Map<Node, Map<Node, DataQualityAssessment>> computedProperties = computedStatements.get(equivalenceClasse);
                //
                for (Node node : equivalenceClasse) {
                    //
                    for (Map.Entry<Node, Set<Node>> propertyEntry : statements.get(node).entrySet()) {
                        //
                        Node property = propertyEntry.getKey();
                        Set<Node> objects = propertyEntry.getValue();
                        //        
                        computedProperties.putIfAbsent(property, new HashMap<>());
                        //
                        Map<Node, DataQualityAssessment> computedObjects = computedProperties.get(property);
                        //
                        for (Node object : objects) {
                            //
                            DataQualityAssessment assessment = new DataQualityAssessment();
                            assessment.setFrequency(frequencies.get(object));
                            assessment.setHomogeneity(new AtomicInteger(0));
                            //
                            computedObjects.putIfAbsent(object, assessment);
                            //                            
                            assessment = computedObjects.get(object);
                            assessment.getHomogeneity().incrementAndGet();
                        }
                    }
                }
            }

            //
            for (Map.Entry<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> computedStatement : computedStatements.entrySet()) {
                Collection<Node> equivalenceClasse = computedStatement.getKey();
                Map<Node, Map<Node, DataQualityAssessment>> computedProperties = computedStatement.getValue();
                System.out.println(equivalenceClasse.toString());
                for (Map.Entry<Node, Map<Node, DataQualityAssessment>> entry : computedProperties.entrySet()) {
                    Node key = entry.getKey();
                    Map<Node, DataQualityAssessment> value = entry.getValue();
                    for (Map.Entry<Node, DataQualityAssessment> entry1 : value.entrySet()) {
                        Node key1 = entry1.getKey();
                        DataQualityAssessment value1 = entry1.getValue();
                        System.out.println("    " + key + " " + key1 + "    " + value1.getFrequency() + "   " + value1.getHomogeneity());
                    }
                }
            }
        }

    }

}
