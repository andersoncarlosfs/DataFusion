/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import com.andersoncarlosfs.model.dq.DataQualityAssessment;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DataFusionAssessment extends HashMap<EquivalenceClass, Map<RDFNode, Map<RDFNode, DataQualityAssessment>>> {

    public DataFusionAssessment() {
        super();
    }

    public DataFusionAssessment(QuotientSet quotientSet) {
        super();
        for (EquivalenceClass equivalenceClass : quotientSet) {
            this.put(equivalenceClass, null);
        }
    }

}
