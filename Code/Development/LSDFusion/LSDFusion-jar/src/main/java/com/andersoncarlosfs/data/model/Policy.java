/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.data.model;

import com.andersoncarlosfs.util.DisjointSet;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author andersoncarlosfs
 */
public class Policy extends AbstractSet<Rule> implements Set<Rule>, Cloneable, Serializable {

    private transient DisjointSet<Rule> data;

    public Policy() {
        data = new DisjointSet<>();
    }

    public Policy(Collection<Rule> rules) {
        data = new DisjointSet<>(rules);
    }

    @Override
    public Iterator<Rule> iterator() {
        return data.iterator();
    }

    @Override
    public int size() {
        return data.size();
    }

}
