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
import com.orange.ngsi.model.ContextElement;
import com.orange.ngsi.model.ContextElementMixIn;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * Configure Converter for serialization and deserialization json
 */
@Configuration
public class ConvertersConfiguration {

    @Bean
    public ObjectMapper jsonV1ObjectMapper(Jackson2ObjectMapperBuilder builder) {

        // Serialize numbers as strings
        builder.featuresToEnable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);

        // Serialize booleans as strings
        SimpleModule booleanAsString = new SimpleModule("BooleanAsString");
        booleanAsString.addSerializer(Boolean.class, new JsonSerializer<Boolean>() {
            @Override public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider)
                    throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());

            }
        });
        builder.modulesToInstall(booleanAsString);

        builder.mixIn(ContextElement.class, ContextElementMixIn.class);

        return builder.build();
    }

    @Bean
    @Resource(name = "jsonV1ObjectMapper")
    public MappingJackson2HttpMessageConverter jsonV1Converter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

}
