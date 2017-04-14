/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Disjoint-set data structure to keep track of a set of equivalence class
 * partitioned into a number of disjoint subsets
 *
 * @param <T>
 *
 * @see <a href="http://algs4.cs.princeton.edu/15uf/">1.5 Case Study:
 * Union-Find</a>
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DisjointSet<T> {

    private final HashMap<T, T> map = new HashMap<>();

    public DisjointSet() {
    }

    /**
     * Search
     *
     * @param node
     * @return
     */
    public T representative(T node) {
        //
        return representative(node, new AbstractMap.SimpleEntry(map.get(node), 0)).getKey();
    }

    /**
     * Search
     *
     * @param node
     * @return
     */
    private AbstractMap.SimpleEntry<T, Integer> representative(T node, AbstractMap.SimpleEntry entry) {
        //        
        if (!node.equals(entry.getKey())) {
            //
            T parent = (T) entry.getKey();
            //
            Integer height = (Integer) entry.getValue();
            //
            entry = representative(parent, new AbstractMap.SimpleEntry(map.get(parent), height++));
            //
            map.put(node, (T) entry.getKey());
        }
        return entry;
    }

    /**
     * Find
     *
     * @param node
     * @return
     */
    public Collection<T> find(T node) {
        //
        T parent = representative(node);
        //
        Set<T> set = new HashSet<>();
        //
        for (Map.Entry<T, T> entry : map.entrySet()) {
            //
            T key = entry.getKey();
            //
            if (key.equals(parent)) {
                //
                set.add(key);
            }
        }
        return set;
    }

    /**
     * Union
     *
     * @param subject
     * @param object
     */
    public void union(T subject, T object) {
        AbstractMap.SimpleEntry<T, Integer> subjectParent = new AbstractMap.SimpleEntry(map.get(subject), 0);
        AbstractMap.SimpleEntry<T, Integer> objectParent = new AbstractMap.SimpleEntry(map.get(object), 0);
        if (map.putIfAbsent(subject, subject) != map.putIfAbsent(object, object)) {
            subjectParent = representative(subject, subjectParent);
            objectParent = representative(object, objectParent);
        }
        if (subjectParent.getKey().equals(objectParent.getKey())) {
            return;
        }
        if (subjectParent.getValue() < subjectParent.getValue()) {
            map.put(subjectParent.getKey(), objectParent.getKey());
        } else {
            map.put(objectParent.getKey(), subjectParent.getKey());
        }
    }

    /**
     *
     * @return a collection of the equivalence classes contained in this
     * disjoint set
     */
    public Collection<Collection<T>> disjointValues() {
        //
        Map<T, Collection<T>> disjointValues = new HashMap<>();
        //
        for (Map.Entry<T, T> entry : map.entrySet()) {
            T key = entry.getKey();
            T value = entry.getValue();
            disjointValues.putIfAbsent(value, new HashSet<>());
            disjointValues.get(value).add(key);
        }
        return disjointValues.values();
    }

    /**
     *
     * @return a collection of the elements contained in this disjoint set
     */
    public Collection<T> values() {
        return map.keySet();
    }

    /**
     * Removes all of the elements from this disjoint set. The disjoint set will
     * be empty after this call returns.
     */
    public void clear() {
        map.clear();
    }

}
