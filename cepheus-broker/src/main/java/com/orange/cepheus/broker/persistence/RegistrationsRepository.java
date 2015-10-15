/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orange.cepheus.broker.exception.RegistrationPersistenceException;
import com.orange.cepheus.broker.exception.SubscriptionPersistenceException;
import com.orange.cepheus.broker.model.Registration;
import com.orange.cepheus.broker.model.Subscription;
import com.orange.ngsi.model.RegisterContext;
import com.orange.ngsi.model.SubscribeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for registrations
 */
@Repository
public class RegistrationsRepository {

    private static Logger logger = LoggerFactory.getLogger(RegistrationsRepository.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    protected void createTableOnStartup() {
        jdbcTemplate.execute("create table if not exists t_registrations (id varchar primary key, expirationDate varchar not null, registerContext varchar not null)");
        jdbcTemplate.execute("create unique index if not exists index_registrationId on t_registrations (id)");
    }

    /**
     * Save a registration.
     * @param registration
     * @throws RegistrationPersistenceException
     */
    public void saveRegistration(Registration registration) throws RegistrationPersistenceException {
        try {
            //Mapping from model to database model
            ObjectWriter writer = mapper.writer();
            RegisterContext registerContext = registration.getRegisterContext();
            String registerContextString = writer.writeValueAsString(registerContext);
            String expirationDate = registration.getExpirationDate().toString();
            //insert into database
            jdbcTemplate.update("insert into t_registrations(id,expirationDate,registerContext) values(?,?,?)", registerContext.getRegistrationId(), expirationDate, registerContextString);
        } catch (Exception e) {
            throw new RegistrationPersistenceException(e);
        }
    }

    /**
     * Update a registration.
     * @param registration
     * @throws RegistrationPersistenceException
     */
    public void updateRegistration(Registration registration) throws RegistrationPersistenceException {
        try {
            //serialization
            ObjectWriter writer = mapper.writer();
            RegisterContext registerContext = registration.getRegisterContext();
            String registerContextString = writer.writeValueAsString(registerContext);
            String expirationDate = registration.getExpirationDate().toString();
            jdbcTemplate.update("update t_registrations set expirationDate=? , registerContext=? where id=?", expirationDate, registerContextString, registerContext.getRegistrationId());
        } catch (Exception e) {
            throw new RegistrationPersistenceException(e);
        }
    }

    /**
     * Get all saved registrations
     * @return registrations map
     * @throws RegistrationPersistenceException
     */
    public Map<String, Registration> getAllRegistrations() throws RegistrationPersistenceException {
        Map<String, Registration> registrations = new ConcurrentHashMap<>();
        try {
            List<Registration> registrationList = jdbcTemplate.query("select id, expirationDate, registerContext from t_registrations",
                    (ResultSet rs, int rowNum) ->  {
                        Registration registration = new Registration();
                        try {
                            registration.setExpirationDate(Instant.parse(rs.getString("expirationDate")));
                            registration.setRegisterContext(mapper.readValue(rs.getString("registerContext"), RegisterContext.class));
                        } catch (IOException e) {
                            throw new SQLException(e);
                        }
                        return registration;
                    });
            registrationList.forEach(registration -> registrations.put(registration.getRegisterContext().getRegistrationId(), registration));
        } catch (DataAccessException e) {
            throw new RegistrationPersistenceException(e);
        }
        return registrations;
    }

    /**
     * Remove a registration.
     * @param registrationId
     * @throws RegistrationPersistenceException
     */
    public void removeRegistration(String registrationId) throws RegistrationPersistenceException {
        try {
            jdbcTemplate.update("delete from t_registrations where id=?", registrationId);
        } catch (DataAccessException e) {
            throw new RegistrationPersistenceException(e);
        }
    }
}
