package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.controller.services.DataFusionService;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionBean implements AutoCloseable {

    private static final Set<Lang> syntaxes = new HashSet(Arrays.asList(Lang.CSV, Lang.JSONLD, Lang.N3, Lang.NQ, Lang.NQUADS, Lang.NT, Lang.NTRIPLES, Lang.RDFJSON, Lang.RDFNULL, Lang.RDFTHRIFT, Lang.RDFXML, Lang.TRIG, Lang.TTL, Lang.TURTLE));

    private static Collection<LinkedHashSet<Property>> complexProperties;

    private Path path;

    private Collection<DataSource> dataSources;

    private DataSource selected;

    private DualListModel<Property> equivalenceProperties;

    private DualListModel<Collection<Property>> mappedProperties;

    private Collection<Property> mappedProperty;

    private Date freshness;

    private String property;

    private String syntax;

    private File result;

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
     * @return the path
     */
    public Path getPath() {
        return path;
    }

    /**
     *
     * @param path the path to set
     */
    public void setPath(Path path) {
        this.path = path;
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
     * @return the freshness
     */
    public Date getFreshness() {
        return freshness;
    }

    /**
     *
     * @param freshness the freshness to set
     */
    public void setFreshness(Date freshness) {
        this.freshness = freshness;
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
    public DualListModel<Collection<Property>> getMappedProperties() {
        return mappedProperties;
    }

    /**
     * @param mappedProperties the mappedProperties to set
     */
    public void setMappedProperties(DualListModel<Collection<Property>> mappedProperties) {
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
     * @return the result
     */
    public File getResult() {
        return result;
    }

    /**
     *
     * @param result the result to set
     */
    public void setResult(File result) {
        this.result = result;
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
            
            result = null;

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, exception.getStackTrace().toString());

        }

    }

    /**
     *
     */
    public void newDataSource() {

        complexProperties = new HashSet();

        List<Property> sourceEquivalencePropertie;
        List<Property> targetEquivalencePropertie;

        sourceEquivalencePropertie = new ArrayList();
        targetEquivalencePropertie = new ArrayList();

        sourceEquivalencePropertie.addAll(DataFusion.EQUIVALENCE_PROPERTIES);

        equivalenceProperties = new DualListModel(sourceEquivalencePropertie, targetEquivalencePropertie);

        List<Collection<Property>> sourceMappedProperties = new ArrayList();
        List<Collection<Property>> targetMappedProperties = new ArrayList();

        mappedProperties = new DualListModel(sourceMappedProperties, targetMappedProperties);

        selected = new DataSource();

        property = null;

        syntax = null;
        
        result = null;

    }

    /**
     *
     */
    public void removeDataSource() {

        dataSources.remove(selected);

        selected = null;
        
        result = null;

    }

    /**
     *
     */
    public void fuseDataSources() {

        try {

            DataFusionService service = new DataFusionService();

            File result = service.getFusedDataSet(path, dataSources);

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, null);

        }

    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {

        try {

            Path path = Files.createTempDirectory(this.path, UUID.randomUUID().toString());

            File file = new File(path.toFile(), event.getFile().getFileName());

            Files.copy(event.getFile().getInputstream(), file.toPath());

            selected.setPath(file.getCanonicalPath());

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, null);

        }

    }

    /**
     *
     */
    public void addEquivalenceProperty() {

        if (property == null) {
            return;
        }

        Property node = ResourceFactory.createProperty(property);

        equivalenceProperties.getTarget().add(node);

        property = null;

    }

    /**
     *
     * @param event
     */
    public void onPropertySelect(SelectEvent event) {
        mappedProperty = (Collection<Property>) event.getObject();
    }

    /**
     *
     * @param event
     */
    public void onPropertyUnselect(UnselectEvent event) {
        mappedProperty = null;
        property = null;
    }

    /**
     *
     * @param event
     */
    public void onPropertyTransfer(TransferEvent event) {
        mappedProperty = null;
        property = null;
    }

    /**
     *
     */
    public void addMappedProperty() {

        if (property == null) {
            return;
        }

        Property node = ResourceFactory.createProperty(property);

        if (mappedProperty == null) {

            LinkedHashSet<Property> complexProperty = new LinkedHashSet();
            complexProperty.add(node);

            mappedProperties.getTarget().add(complexProperty);

            complexProperties.add(complexProperty);

        } else {

            mappedProperties.getSource().remove(mappedProperty);

            mappedProperties.getTarget().remove(mappedProperty);

            mappedProperty.add(node);

            mappedProperties.getTarget().add(mappedProperty);

            mappedProperty = null;

        }

        property = null;

    }

    //#
    /**
     *
     */
    public void addDataSource() throws Exception {

        if (freshness != null) {
            selected.setFreshness(freshness);
        }

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

        selected.setEquivalenceProperties(equivalenceProperties.getTarget());

        for (Collection<Property> properties : mappedProperties.getTarget()) {

            for (Property p : properties) {

                LinkedHashSet<Property> complexProperty = new LinkedHashSet();
                complexProperty.add(p);

                Collection<LinkedHashSet<Property>> c = new HashSet();
                c.add(complexProperty);

                selected.getMappedProperties().add(c);

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

    /**
     *
     */
    @ApplicationScope
    @FacesConverter(value = "mappedPropertiesConverter")
    public static class MappedPropertiesConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {

            for (LinkedHashSet<Property> complexProperty : complexProperties) {

                if (complexProperty.toString().equals(value)) {
                    return complexProperty;
                }

            }

            return null;

        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {

            return value.toString();

        }

    }

}
