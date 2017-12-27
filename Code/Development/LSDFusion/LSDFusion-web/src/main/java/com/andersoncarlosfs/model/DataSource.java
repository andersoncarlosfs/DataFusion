/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import java.nio.file.Path;
import java.util.Date;
import org.apache.jena.riot.Lang;

/**
 *
 * @author anderson
 */
public class DataSource extends com.andersoncarlosfs.data.model.DataSource {

    public DataSource() {
        super(null, null, null, 0.5F);
    }

    public DataSource(Path path, Lang syntax) {
        super(path, syntax);
    }

    public DataSource(Path path, Lang syntax, Long freshness, Float reliability) {
        super(path, syntax, freshness, reliability);
    }

    @Override
    public void setPath(Path path) {
        super.setPath(path);
    }

    @Override
    public void setSyntax(Lang syntax) {
        super.setSyntax(syntax);
    }

    public Date getDate() {
        if (super.getFreshness() == null) {
            return null;
        }
        return new Date(super.getFreshness());
    }

    public void setDate(Date freshness) {
        if (freshness == null) {
            super.setFreshness(null);
        } else {
            super.setFreshness(freshness.getTime());
        }
    }

}
