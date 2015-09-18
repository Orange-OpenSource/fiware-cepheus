package com.orange.ngsi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.orange.ngsi.model.ContextAttribute;
import com.orange.ngsi.model.ContextElement;
import com.orange.ngsi.model.ContextElementMixIn;
import com.orange.ngsi.model.EntityId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.List;

/**
 * Configure Converter for serialization and deserialization json
 */
@Configuration
public class ConvertersConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {

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
    @Primary
    public MappingJackson2HttpMessageConverter jsonConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

}
