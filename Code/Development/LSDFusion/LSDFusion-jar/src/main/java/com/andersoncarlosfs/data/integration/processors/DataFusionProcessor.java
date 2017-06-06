/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.x.model.DataSource;
import com.andersoncarlosfs.util.DisjointSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    private class Bag extends DisjointSet<RDFNode> {

        private final transient Map<Resource, Map<DataSource, Map<Property, Collection<RDFNode>>>> map = new HashMap<>();

        /**
         *
         * @param statement
         * @param dataSource
         */
        private void add(Statement statement, DataSource dataSource) {
            //
            Resource subject = statement.getSubject();
            Property property = statement.getPredicate();
            RDFNode object = statement.getObject();

            //
            this.map.putIfAbsent(subject, new HashMap<>());
            Map map = this.map.get(subject);

            map.putIfAbsent(dataSource, new HashMap<>());
            map = (Map) map.get(dataSource);

            map.putIfAbsent(property, new HashSet<>());
            Collection collection = (Collection) map.get(property);

            if (!collection.add(object)) {
                // Warning, the statement is already present in the dataSouce
            }

        }

        /**
         *
         * @return a view of the values contained this bag partitioned into
         * disjoint subsets
         */
        @Override
        public Collection disjointValues() {
            Collection<Map<RDFNode, Map<Property, Collection<RDFNode>>>> values = new HashSet<>();

            //
            Collection<Collection<RDFNode>> classes = super.disjointValues();

            //
            for (Collection<RDFNode> classe : classes) {

                if (classe.size() < 2) {
                    continue;
                }

                //
                Map value = new HashMap<>();

                //
                for (RDFNode node : classe) {
                    value = disjointValues(node, value);
                }

                //
                values.add(value);

            }

            return values;
        }

        /**
         *
         * @param node
         * @param statements
         * @return a view of the values contained this bag partitioned into
         * disjoint subsets
         */
        private Map disjointValues(RDFNode node, Map values) {

            //
            if (this.map.get(node) == null) {
                return values;
            }

            //
            values.putIfAbsent(node, new HashMap<>());

            //
            for (Map<Property, Collection<RDFNode>> properties : map.get(node).values()) {

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

    }

    private final Bag classes = new Bag();

    /**
     *
     * @param dataSources
     * @throws IOException
     */
    public DataFusionProcessor(DataSource... dataSources) throws IOException {
        for (DataSource dataSource : dataSources) {

            Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());

            StmtIterator statements = model.listStatements();

            while (statements.hasNext()) {

                Statement statement = statements.next();

                if (dataSource.getEquivalenceProperties().contains(statement.getPredicate())) {
                    classes.unionIfAbsent(statement.getSubject(), statement.getObject());
                }

                classes.add(statement, dataSource);

            }

        }
    }

    /**
     * X
     *
     * @param dataSources
     * @return Statements
     * @throws java.io.IOException
     */
    public static Collection<Map<RDFNode, Map<Property, Collection<RDFNode>>>> process(DataSource... dataSources) throws IOException {

        return (new DataFusionProcessor(dataSources)).classes.disjointValues();

    }

}
