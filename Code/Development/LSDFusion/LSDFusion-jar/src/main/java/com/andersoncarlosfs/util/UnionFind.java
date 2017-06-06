/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.util.Collection;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 * @param <E>
 */
public interface UnionFind<E> {

    /**
     * Return the number of disjoint sets
     *
     * @return the number of disjoint sets
     */
    int count();

    /**
     *
     * @return a view of the values contained this set partitioned into disjoint
     * subsets
     */
    Collection<Collection<E>> disjointValues();

    /**
     * Find the disjoint subset of the specified element
     *
     * @param e the element the representative presence in this set is to be
     * found
     * @return the disjoint subset which contains the specified element, or
     * <tt>null</tt> if there was no disjoint subset for the specified element
     */
    Collection<E> find(E e);

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
    E representative(E e);

    /**
     * Union the specified elements
     *
     * @param subject
     * @param object
     */
    void union(E subject, E object);

}
