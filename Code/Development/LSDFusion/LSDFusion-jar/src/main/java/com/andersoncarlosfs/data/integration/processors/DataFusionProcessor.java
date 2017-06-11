/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.util.DisjointMap;
import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
    private class DataBag extends DisjointMap<RDFNode, DisjointMap<RDFNode, DisjointMap<RDFNode, DataSource>>> {

        /**
         *
         * @param statement
         * @param dataSource to be associated with the specified statement
         * @return <tt>true</tt> if this bag did not already contain the
         * specified statement
         */
        private boolean put(Statement statement, DataSource dataSource) {
            Resource subject = statement.getSubject();
            Property property = statement.getPredicate();
            RDFNode object = statement.getObject();

            //
            super.putIfAbsent(subject, new DisjointMap<>());
            Map map = get(subject);

            map.putIfAbsent(dataSource, new HashMap<>());
            map = (Map) map.get(dataSource);

            map.putIfAbsent(property, new HashMap<>());
            map = (Map) map.get(property);

            // Warning, the statement is already present in the dataSouce           
            return map.put(object, dataSource) == dataSource;
        }

        /**
         * Return the number of statements
         *
         * @return the number of statements
         */
        @Override
        public int size() {
            return size(0, this);
        }

        /**
         * Return the number of statements
         *
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

    /**
     *
     */
    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    private final DataBag data = new DataBag();

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

                data.put(statement, dataSource);

                if (dataSource.getEquivalenceProperties().contains(statement.getPredicate())) {
                    data.union(statement.getSubject(), statement.getObject());
                }

            }

        }
    }

}
