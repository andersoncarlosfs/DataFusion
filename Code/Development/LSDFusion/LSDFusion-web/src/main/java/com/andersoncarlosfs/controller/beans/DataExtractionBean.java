package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.AbstractController;
import com.andersoncarlosfs.model.requests.DataExtractionRequest;
import com.andersoncarlosfs.model.responses.DataExtractionResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataExtractionBean extends AbstractController<DataExtractionRequest> {

    private final Collection<DataExtractionResponse> dataExtractionResponses = Collections.emptyList();

    public DataExtractionBean() {
    }

    /**
     *
     * @return the selected
     */
    public DataExtractionResponse getResponse() {
        for (DataExtractionResponse dataExtractionResponse : dataExtractionResponses) {
            if (dataExtractionResponse.getUUID().equals(getSelected().getUUID())) {
                return dataExtractionResponse;
            }
        }
        return null;
    }

    @Override
    public DataExtractionRequest prepareCreate() throws InstantiationException, IllegalAccessException {
        return new DataExtractionRequest((Collection) Collections.emptyList());
    }

    /**
     *
     */
    @Override
    public void destroy() {
        for (Iterator<DataExtractionResponse> iterator = dataExtractionResponses.iterator(); iterator.hasNext();) {
            DataExtractionResponse next = iterator.next();
            if (next.getUUID().equals(getSelected().getUUID())) {
                iterator.remove();
            }
        }
        super.destroy();
    }

    @Override
    protected Class<DataExtractionRequest> getClasse() {
        return DataExtractionRequest.class;
    }

    @FacesConverter(forClass = DataExtractionBean.class)
    public class DataExtractionConverter extends AbstractConverter {

    }

}
