/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.util.DisjointMap;
import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.util.Function;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    
    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    /**
     *
     * @param rules
     * @param dataSources
     * @throws IOException
     */
    public DataFusionProcessor(Map<Collection<Property>, Collection<Function>> rules, DataSource... dataSources) throws IOException {
        // Functions processing
        DisjointMap<Property, Collection<Function>> parameters = new DisjointMap<>();
        
        Boolean duplicatesAllowed = rules.getOrDefault(null, Collections.EMPTY_LIST).contains(Function.DUPLICATE);
        
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
        Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> data = new DisjointMap<>();

        // Frequencies processing
        Map<RDFNode, Map<RDFNode, Object>> complements = new DisjointMap<>();
        
        for (DataSource dataSource : dataSources) {
            
            Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());
            
            StmtIterator statements = model.listStatements();
            
            while (statements.hasNext()) {
                
                Statement statement = statements.next();
                
                Resource subject = statement.getSubject();
                Property property = statement.getPredicate();
                RDFNode object = statement.getObject();

                //Equivalence classes processing     
                Map map = data;
                
                map.putIfAbsent(subject, new HashMap<>());
                map = (Map) map.get(subject);
                
                map.putIfAbsent(property, new HashMap<>());
                map = (Map) map.get(property);
                
                map.putIfAbsent(object, new HashMap<>());
                map = (Map) map.get(object);

                // Warning, the statement is already present in the dataSouce           
                if (map.putIfAbsent(dataSource, 0) == null && parameters.get(property).contains(Function.CONSTRUCT)) {
                    ((DisjointMap) data).union(subject, object);
                }

                // Computing the number of duplicate statements
                map.put(dataSource, ((int) map.get(dataSource)) + 1);

                // Frequencies processing
                map = complements;
                
                map.putIfAbsent(property, new HashMap<>());
                map = (Map) map.get(property);
                
                map.putIfAbsent(object, new HashMap<>());
                map = (Map) map.get(object);

                // Warning, the property is already computed          
                if (map.putIfAbsent(dataSource, 0) == null && duplicatesAllowed) {
                    map.put(dataSource, ((int) map.get(dataSource)) + 1);
                }
                
            }
            
        }

        // Size processing
        int size = 0;
        
        for (Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>> subjects : data.values()) {
            for (Map<RDFNode, Map<DataSource, Integer>> predicates : subjects.values()) {
                if (duplicatesAllowed) {
                    for (Map<DataSource, Integer> objects : predicates.values()) {
                        for (Integer value : objects.values()) {
                            size += value;
                        }
                    }
                }
                size += predicates.values().size();
            }
        }

        //Equivalence classes processing     
        Map<Collection<RDFNode>, Map<Collection<RDFNode>, Map<RDFNode, Object>>> classes = new HashMap<>();
        
        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>>> values = ((DisjointMap) data).disjointValues();
        
        for (Map<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> value : values) {
            
            Collection<RDFNode> subjects = new HashSet<>();
            
            Map<RDFNode, Map<RDFNode, Object>> properties = new HashMap<>();
            
            for (Map.Entry<RDFNode, Map<RDFNode, Map<RDFNode, Map<DataSource, Integer>>>> subject : value.entrySet()) {
                
                subjects.add(subject.getKey());
                
                for (Map.Entry<RDFNode, Map<RDFNode, Map<DataSource, Integer>>> property : subject.getValue().entrySet()) {
                    
                    properties.putIfAbsent(property.getKey(), new HashMap<>());
                    
                    for (Map.Entry<RDFNode, Map<DataSource, Integer>> object : property.getValue().entrySet()) {
                        
                        RDFNode node = obj

                        //
                    }                    
                }
                
            }

            //classes.put(subjects, properties);
        }
        
    }
    
}
