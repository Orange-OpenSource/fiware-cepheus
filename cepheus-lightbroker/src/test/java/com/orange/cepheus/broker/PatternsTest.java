/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */
package com.orange.cepheus.broker;

import com.orange.ngsi.model.EntityId;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Tests for Patterns management
 */
public class PatternsTest {

    @Test
    public void hasTypeNullTest() {
        EntityId entityId = new EntityId();
        assertFalse(new Patterns().hasType(entityId));
    }

    @Test
    public void hasTypeEmptyTest() {
        EntityId entityId = new EntityId();
        entityId.setType("");
        assertFalse(new Patterns().hasType(entityId));
    }

    @Test
    public void hasTypeOKTest() {
        EntityId entityId = new EntityId();
        entityId.setType("temp");
        assertTrue(new Patterns().hasType(entityId));
    }

    @Test
    public void getPatternNullTest() {
        EntityId entityId = new EntityId("A", "string", false);
        assertNull(new Patterns().getPattern(entityId));
    }

    @Test
    public void getPatternTest() {
        EntityId entityId = new EntityId("A*", "string", true);
        assertNotNull(new Patterns().getPattern(entityId));
    }

    @Test
    public void getPatternWtihSamePatternTest() {
        EntityId entityId = new EntityId("A*", "string", true);
        Patterns patterns = new Patterns();
        Pattern pattern = patterns.getPattern(entityId);
        assertEquals(pattern, patterns.getPattern(entityId));
    }

    @Test
    public void getPatternWtihNotSamePatternTest() {
        EntityId entityId = new EntityId("A*", "string", true);
        EntityId entityId2 = new EntityId("B*", "string", true);
        Patterns patterns = new Patterns();
        Pattern pattern = patterns.getPattern(entityId);
        assertNotEquals(pattern, patterns.getPattern(entityId2));
    }

    @Test
    public void getFilterEntityIdEqualWithNoPatternTest() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("A", "string", false);
        EntityId entityIdsearch = new EntityId("A", "string", false);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertTrue(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

    @Test
    public void getFilterEntityIdNotEqualWithNoPatternTest() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("A", "string", false);
        EntityId entityIdsearch = new EntityId("B", "string", false);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertFalse(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

    @Test
    public void getFilterEntityIdEqualWithPatternTest() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("A*", "string", true);
        EntityId entityIdsearch = new EntityId("A*", "string", true);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertTrue(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

    @Test
    public void getFilterEntityIdNotEqualWithPatternTest() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("A*", "string", true);
        EntityId entityIdsearch = new EntityId("B*", "string", true);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertFalse(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

    @Test
    public void getFilterEntityIdNotEqualWithOnePatternTest() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("A|B", "string", true);
        EntityId entityIdsearch = new EntityId("A", "string", false);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertTrue(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

    @Test
    public void getFilterEntityIdNotEqualWithOnePattern2Test() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("A", "string", false);
        EntityId entityIdsearch = new EntityId("A|B", "string", true);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertTrue(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

    @Test
    public void getFilterEntityIdNotEqualWithOnePatternFalseTest() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("A|B", "string", true);
        EntityId entityIdsearch = new EntityId("C", "string", false);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertFalse(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

    @Test
    public void getFilterEntityIdNotEqualWithOnePattern2FalseTest() {
        EntityId entityIdRegisterOrSubscribe = new EntityId("C", "string", false);
        EntityId entityIdsearch = new EntityId("A|B", "string", true);
        Patterns patterns = new Patterns();

        Predicate<EntityId> entityIdPredicate = patterns.getFilterEntityId(entityIdsearch);
        assertFalse(entityIdPredicate.test(entityIdRegisterOrSubscribe));
    }

}
