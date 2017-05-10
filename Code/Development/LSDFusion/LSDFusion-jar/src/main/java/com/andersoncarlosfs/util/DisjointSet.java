/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the <tt>Set</tt> interface, backed by a
 * <tt>HashMap</tt> instance.
 *
 * This data structure keeps track of a set of elements partitioned into a
 * number of disjoint subsets.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @see <a href="http://algs4.cs.princeton.edu/15uf/">1.5 Case Study:
 * Union-Find</a>
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DisjointSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {

    static final long serialVersionUID = 1L;

    private transient HashMap<E, E> map;

    /**
     * Constructs a new empty set.
     */
    public DisjointSet() {
        map = new HashMap<>();
    }

    /**
     * Adds the specified element to this set if it is not already present.
     *
     * @param e element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    @Override
    public boolean add(E e) {
        return map.putIfAbsent(e, e) == null;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param o object to be removed from this set, if present
     * @return <tt>true</tt> if the set contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        if (map.remove(o) != null) {
            //
            E parent = null;
            //
            for (Map.Entry<E, E> entry : map.entrySet()) {
                if (entry.getValue() != o) {
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
     * Returns the representative of the disjoint subset which contains the
     * specified element.
     *
     * @param e the element the representative presence in this set is to be
     * found
     * @return the representative of the disjoint subset which contains the
     * specified element, or <tt>null</tt> if there was no representative for
     * the specified element
     */
    public E representative(E e) {
        //
        return search(e).getKey();
    }

    /**
     * Find the disjoint subset of the specified element
     *
     * @param e the element the representative presence in this set is to be
     * found
     * @return the disjoint subset which contains the specified element, or
     * <tt>null</tt> if there was no disjoint subset for the specified element
     */
    public Collection<E> find(E e) {
        //
        E parent = search(e).getKey();
        //
        Set<E> set = new HashSet<>();
        //
        for (Map.Entry<E, E> entry : map.entrySet()) {
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
    public void union(E subject, E object) {
        //   
        union(search(subject), search(object));
    }

    /**
     * Union the specified elements
     *
     * @param subject
     * @param object
     */
    public void unionIfAbsent(E subject, E object) {
        //
        AbstractMap.SimpleEntry<E, Integer> subjectParent = new AbstractMap.SimpleEntry(subject, 0);
        AbstractMap.SimpleEntry<E, Integer> objectParent = new AbstractMap.SimpleEntry(object, 0);
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
     * @return a view of the values contained this set partitioned into disjoint
     * subsets
     */
    public Collection<Collection<E>> disjointValues() {
        //
        Map<E, Collection<E>> disjointValues = new HashMap<>();
        //
        for (Map.Entry<E, E> entry : map.entrySet()) {
            E key = entry.getKey();
            E value = entry.getValue();
            disjointValues.putIfAbsent(value, new HashSet<>());
            disjointValues.get(value).add(key);
        }
        return disjointValues.values();
    }

    /**
     *
     * @return a collection of the elements contained in this set
     */
    public Collection<E> values() {
        return map.keySet();
    }

    /**
     * Returns an iterator over the elements in this set. The elements are
     * returned in no particular order.
     *
     * @return an Iterator over the elements in this set
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality)
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * Return the number of disjoint sets
     *
     * @return the number of disjoint sets
     */
    public int count() {
        //
        int representatives = 0;
        //
        for (Map.Entry<E, E> entry : map.entrySet()) {
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
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Removes all of the elements from this set. The set will be empty after
     * this call returns.
     */
    @Override
    public void clear() {
        map.clear();
    }

    /**
     * Search the specified element
     *
     * @param e the element the representative presence in this set is to be
     * found
     * @return
     */
    private AbstractMap.SimpleEntry<E, Integer> search(E e) {
        //
        return search(e, new AbstractMap.SimpleEntry(map.get(e), 0));
    }

    /**
     * Search the specified element
     *
     * @param e the element the representative presence in this set is to be
     * found
     * @return
     */
    private AbstractMap.SimpleEntry<E, Integer> search(E e, AbstractMap.SimpleEntry entry) {
        //
        E parent = (E) entry.getKey();
        //        
        if ((e != null) && (!e.equals(parent))) {
            //
            Integer height = (Integer) entry.getValue();
            //
            entry = search(parent, new AbstractMap.SimpleEntry(map.get(parent), height++));
            //
            map.put(e, (E) entry.getKey());
        }
        return entry;
    }

    /**
     * Union the specified elements
     *
     * @param subject
     * @param object
     */
    private void union(AbstractMap.SimpleEntry<E, Integer> subject, AbstractMap.SimpleEntry<E, Integer> object) {
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
