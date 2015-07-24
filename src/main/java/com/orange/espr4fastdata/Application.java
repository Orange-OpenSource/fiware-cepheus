/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata;

import com.orange.espr4fastdata.cep.EsperEventProcessor;
import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.persistence.JsonPersistence;
import com.orange.espr4fastdata.persistence.Persistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.orange")
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ComplexEventProcessor complexEventProcessor() {
        return new EsperEventProcessor();
    }

    @Bean
    public Persistence persistence() {
        return new JsonPersistence();
    }

    @Bean
    public Init init(ComplexEventProcessor complexEventProcessor, Persistence persistence) {
        return new Init(complexEventProcessor, persistence);
    }
}
