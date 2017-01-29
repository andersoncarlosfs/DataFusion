/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class EquivalenceClass extends HashSet<RDFNode> {
    
    private final Property equivalenceRelation;

    public EquivalenceClass(Property equivalenceRelation) {
        super();
        this.equivalenceRelation = equivalenceRelation;
    }

    public EquivalenceClass(Property equivalenceRelation, Collection<? extends RDFNode> instances) {      
        super(instances);
        this.equivalenceRelation = equivalenceRelation;
    }

    public EquivalenceClass(Property equivalenceRelation, RDFNode... instances) {
        super(Arrays.asList(instances));
        this.equivalenceRelation = equivalenceRelation;
    }

    public Property equivalenceRelation() {
        return equivalenceRelation;
    }

}
