/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.orange.ngsi.model.AppendContextElementResponse;
import com.orange.ngsi.model.ContextElement;
import com.orange.ngsi.model.EntityIdMixIn;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;


/**
 * Configure Converter for serialization and deserialization json
 */
@Configuration
public class ConvertersConfiguration {

    @Bean
    public MappingJackson2HttpMessageConverter jsonV1Converter(ObjectMapper objectMapper) {

        // Serialize numbers as strings
        objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);

        // Serialize booleans as strings
        SimpleModule booleanAsString = new SimpleModule("BooleanAsString");
        booleanAsString.addSerializer(Boolean.class, new JsonSerializer<Boolean>() {
            @Override public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider)
                    throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());

            }
        });
        objectMapper.registerModule(booleanAsString);

        objectMapper.addMixIn(ContextElement.class, EntityIdMixIn.class);
        objectMapper.addMixIn(AppendContextElementResponse.class, EntityIdMixIn.class);

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }



}
