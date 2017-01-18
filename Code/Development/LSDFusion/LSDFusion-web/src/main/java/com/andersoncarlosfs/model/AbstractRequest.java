/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import java.util.Calendar;
import java.util.UUID;

/**
 *
 * @author AndersonCarlos
 */
public abstract class AbstractRequest implements Comparable<AbstractRequest>, Cloneable {

    private final UUID uuid = UUID.randomUUID();
    private final Calendar created = Calendar.getInstance();

    public AbstractRequest() {
    }

    /**
     * @return the uuid
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @return the created
     */
    public Calendar getCreated() {
        return created;
    }

    /**
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * @param o
     * @return
     */
    @Override
    public int compareTo(AbstractRequest o) {
        return created.compareTo(o.created);
    }

    /**
     *
     * @throws java.lang.CloneNotSupportedException
     * @see Object#clone()
     * @return
     */
    @Override
    public AbstractRequest clone() throws CloneNotSupportedException {
        return (AbstractRequest) super.clone();
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (!(getClass().isInstance(object))) {
            return false;
        }
        AbstractRequest other = (AbstractRequest) object;
        if ((uuid == null && other.uuid != null) || (uuid != null && !uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

    /**
     *
     * @see java.lang.Object#toString()
     * @return
     */
    @Override
    public String toString() {
        return getClass().getName() + "[UUID=" + uuid + "]";
    }

}
