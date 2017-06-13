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

    /**
     *
     */
    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    private final Map<DataSource, Map<RDFNode, Map<RDFNode, Map<RDFNode, Object>>>> data = new HashMap<>();

    /**
     *
     * @param dataSources
     * @throws IOException
     */
    public DataFusionProcessor(Map<Function, Collection<Property>> rules, DataSource... dataSources) throws IOException {
        for (DataSource dataSource : dataSources) {

            Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());

            StmtIterator statements = model.listStatements();

            while (statements.hasNext()) {

                Statement statement = statements.next();

                Resource subject = statement.getSubject();
                Property property = statement.getPredicate();
                RDFNode object = statement.getObject();

                //
                Map map = this.data;

                map.putIfAbsent(dataSource, new DisjointMap<>());
                map = (Map) map.get(dataSource);

                map.putIfAbsent(subject, new DisjointMap<>());
                map = (Map) map.get(subject);

                map.putIfAbsent(property, new DisjointMap<>());
                map = (Map) map.get(property);

                map.putIfAbsent(object, new DisjointMap<>());
                map = (Map) map.get(object);

                // Warning, the statement is already present in the dataSouce           
                if (map.put(object, new Object()) != null) {
                    continue;
                }

                //
                if (rules.getOrDefault(Function.CONSTRUCT, Collections.EMPTY_SET).contains(property)) {
                    ((DisjointMap) this.data).union(subject, object);
                }

            }

        }

        //
        Property property = null;
        
        for (Object value : rules.getOrDefault(Function.UNION, Collections.EMPTY_SET)) {

            
        }

    }

    /**
     * Return the number of statements
     *
     * @return the number of statements
     */
    public int size() {
        return size(0, data);
    }

    /**
     * Return the number of statements
     *
     * @param size
     * @param map
     * @return the number of statements
     */
    public int size(int size, Map map) {
        for (Object value : map.values()) {
            if (value instanceof Map) {
                size += size(0, (Map) value);
            } else {
                return map.values().size();
            }
        }
        return size;
    }

}
