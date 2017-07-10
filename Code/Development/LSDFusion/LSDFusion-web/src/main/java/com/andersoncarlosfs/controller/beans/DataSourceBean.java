/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.DataSource;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataSourceBean {

    private DataSource dataSource;

    public DataSourceBean() {
    }

    /**
     *
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     *
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     *
     * @return
     */
    public String reset() {

        dataSource = new DataSource();

        return "edit";

    }

}
