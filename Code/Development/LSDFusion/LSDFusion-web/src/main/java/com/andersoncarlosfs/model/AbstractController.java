/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import com.andersoncarlosfs.model.enums.Action;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Abstract class to manipulate Beans
 *
 * @author Anderson Carlos Ferreira da Silva
 * @param <T> the request type
 */
public abstract class AbstractController<T extends AbstractRequest> implements Serializable {

    private T selected;
    private final Collection<T> items = new HashSet<>();

    /**
     *
     * @return
     */
    protected abstract Class<? extends T> getClasse();

    /**
     *
     * @return the selected
     */
    public T getSelected() {
        return selected;
    }

    /**
     *
     * @param selected the selected to set
     */
    public void setSelected(T selected) {
        this.selected = selected;
    }

    /**
     *
     * @return the items
     */
    public Collection<T> getItems() {
        return items;
    }

    /**
     *
     * @return a newly allocated instance of the extended AbstractRequest class.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public T prepareCreate() throws InstantiationException, IllegalAccessException {
        return getClasse().newInstance();
    }

    public void create() {
        manageRequest(Action.CREATE);
    }

    public void destroy() {
        manageRequest(Action.DELETE);
    }

    private void manageRequest(Action action) {

        switch (action) {
            case CREATE:
                break;
            case DELETE:
                items.remove(selected);
                break;
            default:
                break;
        }

        selected = null;

    }

    /**
     *
     * @param <T>
     */
    public abstract class AbstractConverter implements Converter {

        /**
         *
         * @param facesContext
         * @param component
         * @param value
         * @return
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            for (T object : getItems()) {
                if (object.getUUID().toString().equals(value)) {
                    return object;
                }
            }
            return null;
        }

        /**
         *
         * @param facesContext
         * @param component
         * @param object
         * @return
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (getClasse().isInstance(object)) {
                return ((T) object).getUUID().toString();
            }
            return null;
        }

    }

}
