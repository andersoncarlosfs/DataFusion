/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.enums;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public enum Quality {

    POOR(0.34),
    AVERAGE(0.67),
    EXCELLENT(1.0);

    private final Double value;

    private Quality(Double value) {
        this.value = value;
    }

    /**
     *
     * @return the value
     */
    public Double value() {
        return value;
    }

    /**
     *
     * @see Object#toString()
     * @return
     */
    @Override
    public String toString() {
        return getClass().getName() + "[" + name() + "=" + value + "]";
    }

}
