package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.AbstractController;
import com.andersoncarlosfs.model.requests.DataFusionRequest;
import com.andersoncarlosfs.model.responses.DataFusionResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class DataFusionBean extends AbstractController<DataFusionRequest> {

    private final Collection<DataFusionResponse> dataFusionResponses = Collections.emptyList();

    public DataFusionBean() {
    }

    /**
     *
     * @return the selected
     */
    public DataFusionResponse getResponse() {
        for (DataFusionResponse dataFusionResponse : dataFusionResponses) {
            if (dataFusionResponse.getUUID().equals(getSelected().getUUID())) {
                return dataFusionResponse;
            }
        }
        return null;
    }

    @Override
    public DataFusionRequest prepareCreate() throws InstantiationException, IllegalAccessException {
        return new DataFusionRequest((Collection) Collections.emptyList());
    }

    /**
     *
     */
    @Override
    public void destroy() {
        for (Iterator<DataFusionResponse> iterator = dataFusionResponses.iterator(); iterator.hasNext();) {
            DataFusionResponse next = iterator.next();
            if (next.getUUID().equals(getSelected().getUUID())) {
                iterator.remove();
            }
        }
        super.destroy();
    }

    @Override
    protected Class<DataFusionRequest> getClasse() {
        return DataFusionRequest.class;
    }

    @FacesConverter(forClass = DataFusionBean.class)
    public class DataFusionConverter extends AbstractConverter {

    }

}
