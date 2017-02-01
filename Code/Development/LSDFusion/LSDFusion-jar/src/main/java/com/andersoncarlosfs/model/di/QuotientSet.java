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

    private final Collection<Property> equivalenceRelation;

    public QuotientSet(Collection<Property> equivalenceRelation) {
        super();
        this.equivalenceRelation = equivalenceRelation;
    }

    public QuotientSet(Collection<Property> equivalenceRelation, Collection<? extends EquivalenceClass> c) {
        super(c);
        this.equivalenceRelation = equivalenceRelation;
    }

    public QuotientSet(Collection<Property> equivalenceRelation, EquivalenceClass... equivalenceClasses) {
        super(Arrays.asList(equivalenceClasses));
        this.equivalenceRelation = equivalenceRelation;
    }

    /**
     *
     * @return the equivalenceRelation
     */
    public Collection<Property> getEquivalenceRelation() {
        return equivalenceRelation;
    }

    @Override
    public boolean add(EquivalenceClass e) {
        return super.add(e); //To change body of generated methods, choose Tools | Templates.
    }

}
