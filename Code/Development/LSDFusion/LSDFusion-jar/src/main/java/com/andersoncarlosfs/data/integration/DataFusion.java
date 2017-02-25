/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration;

import com.andersoncarlosfs.data.model.DataQualityAssessment;
import com.andersoncarlosfs.data.model.Dataset;
import com.andersoncarlosfs.data.model.LinkedDataset;
import com.andersoncarlosfs.util.DisjointSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.atlas.lib.Sink;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusion {

    /**
     *
     */
    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    private final Collection<Dataset> datasets = new ArrayList<>();
    private final Collection<LinkedDataset> links = new ArrayList<>();

    public DataFusion(Collection<Dataset> datasets) throws IOException {
        for (Dataset dataset : datasets) {
            if (dataset instanceof LinkedDataset) {
                this.links.add((LinkedDataset) dataset);
            } else {
                this.datasets.add(dataset);
            }
        }
    }

    /**
     *
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public DisjointSet findEquivalenceClasses() throws IOException {
        Sink<Triple> sink = new SinkTripleEquivalent();
        for (LinkedDataset dataset : links) {
            StreamRDF stream = new EquivalencePropertyFilterSinkRDF(sink, dataset.getEquivalenceProperties());
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        return (DisjointSet) sink;
    }

    /**
     *
     * @return the data fusion assessment
     * @throws java.io.IOException
     */
    public Sink calculateScore() throws IOException {
        DisjointSet equivalenceClasses = findEquivalenceClasses();
        Sink<Triple> sink = new SinkTripleQualityAssessment(equivalenceClasses);
        StreamRDF stream = new NodeFilterSinkRDF(sink, equivalenceClasses.values());
        for (Dataset dataset : datasets) {
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        return null;
    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class SinkTripleEquivalent extends DisjointSet<Node> implements Sink<Triple> {

        @Override
        public void send(Triple triple) {
            union(triple.getSubject(), triple.getObject());
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class SinkTripleQualityAssessment implements Sink<Triple> {

        private final DisjointSet equivalenceClasses;
        private final Map<Node, Map<Node, DataQualityAssessment>> assessments;

        private SinkTripleQualityAssessment(DisjointSet equivalenceClasses) {
            this.equivalenceClasses = equivalenceClasses;
            this.assessments = (Map) equivalenceClasses.values().parallelStream().collect(Collectors.toMap(v -> v, v -> new HashMap<>()));
        }

        @Override
        public void send(Triple triple) {
            System.out.println(triple.getPredicate());
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class EquivalencePropertyFilterSinkRDF extends StreamRDFBase {

        private final Collection<Node> properties;
        private final Sink<Triple> sink;

        private EquivalencePropertyFilterSinkRDF(Sink<Triple> sink, Collection<Property> properties) {
            if (properties == null) {
                throw new NullPointerException("Equivalence properties cannot be null");
            }
            this.sink = sink;
            this.properties = properties.stream().map(RDFNode::asNode).collect(Collectors.toList());
        }

        /**
         *
         * @see StreamRDFBase#triple(org.apache.jena.graph.Triple)
         */
        @Override
        public void triple(Triple triple) {
            if (properties.contains(triple.getPredicate())) {
                sink.send(triple);
            }
        }

        /**
         *
         * @see StreamRDFBase#finish()
         */
        @Override
        public void finish() {
        }

    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class NodeFilterSinkRDF extends StreamRDFBase {

        private final Sink<Triple> sink;

        private NodeFilterSinkRDF(Sink<Triple> sink) {
            this.sink = sink;
        }

        /**
         *
         * @see StreamRDFBase#triple(org.apache.jena.graph.Triple)
         */
        @Override
        public void triple(Triple triple) {
            sink.send(triple);
        }

        /**
         *
         * @see StreamRDFBase#finish()
         */
        @Override
        public void finish() {
        }

    }

}
