/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.util.DisjointMap;
import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.model.Rule;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import com.andersoncarlosfs.data.model.assessments.DataQualityAssessment;
import com.andersoncarlosfs.data.model.control.DataQualityControl;
import com.andersoncarlosfs.data.util.Function;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 *
 * http://algs4.cs.princeton.edu/42digraph/
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionProcessor {

    private class DataQualityRecords implements DataQualityControl {

        private Float frequency;
        private Integer duplicates;
        private Float homogeneity;
        private Collection<DataSource> dataSources;

        public DataQualityRecords() {
            this.frequency = new Float(0);
            this.duplicates = new Integer(0);
            this.frequency = new Float(0);
            this.dataSources = new HashSet<>();
        }

        @Override
        public Float getFrequency() {
            return frequency;
        }

        @Override
        public Float getHomogeneity() {
            return homogeneity;
        }

        @Override
        public Float getReliability() {
            Float value = null;
            for (DataSource d : dataSources) {
                if (d.getReliability() == null) {
                    continue;
                }
                if (value == null) {
                    value = d.getReliability();
                }
                value = Math.max(value, d.getReliability());
            }
            return value;
        }

        @Override
        public Float getFreshness() {
            Float value = null;
            for (DataSource d : dataSources) {
                if (d.getReliability() == null) {
                    continue;
                }
                if (value == null) {
                    value = d.getReliability();
                }
                value = Math.min(value, d.getReliability());
            }
            return value;
        }

        @Override
        public Collection<RDFNode> getMorePrecise() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone(); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class DataQualityInformation extends DataQualityRecords implements DataQualityAssessment {

        @Override
        public Float getTrustiness() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class DataFusionInformation implements DataFusionAssessment {

        private Collection<Triple> duplicates;

        @Override
        public Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Collection<Triple> getDuplicates() {
            return duplicates;
        }

        @Override
        public Model getModel() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    private DataFusionInformation data = new DataFusionInformation();

    /**
     *
     * @param rules
     * @param dataSources
     * @param duplicatesAllowed
     * @throws IOException
     * @throws java.lang.CloneNotSupportedException
     */
    public DataFusionProcessor(Collection<DataSource> dataSources, Collection<Rule> rules, boolean duplicatesAllowed) throws IOException, CloneNotSupportedException {
        // Rules processing
        DisjointMap<Property, Collection<Function>> parameters = new DisjointMap<>();

        for (Rule rule : rules) {

            Collection<Function> functions = rule.getFunctions();

            Property last = null;

            for (Property current : rule.getProperties()) {

                // Attaching zero or more functions a property 
                parameters.putIfAbsent(current, new HashSet<>());
                parameters.get(current).addAll(functions);

                // Grouping the properties
                if (functions.contains(Function.UNION)) {
                    parameters.union(last, current);
                }

                last = current;

            }

        }

        // Data souces processing
        Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> statements = new DisjointMap<>();

        Map<RDFNode, Map<RDFNode, DataQualityRecords>> complements = new DisjointMap<>();

        for (DataSource dataSource : dataSources) {

            Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());

            StmtIterator iterator = model.listStatements();

            while (iterator.hasNext()) {

                Statement statement = iterator.next();

                Resource subject = statement.getSubject();
                Property property = statement.getPredicate();
                RDFNode object = statement.getObject();

                //Equivalence classes processing     
                Map classes = statements;

                classes.putIfAbsent(subject, new HashMap<>());
                classes = (Map) classes.get(subject);

                classes.putIfAbsent(property, new HashMap<>());
                classes = (Map) classes.get(property);

                classes.putIfAbsent(object, new HashMap<>());
                classes = (Map) classes.get(object);

                // Warning, the statement is already present in the dataSouce           
                Object present = classes.putIfAbsent(dataSource, 0);

                if (present == null && parameters.get(property).contains(Function.CONSTRUCT)) {
                    // Grouping the subjects
                    ((DisjointMap) statements).union(subject, object);
                }

                // Computing the number of duplicate statements
                classes.put(dataSource, ((int) classes.get(dataSource)) + 1);

                // Frequencies processing
                Map frequencies = complements;

                frequencies.putIfAbsent(property, new HashMap<>());
                frequencies = (Map) frequencies.get(property);

                frequencies.putIfAbsent(object, new DataQualityInformation());

                // Warning, the complement is already computed                   
                DataQualityRecords records = (DataQualityRecords) frequencies.get(dataSource);

                if (present == null) {
                    // Computing the frequency of the complements 
                    records.frequency++;
                } else {
                    // Computing the number of duplicate complements 
                    records.duplicates++;
                }

                records.dataSources.add(dataSource);

            }

        }

        // Size processing
        int size = 0;

        for (Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>> subjects : statements.values()) {
            for (Map<RDFNode, Map<DataSource, Integer>> properties : subjects.values()) {
                if (duplicatesAllowed) {
                    for (Map<DataSource, Integer> objects : properties.values()) {
                        for (Integer value : objects.values()) {
                            size += value;
                        }
                    }
                }
                size += properties.values().size();
            }
        }

        // Equivalence classes processing     
        Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>>> classes = new HashMap<>();

        // Retrieving the equivalence classes 
        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>>> values = ((DisjointMap) statements).disjointValues();

        // Computing the duplicate statements
        data.duplicates = new HashSet<>();

        for (Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> value : values) {

            Collection<RDFNode> subjects = new HashSet<>();

            Map<RDFNode, Map<RDFNode, DataQualityAssessment>> properties = new DisjointMap<>();

            for (Map.Entry<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> subject : value.entrySet()) {

                RDFNode s = subject.getKey();

                // Grouping the subjects
                subjects.add(s);

                for (Map.Entry<RDFNode, Map<RDFNode, Map<DataSource, Integer>>> property : subject.getValue().entrySet()) {

                    RDFNode p = property.getKey();

                    properties.putIfAbsent(p, new HashMap<>());

                    Map<RDFNode, DataQualityAssessment> objects = properties.get(p);

                    for (Map.Entry<RDFNode, Map<DataSource, Integer>> object : property.getValue().entrySet()) {

                        RDFNode o = object.getKey();

                        // Searching for duplicate statements
                        Triple t = null;

                        for (Entry<DataSource, Integer> record : object.getValue().entrySet()) {

                            if (record.getValue() > 1 || t != null) {
                                t = new Triple(s.asNode(), p.asNode(), o.asNode());
                            }

                            if (t != null) {
                                data.duplicates.add(t);

                                // Stopping duplicate detection
                                break;
                            }

                        }

                        // Computing the criteria
                        objects.putIfAbsent(o, (DataQualityAssessment) complements.get(p).get(o).clone());

                        DataQualityRecords records = (DataQualityRecords) objects.get(o);
                        
                        // Computing the homogeneity
                        records.homogeneity++;

                    }
                }

            }

            //classes.put(subjects, properties);
        }

    }

}
