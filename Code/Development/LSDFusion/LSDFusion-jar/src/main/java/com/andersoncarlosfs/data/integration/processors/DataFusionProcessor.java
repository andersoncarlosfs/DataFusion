/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.util.DisjointMap;
import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import com.andersoncarlosfs.data.model.assessments.DataQualityAssessment;
import com.andersoncarlosfs.data.model.control.DataQualityControl;
import com.andersoncarlosfs.data.util.Function;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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

    private class DataFusionInformation implements DataFusionAssessment {

        private boolean duplicatesAllowed;

        private int size;

        @Override
        public Map<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Collection<Statement> getDuplicates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Model getModel() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class DataQualityRecords implements DataQualityControl {

        private Map<DataSource, Entry<Integer, Integer>> dataSources;

        @Override
        public Number getFrequency() {
            Integer value = null;
            for (Entry<Integer, Integer> frequency : dataSources.values()) {
                value += frequency.getKey();
                if (data.duplicatesAllowed) {
                    value += frequency.getValue();
                }
            }
            return value;
        }

        @Override
        public Number getHomogeneity() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Float getReliability() {
            Float value = null;
            for (DataSource d : dataSources.keySet()) {
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
        public Number getFreshness() {
            Float value = null;
            for (DataSource d : dataSources.keySet()) {
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

    }

    private class DataQualityInformation extends DataQualityRecords implements DataQualityAssessment {

        @Override
        public Float getFrequency() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Float getHomogeneity() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Float getFreshness() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Float getTrustiness() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    private DataFusionInformation data = new DataFusionInformation();

    /**
     *
     * @param rules
     * @param dataSources
     * @throws IOException
     */
    public DataFusionProcessor(Map<Collection<Property>, Collection<Function>> rules, DataSource... dataSources) throws IOException {
        // Functions processing
        DisjointMap<Property, Collection<Function>> parameters = new DisjointMap<>();

        data.duplicatesAllowed = rules.getOrDefault(null, Collections.EMPTY_LIST).contains(Function.DUPLICATE);

        for (Map.Entry<Collection<Property>, Collection<Function>> entry : rules.entrySet()) {

            Property property = null;

            for (Property p : entry.getKey()) {

                // Many functions can be applied to a property 
                parameters.putIfAbsent(p, new HashSet<>());
                parameters.get(p).addAll(entry.getValue());

                //
                if (entry.getValue().contains(Function.UNION)) {
                    parameters.union(property, p);
                }

            }

        }

        // Data souces processing
        Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> statements = new DisjointMap<>();

        // Frequencies processing
        Map<RDFNode, Map<RDFNode, Map<DataSource, Entry<Integer, Integer>>>> complements = new DisjointMap<>();

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
                Object present = null;

                if ((present = classes.putIfAbsent(dataSource, 0)) == null && parameters.get(property).contains(Function.CONSTRUCT)) {
                    ((DisjointMap) statements).union(subject, object);
                }

                // Computing the number of duplicate statements
                classes.put(dataSource, ((int) classes.get(dataSource)) + 1);

                // Frequencies processing
                Map frequencies = complements;

                frequencies.putIfAbsent(property, new HashMap<>());
                frequencies = (Map) frequencies.get(property);

                frequencies.putIfAbsent(object, new HashMap<>());
                frequencies = (Map) frequencies.get(object);

                // Warning, the property is already computed    
                frequencies.putIfAbsent(dataSource, new AbstractMap.SimpleEntry(0, 0));

                Entry frequency = (Entry) frequencies.get(dataSource);

                // Computing the number of duplicate statements
                if (present == null) {
                    frequencies.put(dataSource, new AbstractMap.SimpleEntry((int) frequency.getKey() + 1, frequency.getValue()));
                } else {
                    frequencies.put(dataSource, new AbstractMap.SimpleEntry(frequency.getKey(), (int) frequency.getValue() + 1));
                }

            }

        }

        // Size processing
        data.size = 0;

        for (Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>> subjects : statements.values()) {
            for (Map<RDFNode, Map<DataSource, Integer>> predicates : subjects.values()) {
                if (data.duplicatesAllowed) {
                    for (Map<DataSource, Integer> objects : predicates.values()) {
                        for (Integer value : objects.values()) {
                            data.size += value;
                        }
                    }
                }
                data.size += predicates.values().size();
            }
        }

        //Equivalence classes processing     
        Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, Object>>> classes = new HashMap<>();

        // Retrieving the equivalence classes 
        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>>> values = ((DisjointMap) statements).disjointValues();

        for (Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> value : values) {

            Collection<RDFNode> subjects = new HashSet<>();

            Map<RDFNode, Map<RDFNode, Object>> properties = new HashMap<>();

            for (Map.Entry<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> subject : value.entrySet()) {

                subjects.add(subject.getKey());

                for (Map.Entry<RDFNode, Map<RDFNode, Map<DataSource, Integer>>> property : subject.getValue().entrySet()) {

                    properties.putIfAbsent(property.getKey(), new HashMap<>());

                    for (Map.Entry<RDFNode, Map<DataSource, Integer>> object : property.getValue().entrySet()) {

                        //RDFNode node = obj
                        //
                    }
                }

            }

            //classes.put(subjects, properties);
        }

    }

}
