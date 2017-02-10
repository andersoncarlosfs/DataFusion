/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.base.block.FileMode;
import org.apache.jena.tdb.sys.SystemTDB;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 * 4,726s
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class DataIntegration implements AutoCloseable {

    public static final Collection<Property> EQUIVALENCE_PROPERTIES = Arrays.asList(OWL.sameAs, SKOS.exactMatch);

    static {

        // Apache Jena - TDB
        SystemTDB.setFileMode(FileMode.direct);

    }

    /**
     *
     * @return the temporaryDirectory
     */
    protected abstract Path getTemporaryDirectory();

    /**
     *
     * @see AutoCloseable#close()
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

        //
        TDB.closedown();
        // Apache Commons IO
        FileUtils.forceDelete(getTemporaryDirectory().toFile());

    }

    /**
     * @see Object#finalize()
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

}
