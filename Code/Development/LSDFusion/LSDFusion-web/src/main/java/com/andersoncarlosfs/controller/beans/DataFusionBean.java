package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.model.DataSource;
import com.andersoncarlosfs.data.model.Rule;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import com.andersoncarlosfs.data.util.Function;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionBean {

    public static final class Constants {

        public static final Collection<Lang> SYNTAXES = getSyntaxes();
        public static final Collection<Function> FUNCTIONS = getFunctions();

        /**
         *
         * @return
         */
        private static final Collection<Lang> getSyntaxes() {

            Collection<Lang> syntaxes = new HashSet<>();

            for (Field field : Lang.class.getFields()) {
                if (field.getType().equals(Lang.class)) {
                    try {
                        syntaxes.add((Lang) field.get(Lang.class));
                    } catch (IllegalArgumentException exception) {
                        //Logger.getLogger(DataSourceBean.class.getName()).log(Level.SEVERE, null, exception);
                    } catch (IllegalAccessException exception) {
                        //Logger.getLogger(DataSourceBean.class.getName()).log(Level.SEVERE, null, exception);
                    }
                }
            }

            return Collections.unmodifiableCollection(syntaxes);

        }

        /**
         * @return the functions
         */
        private static final Collection<Function> getFunctions() {
            return Collections.unmodifiableCollection(Arrays.asList(Function.values()));
        }

    }

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

}
