package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.AbstractController;
import com.andersoncarlosfs.model.requests.DataExtractionRequest;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataExtractionBean extends AbstractController<DataExtractionRequest> {

    public DataExtractionBean() {
    }

    @Override
    protected Class<DataExtractionRequest> getClasse() {
        return DataExtractionRequest.class;
    }

    @FacesConverter(forClass = DataExtractionBean.class)
    public class DataExtractionConverter extends AbstractConverter {

    }

}
