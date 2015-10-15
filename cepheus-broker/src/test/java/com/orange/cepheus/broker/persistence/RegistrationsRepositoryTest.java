/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.persistence;

import com.orange.cepheus.broker.Application;
import com.orange.cepheus.broker.exception.RegistrationPersistenceException;
import com.orange.cepheus.broker.exception.SubscriptionPersistenceException;
import com.orange.cepheus.broker.model.Registration;
import com.orange.cepheus.broker.model.Subscription;
import com.orange.ngsi.model.RegisterContext;
import com.orange.ngsi.model.SubscribeContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.orange.cepheus.broker.Util.createRegisterContextTemperature;
import static com.orange.cepheus.broker.Util.createSubscribeContextTemperature;

/**
 * Tests for RegistrationsRepository
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource(locations="classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegistrationsRepositoryTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Autowired
    RegistrationsRepository registrationsRepository;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Before
    public void init() throws SQLException {
        jdbcTemplate.execute("delete from t_registrations");
    }

    @Test
    public void saveRegistrationTest() throws URISyntaxException, RegistrationPersistenceException {
        RegisterContext registerContext = createRegisterContextTemperature();
        registerContext.setRegistrationId("12345");
        Registration registration = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext);

        registrationsRepository.saveRegistration(registration);

        Map<String, Registration> registrations = registrationsRepository.getAllRegistrations();
        Assert.assertEquals(1, registrations.size());
        Assert.assertEquals(registration.getExpirationDate(), registrations.get("12345").getExpirationDate());
        Assert.assertEquals(registerContext.getDuration(), registrations.get("12345").getRegisterContext().getDuration());
    }

    @Test
    public void saveRegistrationWithDuplicateKeyExceptionTest() throws URISyntaxException, RegistrationPersistenceException {
        thrown.expect(RegistrationPersistenceException.class);
        RegisterContext registerContext = createRegisterContextTemperature();
        registerContext.setRegistrationId("12345");
        Registration registration = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext);
        registrationsRepository.saveRegistration(registration);
        registrationsRepository.saveRegistration(registration);
    }

    @Test
    public void updateRegistrationTest() throws URISyntaxException, RegistrationPersistenceException {
        RegisterContext registerContext = createRegisterContextTemperature();
        registerContext.setRegistrationId("12345");
        Registration registration = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext);
        registrationsRepository.saveRegistration(registration);
        Map<String, Registration> registrations = registrationsRepository.getAllRegistrations();
        Assert.assertEquals(1, registrations.size());
        Assert.assertEquals("PT10S", registrations.get("12345").getRegisterContext().getDuration());
        registerContext.setDuration("PT1D");
        registration.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        registrationsRepository.updateRegistration(registration);
        registrations = registrationsRepository.getAllRegistrations();
        Assert.assertEquals(1, registrations.size());
        Assert.assertEquals("PT1D", registrations.get("12345").getRegisterContext().getDuration());
        Assert.assertEquals(registration.getExpirationDate(), registrations.get("12345").getExpirationDate());
    }

    @Test
    public void updateRegistrationWithExceptionTest() throws URISyntaxException, RegistrationPersistenceException {
        thrown.expect(RegistrationPersistenceException.class);
        Registration registration = new Registration();
        registration.setRegisterContext(new RegisterContext());
        registrationsRepository.updateRegistration(registration);
    }

    @Test
    public void removeRegistrationTest() throws URISyntaxException, RegistrationPersistenceException {
        RegisterContext registerContext = createRegisterContextTemperature();
        registerContext.setRegistrationId("12345");
        Registration registration = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext);
        registrationsRepository.saveRegistration(registration);
        Assert.assertEquals(1, registrationsRepository.getAllRegistrations().size());
        registrationsRepository.removeRegistration("12345");
        Assert.assertEquals(0, registrationsRepository.getAllRegistrations().size());
    }

    @Test
    public void getAllRegistrationsTest() throws URISyntaxException, RegistrationPersistenceException {
        RegisterContext registerContext = createRegisterContextTemperature();
        registerContext.setRegistrationId("12345");
        Registration registration = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext);
        registrationsRepository.saveRegistration(registration);
        RegisterContext registerContext2 = createRegisterContextTemperature();
        registerContext2.setRegistrationId("12346");
        Registration registration2 = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext2);
        registrationsRepository.saveRegistration(registration2);
        RegisterContext registerContext3 = createRegisterContextTemperature();
        registerContext3.setRegistrationId("12347");
        Registration registration3 = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext3);
        registrationsRepository.saveRegistration(registration3);
        Assert.assertEquals(3, registrationsRepository.getAllRegistrations().size());
    }

    @Test
    public void getAllRegistrationsWithExceptionTest() throws URISyntaxException, RegistrationPersistenceException {
        thrown.expect(RegistrationPersistenceException.class);
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.DAYS);
        jdbcTemplate.update("insert into t_registrations(id,expirationDate,registerContext) values(?,?,?)", "12345", expirationDate.toString(), "aaaaaa");
        Map<String, Registration> registrations = registrationsRepository.getAllRegistrations();
    }
}
