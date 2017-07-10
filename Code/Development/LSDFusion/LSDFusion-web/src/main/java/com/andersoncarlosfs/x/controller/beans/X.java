package com.andersoncarlosfs.x.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.controller.beans.NotificationBean;
import com.andersoncarlosfs.data.model.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class X implements AutoCloseable {

    private static final Set<Lang> syntaxes = new HashSet(Arrays.asList(Lang.CSV, Lang.JSONLD, Lang.N3, Lang.NQ, Lang.NQUADS, Lang.NT, Lang.NTRIPLES, Lang.RDFJSON, Lang.RDFNULL, Lang.RDFTHRIFT, Lang.RDFXML, Lang.TRIG, Lang.TTL, Lang.TURTLE));

    private static Collection<LinkedHashSet<String>> complexProperties;

    private Path path;

    private Collection<DataSource> dataSources;

    private DataSource selected;

    private DualListModel<String> equivalenceProperties;

    private DualListModel<Collection<String>> mappedProperties;

    private Collection<String> mappedProperty;

    private Date freshness;

    private String property;

    private String syntax;

    private File result;

    private TreeNode root;

    private StreamedContent file;

    public X() {
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
    public DualListModel<String> getEquivalenceProperties() {
        return equivalenceProperties;
    }

    /**
     * @param equivalenceProperties the equivalenceProperties to set
     */
    public void setEquivalenceProperties(DualListModel<String> equivalenceProperties) {
        this.equivalenceProperties = equivalenceProperties;
    }

    /**
     *
     * @return the mappedProperties
     */
    public DualListModel<Collection<String>> getMappedProperties() {
        return mappedProperties;
    }

    /**
     * @param mappedProperties the mappedProperties to set
     */
    public void setMappedProperties(DualListModel<Collection<String>> mappedProperties) {
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
     * @return the root
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     *
     * @param root the root to set
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }

    /**
     *
     * @return the file
     */
    public StreamedContent getFile() {
        return file;
    }

    /**
     *
     * @param file the file to set
     */
    public void setFile(StreamedContent file) {
        this.file = file;
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
/*
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
*/
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
    public void fuseDataSources() throws Exception {
/*
        try {

           // DataFusionService service = new DataFusionService();

            File file = service.getFusedDataSet(path, dataSources);

            Model model = RDFDataMgr.loadModel(file.getCanonicalPath(), Lang.N3);

            root = new DefaultTreeNode(file.getName(), null);

            for (Iterator<Statement> iterator = model.listStatements(); iterator.hasNext();) {

                Statement statement = iterator.next();

                TreeNode r = new DefaultTreeNode(statement.getSubject().toString());
                TreeNode p = new DefaultTreeNode(statement.getPredicate().toString());
                TreeNode o = new DefaultTreeNode(statement.getObject().toString());

                r.getChildren().add(p);
                p.getChildren().add(o);

                root.getChildren().add(r);

            }

            result = file;

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, exception.getStackTrace().toString());
            
            throw exception;

        }
*/
    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
/*
        try {

            Path path = Files.createTempDirectory(this.path, UUID.randomUUID().toString());

            File file = new File(path.toFile(), event.getFile().getFileName());

            Files.copy(event.getFile().getInputstream(), file.toPath());

            selected.setPath(file.toPath());

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, exception.getStackTrace().toString());

        }
*/
    }

    /**
     *
     */
    public void addEquivalenceProperty() {

        if (property == null) {
            return;
        }

        equivalenceProperties.getTarget().add(property);

        property = null;

    }

    /**
     *
     * @param event
     */
    public void onPropertySelect(SelectEvent event) {
        mappedProperty = (Collection<String>) event.getObject();
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

        if (mappedProperty == null) {

            LinkedHashSet<String> complexProperty = new LinkedHashSet();
            complexProperty.add(property);

            mappedProperties.getTarget().add(complexProperty);

            complexProperties.add(complexProperty);

        } else {

            mappedProperties.getSource().remove(mappedProperty);

            mappedProperties.getTarget().remove(mappedProperty);

            mappedProperty.add(property);

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
/*
        if (freshness != null) {
            selected.setFreshness(freshness);
        }

        if (selected.getPath() == null) {
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

        selected.setEquivalenceProperties(new HashSet<Property>());

        for (String p : equivalenceProperties.getTarget()) {

            selected.getEquivalenceProperties().add(ResourceFactory.createProperty(p));

        }

        for (Collection<String> properties : mappedProperties.getTarget()) {

            for (String p : properties) {

                LinkedHashSet<Property> complexProperty = new LinkedHashSet();
                complexProperty.add(ResourceFactory.createProperty(p));

                Collection<LinkedHashSet<Property>> c = new HashSet();
                c.add(complexProperty);

                selected.getMappedProperties().add(c);

            }

        }

        dataSources.add(selected);

        selected = null;

    }

    public void downloadFile() throws Exception {

        try {

            file = new DefaultStreamedContent(new FileInputStream(result), "", "result");

        } catch (Exception exception) {

            NotificationBean.addErrorMessage(exception, exception.getStackTrace().toString());

            throw exception;

        }
*/
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

            for (LinkedHashSet<String> complexProperty : complexProperties) {

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
