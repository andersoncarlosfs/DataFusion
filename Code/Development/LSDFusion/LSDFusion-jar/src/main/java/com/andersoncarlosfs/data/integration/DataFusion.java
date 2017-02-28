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
import org.apache.jena.sparql.core.Quad;
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
        EquivalenceClasses stream = new EquivalenceClasses();
        for (LinkedDataset dataset : links) {
            stream.equivalenceProperties = dataset.getEquivalenceProperties().stream().map(RDFNode::asNode).collect(Collectors.toList());
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        return stream;
    }

    /**
     *
     * @return the data fusion assessment
     * @throws java.io.IOException
     */
    public Sink calculateScore() throws IOException {
        DataQualityEvaluation stream = new DataQualityEvaluation(findEquivalenceClasses());
        for (Dataset dataset : datasets) {
            RDFDataMgr.parse(stream, dataset.getCanonicalPath(), dataset.getSyntax());
        }
        stream.X();
        return null;
    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class DataQualityEvaluation extends StreamRDFBase {

        private final DisjointSet<Node> equivalenceClasses;
        private final Map<Node, Collection> nodes = new HashMap();
        private final Map<Node, Map<Node, DataQualityAssessment>> assessments = new HashMap();

        public DataQualityEvaluation(DisjointSet equivalenceClasses) {
            this.equivalenceClasses = equivalenceClasses;
        }

        /**
         *
         * @param subject
         * @param predicate
         * @param object
         */
        public void calculateScore(Node subject, Node predicate, Node object) {

            nodes.putIfAbsent(object, new ArrayList());
            nodes.get(object).add(subject);

            assessments.putIfAbsent(object, new HashMap<>());
            assessments.get(object).putIfAbsent(predicate, new DataQualityAssessment());

            DataQualityAssessment assessment = assessments.get(object).get(predicate);

            assessment.setOccurrenceFrequency(assessment.getOccurrenceFrequency() + 1);

        }

        /**
         *
         * @see StreamRDFBase#triple(org.apache.jena.graph.Triple)
         */
        @Override
        public void triple(Triple triple) {
            calculateScore(triple.getSubject(), triple.getPredicate(), triple.getObject());
        }

        /**
         *
         * @see StreamRDFBase#quad(org.apache.jena.sparql.core.Quad)
         */
        @Override
        public void quad(Quad quad) {
            calculateScore(quad.getSubject(), quad.getPredicate(), quad.getObject());
        }

        /**
         *
         * @return
         */
        public void X() {
            for (Map.Entry<Node, Map<Node, DataQualityAssessment>> values : assessments.entrySet()) {
                System.out.println("{");
                System.out.println(" " + values.getKey() + " : { ");
                for (Map.Entry<Node, DataQualityAssessment> assessment : values.getValue().entrySet()) {
                    System.out.println("  " + assessment.getKey() + " : { ");
                    System.out.println("   frequency : " + assessment.getValue().getOccurrenceFrequency());
                    System.out.println("  }");
                }
                System.out.println(" }");
                System.out.println("}");
            }
        }

    }

    /**
     *
     * @author Anderson Carlos Ferreira da Silva
     */
    private class EquivalenceClasses extends DisjointSet<Node> implements StreamRDF {

        private Collection<Node> equivalenceProperties;

        /**
         *
         * @param subject
         * @param predicate
         * @param object
         * @see DisjointSet#union(java.lang.Object, java.lang.Object)
         */
        public void union(Node subject, Node predicate, Node object) {
            if (equivalenceProperties.contains(predicate)) {
                super.union(subject, object);
            }
        }

        /**
         *
         * @see StreamRDF#start()
         */
        @Override
        public void start() {
        }

        /**
         *
         * @see StreamRDF#triple(org.apache.jena.graph.Triple)
         */
        @Override
        public void triple(Triple triple) {
            union(triple.getSubject(), triple.getPredicate(), triple.getObject());
        }

        /**
         *
         * @see StreamRDF#quad(org.apache.jena.sparql.core.Quad)
         */
        @Override
        public void quad(Quad quad) {
            union(quad.getSubject(), quad.getPredicate(), quad.getObject());
        }

        /**
         *
         * @see StreamRDF#base(java.lang.String)
         */
        @Override
        public void base(String base) {
        }

        /**
         *
         * @see StreamRDF#prefix(java.lang.String, java.lang.String)
         */
        @Override
        public void prefix(String prefix, String iri) {
        }

        /**
         *
         * @see StreamRDF#finish()
         */
        @Override
        public void finish() {
        }

    }

}
