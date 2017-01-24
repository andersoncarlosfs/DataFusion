/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.di.DataFusion;
import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.util.Collection;
import javax.enterprise.context.RequestScoped;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@RequestScoped
public class DataFusionService {

    public void fuseData(DataSource... dataSources) {
    }

    public Collection<Collection<RDFNode>> getEquivalentClasses(DataSource... dataSources) throws IOException {
        DataFusion dataFusion = new DataFusion(dataSources);
        return dataFusion.getEquivalentClasses();
    }

}
