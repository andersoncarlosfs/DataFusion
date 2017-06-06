
import com.andersoncarlosfs.data.integration.processors.DataFusionProcessor;
import com.andersoncarlosfs.data.model.assessments.DataQualityInformation;
import com.andersoncarlosfs.x.data.integration.DataFusion;
import com.andersoncarlosfs.x.model.DataSource;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author AndersonCarlos
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            DataSource dataSet = new DataSource("C:/Users/AndersonCarlos/Desktop/LSDFusion/Datasets/Books/dataset.rdf");
            dataSet.setSyntax(Lang.N3);
            //
            DataSource link = new DataSource("C:/Users/AndersonCarlos/Desktop/LSDFusion/Datasets/Books/links.rdf");
            link.setSyntax(Lang.N3);
            link.setEquivalenceProperties(DataFusion.EQUIVALENCE_PROPERTIES);

            for (Map.Entry<Collection<RDFNode>, Map<Property, Map<RDFNode, DataQualityInformation>>> statements : DataFusionProcessor.process(dataSet, link).entrySet()) {
                System.out.println("{");
                System.out.println("\t" + statements.getKey());
                for (Map.Entry<Property, Map<RDFNode, DataQualityInformation>> properties : statements.getValue().entrySet()) {
                    System.out.println("\t\t" + properties.getKey());
                    for (Map.Entry<RDFNode, DataQualityInformation> objects : properties.getValue().entrySet()) {
                        System.out.println("\t\t\t" + objects.getKey());
                        System.out.println("\t\t\t\t" + "F" + objects.getValue().getFrequency()
                                + " F" + objects.getValue().getFreshness() + " H" + objects.getValue().getHomogeneity()
                                + " R" + objects.getValue().getReliability() + " M" + objects.getValue().getMorePrecise());
                    }
                }
                System.out.println("}");
            }
        } catch (IOException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
