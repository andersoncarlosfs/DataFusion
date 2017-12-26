/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.model.assessments.DataFusionAssessment;
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
    public Collection<Collection<RDFNode>> getEquivalenceClasses() {
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
     * @return
     */
    public void reset() {

        assessment = null;
        equivalenceClass = null;

    }

}
