package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.DataSource;
import com.andersoncarlosfs.data.model.Rule;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import java.util.Collection;
import java.util.HashSet;
import javax.faces.context.FacesContext;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionBean {

    private final Collection<DataSource> dataSources = new HashSet<>();
    private final Collection<Rule> rules = new HashSet<>();

    private boolean duplicatesAllowed;

    private DataFusionAssessment assessment;

    public DataFusionBean() {

    }

    /**
     * @return the dataSources
     */
    public Collection<DataSource> getDataSources() {
        return dataSources;
    }

    /**
     * @return the rules
     */
    public Collection<Rule> getRules() {
        return rules;
    }

    /**
     * @return the duplicatesAllowed
     */
    public boolean isDuplicatesAllowed() {
        return duplicatesAllowed;
    }

    /**
     * @param duplicatesAllowed the duplicatesAllowed to set
     */
    public void setDuplicatesAllowed(boolean duplicatesAllowed) {
        this.duplicatesAllowed = duplicatesAllowed;
    }

    /**
     * @return the properties
     */
    public Collection<Property> getProperties() {

        Collection<Property> properties = new HashSet<>();

        for (DataSource dataSource : dataSources) {

            Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());

            StmtIterator iterator = model.listStatements();

            while (iterator.hasNext()) {

                Statement statement = iterator.next();

                Property property = statement.getPredicate();

                properties.add(property);

            }

        }

        return properties;

    }

    /**
     *
     * @return the assessment
     */
    public DataFusionAssessment getAssessment() {
        return assessment;
    }

    /**
     *
     */
    public void reset() {

        dataSources.clear();
        rules.clear();

        duplicatesAllowed = false;

        assessment = null;

    }

    /**
     *
     * @param object
     * @return
     */
    public String save(Object object) {

        if (object == null || FacesContext.getCurrentInstance().isValidationFailed()) {
            return null;
        }

        try {
            
            if (object instanceof DataSource) {
                dataSources.add((DataSource) object);
            }

            if (object instanceof Rule) {
                rules.add((Rule) object);
            }
            
        } catch (ClassCastException exception) {
            
            FacesContext.getCurrentInstance().getExternalContext().log(exception.getMessage(), exception);
            
            return null;
            
        }

        return "/pages/private/datafusion/main";

    }
    
    /**
     *
     * @param object
     * @return
     */
    public String remove(Object object) {

        if (object == null || FacesContext.getCurrentInstance().isValidationFailed()) {
            return null;
        }

        try {
            
            if (object instanceof DataSource) {
                dataSources.add((DataSource) object);
            }

            if (object instanceof Rule) {
                rules.add((Rule) object);
            }
            
        } catch (ClassCastException exception) {
            
            FacesContext.getCurrentInstance().getExternalContext().log(exception.getMessage(), exception);
            
            return null;
            
        }

        return "/pages/private/datafusion/main";

    }

}
