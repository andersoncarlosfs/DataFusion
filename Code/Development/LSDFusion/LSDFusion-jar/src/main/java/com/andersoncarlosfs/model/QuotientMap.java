/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class QuotientMap extends HashMap<EquivalenceClass, Map<RDFNode, Map<RDFNode, QualityAssessment>>> {

    public QuotientMap() {
        super();
    }

    public QuotientMap(QuotientSet quotientSet) {
        super();
        for (EquivalenceClass equivalenceClass : quotientSet) {
            this.put(equivalenceClass, null);
        }
    }

}
