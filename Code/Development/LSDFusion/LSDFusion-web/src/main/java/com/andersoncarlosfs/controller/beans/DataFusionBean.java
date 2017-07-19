package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.controller.ConstantConverters;
import com.andersoncarlosfs.controller.util.Notificator;
import com.andersoncarlosfs.model.DataSource;
import com.andersoncarlosfs.data.model.Rule;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import com.andersoncarlosfs.model.enums.Action;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionBean {

    private final Collection<DataSource> dataSources = new HashSet<>();
    
    private final Collection<Property> properties = new HashSet<>();
    
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
        return persist(Action.UPDATE, object);
    }
    
    /**
     *
     * @param object
     * @return
     */
    public String remove(Object object) {
        return persist(Action.DELETE, object);
    }
    
    /**
     * 
     * @param action
     * @param object
     * @return 
     */
    private String persist(Action action, Object object) {
        
        if (FacesContext.getCurrentInstance().isValidationFailed()) {
            return null;
        }
        
        Collection collection = null;
        
        if (object instanceof DataSource) {
                
            DataSource dataSource = (DataSource) object;
                
            if (dataSource.getDate() != null && Calendar.getInstance().getTime().before(dataSource.getDate())) {
                    
                String message = "Freshness: Validation Warning: Date is in the future";
                    
                Notificator.addWarningMessage(message, message);
                    
            }
                
            collection = dataSources;
                
        }

        if (object instanceof Rule) {
            
            collection = rules;
            
        }   
        
        
        if (collection == object) {
            
            String message = "Object: Persistance Error: Object is null";           
            
            Notificator.addErrorMessage(message, message);
            
            return null;
            
        }
        
        try {
          
            switch (action) {
                
                case CREATE:
                
                case UPDATE:
                    
                    if (object instanceof DataSource && !dataSources.contains(object)) {
                        
                        DataSource dataSource = (DataSource) object;
                        
                        Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());
                    
                        StmtIterator iterator = model.listStatements();

                        while (iterator.hasNext()) {

                            Statement statement = iterator.next();

                            Property property = statement.getPredicate();

                            properties.add(property);

                        }
                        
                    }
                    
                    collection.add(object);
                
                    break;
                
                case DELETE:
                    
                    collection.remove(object);
                    
                    if (object instanceof DataSource) {
                        
                        dataSources.clear();
                        
                        for (DataSource dataSource : dataSources) {
                            
                            Model model = RDFDataMgr.loadModel(dataSource.getPath().toString(), dataSource.getSyntax());
                    
                            StmtIterator iterator = model.listStatements();

                            while (iterator.hasNext()) {

                                Statement statement = iterator.next();

                                Property property = statement.getPredicate();

                                properties.add(property);

                            }
                            
                        }
                        
                    }
                    
                    break;
                    
                default:
                    
                    String message = "Object: Internal Error: Action is undefined";
                        
                    String details = "Please, contact the administrator";

                    Notificator.addErrorMessage(message, details);
                        
                    return null;
                
            }

        } catch (RiotException exception) {
                        
            String message = "File: Internal Error: File is unreadable";
                        
            String details = "Please, check the file contents and syntax";

            Notificator.addErrorMessage(message, details);

            Notificator.log(message, exception);
                        
            return null;
                                   
 
        } catch (ClassCastException exception) {
            
            StringBuilder message = new StringBuilder(object.getClass().getSimpleName());
            
            if (message.toString().isEmpty()) {
                message.append("Object");
            }
            
            message.append(": Persistance Error: Object is unknown");           
            
            Notificator.addErrorMessage(message.toString(), message.toString());
            
            Notificator.log(message.toString(), exception);
            
            return null;
            
        } 

        return collection.isEmpty() ? "/pages/private/datafusion/main?faces-redirect=true" : "list?faces-redirect=true";
        
    }
    
    /**
     *
     */
    @FacesConverter(value = "propertyConverter")
    public class PropertyConverter extends ConstantConverters.AbstractConverter<Property> {

        @Override
        protected Collection<Property> getCollection() {
            return properties;
        }

    }

}
