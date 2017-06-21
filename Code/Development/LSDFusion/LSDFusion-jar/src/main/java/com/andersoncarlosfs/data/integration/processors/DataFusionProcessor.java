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
import java.util.Collections;
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

    private class DataQualityRecords implements DataQualityControl, Cloneable {

        private Float frequency;
        //private Integer duplicates;
        private Float homogeneity;
        private Collection<DataSource> dataSources;
        private Collection<RDFNode> morePrecise;

        public DataQualityRecords() {
            this.frequency = new Float(0);
            //this.duplicates = new Integer(0);
            this.homogeneity = new Float(0);
            this.dataSources = new HashSet<>();
            this.morePrecise = new HashSet<>();
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
            return value == null ? new Float(0.5) : value;
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
            return morePrecise;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    }

    private class DataQualityInformation extends DataQualityRecords implements DataQualityAssessment {

        private Float freshness;
        private Float trustiness;

        @Override
        public Float getFreshness() {
            return freshness == null ? super.getFreshness() : freshness;
        }

        @Override
        public Float getTrustiness() {
            return trustiness;
        }

    }

    private class DataFusionInformation implements DataFusionAssessment {

        private Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>>> values;
        private Collection<Triple> duplicates;

        @Override
        public Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment() {
            return values;
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
                if (functions.contains(Function.MAP)) {
                    parameters.union(last, current);
                }

                last = current;

            }

        }

        // Data souces processing
        Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> statements = new DisjointMap<>();

        Map<RDFNode, Map<RDFNode, DataQualityRecords>> complements = new DisjointMap<>();

        long durations = 0;

        for (DataSource dataSource : dataSources) {

            Long freshness = dataSource.getFreshness();

            if (freshness != null) {
                durations += freshness;
            }

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

                if (present == null && parameters.getOrDefault(property, Collections.EMPTY_SET).contains(Function.CONSTRUCT)) {
                    //
                    statements.putIfAbsent(object, new HashMap<>());
                    
                    // Grouping the subjects                    
                    ((DisjointMap) statements).union(subject, object);
                }

                // Computing the absolute number of duplicate statements
                classes.put(dataSource, ((int) classes.get(dataSource)) + 1);

                // Frequencies processing
                Map frequencies = complements;

                frequencies.putIfAbsent(property, new HashMap<>());
                frequencies = (Map) frequencies.get(property);

                frequencies.putIfAbsent(object, new DataQualityInformation());

                // Warning, the complement is already computed                   
                DataQualityRecords records = (DataQualityRecords) frequencies.get(object);

                if (present == null) {
                    // Computing the absolute frequency of the complements 
                    records.frequency++;
                } else {
                    // Computing the absolute number of duplicate complements 
                    //records.duplicates++;
                }

                if (duplicatesAllowed) {
                    records.frequency++;
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
        data.values = new HashMap<>();

        // Computing the duplicate statements
        data.duplicates = new HashSet<>();

        // Retrieving the equivalence classes 
        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>>> classes = ((DisjointMap) statements).disjointValues();

        for (Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> map : classes) {

            Collection<RDFNode> subjects = new HashSet<>();

            Map<RDFNode, Map<RDFNode, DataQualityAssessment>> outlines = new DisjointMap<>();

            for (Map.Entry<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> subject : map.entrySet()) {

                RDFNode s = subject.getKey();

                // Grouping the subjects
                subjects.add(s);

                // Last property
                RDFNode last = null;

                for (Map.Entry<RDFNode, Map<RDFNode, Map<DataSource, Integer>>> property : subject.getValue().entrySet()) {

                    // Current property
                    RDFNode current = property.getKey();

                    outlines.putIfAbsent(current, new HashMap<>());

                    // Grouping the properties
                    if (parameters.getOrDefault(current, Collections.EMPTY_SET).contains(Function.MAP)) {
                        ((DisjointMap) outlines).union(last, current);
                    }

                    last = current;

                    Map<RDFNode, DataQualityAssessment> objects = outlines.get(current);

                    for (Map.Entry<RDFNode, Map<DataSource, Integer>> object : property.getValue().entrySet()) {

                        RDFNode o = object.getKey();

                        // Searching for duplicate statements
                        Triple t = null;

                        for (Entry<DataSource, Integer> record : object.getValue().entrySet()) {

                            if (record.getValue() > 1 || t != null) {
                                t = new Triple(s.asNode(), current.asNode(), o.asNode());
                            }

                            if (t != null) {

                                data.duplicates.add(t);

                                // Stopping duplicate detection
                                break;

                            }

                        }

                        // Processing the absolute criteria
                        objects.putIfAbsent(o, (DataQualityAssessment) complements.get(current).get(o).clone());

                        DataQualityRecords records = (DataQualityRecords) objects.get(o);

                        // Computing the absolute homogeneity
                        if (duplicatesAllowed) {
                            for (Integer value : object.getValue().values()) {
                                records.homogeneity += value;
                            }
                        } else {
                            records.homogeneity++;
                        }

                    }
                }

            }

            // Retrieving the mappings
            Collection<Map<RDFNode, Map<RDFNode, DataQualityAssessment>>> mappings = ((DisjointMap) outlines).disjointValues();

            Map<Collection<RDFNode>, Map<RDFNode, DataQualityAssessment>> summary = new DisjointMap<>();

            for (Map<RDFNode, Map<RDFNode, DataQualityAssessment>> mapping : mappings) {

                Collection<RDFNode> predicates = mapping.keySet();

                Collection<Function> functions = new HashSet<>();

                for (RDFNode p : predicates) {
                    functions.addAll(parameters.getOrDefault(p, Collections.EMPTY_SET));
                }

                Map<RDFNode, DataQualityAssessment> objects = new HashMap<>();

                // Best object
                RDFNode best = null;

                for (Map<RDFNode, DataQualityAssessment> object : mapping.values()) {
                    for (Entry<RDFNode, DataQualityAssessment> value : object.entrySet()) {

                        RDFNode current = value.getKey();
                        DataQualityRecords v = (DataQualityRecords) value.getValue();

                        // Processing the absolute criteria
                        objects.putIfAbsent(current, (DataQualityAssessment) new DataQualityInformation());

                        DataQualityRecords records = (DataQualityRecords) objects.get(current);

                        // Computing the absolute frequency
                        records.frequency += v.frequency;

                        // Computing the absolute homogeneity
                        records.homogeneity += v.homogeneity;

                        // Computing the absolute freshness and absolute reability
                        records.dataSources.addAll(v.dataSources);

                        Float numeric = null;

                        // Applying the functions
                        if (best != null && current.isLiteral()) {
                            if (functions.contains(Function.MIN)) {
                                numeric = Math.max(best.asLiteral().getFloat(), current.asLiteral().getFloat());
                            }
                            if (functions.contains(Function.MAX)) {
                                numeric = Math.min(best.asLiteral().getFloat(), current.asLiteral().getFloat());
                            }
                        } else if (current.isLiteral()) {
                            best = current;
                        }

                        // Processing the  more precises
                        if (numeric != null) {

                            DataQualityRecords outstanding = (DataQualityRecords) object.get(best);

                            if (numeric != best.asLiteral().getFloat()) {

                                ((DataQualityInformation) records).trustiness = ((DataQualityInformation) outstanding).trustiness;

                                ((DataQualityInformation) outstanding).trustiness = new Float(0);

                                records.morePrecise.addAll(outstanding.morePrecise);

                                outstanding.morePrecise.clear();

                                outstanding = records;

                            } else {
                                outstanding.morePrecise.add(current);
                            }

                            ((DataQualityInformation) outstanding).trustiness++;

                        } else {
                            for (Entry<RDFNode, DataQualityAssessment> record : object.entrySet()) {

                                RDFNode node = record.getKey();
                                DataQualityRecords trustiness = (DataQualityRecords) record.getValue();

                                if (!current.isLiteral()) {
                                    break;
                                }

                                if (current.equals(node) || !node.isLiteral()) {
                                    continue;
                                }

                                if (current.asLiteral().getString().contains(node.asLiteral().getString())) {
                                    records.morePrecise.add(node);
                                }

                                if (node.asLiteral().getString().contains(current.asLiteral().getString())) {
                                    trustiness.morePrecise.add(current);
                                }

                            }
                        }

                    }
                }

                summary.put(predicates, objects);

            }

            // Processing the relative criteria
            int count = 0;

            for (Map<RDFNode, DataQualityAssessment> object : summary.values()) {
                for (DataQualityAssessment value : object.values()) {
                    count += ((DataQualityRecords) value).homogeneity;
                }
            }

            for (Map<RDFNode, DataQualityAssessment> object : summary.values()) {

                Collection<DataQualityAssessment> assessments = object.values();

                float trustinesses = 0;

                for (DataQualityAssessment value : assessments) {

                    DataQualityRecords records = (DataQualityRecords) value;

                    // Computing the relative frequency
                    records.frequency /= size;

                    // Computing the relative homogeneity
                    records.homogeneity /= count;

                    // Computing the relative freshness
                    if (records.getFreshness() != null) {
                        ((DataQualityInformation) value).freshness = records.getFreshness() / durations;
                    } else {
                        ((DataQualityInformation) value).freshness = new Float(0.5);
                    }

                    // Computing the absolute trustiness
                    if (((DataQualityInformation) value).trustiness == null) {
                        ((DataQualityInformation) value).trustiness = value.getScore();
                    }

                    trustinesses += ((DataQualityInformation) value).trustiness;

                }

                // Computing the relative trustiness
                for (DataQualityAssessment value : assessments) {
                    ((DataQualityInformation) value).trustiness /= trustinesses;
                }

            }

            data.values.put(subjects, summary);

        }

    }

    public static DataFusionAssessment process(Collection<DataSource> dataSources, Collection<Rule> rules, boolean duplicatesAllowed) throws IOException, CloneNotSupportedException {
        return new DataFusionProcessor(dataSources, rules, true).data;
    }

}
