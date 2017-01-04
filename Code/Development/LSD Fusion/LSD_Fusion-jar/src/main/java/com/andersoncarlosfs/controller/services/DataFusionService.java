/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.controller.services;

import com.andersoncarlosfs.model.entities.Instance;
import com.andersoncarlosfs.model.entities.Property;
import com.andersoncarlosfs.model.entities.Value;
import com.andersoncarlosfs.model.enums.Configuration;
import com.andersoncarlosfs.model.enums.Quality;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Anderson Carlos Ferreira da Silva
 */
@ApplicationScoped
public class DataFusionService {

    private static final Pattern pattern;

    static {
        pattern = Pattern.compile(Configuration.PunctuationMarks.value());
    }

    public void fuseData(HashMap<URI, Instance> instances, ArrayList<ArrayList<URI>> reconciled) {

    }

    // calculate all quality criteria for a value
    public void calculateScore(HashMap<URI, Instance> instances, ArrayList<ArrayList<URI>> reconciled, Float homogeneityThreshold, Float occurenceFrequencyThreshold) {

        for (URI name : instances.keySet()) {
            Instance instance = instances.get(name);
            ArrayList<URI> rec = new ArrayList<>();

            for (int k = 0; k < reconciled.size(); k++) {
                if (reconciled.get(k).contains(instance.getUri())) {
                    rec = reconciled.get(k);
                }
            }

            ArrayList<Instance> references = new ArrayList<>();
            for (int g = 0; g < rec.size(); g++) {
                references.add(instances.get(rec.get(g)));
            }

            for (int i = 0; i < instance.getProperties().size(); i++) {
                Property prop = instance.getProperties().get(i);
                ArrayList<Value> homog = new ArrayList<>();
                for (int a = 0; a < references.size(); a++) {
                    for (int b = 0; b < references.get(a).getProperties().size(); b++) {
                        if (!(references.get(a).getProperties().isEmpty())) {
                            if (references.get(a).getProperties().get(b).getName().equals(prop.getName())) {
                                homog.addAll(references.get(a).getProperties().get(b).getValues());
                            }
                        }
                    }
                }

                // discover implausible values and calculate quality score for
                // plausible values
                for (int j = 0; j < prop.getValues().size(); j++) {
                    Value value = prop.getValues().get(j);
                    String valueStr = value.getValue();

                    Integer frequency = 0;

                    // calculate homogeneity
                    for (int o = 0; o < homog.size(); o++) {
                        if (homog.get(o).getValue().equals(valueStr)) {
                            ++frequency;
                        }
                    }

                    Integer mpBonus = 0;
                    // calculate morePrecise
                    for (int o = 0; o < homog.size(); o++) {
                        boolean ret = isMorePrecise(value, (Value) homog.get(o));
                        if (ret == true) {
                            ++mpBonus;
                        }

                    }

                    Float homogeneity = ((float) frequency / homog.size());
                    value.setHomogeneity(homogeneity);

                    // calculate occurrence frequency
                    frequency = 0;
                    for (int o = 0; o < prop.getValues().size(); o++) {
                        if (prop.getValues().get(o).getValue().equals(valueStr)) {
                            ++frequency;
                        }
                    }
                    Float occurrenceFrequency = ((float) frequency / prop.getValues().size());
                    value.setOccurrenceFrequency(occurrenceFrequency);

                    if (/* violatesRules(value) || */(value.getHomogeneity() < homogeneityThreshold || value.getOccurrenceFrequency() < occurenceFrequencyThreshold)) {
                        value.setImplausible(true);
                    } else // apparemment inutile mais pourquoi ?
                    {
                        value.setImplausible(false);
                    }

                    if (!value.getImplausible()) { // si plausible
                        value.setQualityScore((value.getHomogeneity() + value.getOccurrenceFrequency() + value.getSource().getFreshness() + value.getSource().getReliability()) / 4);
                        value.setQualityValue(getQualityValue(value.getQualityScore()));
                    }

                }//Exploration of the set of values a given P.
            }//Exploration of the set of properties
        }

    }

    //define if a value is more precise than another
    private Boolean isMorePrecise(Value val1, Value val2) {

        String v11 = val1.getValue();
        String v21 = val2.getValue();
        String v1 = pattern.matcher(v11).replaceAll("");
        String v2 = pattern.matcher(v21).replaceAll("");

        //if ((v1.contains(v2)))// && (!v1.equals(v2)))
        if (!(v1.equals(v2)) && (v1.indexOf(v2) > -1)) {
            if (!(val1.getIsMorePreciseThanStr()).contains(v21)) //val1.addToMorePrecise(val2);
            {
                val1.getIsMorePreciseThanStr().add(v21);
            }
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }

    }

    private String getQualityValue(Float qualityScore) {
        if (qualityScore <= Quality.POOR.value()) {
            return Quality.POOR.name().toLowerCase();
        } else if (qualityScore <= Quality.AVERAGE.value()) {
            return Quality.AVERAGE.name().toLowerCase();
        } else {
            return Quality.EXCELLENT.name().toLowerCase();
        }
    }

}
