/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller;

import com.andersoncarlosfs.data.util.Function;
import static com.andersoncarlosfs.model.Constants.FUNCTIONS;
import static com.andersoncarlosfs.model.Constants.SYNTAXES;
import java.util.Collection;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.jena.riot.Lang;

/**
 *
 * @author anderson
 */
public class Converters {

    /**
     *
     * @param <T>
     */
    private static abstract class AbstractConverter<T> implements Converter {

        protected abstract Collection<T> getCollection();

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value != null) {
                for (T t : getCollection()) {
                    if (value.equals(String.valueOf(t))) {
                        return t;
                    }
                }
            }
            return null;
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            return String.valueOf(value);
        }

    }

    /**
     *
     */
    @FacesConverter(value = "syntaxConverter")
    public static class SyntaxConverter extends AbstractConverter<Lang> {

        @Override
        protected Collection<Lang> getCollection() {
            return SYNTAXES;
        }

    }

    /**
     *
     */
    @FacesConverter(value = "functionConverter")
    public static class FunctionConverter extends AbstractConverter<Function> {

        @Override
        protected Collection<Function> getCollection() {
            return FUNCTIONS;
        }

    }

}
