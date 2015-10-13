/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orange.cepheus.broker.exception.SubscriptionPersistenceException;
import com.orange.cepheus.broker.model.Subscription;
import com.orange.ngsi.model.SubscribeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
 * Repository for Subscriptions
 */
@Repository
public class SubscriptionsRepository {
    private static Logger logger = LoggerFactory.getLogger(SubscriptionsRepository.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Subscription Bean used for the mapping between a subscription and the table
     */
    private static class SubscriptionDataBase {
        String subscriptionId;
        String expirationDate;
        String subscribeContextString;

        public SubscriptionDataBase() {
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        public void setSubscribeContextString(String subscribeContextString) {
            this.subscribeContextString = subscribeContextString;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public String getSubscribeContextString() {
            return subscribeContextString;
        }
    }

    @PostConstruct
    protected void createTableOnStartup() {
        jdbcTemplate.execute("create table if not exists t_subscriptions (id varchar, expirationDate varchar, subscribeContext varchar)");
        jdbcTemplate.execute("create unique index if not exists index_subscriptionId on t_subscriptions (id)");
    }

    /**
     * Save a subscription.
     * @param subscription
     * @throws SubscriptionPersistenceException
     */
    public void saveSubscription(Subscription subscription) throws SubscriptionPersistenceException {

        try {
            //Mapping from model to database model
            ObjectWriter writer = mapper.writer();
            String susbcribeContextString = writer.writeValueAsString(subscription.getSubscribeContext());
            String expirationDate = subscription.getExpirationDate().toString();
            //insert into database
            jdbcTemplate.update("insert into t_subscriptions(id,expirationDate,subscribeContext) values(?,?,?)", subscription.getSubscriptionId(), expirationDate, susbcribeContextString);
        } catch (Exception e) {
            throw new SubscriptionPersistenceException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Save a subscription updated
     * @param subscription
     * @throws SubscriptionPersistenceException
     */
    public void updateSubscription(Subscription subscription) throws SubscriptionPersistenceException {

        try {
        //serialization
        ObjectWriter writer = mapper.writer();
        String susbcribeContextString = writer.writeValueAsString(subscription.getSubscribeContext());
        String expirationDate = subscription.getExpirationDate().toString();
        jdbcTemplate.update("update t_subscriptions set expirationDate=? , subscribeContext=? where id=?", expirationDate, susbcribeContextString, subscription.getSubscriptionId());
        } catch (Exception e) {
            throw new SubscriptionPersistenceException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Get all subscriptions saved
     * @return subscriptions map
     * @throws SubscriptionPersistenceException
     */
    public Map<String, Subscription> getAllSubscriptions() throws SubscriptionPersistenceException {
        Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();
        try {
            List<SubscriptionDataBase> subscriptionDataBaseList = jdbcTemplate.query("select id, expirationDate, subscribeContext from t_subscriptions", new SubscriptionMapper());
            subscriptionDataBaseList.forEach(subscriptionDataBase -> {
                try {
                    SubscribeContext subscribeContext = mapper.readValue(subscriptionDataBase.getSubscribeContextString(), SubscribeContext.class);
                    Instant expirationDate = Instant.parse(subscriptionDataBase.getExpirationDate());
                    String subscriptionId = subscriptionDataBase.getSubscriptionId();
                    Subscription subscription = new Subscription(subscriptionId, expirationDate, subscribeContext);
                    subscriptions.put(subscriptionId, subscription);
                } catch (IOException e) {
                    logger.warn("failed to get subscription {}", subscriptionDataBase.getSubscribeContextString());
                }
            });
        } catch (DataAccessException e) {
            throw new SubscriptionPersistenceException(e.getMessage(),e.getCause());
        }
        return subscriptions;
    }

    /**
     * Remove a subscription.
     * @param subscriptionId
     */
    public void removeSubscription(String subscriptionId) {
        jdbcTemplate.update("delete from t_subscriptions where id=?", subscriptionId);
    }

    private static final class SubscriptionMapper implements RowMapper<SubscriptionDataBase> {

        public SubscriptionDataBase mapRow(ResultSet rs, int rowNum) throws SQLException {
            SubscriptionDataBase subscriptionDataBase = new SubscriptionDataBase();
            subscriptionDataBase.setSubscriptionId(rs.getString("id"));
            subscriptionDataBase.setExpirationDate(rs.getString("expirationDate"));
            subscriptionDataBase.setSubscribeContextString(rs.getString("subscribeContext"));
            return subscriptionDataBase;
        }
    }

}
