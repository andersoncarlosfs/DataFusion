/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class QuotientSet extends HashSet<EquivalenceClass> {

    private final Property equivalenceRelation;

    public QuotientSet(Property equivalenceRelation) {
        super();
        this.equivalenceRelation = equivalenceRelation;
    }

    public QuotientSet(Property equivalenceRelation, Collection<? extends EquivalenceClass> c) {
        super(c);
        this.equivalenceRelation = equivalenceRelation;
    }

    public QuotientSet(Property equivalenceRelation, EquivalenceClass... equivalenceClasses) {
        super(Arrays.asList(equivalenceClasses));
        this.equivalenceRelation = equivalenceRelation;
    }

    /**
     *
     * @return the equivalenceRelation
     */
    public Property getEquivalenceRelation() {
        return equivalenceRelation;
    }

}
