/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import com.andersoncarlosfs.data.util.Function;
import com.andersoncarlosfs.util.DisjointMap;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author andersoncarlosfs
 */
public class Policy extends AbstractCollection<Rule> {

    private transient DisjointMap<Property, Map<Function, Collection<Object>>> data;

    public Policy() {
        data = new DisjointMap<>();
    }

    public Policy(Collection<Rule> rules) {
        //
        data = new DisjointMap<>(Math.max((int) (rules.size() / .75f) + 1, 16));

        //
        addAll(rules);
    }

    @Override
    public Iterator<Rule> iterator() {
        return values().iterator();
    }

    @Override
    public int size() {
        int size = 0;

        for (Entry<Property, Map<Function, Collection<Object>>> property_entry : data.entrySet()) {
            for (Entry<Function, Collection<Object>> property_function_entry : property_entry.getValue().entrySet()) {

                int arguments = property_function_entry.getValue().size();

                if (arguments == 0) {
                    arguments = 1;
                }

                size += arguments;

            }
        }

        return size;
    }

    /**
     * Returns <tt>true</tt> if this set contains no rules.
     *
     * @return <tt>true</tt> if this set contains no rules
     */
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public boolean contains(Object object) {
        return contains((Rule) object);
    }

    public boolean contains(Rule rule) {
        return contains(rule.getProperty(), rule.getFunction(), rule.getValue());
    }

    public boolean contains(Property property) {
        return data.containsKey(property);
    }

    public boolean contains(Property property, Function function) {
        for (Map<Function, Collection<Object>> functions : exists(property).values()) {
            if (functions.keySet().contains(function)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Property property, Function function, Object object) {
        for (Map<Function, Collection<Object>> functions : exists(property).values()) {
            if (functions.getOrDefault(function, Collections.EMPTY_SET).contains(object)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(Rule rule) {
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

    public boolean remove(Rule rule) {
        Property property = rule.getProperty();
        Function function = rule.getFunction();
        Object value = rule.getValue();

        //
        Map<Function, Collection<Object>> functions = data.getOrDefault(property, Collections.EMPTY_MAP);

        //
        if (functions.isEmpty()) {
            return data.remove(property) != null;
        }

        Collection<Object> arguments = functions.getOrDefault(function, Collections.EMPTY_SET);

        //
        if (arguments.size() <= 1) {
            return functions.remove(function) != null;
        }

        return arguments.remove(value);
    }

    @Override
    public void clear() {
        data.clear();
    }

    public Map<Function, Collection<Object>> getFunctions(Property property) {
        Map<Function, Collection<Object>> map = new HashMap<>();

        for (Map<Function, Collection<Object>> functions : exists(property).values()) {
            for (Entry<Function, Collection<Object>> entry : functions.entrySet()) {

                //
                map.putIfAbsent(entry.getKey(), entry.getValue());
                map.get(entry.getKey()).addAll(entry.getValue());

            }
        }

        return map;
    }

    private Collection<Rule> values() {
        Collection<Rule> values = new HashSet<>();

        for (Entry<Property, Map<Function, Collection<Object>>> property_entry : data.entrySet()) {
            for (Entry<Function, Collection<Object>> property_function_entry : property_entry.getValue().entrySet()) {

                //
                if (property_function_entry.getValue().isEmpty()) {
                    values.add(new Rule(property_function_entry.getKey(), property_entry.getKey()));
                } else {
                    for (Object object : property_function_entry.getValue()) {
                        values.add(new Rule(property_function_entry.getKey(), property_entry.getKey(), object));
                    }
                }

            }
        }

        return values;
    }

    private Map<Property, Map<Function, Collection<Object>>> exists(Property property) {
        Map<Property, Map<Function, Collection<Object>>> map = data.find(property);

        if (map == null) {
            map = Collections.EMPTY_MAP;
        }

        return map;
    }

}
