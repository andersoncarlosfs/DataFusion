/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.responses;

import com.andersoncarlosfs.annotations.scopes.RequestScope;
import com.andersoncarlosfs.model.AbstractResponse;
import com.andersoncarlosfs.model.enums.Status;
import com.andersoncarlosfs.model.requests.DataFusionRequest;

/**
 *
 * @author AndersonCarlos
 */
@RequestScope
public class DataFusionResponse extends AbstractResponse {

    public DataFusionResponse(DataFusionRequest dataFusionRequest) {
        super(dataFusionRequest.getUUID());
    }

    public DataFusionResponse(DataFusionRequest dataFusionRequest, Status status) {
        super(dataFusionRequest.getUUID(), status);
    }

}
