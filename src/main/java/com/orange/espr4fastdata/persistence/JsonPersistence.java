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
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;

/**
 * Created by pborscia on 30/06/2015.
 */
@ComponentScan
public class JsonPersistence implements Persistence {

    private static Logger logger = LoggerFactory.getLogger(JsonPersistence.class);

    @Value("${file.directory:/tmp/esper4fastdatajson}")
    private String ConfigurationFileDirectory;

    public void JsonPersistence() {

    }

    /**
     * Load persited configuration to the CEP
     * @throws PersistenceException when the configuration could not be loaded successfully
     */
    public Configuration loadConfiguration() throws PersistenceException {

        File confFile = null;

        logger.debug("Load Configuration from {}", this.ConfigurationFileDirectory );

        if (this.ConfigurationFileDirectory == null) {
            logger.warn("Configuration File path is null ");
            return null;
        } else {
            confFile = new File(this.ConfigurationFileDirectory);
            if (!confFile.exists()) {
                logger.warn("Configuration File {} doesn't exist", this.ConfigurationFileDirectory);
                return null;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        try {
            mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            return mapper.readValue(confFile, Configuration.class );

        } catch (IOException e) {
            throw new PersistenceException("Failed to load configuration", e);
        }
    }

    /**
     * Save configuration
     * @throws PersistenceException when the configuration could not be saved successfully
     */
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
