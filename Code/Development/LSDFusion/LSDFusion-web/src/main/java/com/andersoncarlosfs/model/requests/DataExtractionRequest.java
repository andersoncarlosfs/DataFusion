/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.requests;

import com.andersoncarlosfs.annotations.scopes.RequestScope;
import com.andersoncarlosfs.model.AbstractRequest;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author AndersonCarlos
 */
@RequestScope
public class DataExtractionRequest extends AbstractRequest {

    private final Collection<File> files;

    public DataExtractionRequest(Collection<File> files) {
        this.files = files;
    }

    public DataExtractionRequest(File... files) {
        this.files = Arrays.asList(files);
    }

    /**
     * @return the files
     */
    public Collection<File> getFiles() {
        return Collections.unmodifiableCollection(files);
    }

}
