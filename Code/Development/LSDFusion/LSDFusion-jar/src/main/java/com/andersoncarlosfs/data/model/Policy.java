/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import com.andersoncarlosfs.data.util.Function;
import com.andersoncarlosfs.util.DisjointMap;
import java.util.Collection;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class Policy {

    DisjointMap<Property, Collection<Function>> rules = new DisjointMap<>();

    public Policy() {
        rules = new DisjointMap<>();
    }
    /*
    public addRules
    
    public getRules(){
        
    }
*/
}
