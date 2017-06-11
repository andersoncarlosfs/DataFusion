/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
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

    private transient DisjointMap<E, Object> data;

    /**
     * Constructs a new empty set.
     */
    public DisjointSet() {
        data = new DisjointMap<>();
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
        return data.putIfAbsent(e, new Object()) == null;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param o object to be removed from this set, if present
     * @return <tt>true</tt> if the set contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        return data.remove(o) != null;
    }

    /**
     * Returns the representative of the disjoint subset which contains the
     * specified element.
     *
     * @param e the element whose representative presence in this set is to be
     * found
     * @return the representative of the disjoint subset which contains the
     * specified element, or <tt>null</tt> if there was no representative for
     * the specified element
     */
    public E representative(E e) {
        return data.representative(e);
    }

    /**
     * Find the disjoint subset of the specified element
     *
     * @param e the element of the subset in this set that is to be found
     * @return the disjoint subset which contains the specified element, or
     * <tt>null</tt> if there was no disjoint subset for the specified element
     */
    public Collection<E> find(E e) {
        return data.find(e).keySet();
    }

    /**
     * Union of the specified elements
     *
     * @param subject
     * @param object
     */
    public void union(E subject, E object) {
        data.union(subject, object);
    }

    /**
     * Union of the specified elements
     *
     * @param subject
     * @param object
     */
    public void unionIfAbsent(E subject, E object) {
        //
        add(subject);
        add(object);
        // 
        union(subject, object);
    }

    /**
     *
     * @return a view of the values contained in this set partitioned into
     * disjoint subsets
     */
    public Collection<Collection<E>> disjointValues() {
        Collection<Collection<E>> disjointValues = new HashSet<>();
        //
        for (Map<E, Object> map : data.disjointValues()) {
            disjointValues.add(map.keySet());
        }
        return disjointValues;
    }

    /**
     * Returns an iterator over the elements in this set. The elements are
     * returned in no particular order.
     *
     * @return an Iterator over the elements in this set
     */
    @Override
    public Iterator<E> iterator() {
        return data.keySet().iterator();
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality)
     */
    @Override
    public int size() {
        return data.size();
    }

    /**
     * Return the number of disjoint sets
     *
     * @return the number of disjoint sets
     */
    public int count() {
        return data.count();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements
     */
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Removes all of the elements from this set. The set will be empty after
     * this call returns.
     */
    @Override
    public void clear() {
        data.clear();
    }

}
