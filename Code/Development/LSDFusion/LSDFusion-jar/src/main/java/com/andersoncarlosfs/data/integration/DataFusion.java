/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration;

import com.andersoncarlosfs.data.model.DataQualityAssessment;
import com.andersoncarlosfs.data.model.Dataset;
import com.andersoncarlosfs.data.model.LinkedDataset;
import com.andersoncarlosfs.util.DisjointSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.atlas.lib.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.Quad;
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
    
    private final Collection<Dataset> datasets = new ArrayList<>();
    private final Collection<LinkedDataset> links = new ArrayList<>();
    
    public DataFusion(Collection<Dataset> datasets) throws IOException {
        for (Dataset dataset : datasets) {
            if (dataset instanceof LinkedDataset) {
                this.links.add((LinkedDataset) dataset);
            } else {
                this.datasets.add(dataset);
            }
        }
    }

    /**
     *
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public DisjointSet findEquivalenceClasses() throws IOException {
        EquivalenceClasses stream = new EquivalenceClasses();
        for (LinkedDataset dataset : links) {
            stream.equivalenceProperties = dataset.getEquivalenceProperties().stream().map(RDFNode::asNode).collect(Collectors.toList());
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        return stream;
    }

    /**
     *
     * @return the data fusion assessment
     * @throws java.io.IOException
     */
    public Sink calculateScore() throws IOException {
        DataQualityEvaluation stream = new DataQualityEvaluation(findEquivalenceClasses());
        for (Dataset dataset : datasets) {
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        stream.X();
        return null;
    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class EquivalenceClasses extends DisjointSet<Node> implements StreamRDF {
        
        private Collection<Node> equivalenceProperties;

        /**
         *
         * @param subject
         * @param predicate
         * @param object
         * @see DisjointSet#union(java.lang.Object, java.lang.Object)
         */
        public void union(Node subject, Node predicate, Node object) {
            if (equivalenceProperties.contains(predicate)) {
                super.union(subject, object);
            }
        }

        /**
         *
         * @see StreamRDF#start()
         */
        @Override
        public void start() {
        }

        /**
         *
         * @see StreamRDF#triple(org.apache.jena.graph.Triple)
         */
        @Override
        public void triple(Triple triple) {
            union(triple.getSubject(), triple.getPredicate(), triple.getObject());
        }

        /**
         *
         * @see StreamRDF#quad(org.apache.jena.sparql.core.Quad)
         */
        @Override
        public void quad(Quad quad) {
            union(quad.getSubject(), quad.getPredicate(), quad.getObject());
        }

        /**
         *
         * @see StreamRDF#base(java.lang.String)
         */
        @Override
        public void base(String base) {
        }

        /**
         *
         * @see StreamRDF#prefix(java.lang.String, java.lang.String)
         */
        @Override
        public void prefix(String prefix, String iri) {
        }

        /**
         *
         * @see StreamRDF#finish()
         */
        @Override
        public void finish() {
        }
        
    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class DataQualityEvaluation extends StreamRDFBase {
        
        private final Collection<Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>>> computedStatements;
        private final Map<Node, Map<Node, Map<Node, DataQualityAssessment>>> statements;
        private final Map<Node, DataQualityAssessment> assessments;
        
        public DataQualityEvaluation(DisjointSet<Node> equivalenceClasses) {
            computedStatements = new ArrayList();
            for (Collection equivalenceClasse : equivalenceClasses.disjointValues()) {
                Map assessment = new HashMap();
                assessment.put(equivalenceClasse, null);
                computedStatements.add(assessment);
            }
            statements = new HashMap();
            assessments = new HashMap();
        }

        /**
         *
         * @param subject
         * @param predicate
         * @param object
         */
        private void parse(Node subject, Node predicate, Node object) {
            //
            assessments.putIfAbsent(object, new DataQualityAssessment(0, 1));
            //
            DataQualityAssessment assessment = assessments.get(object);
            assessment.setFrequency(assessment.getFrequency() + 1);
            //
            statements.putIfAbsent(subject, new HashMap<>());
            //
            Map<Node, Map<Node, DataQualityAssessment>> properties = statements.get(subject);
            properties.putIfAbsent(predicate, new HashMap<>());
            //
            Map<Node, DataQualityAssessment> objects = properties.get(predicate);
            objects.put(object, assessment);
            //  
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
            for (Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> statements : computedStatements) {
                for (Collection<Node> equivalenceClasse : statements.keySet()) {
                    for (Node node : equivalenceClasse) {
                        Map<Node, Map<Node, DataQualityAssessment>> properties = this.statements.get(node);
                        if (properties == null) {
                            continue;
                        }
                        if (statements.putIfAbsent(equivalenceClasse, properties) != null) {
                            Map<Node, Map<Node, DataQualityAssessment>> computedProperties = statements.get(equivalenceClasse);
                            for (Map.Entry<Node, Map<Node, DataQualityAssessment>> entryProperty : properties.entrySet()) {
                                Node property = entryProperty.getKey();
                                Map<Node, DataQualityAssessment> objects = entryProperty.getValue();
                                if (computedProperties.putIfAbsent(property, objects) != null) {
                                    System.out.println("com.andersoncarlosfs.data.integration.DataFusion.DataQualityEvaluation.X()");
                                    Map<Node, DataQualityAssessment> computedObjects = computedProperties.get(property);
                                    for (Map.Entry<Node, DataQualityAssessment> entryObject : objects.entrySet()) {
                                        Node object = entryObject.getKey();
                                        DataQualityAssessment assessment = entryObject.getValue();
                                        if (computedObjects.putIfAbsent(object, assessment) != null) {
                                            assessment = computedObjects.get(object);
                                            assessment.setHomogeneity(assessment.getHomogeneity() + 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Map<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> statements : computedStatements) {
                for (Map.Entry<Collection<Node>, Map<Node, Map<Node, DataQualityAssessment>>> computedStatement : statements.entrySet()) {
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
    
}
