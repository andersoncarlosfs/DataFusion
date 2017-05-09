/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
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
public class DisjointSet_<T> extends AbstractSet<T> {

    private final HashMap<T, T> map = new HashMap<>();

    public DisjointSet_() {
    }

    /**
     * Adds the specified element to this set if it is not already present.
     *
     * @param node element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    @Override
    public boolean add(T node) {
        return map.putIfAbsent(node, node) == null;
    }
    
    /**
     * Removes the specified element from this set if it is present.
     *
     * @param node object to be removed from this set, if present
     * @return <tt>true</tt> if the set contained the specified element
     */
    @Override
    public boolean remove(Object node) {        
        if (map.remove(node)!= null) {
            //
            T parent = null;
            //
            for (Map.Entry<T, T> entry : map.entrySet()) {                
                if (entry.getValue() != node) {
                    continue;
                }
                if (parent == null) {                    
                    parent = entry.getValue();
                }
                entry.setValue(parent);
            }
            return true;
        }        
        return false;
    }        
    
    /**
     * 
     *
     * @param node
     * @return
     */
    public T representative(T node) {
        //
        return search(node).getKey();
    }  
    
    /**
     * Find 
     *
     * @param node
     * @return
     */
    public Collection<T> find(T node) {
        //
        T parent = search(node).getKey();
        //
        Set<T> set = new HashSet<>();
        //
        for (Map.Entry<T, T> entry : map.entrySet()) {
            //
            if (entry.getValue().equals(parent)) {
                //
                set.add(entry.getKey());
            }
        }
        return set;
    }

    /**
     * Union the specified elements
     *
     * @param subject
     * @param object
     */
    public void union(T subject, T object) {
        //   
        union(search(subject), search(object));
    }
    
    /**
     * Union the specified elements
     *
     * @param subject
     * @param object
     */
    public void unionIfAbsent(T subject, T object) {
        //
        AbstractMap.SimpleEntry<T, Integer> subjectParent = new AbstractMap.SimpleEntry(subject, 0);
        AbstractMap.SimpleEntry<T, Integer> objectParent = new AbstractMap.SimpleEntry(object, 0);
        //
        if (map.putIfAbsent(subject, subject) != map.putIfAbsent(object, object)) {
            //
            subjectParent = search(subject, subjectParent);
            objectParent = search(object, objectParent);
        }
        // 
        union(subjectParent, objectParent);
    }
    
    /**
     *
     * @return a collection of the equivalence classes contained in this disjoint set
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
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality)
     */
    public int size() {
        return map.size();
    }  
    
    /**     
     *
     * @return the number of disjoint sets
     */
    public int count() {
        //
        int representatives = 0;
        //
        for (Map.Entry<T, T> entry : map.entrySet()) {
            if (entry.getValue().equals(entry.getKey())) {
                representatives++;
            }
        }
        return representatives;
    }   
    
    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Removes all of the elements from this disjoint set. 
     * The disjoint set will be empty after this call returns.
     */
    public void clear() {
        map.clear();
    }    

    /**
     * Search the specified element
     *
     * @param node
     * @return
     */
    private AbstractMap.SimpleEntry<T, Integer> search(T node) {
        //
        return search(node, new AbstractMap.SimpleEntry(map.get(node), 0));
    }

    /**
     * Search the specified element
     *
     * @param node
     * @return
     */
    private AbstractMap.SimpleEntry<T, Integer> search(T node, AbstractMap.SimpleEntry entry) {
        //
        T parent = (T) entry.getKey();
        //        
        if ((node != null) && (!node.equals(parent))) {
            //
            Integer height = (Integer) entry.getValue();
            //
            entry = search(parent, new AbstractMap.SimpleEntry(map.get(parent), height++));
            //
            map.put(node, (T) entry.getKey());
        }
        return entry;
    }

    /**
     * Union the specified elements
     *
     * @param subject
     * @param object
     */
    private void union(AbstractMap.SimpleEntry<T, Integer> subject, AbstractMap.SimpleEntry<T, Integer> object) {
        if ((subject.getKey()) == null || (object.getKey() == null) || subject.getKey().equals(object.getKey())) {
            return;
        }
        //
        if (subject.getValue() < object.getValue()) {
            map.put(subject.getKey(), object.getKey());
        } else {
            map.put(object.getKey(), subject.getKey());
        }
    }
   
}
