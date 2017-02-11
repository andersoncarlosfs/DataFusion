/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration;

import com.andersoncarlosfs.data.model.Dataset;
import com.andersoncarlosfs.util.UnionFind;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

    private final Collection<Dataset> datasets;
    private final Collection<Dataset> links;

    public DataFusion(Collection<Dataset> datasets, Dataset... links) throws IOException {
        this.datasets = Collections.unmodifiableCollection(datasets);
        if (links.length > 0) {
            this.links = Arrays.asList(links);
        } else {
            this.links = this.datasets;
        }
    }

    /**
     *
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public Sink findEquivalenceClasses() throws IOException {
        Sink<Triple> sink = new SinkTripleEquivalent();
        StreamRDF stream = new EquivalencePropertyFilterSinkRDF(sink);
        for (Dataset dataset : links) {
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        return (SinkTripleEquivalent) sink;
    }

    /**
     *
     * @return the data fusion assessment
     */
    public DataFusionAssessment calculateScore() {
        return null;
    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    public class SinkTripleEquivalent extends UnionFind<Node> implements Sink<Triple> {

        private SinkTripleEquivalent() {
        }

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
    private class EquivalencePropertyFilterSinkRDF extends StreamRDFBase {

        private final Collection<Node> properties;
        private final Sink<Triple> sink;

        private EquivalencePropertyFilterSinkRDF(Sink<Triple> sink) {
            this.sink = sink;
            this.properties = EQUIVALENCE_PROPERTIES.stream().map(RDFNode::asNode).collect(Collectors.toList());
        }

        private EquivalencePropertyFilterSinkRDF(Sink<Triple> sink, Collection<Node> properties) {
            this.sink = sink;
            this.properties = properties;
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
            sink.flush();
        }

    }

}
