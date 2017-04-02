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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.graph.FrontsNode;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
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

    private final Collection<DataSource> dataSources;

    public DataFusion(Collection<DataSource> dataSources) throws IOException {
        this.dataSources = dataSources;
    }

    /**
     *
     * @return the data fusion assessment
     * @throws java.io.IOException
     */
    public Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> getDataQualityAssessment() throws IOException {
        DataQualityEvaluation stream = new DataQualityEvaluation();
        for (DataSource dataSource : dataSources) {
            stream.parse(dataSource);
        }
        return stream.computeDataQualityAssessment();
    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class DataQualityEvaluation extends StreamRDFBase {

        private Map<Node, Map<Node, Map<Node, LocalDate>>> statements;
        private Map<Node, AtomicInteger> frequencies;
        private Set<LocalDate> freshnesses;
        //# These variables need to be removed
        //
        private LocalDate freshness;
        //#
        private DisjointSet<Node> equivalenceClasses;
        private Collection<Node> equivalenceProperties;
        private Collection<Collection<LinkedHashSet<Node>>> mappedProperties;

        public DataQualityEvaluation() {
            //
            frequencies = new HashMap();
            //
            freshnesses = new LinkedHashSet();
            //
            statements = new HashMap();
            //            
            equivalenceClasses = new DisjointSet();
            //
            mappedProperties = new HashSet();
        }

        /**
         *
         * @param dataSource
         */
        public void parse(DataSource dataSource) throws IOException {
            //            
            freshnesses.add(dataSource.getFreshness());
            //# These variables need to be removed
            //
            freshness = dataSource.getFreshness();
            //#
            //
            equivalenceProperties = new HashSet();
            //
            for (Property property : dataSource.getEquivalenceProperties()) {
                equivalenceProperties.add(property.asNode());
            }
            //
            for (Collection<LinkedHashSet<FrontsNode>> mappedNodes : dataSource.getMappedProperties()) {
                //
                for (Collection<FrontsNode> complexProperty : mappedNodes) {
                    //
                    Collection complexNode = new LinkedHashSet();
                    //
                    for (FrontsNode node : complexProperty) {
                        // 
                        complexNode.add(node.asNode());
                    }
                    //
                    mappedProperties.add(complexNode);
                }
            }
            //            
            RDFDataMgr.parse(this, dataSource.getCanonicalPath(), dataSource.getSyntax());
        }

        /**
         *
         * @param subject
         * @param predicate
         * @param object
         */
        private void parse(Node subject, Node predicate, Node object) {

            // Map statements
            statements.putIfAbsent(subject, new HashMap());
            // 
            Map<Node, Map<Node, LocalDate>> properties = statements.get(subject);
            properties.putIfAbsent(predicate, new HashMap());
            //
            Map<Node, LocalDate> objects = properties.get(predicate);
            //# These assignments need to be removed
            //
            objects.putIfAbsent(object, freshness);
            //
            if ((freshness != null) && (freshness.isBefore(objects.putIfAbsent(object, freshness)))) {
                objects.put(object, freshness);
            }
            //#

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
        public Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> computeDataQualityAssessment() {

            // 
            Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> computedStatements = new HashMap();

            // Loop equivalence classes
            for (Collection<Node> equivalenceClass : equivalenceClasses.disjointValues()) {
                // Map equivalence classes of statements      
                computedStatements.putIfAbsent(equivalenceClass, new HashMap());
                // Get properties of equivalence class
                Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>> computedProperties = computedStatements.get(equivalenceClass);
                // Loop subjects of an equivalence class
                for (Node subject : equivalenceClass) {
                    // Loop properties of a subject
                    for (Map.Entry<Node, Map<Node, LocalDate>> propertyEntry : statements.get(subject).entrySet()) {
                        //
                        Node property = propertyEntry.getKey();
                        Map<Node, LocalDate> objects = propertyEntry.getValue();
                        //
                        LinkedHashSet<Node> complexProperty = new LinkedHashSet();
                        complexProperty.add(property);
                        //
                        Collection<LinkedHashSet<Node>> mappedNodes = new HashSet();
                        mappedNodes.add(complexProperty);
                        //       
                        for (Iterator<Collection<LinkedHashSet<Node>>> iterator = mappedProperties.iterator(); iterator.hasNext();) {
                            //
                            mappedNodes = iterator.next();
                            //
                            if (mappedNodes.contains(complexProperty)) {
                                //
                                break;
                            } else {
                                //
                                //                                
                            }
                            //
                            mappedNodes = new HashSet();
                            mappedNodes.add(complexProperty);
                        }
                        //
                        for (LinkedHashSet<Node> complexNode : mappedNodes) {
                            // Map properties of equivalence class  
                            computedProperties.putIfAbsent(complexNode, new HashMap());
                            // Get objects of equivalence class
                            Map<Node, DataQualityAssessment> computedObjects = computedProperties.get(complexNode);
                            // Loop objects of a property
                            for (Map.Entry<Node, LocalDate> objectEntry : objects.entrySet()) {
                                //
                                Node object = objectEntry.getKey();                               
                                LocalDate freshness = objectEntry.getValue();
                                // Compute frequency
                                DataQualityAssessment assessment = new DataQualityAssessment();
                                assessment.setFrequency(frequencies.get(object));
                                assessment.setHomogeneity(new AtomicInteger(0));
                                // Map objects
                                computedObjects.putIfAbsent(object, assessment);
                                // Compute homogeneity                            
                                assessment = computedObjects.get(object);
                                assessment.getHomogeneity().incrementAndGet();
                                // 
                                for (Map.Entry<Node, DataQualityAssessment> computedObjectEntry : computedObjects.entrySet()) {
                                    Node node = computedObjectEntry.getKey();
                                    DataQualityAssessment value = computedObjectEntry.getValue();
                                    //
                                    if (!object.isLiteral()) {
                                        break;
                                    }
                                    //
                                    if (object.equals(node)) {
                                        continue;
                                    }
                                    //
                                    if (!node.isLiteral()) {
                                        continue;
                                    }
                                    //                                
                                    if (object.getLiteralValue().toString().contains(node.getLiteralValue().toString())) {
                                        assessment.getMorePrecise().add(node);
                                    }
                                    //
                                    if (node.getLiteralValue().toString().contains(object.getLiteralValue().toString())) {
                                        value.getMorePrecise().add(object);
                                    }
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
