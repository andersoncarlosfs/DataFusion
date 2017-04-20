/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.controller.services;

import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.DataQualityAssessment;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusionService {

    /**
     *
     * @param dataSources
     * @return the equivalence classes
     * @throws java.io.IOException
     */
    public Collection<Collection<Node>> findEquivalenceClasses(Collection<DataSource> dataSources) throws IOException {
        return new DataFusion(dataSources).getDataQualityAssessment().keySet();
    }

    /**
     *
     * @param dataSources
     * @return
     * @throws IOException
     */
    public Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> getDataQualityAssessment(Collection<DataSource> dataSources) throws IOException {
        return new DataFusion(dataSources).getDataQualityAssessment();
    }

    /**
     *
     * @param path
     * @param dataSources
     * @return
     * @throws IOException
     */
    public File getFusedDataSet(Path path, Collection<DataSource> dataSources) throws IOException {
        File file = Files.createTempFile(path, null, null).toFile();

        PrintWriter writer = new PrintWriter(file);

        Map<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> computedStatements = getDataQualityAssessment(dataSources);

        for (Map.Entry<Collection<Node>, Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>>> computedStatement : computedStatements.entrySet()) {

            Collection<Node> equivalenceClasse = computedStatement.getKey();
            Map<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>> computedProperties = computedStatement.getValue();

            String equivalence = "<" + NodeFactory.createBlankNode("http://fusion-result/" + UUID.randomUUID()).toString() + ">";

            for (Node subject : equivalenceClasse) {

                writer.println("<" + subject + "> " + "<http://fusion-result/rdf/hasEquivalent> " + equivalence + " .");

                for (Map.Entry<LinkedHashSet<Node>, Map<Node, DataQualityAssessment>> entry : computedProperties.entrySet()) {

                    LinkedHashSet<Node> complexProperty = entry.getKey();
                    Map<Node, DataQualityAssessment> value = entry.getValue();

                    for (Map.Entry<Node, DataQualityAssessment> computedObjects : value.entrySet()) {

                        Node object = computedObjects.getKey();
                        DataQualityAssessment assessment = computedObjects.getValue();

                        writer.append("<" + subject + "> ");

                        int i = 0;

                        for (Node property : complexProperty) {

                            writer.append("<" + property + "> ");

                            if (i > 2) {

                                writer.println(".");

                                i = 0;

                            }

                            i++;

                        }

                        String blank = "<" + NodeFactory.createBlankNode("http://fusion-result/" + UUID.randomUUID()).toString() + ">";

                        String v = "";

                        if (object.isLiteral()) {
                            v = blank + " <http://fusion-result/hasValue> " + object + " .";
                        } else {
                            blank = "<" + object + "> ";
                        }

                        writer.append(blank + " ");

                        writer.println(".");

                        if(!v.isEmpty()){
                            writer.println(v);
                        }
                        
                        if (assessment == null) {
                            continue;
                        }

                        writer.println(blank + " <http://fusion-result/hasFrequency> \"" + assessment.getFrequency() + "\" .");

                        writer.println(blank + " <http://fusion-result/hasHomogeneity> \"" + assessment.getHomogeneity() + "\" .");

                        for (Node node : assessment.getMorePrecise()) {

                            if (node.isLiteral()) {
                                writer.println(blank + " <http://fusion-result/hasMorePrecise> " + node + " .");
                            } else {
                                writer.println(blank + " <http://fusion-result/hasMorePrecise> " + "<" + node + ">" + " .");
                            }

                        }

                        Float mark = (assessment.getFreshness() + assessment.getFrequency() + assessment.getHomogeneity() + assessment.getReliability() + (1 * assessment.getMorePrecise().size())) / (4 + assessment.getMorePrecise().size());

                        writer.println("<" + subject + "> " + " <http://fusion-result/hasScore> \"" + mark + "\" .");

                    }
                }
            }
        }

        writer.close();

        return file;
    }
}
