/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.util.Collection;
import java.util.function.Predicate;

/**
 *
 * @param <E> the type of elements maintained by this set
 *
 * @see <a href="http://algs4.cs.princeton.edu/13stacks/">1.3 Bags, Queues, and
 * Stacks</a>
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public interface Bag<E> extends Collection<E> {

    @Override
    default public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    default public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Override
    default public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("removeIf");
    }

    @Override
    public default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    default public void clear() {
        throw new UnsupportedOperationException("clear");
    }

}
