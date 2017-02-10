/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.integration;

import com.andersoncarlosfs.data.model.DataQualityAssessment;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionAssessment extends HashMap<Collection<RDFNode>, Map<RDFNode, Map<RDFNode, DataQualityAssessment>>> {

    public DataFusionAssessment() {
        super();
    }

    public DataFusionAssessment(Collection<Collection<RDFNode>> quotientSet) {
        super();
        for (Collection<RDFNode> equivalenceClass : quotientSet) {
            this.put(equivalenceClass, null);
        }
    }

}
