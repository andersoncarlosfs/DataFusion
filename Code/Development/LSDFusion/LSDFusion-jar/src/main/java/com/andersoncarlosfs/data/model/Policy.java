/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import com.andersoncarlosfs.data.util.Function;
import com.andersoncarlosfs.util.DisjointMap;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author andersoncarlosfs
 */
public class Policy extends AbstractSet<Rule> implements Set<Rule>, Cloneable, Serializable {

    private transient DisjointMap<Property, Map<Function, Collection<Object>>> data;

    public Policy() {
        data = new DisjointMap<>();
    }

    public Policy(Collection<Rule> rules) {
        data = new DisjointMap<>(Math.max((int) (rules.size() / .75f) + 1, 16));
        addAll(rules);
    }

    @Override
    public Iterator<Rule> iterator() {
        return values().iterator();
    }

    @Override
    public int size() {
        return values().size();
    }

    public boolean contains(Property property, Function function) {
        Map<Property, Map<Function, Collection<Object>>> map = data.find(property);
        
        if(map == null) {
            return false;
        }
        
        //
        for (Map<Function, Collection<Object>> functions : map.values()) {
            if (functions.keySet().contains(function)) {
                return true;
            }
        }
        
        return false;
    }

    public boolean contains(Function function) {
        for (Map<Function, Collection<Object>> functions : data.values()) {
            if (functions.keySet().contains(function)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(Rule rule) {
        //
        Property property = rule.getProperty();
        Function function = rule.getFunction();
        Object value = rule.getValue();

        // Attaching zero or more functions to a property 
        boolean PRESENT = data.putIfAbsent(property, new HashMap<>()) == null;

        //
        Map<Function, Collection<Object>> arguments = data.get(property);

        // Attaching zero or more arguments to a property 
        PRESENT = arguments.putIfAbsent(function, new HashSet<>()) == null || PRESENT;
        PRESENT = arguments.get(function).add(value) || PRESENT;

        // Grouping the properties
        if (function == Function.MAPPING) {
            data.union(property, (Property) value);
        }

        return PRESENT;
    }

    @Override
    public void clear() {
        data.clear();
    }

    private Collection<Rule> values() {
        Collection<Rule> values = new HashSet<>();
        //
        for (Entry<Property, Map<Function, Collection<Object>>> property_entry : data.entrySet()) {
            for (Entry<Function, Collection<Object>> property_function_entry : property_entry.getValue().entrySet()) {
                //
                if (property_function_entry.getValue().isEmpty()) {
                    values.add(new Rule(property_function_entry.getKey(), property_entry.getKey()));
                } else {
                    //
                    for (Object object : property_function_entry.getValue()) {
                        values.add(new Rule(property_function_entry.getKey(), property_entry.getKey(), object));
                    }
                }
            }
        }
        return values;
    }

}
