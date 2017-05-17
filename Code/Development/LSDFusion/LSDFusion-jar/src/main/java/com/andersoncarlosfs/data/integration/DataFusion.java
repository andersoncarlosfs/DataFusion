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
import java.time.temporal.ChronoUnit;
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
import org.apache.jena.riot.system.StreamRDF;
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

    private class DataQualityCriteria {
        
        private LocalDate freshness;
        
        private Float reliability;

        public DataQualityCriteria() {
        }

        public DataQualityCriteria(LocalDate freshness, Float reliability) {
            this.freshness = freshness;
            this.reliability = reliability;
        }                
        
    }
    
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
    private class DataQualityEvaluation implements StreamRDF {

        private Map<Node, Map<Node, Map<Node, DataQualityCriteria>>> statements;
        private Set<LocalDate> freshnesses;
        private Set<Float> reliabilities;
        //# These variables need to be removed
        // 
        //
        private DataQualityCriteria criteria;
        private Map<Node, Map<Node, AtomicInteger>> frequencies;
        //#
        private DisjointSet<Node> equivalenceClasses;
        private Collection<Node> equivalenceProperties;
        private Collection<Collection<LinkedHashSet<Node>>> mappedProperties;

        public DataQualityEvaluation() {
            //
            freshnesses = new LinkedHashSet<>();
            //
            reliabilities = new LinkedHashSet<>();
            //
            frequencies = new HashMap<>();
            //
            statements = new HashMap<>();
            //            
            equivalenceClasses = new DisjointSet<>();
            //
            equivalenceProperties = new HashSet();
            //
            mappedProperties = new HashSet();                  
        }

        /**
         *
         * @param dataSource
         */
        public void parse(DataSource dataSource) throws IOException {
            // Add a freshness           
            freshnesses.add(dataSource.getFreshness());
            reliabilities.add(dataSource.getReliability());
            //# These variables need to be removed
            //
            criteria = new DataQualityCriteria();
            // Asign the freshness
            criteria.freshness = dataSource.getFreshness();
            // Asign the reliability
            criteria.reliability = dataSource.getReliability();
            //#
            // Clear
            equivalenceProperties.clear();
            //
            for (Property property : dataSource.getEquivalenceProperties()) {
                equivalenceProperties.add(property.asNode());
            }
            //
            for (Collection<LinkedHashSet<Property>> mappedNodes : dataSource.getMappedProperties()) {
                //
                for (Collection<Property> complexProperty : mappedNodes) {
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
            RDFDataMgr.parse(this, dataSource.getPath().toString(), dataSource.getSyntax());
        }

        /**
         *
         * @param subject
         * @param predicate
         * @param object
         */
        private void parse(Node subject, Node predicate, Node object) {
            
            // Find equivalence classes
            if (equivalenceProperties.contains(predicate)) {
                //
                equivalenceClasses.union(subject, object);
                //
                return;
            }
            
            // Compute frequencies
            frequencies.putIfAbsent(predicate, new HashMap<>());
            Map<Node, AtomicInteger> objects1= frequencies.get(predicate);                       
            objects1.putIfAbsent(object, new AtomicInteger(0));
            objects1.get(object).incrementAndGet();

            // Map statements
            statements.putIfAbsent(subject, new HashMap());
            // 
            Map<Node, Map<Node, DataQualityCriteria>> properties = statements.get(subject);
            properties.putIfAbsent(predicate, new HashMap());
            //
            Map<Node, DataQualityCriteria> objects2 = properties.get(predicate);
            //# These assignments need to be removed
            // 
            objects2.putIfAbsent(object, criteria);
            //
            DataQualityCriteria criteria2 = objects2.get(object);
            //
            if (criteria.freshness != null) {
                // 
                if (criteria.freshness.isBefore(criteria2.freshness)) {
                    // Put the oldest date
                    criteria2.freshness = criteria.freshness;
                }
            }
            //
            if (criteria.reliability != null) {
                // 
                if (criteria.reliability > criteria2.reliability) {
                    // Put the oldest date
                    criteria2.reliability = criteria.reliability;
                }
            }
            //#           

        }

        @Override
        public void start() {
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
            triple(quad.asTriple());
        }

        @Override
        public void base(String base) {
        }

        @Override
        public void prefix(String prefix, String iri) {
        }

        @Override
        public void finish() {
        }

        /**
         *
         * @return
         */
        public Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> computeDataQualityAssessment() {

            //
            Map<Node, Map<Node, Float>> computedFrequencies = new HashMap<>();
            //
            for (Map.Entry<Node, Map<Node, AtomicInteger>> entry1 : frequencies.entrySet()) {
                //
                Node property = entry1.getKey();
                Map<Node, AtomicInteger> objects = entry1.getValue();
                //
                computedFrequencies.putIfAbsent(property, new HashMap<>());
                Map<Node, Float> computedObjects = computedFrequencies.get(property);
                //
                Float computedFrequency = 0F;
                //
                for (AtomicInteger frequency : objects.values()) {                  
                    computedFrequency +=  frequency.floatValue();
                }
                //
                for (Map.Entry<Node, AtomicInteger> entry2 : objects.entrySet()) {
                    //
                    Node object = entry2.getKey();
                    AtomicInteger frequency = entry2.getValue();                    
                    //
                    computedObjects.putIfAbsent(object, (frequency.floatValue() / computedFrequency));
                }
                
            }
            
            //
            Float durations = new Float(0);
            //
            for (LocalDate freshness : freshnesses) {
                //
                if (freshness == null) {
                    continue;
                }
                //
                durations += ChronoUnit.DAYS.between(LocalDate.now(), freshness);
            }

            //
            Map<LocalDate, Float> computedFreshness = new HashMap();
            //            
            for (LocalDate freshness : freshnesses) {
                //
                if (freshness == null) {
                    //
                    computedFreshness.putIfAbsent(freshness, 0.0f);
                } else {
                    //
                    computedFreshness.putIfAbsent(freshness, ChronoUnit.DAYS.between(LocalDate.now(), freshness) / durations);
                }
            }

            // 
            Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> computedStatements = new HashMap();

            //
            Collection<Collection<Node>> equivalenceClasses = this.equivalenceClasses.disjointValues();

            // Loop equivalence classes
            for (Collection<Node> equivalenceClass : equivalenceClasses) {
                // Map equivalence classes of statements      
                computedStatements.putIfAbsent(equivalenceClass, new HashMap());
                // Get properties of equivalence class
                Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>> computedProperties = computedStatements.get(equivalenceClass);
                // Loop subjects of an equivalence class
                for (Node subject : equivalenceClass) {
                    //
                    if (statements.get(subject) == null) {
                        continue;
                    }
                    // Loop properties of a subject
                    for (Map.Entry<Node, Map<Node, DataQualityCriteria>> propertyEntry : statements.get(subject).entrySet()) {
                        //
                        Node property = propertyEntry.getKey();
                        Map<Node, DataQualityCriteria> objects = propertyEntry.getValue();
                        //
                        LinkedHashSet<Node> complexProperty = new LinkedHashSet();
                        complexProperty.add(property);
                        //
                        Collection<LinkedHashSet<Node>> mappedNodes = new HashSet();
                        mappedNodes.add(complexProperty);
                        // Loop properties
                        for (Iterator<Collection<LinkedHashSet<Node>>> iterator = mappedProperties.iterator(); iterator.hasNext();) {
                            //
                            mappedNodes = iterator.next();
                            //
                            if (mappedNodes.contains(complexProperty)) {
                                //
                                break;
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
                            for (Map.Entry<Node, DataQualityCriteria> objectEntry : objects.entrySet()) {
                                //
                                Node object = objectEntry.getKey();
                                DataQualityCriteria criteria3 = objectEntry.getValue();
                                //
                                DataQualityAssessment assessment = new DataQualityAssessment();
                                // Map objects
                                computedObjects.putIfAbsent(object, assessment);
                                //                         
                                assessment = computedObjects.get(object);
                                // Compute reliability
                                assessment.setReliability(criteria3.reliability);
                                //
                                if (assessment.getReliability() == null) {
                                    assessment.setReliability(0F);
                                }
                                // Compute freshness
                                assessment.setFreshness(computedFreshness.get(criteria3.freshness));
                                // Compute frenquency
                                if (assessment.getFrequency() == null) {
                                    assessment.setFrequency(computedFrequencies.get(property).get(object));
                                }
                                // Compute homogeneity                                
                                Float homogeneity = assessment.getHomogeneity();
                                if (homogeneity == null) {
                                    homogeneity = 0F;
                                }
                                homogeneity++;            
                                assessment.setHomogeneity(homogeneity);
                                //
                                if (assessment.getMorePrecise() == null) {
                                    assessment.setMorePrecise(new HashSet());
                                }
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
                // Compute homogeneity    
                for (Map.Entry<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>> entry : computedProperties.entrySet()) {
                    //
                    LinkedHashSet<Node> property = entry.getKey();
                    Map<Node, DataQualityAssessment> assessments = entry.getValue();
                    //
                    Float computedHomogeneity = 0F;
                    //
                    for (DataQualityAssessment homogeneity : assessments.values()) {
                        computedHomogeneity += homogeneity.getHomogeneity();
                    }
                    //
                    for (DataQualityAssessment assessment : assessments.values()) {
                        Float homogeneity = assessment.getHomogeneity() / computedHomogeneity;
                        assessment.setHomogeneity(homogeneity);
                    }
                }
                
            }

            return computedStatements;

        }

    }

}
