/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.entities;

import java.util.ArrayList;

/**
 *
 * @author Ioanna Giannopoulou and Anderson Carlos Ferreira da Silva
 */
public class Property {

    private String name;
    private ArrayList<Value> values;

    public Property() {
    }

    public Property(String name, ArrayList<Value> values) {
        this.name = name;
        this.values = values;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the values
     */
    public ArrayList<Value> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }

}
