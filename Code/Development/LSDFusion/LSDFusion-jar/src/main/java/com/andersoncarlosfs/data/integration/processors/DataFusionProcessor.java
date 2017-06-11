/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration.processors;

import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import com.andersoncarlosfs.data.model.assessments.DataQualityAssessment;
import com.andersoncarlosfs.data.model.assessments.DataQualityInformation;
import com.andersoncarlosfs.x.model.DataSource;
import com.andersoncarlosfs.util.DisjointSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.jena.graph.Node;
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

    /**
     *
     */
    private class DataFusionRecords implements DataFusionAssessment {
       
        @Override
        public Map<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityAssessment>>> getComputedDataQualityAssessment() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Model getModel() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Collection<Statement> getDuplicates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    /**
     *
     */
    private class DataBag extends DisjointSet<RDFNode> {

        private final transient Map<Resource, Map<DataSource, Map<Property, Map<RDFNode, Object>>>> map = new HashMap<>();

        /**
         * Adds the specified statement associated with a data source to this
         * bag if it is not already present.
         *
         * @param statement to be added to this bag
         * @param dataSource to be associated with the specified statement
         * @return <tt>true</tt> if this bag did not already contain the
         * specified statement
         */
        private boolean add(Statement statement, DataSource dataSource) {
            //
            Resource subject = statement.getSubject();
            Property property = statement.getPredicate();
            RDFNode object = statement.getObject();

            //
            this.map.putIfAbsent(subject, new HashMap<>());
            Map map = this.map.get(subject);

            map.putIfAbsent(dataSource, new HashMap<>());
            map = (Map) map.get(dataSource);

            map.putIfAbsent(property, new HashMap<>());
            map = (Map) map.get(property);

            // Warning, the statement is already present in the dataSouce           
            return map.put(object, null) == null;

        }

        /**
         * Return the number of statements
         *
         * @return the number of statements
         */
        @Override
        public int size() {
            return size(0, map);
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
    private class DataQualityRecords implements DataQualityInformation {

        private Integer frequency;
        private Integer homogeneity;
        private Float reliability;
        private Long freshness;
        private Collection<Node> morePrecise;

        public DataQualityRecords() {
            frequency = 0;
            homogeneity = 0;
            morePrecise = new HashSet<>();
        }

        /**
         *
         * @param frequency the frequency to set
         */
        @Override
        public Number getFrequency() {
            return frequency;
        }

        /**
         *
         * @return the homogeneity
         */
        @Override
        public Number getHomogeneity() {
            return homogeneity;
        }

        /**
         *
         * @return the reliability
         */
        @Override
        public Float getReliability() {
            return reliability;
        }

        /**
         * Returns the timestamp.
         *
         * @return the freshness
         */
        @Override
        public Number getFreshness() {
            return freshness;
        }

        /**
         *
         * @return the morePrecise
         */
        @Override
        public Collection<Node> getMorePrecise() {
            return morePrecise;
        }

    }

    private final DataBag classes = new DataBag();

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

    public Map<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityInformation>>> computeDataQualityAssessment() {
        Map values = new HashMap<>();

        //
        Collection<Collection<RDFNode>> classes = this.classes.disjointValues();

        //
        for (Collection<RDFNode> classe : classes) {

            //
            values.put(classe, new HashMap<>());

        }

        return values;

    }

    private Map computeDataQualityAssessment(RDFNode node, Map values) {

        //
        if (classes.map.get(node) == null) {
            return values;
        }

        //
        for (Map.Entry<DataSource, Map<Property, Map<RDFNode, Object>>> provenances : classes.map.get(node).entrySet()) {

            DataSource dataSource = provenances.getKey();
            Map<Property, Map<RDFNode, Object>> properties = provenances.getValue();

            //
            for (Map.Entry<Property, Map<RDFNode, Object>> entry : properties.entrySet()) {

                //
                Property property = entry.getKey();
                Map<RDFNode, Object> objects = entry.getValue();

                for (RDFNode object : objects.keySet()) {
                    //
                    if (values.size() == computeDataQualityAssessment(object, values).size()) {
                        //                    
                        values.putIfAbsent(property, new HashMap<>());
                        ((Map) values.get(property)).putIfAbsent(object, new DataQualityRecords());
                        DataQualityRecords information = (DataQualityRecords) ((Map) values.get(property)).get(object);

                        information.frequency++;

                        if (dataSource.getFreshness() != null) {
                            information.freshness = Math.min(information.freshness, dataSource.getFreshness().toEpochDay());
                        }

                        if (dataSource.getReliability() != null) {
                            information.reliability = Math.max(information.reliability, dataSource.getReliability());
                        }

                    }
                }

            }

        }

        return values;

    }

    public static Map<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityInformation>>> process(DataSource... dataSources) throws IOException {
        return (new DataFusionProcessor(dataSources)).computeDataQualityAssessment();
    }

}
