package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionBean implements AutoCloseable {

    private Path path;

    private static final Set<Lang> syntaxes = new HashSet(Arrays.asList(Lang.CSV, Lang.JSONLD, Lang.N3, Lang.NQ, Lang.NQUADS, Lang.NT, Lang.NTRIPLES, Lang.RDFJSON, Lang.RDFNULL, Lang.RDFTHRIFT, Lang.RDFXML, Lang.TRIG, Lang.TTL, Lang.TURTLE));

    private Collection<DataSource> dataSources;

    private DataSource selected;

    private DualListModel<Property> equivalenceProperties;

    private DualListModel<Property> mappedProperties;

    private String property;

    private String syntax;

    public DataFusionBean() {
    }

    /**
     *
     * @return the syntaxes
     */
    public Set<Lang> getSyntaxes() {
        return syntaxes;
    }

    /**
     *
     * @return the dataSources
     */
    public Collection<DataSource> getDataSources() {
        return dataSources;
    }

    /**
     *
     * @param dataSources the dataSources to set
     */
    public void setDataSources(Collection<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    /**
     *
     * @return the selected
     */
    public DataSource getSelected() {
        return selected;
    }

    /**
     *
     * @param selected the selected to set
     */
    public void setSelected(DataSource selected) {
        this.selected = selected;
    }

    /**
     *
     * @return the equivalenceProperties
     */
    public DualListModel<Property> getEquivalenceProperties() {
        return equivalenceProperties;
    }

    /**
     * @param equivalenceProperties the equivalenceProperties to set
     */
    public void setEquivalenceProperties(DualListModel<Property> equivalenceProperties) {
        this.equivalenceProperties = equivalenceProperties;
    }

    /**
     *
     * @return the mappedProperties
     */
    public DualListModel<Property> getMappedProperties() {
        return mappedProperties;
    }

    /**
     * @param mappedProperties the mappedProperties to set
     */
    public void setMappedProperties(DualListModel<Property> mappedProperties) {
        this.mappedProperties = mappedProperties;
    }

    /**
     *
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     *
     * @param property the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     *
     * @return the syntax
     */
    public String getSyntax() {
        return syntax;
    }

    /**
     *
     * @param syntax the syntax to set
     */
    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    /**
     *
     */
    public void newDataFusion() {

        try {

            if (path != null) {
                deleteFiles();
            }

            dataSources = new HashSet();

            selected = null;

            path = Files.createTempDirectory(UUID.randomUUID().toString());

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, exception.getStackTrace().toString());

        }

    }

    /**
     *
     */
    public void newDataSource() {

        List<Property> source;
        List<Property> target;

        source = new ArrayList<Property>();
        target = new ArrayList<Property>();

        source.addAll(DataFusion.EQUIVALENCE_PROPERTIES);

        equivalenceProperties = new DualListModel(source, target);

        source = new ArrayList<Property>();
        target = new ArrayList<Property>();

        mappedProperties = new DualListModel(source, target);

        property = new String();

        selected = new DataSource();

    }

    /**
     *
     */
    public void removeDataSource() {

        dataSources.remove(selected);

        selected = null;

    }

    /**
     *
     */
    public void fuseDataSources() {

    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {

        System.out.println(event.getFile().getFileName());

        try {
            
           File file = new File(path.toFile(), event.getFile().getFileName());
           
           Files.copy(event.getFile().getInputstream(), file.toPath());
           
           selected.setPath(file.getCanonicalPath());

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, null);

        }

        System.out.println(selected.getPath());

    }

    /**
     *
     */
    public void addEequivalenceProperty() {

        if (property.trim().isEmpty()) {
            return;
        }

        Property node = ResourceFactory.createProperty(property);

        equivalenceProperties.getSource().add(node);

        property = new String();

    }

    /**
     *
     */
    public void addMappedProperty() {

        if (property.trim().isEmpty()) {
            return;
        }

        Property node = ResourceFactory.createProperty(property);

        //selected.getMappedProperties().add(node);
        property = new String();

    }

    //#
    /**
     *
     */
    public void addDataSource() throws Exception {

        if (selected.getPath().trim().isEmpty()) {
            FacesContext.getCurrentInstance().validationFailed();
            NotificationBean.addErrorMessage(new Exception(""), "");
        }

        if (FacesContext.getCurrentInstance().isValidationFailed()) {
            return;
        }

        for (Lang l : syntaxes) {
            if (l.getName().equals(syntax)) {
                selected.setSyntax(l);
                break;
            }
        }

        dataSources.add(selected);

        selected = null;

    }

    /**
     *
     * @throws IOException
     */
    private void deleteFiles() throws IOException {
        FileUtils.deleteDirectory(path.toFile());
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        deleteFiles();
    }

}
