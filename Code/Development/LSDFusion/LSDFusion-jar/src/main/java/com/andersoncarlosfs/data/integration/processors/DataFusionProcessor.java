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
import java.util.AbstractMap;
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

    private class DataQualityRecords implements DataQualityAssessment {

        private Float frequency = 0F;
        private Float homogeneity = 0F;
        private Float reliability = Float.NEGATIVE_INFINITY;
        private Float freshness = Float.POSITIVE_INFINITY;
        private Collection<RDFNode> morePrecise = new HashSet<>();

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
            return reliability;
        }

        @Override
        public Float getFreshness() {
            return freshness;
        }

        @Override
        public Collection<RDFNode> getMorePrecise() {
            return morePrecise;
        }

    }

    private class DataFusionInformation implements DataFusionAssessment {

        private Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>> values;

        @Override
        public Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>> getComputedDataQualityAssessment() {
            return values;
        }

        @Override
        public Collection<Triple> getDuplicates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Model getModel() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    private static final Comparator comparator = new Comparator<Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>() {
        @Override
        public int compare(Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> o1, Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> o2) {
            return o2.getValue().getKey().getScore().compareTo(o1.getValue().getKey().getScore());
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
    public DataFusionProcessor(Collection<? extends DataSource> dataSources, Collection<Rule> rules) throws IOException {
        // Rules processing
        DisjointMap<Property, Map<Function, Collection<Object>>> parameters = new DisjointMap<>();

        for (Rule rule : rules) {

            Property property = rule.getProperty();
            Function function = rule.getFunction();
            Object value = rule.getValue();

            // Attaching zero or more functions to a property 
            parameters.putIfAbsent(property, new HashMap<>());

            Map<Function, Collection<Object>> arguments = parameters.get(property);

            // Attaching zero or more arguments to a property 
            arguments.putIfAbsent(function, new HashSet<>());
            arguments.get(function).add(value);

            // Grouping the properties
            if (function == Function.MAPPING) {
                parameters.union(property, (Property) value);
            }

        }

        // Allowing duplicates
        Boolean deduplicate = (Boolean) parameters.getOrDefault(null, Collections.EMPTY_MAP).getOrDefault(Function.DEDUPLICATE, Boolean.FALSE);

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
                if (parameters.getOrDefault(property, Collections.EMPTY_MAP).containsKey(Function.ESCAPE)) {
                    continue;
                }

                // Equivalence classes processing 
                if (parameters.getOrDefault(property, Collections.EMPTY_MAP).containsKey(Function.IDENTITY)) {

                    // Putting the equivalent members 
                    statements.putIfAbsent(subject, new HashMap<>());
                    statements.putIfAbsent(object, new HashMap<>());

                    // Grouping the members                    
                    ((DisjointMap) statements).union(subject, object);

                }

                // Statements processing
                statements.putIfAbsent(subject, new HashMap<>());

                Map<RDFNode, Map<RDFNode, Collection<DataSource>>> subjects_predicates = statements.get(subject);

                subjects_predicates.putIfAbsent(property, new HashMap<>());

                Map<RDFNode, Collection<DataSource>> subjects_predicates_objects = subjects_predicates.get(property);

                subjects_predicates_objects.putIfAbsent(object, new HashSet<>());

                // Warning, the statement is already present in the dataSouce
                subjects_predicates_objects.get(object).add(dataSource);

                // Complements processing
                complements.putIfAbsent(property, new HashMap<>());

                Map<RDFNode, DataQualityRecords> predicates_objects = complements.get(property);

                predicates_objects.putIfAbsent(object, new DataQualityRecords());

                // Counting the frequency
                predicates_objects.get(object).frequency++;

            }

        }

        // Size processing
        int size = 0;

        for (Map<RDFNode, Map<RDFNode, Collection<DataSource>>> subjects_predicates : statements.values()) {
            for (Map<RDFNode, Collection<DataSource>> subjects_predicates_objects : subjects_predicates.values()) {
                if (deduplicate) {
                    size += subjects_predicates_objects.values().size();
                } else {
                    for (Collection<DataSource> value : subjects_predicates_objects.values()) {
                        size += value.size();
                    }
                }
            }
        }

        // Equivalence classes processing     
        data.values = new HashMap<>();

        // Retrieving the equivalence classes 
        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>>> classes = ((DisjointMap) statements).disjointValues();

        for (Map<RDFNode, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>> classe : classes) {

            Map<RDFNode, Collection<DataSource>> subjects = new HashMap<>();

            Map<RDFNode, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> classe_complements = new DisjointMap<>();

            for (Entry<RDFNode, Map<RDFNode, Map<RDFNode, Collection<DataSource>>>> classe_subject : classe.entrySet()) {

                RDFNode subject = classe_subject.getKey();

                Map<RDFNode, Map<RDFNode, Collection<DataSource>>> classe_subject_predicates = classe_subject.getValue();

                // Grouping the subjects
                subjects.putIfAbsent(subject, new HashSet<>());

                // Last predicate
                RDFNode last_predicate = null;

                // Retrieving the subject provenance 
                Collection<DataSource> subject_provenance = subjects.get(subject);

                for (Entry<RDFNode, Map<RDFNode, Collection<DataSource>>> classe_subject_predicate : classe_subject_predicates.entrySet()) {

                    // Current property
                    RDFNode current_predicate = classe_subject_predicate.getKey();

                    Map<RDFNode, Collection<DataSource>> classe_subject_predicate_objects = classe_subject_predicate.getValue();

                    classe_complements.putIfAbsent(current_predicate, new HashMap<>());

                    // Grouping the predicates
                    if (parameters.getOrDefault(current_predicate, Collections.EMPTY_MAP).containsKey(Function.MAPPING)) {
                        ((DisjointMap) classe_complements).union(last_predicate, current_predicate);
                    }

                    last_predicate = current_predicate;

                    Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> objects = classe_complements.get(last_predicate);

                    for (Entry<RDFNode, Collection<DataSource>> classe_subject_predicate_object : classe_subject_predicate_objects.entrySet()) {

                        RDFNode object = classe_subject_predicate_object.getKey();

                        // Provenance
                        Collection<DataSource> provenance = classe_subject_predicate_object.getValue();

                        // Processing the absolute criteria                          
                        objects.putIfAbsent(object, new AbstractMap.SimpleEntry<>(complements.get(last_predicate).get(object), provenance));

                        DataQualityRecords records = (DataQualityRecords) objects.get(object).getKey();

                        int duplicates = provenance.size();

                        // Computing the absolute homogeneity
                        if (deduplicate == false && duplicates > 1) {
                            records.homogeneity += duplicates;
                        } else {
                            records.homogeneity++;
                        }

                        // Adding the provenance
                        subject_provenance.addAll(provenance);

                    }
                }

            }

            // Retrieving the mappings
            Collection<Map<RDFNode, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>> mappings = ((DisjointMap) classe_complements).disjointValues();

            Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> summary = new DisjointMap<>();

            for (Map<RDFNode, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> mapping : mappings) {

                Map<RDFNode, Collection<DataSource>> predicates = new HashMap<>();

                Map<Function, Collection<Object>> functions = new HashMap<>();

                // TO REDO:
                for (RDFNode predicate : mapping.keySet()) {
                    for (Entry<Function, Collection<Object>> entry : parameters.getOrDefault(predicate, new HashMap<>()).entrySet()) {
                        functions.putIfAbsent(entry.getKey(), entry.getValue());
                        functions.get(entry.getKey()).addAll(entry.getValue());
                    }
                }

                Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> objects = new HashMap<>();

                // Best object
                RDFNode best_object = null;

                for (Entry<RDFNode, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> mapping_objects : mapping.entrySet()) {

                    RDFNode predicate = mapping_objects.getKey();

                    // Grouping the predicates
                    predicates.putIfAbsent(predicate, new HashSet<>());

                    // Retrieving the subject provenance 
                    Collection<DataSource> predicate_provenance = predicates.get(predicate);

                    for (Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> mapping_object : mapping_objects.getValue().entrySet()) {

                        RDFNode current_object = mapping_object.getKey();

                        Entry<DataQualityAssessment, Collection<DataSource>> current_entry = mapping_object.getValue();

                        DataQualityRecords current_records = (DataQualityRecords) current_entry.getKey();

                        Collection<DataSource> current_provenance = current_entry.getValue();

                        // Adding the provenance
                        predicate_provenance.addAll(current_provenance);

                        Entry<DataQualityAssessment, Collection<DataSource>> previous_entry = objects.putIfAbsent(current_object, current_entry);

                        // Processing the absolute criteria
                        if (previous_entry != null) {

                            previous_entry = objects.get(current_object);

                            DataQualityRecords previous_record = (DataQualityRecords) previous_entry.getKey();

                            // Computing the absolute frequency
                            previous_record.frequency += current_records.frequency;

                            // Computing the absolute homogeneity
                            previous_record.homogeneity += current_records.homogeneity;

                            // Computing the absolute freshness and absolute reability
                            previous_entry.getValue().addAll(current_provenance);

                            current_records = previous_record;

                        }

                        // Applying the functions
                        if (functions.containsKey(Function.AVG) || functions.containsKey(Function.EXTRA_KNOWLEDGE)) {
                            continue;
                        }

                        if (functions.containsKey(Function.MAX) || functions.containsKey(Function.MIN)) {

                            try {

                                Float elect = best_object.asLiteral().getFloat();
                                Float candidate = current_object.asLiteral().getFloat();

                                if (functions.containsKey(Function.MIN)) {
                                    elect = Math.min(elect, candidate);
                                }
                                if (functions.containsKey(Function.MAX)) {
                                    elect = Math.max(elect, candidate);
                                }

                                DataQualityRecords outstanding = (DataQualityRecords) objects.get(best_object).getKey();

                                if (elect != best_object.asLiteral().getFloat()) {

                                    current_records.morePrecise.addAll(outstanding.morePrecise);

                                    outstanding.morePrecise.clear();

                                    outstanding = current_records;

                                    current_object = best_object;

                                }

                                outstanding.morePrecise.add(current_object);

                            } catch (LiteralRequiredException | NumberFormatException e) {
                                // Do nothing 
                            } catch (NullPointerException e) {
                                if (NumberUtils.isCreatable(current_object.asLiteral().getValue().toString())) {
                                    best_object = current_object;
                                }
                            }

                        } else {
                            for (Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> complement : objects.entrySet()) {

                                RDFNode complement_object = complement.getKey();

                                DataQualityRecords complement_records = (DataQualityRecords) complement.getValue().getKey();

                                if (!current_object.isLiteral()) {
                                    break;
                                }

                                if (!complement_object.isLiteral() || current_object.equals(complement_object)) {
                                    continue;
                                }

                                if (current_object.asLiteral().getString().contains(complement_object.asLiteral().getString())) {
                                    current_records.morePrecise.add(complement_object);
                                }

                                if (complement_object.asLiteral().getString().contains(current_object.asLiteral().getString())) {
                                    complement_records.morePrecise.add(current_object);
                                }

                            }

                        }

                    }
                }

                // Applying the functions
                if (functions.containsKey(Function.AVG)) {

                    DataQualityRecords records = new DataQualityRecords();

                    records.morePrecise = objects.keySet();

                    Float average = new Float(0);

                    for (RDFNode object : records.morePrecise) {
                        try {
                            average += object.asLiteral().getFloat();
                        } catch (NumberFormatException e) {
                            // Do nothing 
                        }
                    }

                    objects.put(ResourceFactory.createTypedLiteral(average), new AbstractMap.SimpleEntry<>(records, Collections.EMPTY_SET));

                }

                // TO REDO:
                if (functions.containsKey(Function.EXTRA_KNOWLEDGE)) {

                    // Getting the extra knowledge
                    Path path = (Path) functions.get(Function.EXTRA_KNOWLEDGE).iterator().next();

                    Queue<RDFNode> nodes = new LinkedList<>(objects.keySet());

                    while (!nodes.isEmpty()) {

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

                                    objects.get(current_object).getKey().getMorePrecise().add(previous_object);

                                    previous_object = current_object;

                                } else {

                                    objects.get(previous_object).getKey().getMorePrecise().add(current_object);

                                }

                            }

                            nodes = temp;

                        }

                        reader.close();

                    }

                }

                summary.put(predicates, objects);

            }

            // Processing the relative criteria
            int count = 0;

            for (Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> object : summary.values()) {
                for (Entry<DataQualityAssessment, Collection<DataSource>> value : object.values()) {
                    count += ((DataQualityRecords) value.getKey()).homogeneity;
                }
            }

            for (Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> object : summary.values()) {

                for (Entry<DataQualityAssessment, Collection<DataSource>> value : object.values()) {

                    DataQualityRecords records = (DataQualityRecords) value.getKey();

                    // Computing the relative homogeneity
                    if (records.homogeneity == 0F) {

                        records.homogeneity = 1F;
                        records.frequency = 1F;
                        records.reliability = 1F;
                        records.freshness = 1F;

                        continue;

                    }

                    // Computing the relative homogeneity    
                    records.homogeneity /= count;

                    // Computing the relative frequency
                    records.frequency /= size;

                    for (DataSource dataSource : value.getValue()) {

                        // Computing the relative reliability
                        if (dataSource.getReliability() != null) {
                            records.reliability = Math.max(records.reliability, dataSource.getReliability());
                        }

                        // Computing the relative freshness
                        if (dataSource.getFreshness() != null) {
                            records.freshness = Math.min(records.freshness, dataSource.getFreshness());
                        }

                    }

                    // Computing the relative reliability
                    if (records.reliability == Float.NEGATIVE_INFINITY) {
                        records.reliability = 0.5F;
                    }

                    // Computing the relative freshness
                    if (records.freshness == Float.POSITIVE_INFINITY) {
                        records.freshness = 0.5F;
                    } else {                        
                        records.freshness /= durations;
                    }

                }

            }

            data.values.put(subjects, summary);

        }

        // Ordering the values
        Map<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>> values = new HashMap<>();

        for (Entry<Map<RDFNode, Collection<DataSource>>, Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>>> value : data.values.entrySet()) {

            Map<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> summary = new HashMap<>();

            for (Entry<Map<RDFNode, Collection<DataSource>>, Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> complement : value.getValue().entrySet()) {

                List<Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>>> list = new LinkedList<>(complement.getValue().entrySet());

                Collections.sort(list, comparator);

                Map<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> objects = new LinkedHashMap<>();

                for (Entry<RDFNode, Entry<DataQualityAssessment, Collection<DataSource>>> entry : list) {
                    objects.put(entry.getKey(), entry.getValue());
                }

                summary.put(complement.getKey(), objects);

            }

            values.put(value.getKey(), summary);

        }

        data.values = values;

    }

    public static DataFusionAssessment process(Collection<? extends DataSource> dataSources, Collection<Rule> rules) throws IOException, CloneNotSupportedException {
        return new DataFusionProcessor(dataSources, rules).data;
    }

}
