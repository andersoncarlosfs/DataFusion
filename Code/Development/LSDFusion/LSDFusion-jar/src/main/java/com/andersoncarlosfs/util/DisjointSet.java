/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

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

    private final HashMap<T, Collection<T>> map = new HashMap<>();

    public DisjointSet() {
    }

    /**
     * Search
     *
     * @param node
     * @return
     */
    public T representative(T node) {
        Collection nodeCollection = map.get(node);
        if (nodeCollection.size() > 1) {
            return node;
        }
        T parent = (T) nodeCollection.iterator().next();
        if (parent.equals(node)) {
            return parent;
        }
        return representative(parent);
    }

    /**
     * Find
     *
     * @param node
     * @return
     */
    public Collection<T> find(T node) {
        return map.getOrDefault(find(node), null);
    }

    /**
     * Union
     *
     * @param subject
     * @param object
     */
    public void union(T subject, T object) {
        T subjectParent = subject;
        T objectParent = object;
        if (map.putIfAbsent(subject, new HashSet<>(Arrays.asList(subject))) != map.putIfAbsent(object, new HashSet<>(Arrays.asList(object)))) {
            subjectParent = representative(subject);
            objectParent = representative(object);
        }
        if (subjectParent.equals(objectParent)) {
            return;
        }
        Collection subjectCollection = map.get(subjectParent);
        Collection objectCollection = map.get(objectParent);
        if (subjectCollection.size() < objectCollection.size()) {
            objectCollection.addAll(subjectCollection);
            map.put(subjectParent, new HashSet<>(Arrays.asList(objectParent)));
        } else {
            subjectCollection.addAll(objectCollection);
            map.put(objectParent, new HashSet<>(Arrays.asList(subjectParent)));
        }
    }

    /**
     *
     * @return a collection of the equivalence classes contained in this
     * disjoint set
     */
    public Collection<Collection<T>> disjointValues() {
        return map.values().parallelStream().filter((Collection<T> c) -> c.size() > 1).collect(Collectors.toList());
    }

    /**
     *
     * @return a collection of the elements contained in this disjoint set
     */
    public Collection<T> values() {
        return map.keySet();
    }

}
