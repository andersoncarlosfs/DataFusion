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
     * @param dataSources
     * @throws IOException
     */
    public DataFusionProcessor(Map<Function, Collection<Property>> rules, DataSource... dataSources) throws IOException {
        Map<RDFNode, Map<RDFNode, Map<RDFNode, Object>>> data = new DisjointMap<>();

        for (DataSource dataSource : dataSources) {

            Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());

            StmtIterator statements = model.listStatements();

            while (statements.hasNext()) {

                Statement statement = statements.next();

                Resource subject = statement.getSubject();
                Property property = statement.getPredicate();
                RDFNode object = statement.getObject();

                //
                Map map = data;

                map.putIfAbsent(subject, new DisjointMap<>());
                map = (Map) map.get(subject);

                map.putIfAbsent(property, new DisjointMap<>());
                map = (Map) map.get(property);

                map.putIfAbsent(object, new HashMap<>());
                map = (Map) map.get(object);

                // Warning, the statement is already present in the dataSouce           
                if (map.putIfAbsent(dataSource, 0) == null && rules.getOrDefault(Function.CONSTRUCT, Collections.EMPTY_SET).contains(property)) {
                    ((DisjointMap) data).union(subject, object);
                }

                // Computing the number of duplicate statements
                map.put(dataSource, ((int) map.get(dataSource)) + 1);

            }

        }

        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Object>>>> equivalenceClasses = ((DisjointMap) data).disjointValues();

        for (Map<RDFNode, Map<RDFNode, Object>> value : data.values()) {

        }
        //
        Property property = null;

        for (Property node : rules.getOrDefault(Function.UNION, Collections.emptySet())) {

        }

    }

    /**
     * Return the number of statements
     *
     * @param map
     * @return the number of statements
     */
    private int size(Map map) {
        return size(0, map);
    }

    /**
     * Return the number of statements
     *
     * @param size
     * @param map
     * @return the number of statements
     */
    private int size(int size, Map map) {
        for (Object value : map.values()) {
            if (value instanceof Map) {
                size += size(0, (Map) value);
            } else {
                return map.values().size();
            }
        }
        return size;
    }

    /*
    private Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Object>>>> equivalenceClasses() {
        Collection<Map<RDFNode, Map<RDFNode, Map<RDFNode, Object>>>> values = new HashSet<>();

        Collection<Collection<RDFNode>> classes = ((DisjointMap) data).disjointValues();

        for (Collection<RDFNode> classe : classes) {

            if (classe.size() > 1) {

                //
                Map value = new HashMap<>();

                //
                for (RDFNode node : classe) {
                    value = disjointValues(node, value);
                }

                //
                values.add(value);

            }

        }

        return values;
    }

  
     *
     * @param node
     * @param statements
     * @return a view of the values contained this bag partitioned into disjoint
     * subsets
   
    private Map disjointValues(RDFNode node, Map values) {

        //
        if (data.get(node) == null) {
            return values;
        }

        //
        values.putIfAbsent(node, new HashMap<>());

        //
        for (Map<Property, Map<RDFNode, Object>> properties : data.get(node).values()) {

            //
            for (Map.Entry<Property, Collection<RDFNode>> entry : properties.entrySet()) {

                //
                Property property = entry.getKey();
                Collection<RDFNode> objects = entry.getValue();

                //
                if (((Map) values.get(node)).putIfAbsent(property, objects) != null) {
                    ((Set) ((Map) values.get(node)).get(property)).addAll(objects);
                }

                for (RDFNode object : objects) {
                    values = disjointValues(object, values);
                }

            }

        }

        return values;
    }
     */
}
