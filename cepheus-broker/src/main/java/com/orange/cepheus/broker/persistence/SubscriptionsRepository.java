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
import com.orange.ngsi.model.SubscribeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static class Subscription {
        String subscriptionId;
        String subscribeContextString;

        public Subscription() {
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        public void setSubscribeContextString(String subscribeContextString) {
            this.subscribeContextString = subscribeContextString;
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
        jdbcTemplate.execute("create table if not exists t_subscriptions (id varchar, subscribeContext varchar)");
        jdbcTemplate.execute("create unique index if not exists index_subscriptionId on t_subscriptions (id)");
    }

    /**
     * Save a subscription.
     * @param subscriptionId
     * @param subscribeContext
     * @throws JsonProcessingException
     */
    public void saveSubscription(String subscriptionId, SubscribeContext subscribeContext) throws JsonProcessingException {
        //serialization
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        String susbcribeContextString = writer.writeValueAsString(subscribeContext);

        jdbcTemplate.update("insert into t_subscriptions(id,subscribeContext) values(?,?)", subscriptionId, susbcribeContextString);
    }

    /**
     * Save a subscription updated
     * @param subscriptionId
     * @param subscribeContext
     * @throws JsonProcessingException
     */
    public void updateSubscription(String subscriptionId, SubscribeContext subscribeContext) throws JsonProcessingException {
        //serialization
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer();
        String susbcribeContextString = writer.writeValueAsString(subscribeContext);

        jdbcTemplate.update("update t_subscriptions set subscribeContext=? where id=?", susbcribeContextString, subscriptionId);
    }

    /**
     * Get all subscriptions saved
     * @return subscriptions map
     */
    public Map<String, SubscribeContext> getAllSubscriptions() {
        Map<String, SubscribeContext> subscriptions = new ConcurrentHashMap<>();
        List<Subscription> subscriptionList = jdbcTemplate.query( "select id, subscribeContext from t_subscriptions", new SubscriptionMapper());
        ObjectMapper mapper = new ObjectMapper();
        subscriptionList.forEach(subscription -> {
            try {
                SubscribeContext subscribeContext = mapper.readValue(subscription.getSubscribeContextString(), SubscribeContext.class);
                subscriptions.put(subscription.getSubscriptionId(), subscribeContext);
            } catch (IOException e) {
                logger.warn("failed to get subscription {}", subscription.getSubscribeContextString());
            }
        });
        return subscriptions;
    }

    /**
     * Remove a subscription.
     * @param subscriptionId
     */
    public void removeSubscription(String subscriptionId) {
        jdbcTemplate.update("delete from t_subscriptions where id=?", subscriptionId);
    }

    private static final class SubscriptionMapper implements RowMapper<Subscription> {

        public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
            Subscription subscription = new Subscription();
            subscription.setSubscriptionId(rs.getString("id"));
            subscription.setSubscribeContextString(rs.getString("subscribeContext"));
            return subscription;
        }
    }

}
