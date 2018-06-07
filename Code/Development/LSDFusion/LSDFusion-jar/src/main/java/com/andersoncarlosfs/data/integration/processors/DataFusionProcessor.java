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
import com.andersoncarlosfs.data.util.Function;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.LiteralRequiredException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionProcessor {

    private class DataQualityRecords implements DataQualityAssessment, Cloneable {

        private Float homogeneity;
        private Collection<DataSource> dataSources;
        private Collection<RDFNode> morePrecise;

        public DataQualityRecords() {
            this.homogeneity = new Float(0);
            this.dataSources = new HashSet<>();
            this.morePrecise = new HashSet<>();
        }

        @Override
        public Float getFrequency() {
            return (float) dataSources.size();
        }

        @Override
        public Float getHomogeneity() {
            return homogeneity;
        }

        @Override
        public Float getReliability() {
            Float value = Float.NEGATIVE_INFINITY;
            for (DataSource d : dataSources) {
                if (d.getReliability() == null) {
                    continue;
                }
                value = Math.max(value, d.getReliability());
            }
            return value == Float.NEGATIVE_INFINITY ? null : value;
        }

        @Override
        public Float getFreshness() {
            Float value = Float.POSITIVE_INFINITY;
            for (DataSource d : dataSources) {
                if (d.getFreshness() == null) {
                    continue;
                }
                value = Math.min(value, d.getFreshness());
            }
            return value == Float.POSITIVE_INFINITY ? null : value;
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

    private class DataQualityInformation extends DataQualityRecords {

        private Float frequency;
        private Float reliability;
        private Float freshness;

        private DataQualityInformation() {
            this.frequency = new Float(0);
            this.reliability = new Float(0);
            this.freshness = new Float(0);
        }

        @Override
        public Float getFrequency() {
            return frequency;
        }

        public Float getFrequency(Boolean isAbsolute) {
            if (isAbsolute) {
                return super.getFrequency();
            }
            return frequency;
        }

        @Override
        public Float getReliability() {
            return reliability;
        }

        public Float getReliability(Boolean isAbsolute) {
            if (isAbsolute) {
                return super.getReliability();
            }
            return reliability;
        }

        @Override
        public Float getFreshness() {
            return freshness;
        }

        public Float getFreshness(Boolean isAbsolute) {
            if (isAbsolute) {
                return super.getFreshness();
            }
            return freshness;
        }

    }

    private class DataFusionInformation implements DataFusionAssessment {

        private Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>>> values;
        private Collection<Triple> duplicates;

        @Override
        public Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment() {
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

    private static final Comparator comparator = new Comparator<Map.Entry<RDFNode, DataQualityAssessment>>() {
        @Override
        public int compare(Entry<RDFNode, DataQualityAssessment> o1, Entry<RDFNode, DataQualityAssessment> o2) {
            return o2.getValue().getScore().compareTo(o1.getValue().getScore());
        }
    };

    private DataFusionInformation data = new DataFusionInformation();

    /**
     *
     * @param rules
     * @param dataSources
     * @param duplicatesAllowed
     * @throws IOException
     * @throws java.lang.CloneNotSupportedException
     */
    public DataFusionProcessor(Collection<? extends DataSource> dataSources, Collection<Rule> rules, boolean duplicatesAllowed) throws IOException, CloneNotSupportedException {
        // Rules processing
        DisjointMap<Property, Collection<Function>> parameters = new DisjointMap<>();
        DisjointMap<Property, Collection<Object>> arguments = new DisjointMap<>();

        for (Rule rule : rules) {

            Function function = rule.getFunction();
            Property property = rule.getProperty();
            Object value = rule.getValue();

            // Attaching zero or more functions a property 
            parameters.putIfAbsent(property, new HashSet<>());
            parameters.get(property).add(function);

            switch (function) {
                // Attaching zero or more arguments a property 
                case EXTRA_KNOWLEDGE:
                    arguments.putIfAbsent(property, new HashSet<>());
                    arguments.get(property).add(rule.getValue());
                    break;
                // Grouping the properties
                case MAPPING:
                    parameters.union(property, (Property) value);
                    arguments.union(property, (Property) value);
                    break;
            }

        }

        // Data souces processing
        Map<RDFNode, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>> statements = new DisjointMap<>();

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

                // Escaping the predicates
                if (parameters.getOrDefault(property, Collections.EMPTY_SET).contains(Function.ESCAPE)) {
                    continue;
                }

                // Equivalence classes processing 
                if (parameters.getOrDefault(property, Collections.EMPTY_SET).contains(Function.IDENTITY)) {

                    // Putting the equivalent members 
                    statements.putIfAbsent(subject, new HashMap<>());
                    statements.putIfAbsent(object, new HashMap<>());

                    // Grouping the members                    
                    ((DisjointMap) statements).union(subject, object);

                    // Escaping
                    continue;

                }

                // Statements processing
                statements.putIfAbsent(subject, new HashMap<>());

                Map<RDFNode, Map<RDFNode, Collection<DataSource>>> subjects_predicates = statements.get(subject);

                subjects_predicates.putIfAbsent(property, new HashMap<>());

                Map<RDFNode, Collection<DataSource>> subjects_predicates_objects = subjects_predicates.get(property);

                subjects_predicates_objects.putIfAbsent(object, new HashSet<>());

                Collection<DataSource> subjects_predicates_objects_dataSources = subjects_predicates_objects.get(object);

                // Complements processing
                complements.putIfAbsent(property, new HashMap<>());

                Map<RDFNode, DataQualityRecords> predicates_objects = complements.get(property);

                predicates_objects.putIfAbsent(object, new DataQualityInformation());

                // Warning, the statement is already present in the dataSouce
                if (subjects_predicates_objects_dataSources.add(dataSource)) {
                    ((DataQualityRecords) predicates_objects.get(object)).dataSources.add(dataSource);
                }

            }

        }

        // Size processing
        int statements_size = 0;

        for (Map<RDFNode, Map<RDFNode, Collection<DataSource>>> subjects : statements.values()) {
            for (Map<RDFNode, Collection<DataSource>> properties : subjects.values()) {
                if (duplicatesAllowed) {
                    statements_size += properties.values().size();
                } else {
                    statements_size++;
                }
            }
        }

        // Equivalence classes processing     
        data.values = new HashMap<>();

        // Computing the duplicate statements
        data.duplicates = new HashSet<>();

        // Retrieving the equivalence classes 
        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>>> classes = ((DisjointMap) statements).disjointValues();

        for (Map<RDFNode, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>> classe : classes) {

            Map<RDFNode, Collection<DataSource>> subjects = new HashMap<>();

            Map<RDFNode, Map<RDFNode, DataQualityAssessment>> classe_complements = new DisjointMap<>();

            for (Map.Entry<RDFNode, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>> classe_subject : classe.entrySet()) {

                RDFNode subject = classe_subject.getKey();

                Map<RDFNode, Map<RDFNode, Collection<DataSource>>> classe_subject_predicates = classe_subject.getValue();

                // Grouping the subjects
                subjects.putIfAbsent(subject, new HashSet<>());

                // Last predicate
                RDFNode last_predicate = null;
                
                // Retrieving the subject provenance 
                Collection<DataSource> subject_provenance = subjects.get(subject);

                for (Map.Entry<RDFNode, Map<RDFNode, Collection<DataSource>>> classe_subject_predicate : classe_subject_predicates.entrySet()) {

                    // Current property
                    RDFNode current_predicate = classe_subject_predicate.getKey();

                    Map<RDFNode, Collection<DataSource>> classe_subject_predicate_objects = classe_subject_predicate.getValue();

                    classe_complements.putIfAbsent(current_predicate, new HashMap<>());

                    // Grouping the predicates
                    if (parameters.getOrDefault(current_predicate, Collections.EMPTY_SET).contains(Function.MAPPING)) {
                        ((DisjointMap) classe_complements).union(last_predicate, current_predicate);
                    }

                    last_predicate = current_predicate;

                    Map<RDFNode, DataQualityAssessment> objects = classe_complements.get(last_predicate);

                    for (Map.Entry<RDFNode, Collection<DataSource>> classe_subject_predicate_object : classe_subject_predicate_objects.entrySet()) {

                        RDFNode object = classe_subject_predicate_object.getKey();

                        // Processing the absolute criteria
                        Map<RDFNode, DataQualityRecords> complement_objects = complements.get(last_predicate);

                        DataQualityRecords complement_object_records = (DataQualityRecords) complement_objects.get(object);

                        DataQualityRecords complement_object_records_clone = (DataQualityRecords) complement_object_records.clone();

                        objects.putIfAbsent(object, (DataQualityAssessment) complement_object_records_clone);

                        DataQualityRecords records = (DataQualityRecords) objects.get(object);

                        int duplicates = classe_subject_predicate_object.getValue().size();

                        // Searching for duplicate statements
                        if (duplicates > 1) {
                            data.duplicates.add(new Triple(subject.asNode(), last_predicate.asNode(), object.asNode()));
                        }

                        // Computing the absolute homogeneity
                        if (duplicatesAllowed) {
                            records.homogeneity += duplicates;
                        } else {
                            records.homogeneity++;
                        }
                        
                        // Adding the provenance
                        subject_provenance.addAll(records.dataSources);
                        
                    }
                }

            }

            // Retrieving the mappings
            Collection<Map<RDFNode, Map<RDFNode, DataQualityAssessment>>> mappings = ((DisjointMap) classe_complements).disjointValues();

            Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>> summary = new DisjointMap<>();

            for (Map<RDFNode, Map<RDFNode, DataQualityAssessment>> mapping : mappings) {

                Map<RDFNode, Collection<DataSource>> predicates = new HashMap<>();
               
                Collection<Function> functions = new HashSet<>();
               
                Collection<Path> knowledge = new HashSet<>();
                                
                for (RDFNode predicate : mapping.keySet()) {
                    
                    predicates.putIfAbsent(predicate, new HashSet<>());
                    
                    functions.addAll(parameters.getOrDefault(predicate, Collections.EMPTY_SET));
                   
                    knowledge.addAll(arguments.getOrDefault(predicate, Collections.EMPTY_SET));
                    
                }

                Map<RDFNode, DataQualityAssessment> objects = new HashMap<>();

                // Best object
                RDFNode best_object = null;

                for (Map<RDFNode, DataQualityAssessment> mapping_objects : mapping.values()) {
                    for (Entry<RDFNode, DataQualityAssessment> mapping_object : mapping_objects.entrySet()) {

                        RDFNode current_object = mapping_object.getKey();

                        DataQualityRecords v = (DataQualityRecords) mapping_object.getValue();

                        // Processing the absolute criteria
                        objects.putIfAbsent(current_object, (DataQualityAssessment) new DataQualityInformation());

                        DataQualityRecords records = (DataQualityRecords) objects.get(current_object);

                        // Computing the absolute frequency
                        ((DataQualityInformation) records).frequency += ((DataQualityInformation) v).getFrequency(true);

                        // Computing the absolute homogeneity
                        records.homogeneity += v.homogeneity;

                        // Computing the absolute freshness and absolute reability
                        records.dataSources.addAll(v.dataSources);

                        // Applying the functions
                        if (functions.contains(Function.AVG) || functions.contains(Function.EXTRA_KNOWLEDGE)) {
                            continue;
                        }

                        if (functions.contains(Function.MAX) || functions.contains(Function.MIN)) {

                            try {

                                Float elect = best_object.asLiteral().getFloat();
                                Float candidate = current_object.asLiteral().getFloat();

                                if (functions.contains(Function.MIN)) {
                                    elect = Math.min(elect, candidate);
                                }
                                if (functions.contains(Function.MAX)) {
                                    elect = Math.max(elect, candidate);
                                }

                                DataQualityRecords outstanding = (DataQualityRecords) objects.get(best_object);

                                if (elect != best_object.asLiteral().getFloat()) {

                                    records.morePrecise.addAll(outstanding.morePrecise);

                                    outstanding.morePrecise.clear();

                                    outstanding = records;

                                    current_object = best_object;

                                }

                                outstanding.morePrecise.add(current_object);

                            } catch (LiteralRequiredException |NumberFormatException e) {
                                // Do nothing 
                            } catch (NullPointerException e) {
                                if (NumberUtils.isCreatable(current_object.asLiteral().getValue().toString())) {
                                    best_object = current_object;
                                }
                            }

                        } else {
                            for (Entry<RDFNode, DataQualityAssessment> complement : objects.entrySet()) {

                                RDFNode complement_object = complement.getKey();
                                DataQualityRecords complement_records = (DataQualityRecords) complement.getValue();

                                if (!current_object.isLiteral()) {
                                    break;
                                }

                                if (!complement_object.isLiteral() || current_object.equals(complement_object)) {
                                    continue;
                                }

                                if (current_object.asLiteral().getString().contains(complement_object.asLiteral().getString())) {
                                    records.morePrecise.add(complement_object);
                                }

                                if (complement_object.asLiteral().getString().contains(current_object.asLiteral().getString())) {
                                    complement_records.morePrecise.add(current_object);
                                }

                            }

                        }

                    }
                }

                // Applying the functions
                if (functions.contains(Function.AVG)) {

                    DataQualityInformation records = new DataQualityInformation();

                    //((DataQualityRecords) records).frequency = new Float(1);   
                    //((DataQualityRecords) records).homogeneity = new Float(1);   
                    ((DataQualityRecords) records).morePrecise = objects.keySet();

                    records.freshness = new Float(1);

                    Float average = new Float(0);

                    for (RDFNode object : ((DataQualityRecords) records).morePrecise) {
                        try {
                            average += object.asLiteral().getFloat();
                        } catch (NumberFormatException e) {
                            // Do nothing 
                        }
                    }

                    objects.put(ResourceFactory.createTypedLiteral(average), records);

                }

                // TO REDO:
                if (functions.contains(Function.EXTRA_KNOWLEDGE)) {

                    // Getting the extra knowledge
                    Path path = knowledge.iterator().next();

                    Queue<RDFNode> nodes = new LinkedList<>(objects.keySet());

                    while (!nodes.isEmpty()) {

                        DataQualityInformation records = new DataQualityInformation();

                        // Getting the object
                        RDFNode previous_object = nodes.poll();
                        String previous_value = previous_object.asLiteral().getString();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path)));

                        String line = null;

                        // Searching for the hierarchy
                        while ((line = reader.readLine()) != null) {

                            if (!line.contains(previous_value)) {
                                continue;
                            }

                            String[] values = line.split("\\t");

                            // Computing the precision of the object
                            int previous_position = values.length - 1;

                            while (!previous_value.equals(values[previous_position])) {
                                previous_position--;
                            }

                            Queue<RDFNode> temp = new LinkedList<>();

                            while (!nodes.isEmpty()) {

                                // Getting the object
                                RDFNode current_object = nodes.poll();
                                String current_value = current_object.asLiteral().getString();

                                if (!line.contains(current_value) && temp.add(current_object)) {
                                    continue;
                                }

                                // Computing the precision of the object
                                int current_position = values.length - 1;

                                while (!current_value.equals(values[current_position])) {
                                    current_position--;
                                }

                                // Updating the best object
                                if (current_position > previous_position) {

                                    records.getMorePrecise().add(previous_object);

                                    previous_object = current_object;

                                } else {

                                    records.getMorePrecise().add(current_object);

                                }

                            }

                            nodes = temp;

                        }

                        reader.close();

                        objects.put(previous_object, records);

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

                for (DataQualityAssessment value : assessments) {

                    DataQualityInformation information = (DataQualityInformation) value;

                    // Computing the relative frequency
                    information.frequency /= statements_size;

                    // Computing the relative homogeneity
                    ((DataQualityRecords) value).homogeneity /= count;

                    // Computing the relative reliability
                    information.reliability = information.getReliability(true);
                    if (information.reliability == null) {
                        information.reliability = new Float(0.5);
                    }

                    // Computing the relative freshness
                    information.freshness = information.getFreshness(true);
                    if (information.freshness == null) {
                        information.freshness = new Float(0.5);
                    } else {
                        information.freshness /= durations;
                    }

                }

            }

            data.values.put(subjects, summary);

        }

        // Ordering the values
        Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>>> values = new HashMap<>();

        for (Entry<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>>> value : data.values.entrySet()) {

            Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>> summary = new HashMap<>();

            for (Entry<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, DataQualityAssessment>> complement : value.getValue().entrySet()) {

                List<Map.Entry<RDFNode, DataQualityAssessment>> list = new LinkedList<>(complement.getValue().entrySet());

                Collections.sort(list, comparator);

                Map<RDFNode, DataQualityAssessment> objects = new LinkedHashMap<>();

                for (Entry<RDFNode, DataQualityAssessment> entry : list) {
                    objects.put(entry.getKey(), entry.getValue());
                }

                summary.put(complement.getKey(), objects);

            }

            values.put(value.getKey(), summary);

        }

        data.values = values;

    }

    public static DataFusionAssessment process(Collection<? extends DataSource> dataSources, Collection<Rule> rules, boolean duplicatesAllowed) throws IOException, CloneNotSupportedException {
        return new DataFusionProcessor(dataSources, rules, true).data;
    }

}
