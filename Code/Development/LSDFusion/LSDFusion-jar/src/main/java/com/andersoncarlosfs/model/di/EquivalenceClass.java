/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class EquivalenceClass extends HashSet<RDFNode> {

    public EquivalenceClass() {
        super();
    }
    
    public EquivalenceClass(Collection<? extends RDFNode> c) {
        super(c);
    }

    public EquivalenceClass(RDFNode... instances) {
        super(Arrays.asList(instances));
    }

}
