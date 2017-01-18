/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.responses;

import com.andersoncarlosfs.annotations.scopes.RequestScope;
import com.andersoncarlosfs.model.AbstractResponse;
import com.andersoncarlosfs.model.enums.Status;
import com.andersoncarlosfs.model.requests.DataExtractionRequest;

/**
 *
 * @author AndersonCarlos
 */
@RequestScope
public class DataExtractionResponse extends AbstractResponse {

    public DataExtractionResponse(DataExtractionRequest dataExtractionRequest) {
        super(dataExtractionRequest.getUUID());
    }

    public DataExtractionResponse(DataExtractionRequest dataExtractionRequest, Status status) {
        super(dataExtractionRequest.getUUID(), status);
    }

}
