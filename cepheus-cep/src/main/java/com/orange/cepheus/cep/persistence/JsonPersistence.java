/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.persistence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.cepheus.cep.model.Configuration;
import org.omg.CosNaming.NamingContextExtPackage.URLStringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Persistence implementation using JSON files in a single folder
 */
@Component
public class JsonPersistence implements Persistence {

    private static Logger logger = LoggerFactory.getLogger(JsonPersistence.class);

    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    private final static String PREFIX = "cep-";
    private final static String EXT = ".json";

    @Value("${data.path}")
    private String dataPath;

    public boolean configurationExists(String id) {
        if (dataPath == null || dataPath.isEmpty()) {
            logger.error("data.path is undefined");
            return false;
        }
        return new File(this.dataPath+idToFilename(id)).exists();
    }

    public Configuration loadConfiguration(String id) throws PersistenceException {
        String filename = idToFilename(id);
        logger.info("Load configuration from {}", this.dataPath+filename);

        try {
            return mapper.readValue(new File(this.dataPath+filename), Configuration.class);
        } catch (IOException e) {
            throw new PersistenceException("Failed to load configuration", e);
        }
    }

    public void saveConfiguration(String id, Configuration configuration) throws PersistenceException {
        String filename = idToFilename(id);
        logger.info("Save configuration in {}", this.dataPath+filename);

        try {
            File confFile = new File(this.dataPath+filename);
            mapper.writer().writeValue(confFile, configuration);
        } catch (IOException e) {
            throw new PersistenceException("Failed to save new configuration", e);
        }
    }

    public Collection<String> listConfigurations() {
        LinkedList<String> configurationIds = new LinkedList<>();

        File[] configurationFiles = new File(this.dataPath).listFiles((dir, name) -> name.startsWith(PREFIX) && name.endsWith(EXT));
        if (configurationFiles != null) {
            for (File file : configurationFiles) {
                configurationIds.add(filenameToId(file.getName()));
            }
        }

        return configurationIds;
    }

    /**
     * Prepare the id to be compatible to a filename
     */
    private String idToFilename(String id) {
        return PREFIX + id.replace('/', '-') + EXT;
    }

    /**
     * Convert the filename back to the initial id
     */
    private String filenameToId(String filename) {
        return filename.substring(PREFIX.length(), filename.length() - EXT.length()).replace('-', '/');
    }
}
