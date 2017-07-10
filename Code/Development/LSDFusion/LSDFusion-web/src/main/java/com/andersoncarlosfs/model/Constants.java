/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.apache.jena.riot.Lang;

/**
 *
 * @author anderson
 */
public class Constants  {
    
    public static final Collection<Lang> SYNTAXES = getSyntaxes();
    
    /**
     *
     * @return
     */
    private static final Collection<Lang> getSyntaxes() {

        Collection<Lang> syntaxes = new HashSet<>();

        for (Field field : Lang.class.getFields()) {
            if (field.getType().equals(Lang.class)) {
                try {
                    syntaxes.add((Lang) field.get(Lang.class));
                } catch (IllegalArgumentException exception) {
                    //Logger.getLogger(DataSourceBean.class.getName()).log(Level.SEVERE, null, exception);
                } catch (IllegalAccessException exception) {
                    //Logger.getLogger(DataSourceBean.class.getName()).log(Level.SEVERE, null, exception);
                }
            }
        }

        return Collections.unmodifiableCollection(syntaxes);

    }
    
}
