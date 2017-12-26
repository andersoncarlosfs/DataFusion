/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
import com.andersoncarlosfs.data.model.assessments.DataQualityAssessment;
import java.util.Collection;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionAssessmentBean {

    private DataFusionAssessment assessment;
    private Collection<RDFNode> equivalenceClass;
    private Collection<RDFNode> equivalenceClassProperties;
    private DataQualityAssessment details;

    public DataFusionAssessmentBean() {
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
     * @param assessment the assessment to set
     */
    public void setAssessment(DataFusionAssessment assessment) {
        this.assessment = assessment;
    }

    /**
     *
     */
    public Collection<Collection<RDFNode>> getClasses() {
        return assessment.getComputedDataQualityAssessment().keySet();
    }

    /**
     *
     */
    public Collection<RDFNode> getEquivalenceClass() {
        return equivalenceClass;
    }

    /**
     *
     */
    public void setEquivalenceClass(Collection<RDFNode> equivalenceClass) {
        this.equivalenceClass = equivalenceClass;
    }

    /**
     *
     */
    public Collection<Collection<RDFNode>> getProperties() {
        return assessment.getComputedDataQualityAssessment().get(equivalenceClass).keySet();
    }

    /**
     *
     */
    public Collection<RDFNode> getEquivalenceClassProperties() {
        return equivalenceClassProperties;
    }

    /**
     *
     */
    public void setEquivalenceClassProperties(Collection<RDFNode> equivalenceClassProperties) {
        this.equivalenceClassProperties = equivalenceClassProperties;
    }

    /**
     *
     */
    public Collection<RDFNode> getValues() {
        return assessment.getComputedDataQualityAssessment().get(equivalenceClass).get(equivalenceClassProperties).keySet();
    }

    /**
     *
     */
    public DataQualityAssessment getDetails() {
        return details;
    }

    /**
     *
     */
    public void setDetails(RDFNode value) {
        this.details = assessment.getComputedDataQualityAssessment().get(equivalenceClass).get(equivalenceClassProperties).get(value);
    }

    /**
     *
     */
    public void reset() {

        assessment = null;
        equivalenceClass = null;
        equivalenceClassProperties = null;
        details = null;

    }

}
