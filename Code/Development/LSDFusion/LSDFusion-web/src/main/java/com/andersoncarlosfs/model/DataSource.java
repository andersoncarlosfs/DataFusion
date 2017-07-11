/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import java.nio.file.Path;
import org.apache.jena.riot.Lang;

/**
 *
 * @author anderson
 */
public class DataSource extends com.andersoncarlosfs.data.model.DataSource {

    private final Float PERCENT = 100.0F;
    
    public DataSource() {
        super(null, Lang.RDFXML, null, 0.5F);
    }
    
    @Override
    public void setPath(Path path) {
        super.setPath(path); 
    }

    @Override
    public void setSyntax(Lang syntax) {
        super.setSyntax(syntax); 
    }

    @Override
    public void setFreshness(Long freshness) {
        super.setFreshness(freshness); 
    }

    @Override
    public Float getReliability() {
        return (super.getReliability() * PERCENT);
    }
    
    @Override
    public void setReliability(Float reliability) {
        super.setReliability(reliability / PERCENT); 
    }

}
