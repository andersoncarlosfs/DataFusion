/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.beans;

import com.andersoncarlosfs.annotations.scopes.ApplicationScope;
import com.andersoncarlosfs.model.Rule;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScope
public class RuleBean {

    private Rule rule;
    
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
    public String reset() {

        rule = new Rule();

        return "edit?faces-redirect=true";

    }

}
