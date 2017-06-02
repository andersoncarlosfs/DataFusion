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

    private class StatementBag extends DisjointSet<RDFNode> {

        private final transient HashMap<Resource, DataSource> map = new HashMap<>();

        /**
         *
         * @param statement
         * @param dataSource
         */
        public void unionIfAbsent(Statement statement, DataSource dataSource) {

            Resource subject = statement.getSubject();
            Property property = statement.getPredicate();
            RDFNode object = statement.getObject();

            map.put(subject, dataSource);

            super.unionIfAbsent(subject, object);

        }

    }

    private final StatementBag equivalenceClasses = new StatementBag();

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
                    equivalenceClasses.unionIfAbsent(statement, dataSource);
                }

            }

        }
    }

    /**
     *
     * @param dataSources
     * @throws java.io.IOException
     */
    public static void process(DataSource... dataSources) throws IOException {

        new DataFusionProcessor(dataSources);

    }

}
