/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.util;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class DisjointMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {

    static final long serialVersionUID = 1L;

    private transient HashMap<K, Entry<V, K>> data;

    /**
     * Constructs a new empty map.
     */
    public DisjointMap() {
        data = new HashMap<>();
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public V put(K key, V value) {
        Entry<V, K> entry = data.get(key);
        //
        if (entry != null) {
            data.put(key, new SimpleEntry<>(value, entry.getValue()));
        }
        //
        data.put(key, (entry = new SimpleEntry<>(value, key)));
        //
        return entry.getKey();
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    public V get(Object key) {
        Entry<V, K> entry = data.get(key);
        if (entry == null) {
            return null;
        }
        return data.get(key).getKey();
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    public V remove(Object key) {
        Entry<V, K> value = data.remove(key);
        //
        if (value == null) {
            return null;
        }
        //
        K representative = null;
        //
        for (Entry<K, Entry<V, K>> entry : data.entrySet()) {
            K k = entry.getKey();
            Entry<V, K> e = entry.getValue();
            //
            if (e.getValue() != key) {
                continue;
            }
            if (representative == null) {
                representative = k;
            }
            e.setValue(k);
        }
        return value.getKey();
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public K representative(K key) {
        return search(key).getKey();
    }

    /**
     *
     * @param key
     * @return
     */
    public Map<K, V> find(K key) {
        K representative = representative(key);
        //
        Map<K, V> map = new HashMap<>();
        //
        for (Entry<K, Entry<V, K>> entry : data.entrySet()) {
            K k = entry.getKey();
            Entry<V, K> e = entry.getValue();
            //
            if (e.getValue().equals(representative)) {
                //
                map.put(k, e.getKey());
            }
        }
        return map;
    }

    /**
     * Union the specified elements
     *
     * @param subject
     * @param object
     */
    public void union(K subject, K object) {
        union(search(subject), search(object));
    }

    /**
     *
     * @return a view of the values contained in this map partitioned into
     * disjoint maps
     */
    public Collection<Map<K, V>> disjointValues() {
        Map<K, Map<K, V>> disjointValues = new HashMap<>();
        //
        for (Entry<K, Entry<V, K>> entry : data.entrySet()) {
            K key = entry.getKey();
            Entry<V, K> value = entry.getValue();
            K representative = representative(value.getValue());
            disjointValues.putIfAbsent(representative, new HashMap<>());
            disjointValues.get(representative).put(key, value.getKey());
        }
        return disjointValues.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Map<K, V> entries = new HashMap<>();
        //
        for (Entry<K, Entry<V, K>> entry : data.entrySet()) {
            K key = entry.getKey();
            Entry<V, K> value = entry.getValue();
            entries.put(key, value.getKey());
        }
        return entries.entrySet();
    }

    /**
     * Returns the number of elements in this map (its cardinality).
     *
     * @return the number of elements in this map (its cardinality)
     */
    @Override
    public int size() {
        return data.size();
    }

    /**
     * Return the number of disjoint maps
     *
     * @return the number of disjoint maps
     */
    public int count() {
        int representatives = 0;
        //
        for (Entry<K, Entry<V, K>> entry : data.entrySet()) {
            //
            if (entry.getValue().getKey().equals(entry.getKey())) {
                representatives++;
            }
        }
        return representatives;
    }

    /**
     * Returns <tt>true</tt> if this map contains no elements.
     *
     * @return <tt>true</tt> if this map contains no elements
     */
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Removes all of the elements from this map. The map will be empty after
     * this call returns.
     */
    @Override
    public void clear() {
        data.clear();
    }

    /**
     * Search the specified element
     *
     * @param e the element whose presence in this map is to be found
     * @return
     */
    private Entry<K, Integer> search(K k) {
        Entry<V, K> entry = data.get(k);
        if (entry == null) {
            return new SimpleEntry(null, 0);
        }
        return search(k, new SimpleEntry(entry.getValue(), 0));
    }

    /**
     * Search the specified element
     *
     * @param e the element whose presence in this map is to be found
     * @return
     */
    private Entry<K, Integer> search(K k, Entry<K, Integer> entry) {
        K representative = entry.getKey();
        //        
        if ((k != null) && (!k.equals(representative))) {
            //
            Integer height = entry.getValue();
            //
            entry = search(representative, new SimpleEntry(data.get(representative).getValue(), height++));
            //
            data.get(k).setValue(entry.getKey());
        }
        return entry;
    }

    /**
     * Union of the specified elements
     *
     * @param subject
     * @param object
     */
    private void union(Entry<K, Integer> subject, Entry<K, Integer> object) {
        if ((subject.getKey()) == null || (object.getKey() == null) || subject.getKey().equals(object.getKey())) {
            return;
        }
        //
        if (subject.getValue() < object.getValue()) {
            data.get(subject.getKey()).setValue(object.getKey());
        } else {
            data.get(object.getKey()).setValue(subject.getKey());
        }
    }

}
