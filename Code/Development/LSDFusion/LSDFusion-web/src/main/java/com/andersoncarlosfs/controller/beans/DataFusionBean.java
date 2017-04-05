package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.integration.DataFusion;
import com.andersoncarlosfs.data.model.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionBean {

    private Collection<DataSource> dataSources;

    private DataSource selected;

    private UploadedFile file;

    private DualListModel<Property> equivalenceProperties;

    private DualListModel<Property> mappedProperties;

    private String property;

    public DataFusionBean() {
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
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     *
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
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
        return equivalenceProperties;
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
     */
    public void newDataFusion() {

        dataSources = new HashSet();

        selected = null;

    }

    /**
     *
     */
    public void newDataSource() {

        List<Property> source;
        List<Property> target;

        selected = new DataSource();

        source = new ArrayList<Property>();
        target = new ArrayList<Property>();

        source.addAll(DataFusion.EQUIVALENCE_PROPERTIES);

        equivalenceProperties = new DualListModel(source, target);

        source = new ArrayList<Property>();
        target = new ArrayList<Property>();

        mappedProperties = new DualListModel(source, target);

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
     */
    public void addDataSource() {

        dataSources.add(selected);

        selected = null;

    }

    /**
     *
     */
    public void addEequivalenceProperty() {

        Property node = ResourceFactory.createProperty(property);

        equivalenceProperties.getSource().add(node);

        property = new String();

    }

    /**
     *
     */
    public void addMappedProperty() {

        Property node = ResourceFactory.createProperty(property);

        //selected.getMappedProperties().add(node);
        property = new String();

    }

}
