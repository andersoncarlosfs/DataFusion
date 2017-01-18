/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.requests;

import com.andersoncarlosfs.model.AbstractRequest;
import java.io.File;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author AndersonCarlos
 */
@RequestScoped
public class DataExtractionRequest extends AbstractRequest {

    private File files;

    public DataExtractionRequest() {
    }

}
