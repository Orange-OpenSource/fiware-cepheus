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
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.persistence.JsonPersistence;
import com.orange.espr4fastdata.persistence.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan("com.orange")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    public ComplexEventProcessor complexEventProcessor;

    @Autowired
    public Persistence persistence;

    @Autowired
    public Init initEspr4fastdata;


}
