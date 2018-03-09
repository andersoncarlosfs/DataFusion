/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.data.util.Function;
import com.andersoncarlosfs.model.Rule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class RuleBean {

    private Rule rule;
    private UploadedFile file;
    
    public RuleBean() {
    }

    /**
     * @return the rule
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * @param rule the rule to set
     */
    public void setRule(Rule rule) {
        this.rule = rule;
    }
    
    /**
     *
     * @return
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     *
     * @param file
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     *
     * @return
     */
    public String skip() {

        rule = new Rule();

        return "edit?faces-redirect=true";

    }
    
    /**
     *
     * @return
     */
    public String reset() {

        rule = null;
        
        file = null;

        return "edit?faces-redirect=true";

    }
    
    /**
     *
     * @throws java.lang.Exception
     */
    public void upload() throws Exception {

        if (file.getSize() > 0) {
            
            rule = new Rule();

            Path path = Files.createTempDirectory(null).resolve(file.getFileName());

            Files.copy(file.getInputstream(), path, StandardCopyOption.REPLACE_EXISTING);

            rule.setPath(path);
            
            rule.setFunctions(Arrays.asList(Function.EXTRA_KNOWLEDGE));

        } else {

            file = null;

        }

    }

}
