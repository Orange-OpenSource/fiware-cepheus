/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.persistence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.model.cep.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Persistence implementation using a JSON file
 */
@Component
public class JsonPersistence implements Persistence {

    private static Logger logger = LoggerFactory.getLogger(JsonPersistence.class);

    @Value("${file.directory:/tmp/esper4fastdatajson}")
    private String ConfigurationFileDirectory;

    @Override
    public Boolean checkConfigurationDirectory() {

        if (this.ConfigurationFileDirectory == null) {
            logger.warn("Configuration File path is null ");
            return false;
        } else {
            File confFile = new File(this.ConfigurationFileDirectory);
            if (!confFile.exists()) {
                logger.warn("Configuration File {} doesn't exist", this.ConfigurationFileDirectory);
                return false;
            }
        }
        return true;
    }

    @Override
    public Configuration loadConfiguration() throws PersistenceException {

        logger.info("Load configuration from {}", this.ConfigurationFileDirectory );

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        try {
            mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            return mapper.readValue(new File(this.ConfigurationFileDirectory), Configuration.class );

        } catch (IOException e) {
            throw new PersistenceException("Failed to load configuration", e);
        }
    }

    @Override
    public void saveConfiguration(Configuration configuration) throws PersistenceException {
        logger.debug("SAVE Configuration in {}", this.ConfigurationFileDirectory );

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        try {
            File confFile = new File(this.ConfigurationFileDirectory);
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(confFile, configuration);
        } catch (IOException e) {
            throw new PersistenceException("Failed to save new configuration", e);
        }
    }
}
