/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.ngsi.model.EntityId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Handle patterns of entityId
 */
@Component
public class Patterns {

    /**
     * Cache of compiled patterns
     */
    private Map<String, Pattern> cachedPatterns = new ConcurrentHashMap<>();

    /**
     * Compile (or get from cache) the patter corresponding to the entity id
     * @param entityId the entity id
     * @return the pattern, or null if entity id is not a pattern
     * @throws PatternSyntaxException
     */
    public Pattern getPattern(final EntityId entityId) throws PatternSyntaxException {
        if (!entityId.getIsPattern()) {
            return null;
        }
        String id = entityId.getId();
        Pattern pattern = cachedPatterns.get(id);
        if (pattern == null) {
            pattern = Pattern.compile(id);
            cachedPatterns.put(id, pattern);
        }
        return pattern;
    }

    /**
     * @return TRUE if the type is not null or empty
     */
    public boolean hasType(final EntityId entityId) {
        final String type = entityId.getType();
        return type != null && !"".equals(type);
    }

    /**
     * construct the predicate to filter on entityId
     * @param searchEntityId the entity id to search
     * @return the predicate to filter on entityId
     */
    public Predicate<EntityId> getFilterEntityId(EntityId searchEntityId) {
        final boolean searchType = hasType(searchEntityId);
        final Pattern pattern = getPattern(searchEntityId);

        Predicate<EntityId> filterEntityId = entityId -> {
            // Match by type if any
            if (!searchType) {
                if (hasType(entityId)) {
                    return false;
                }
            } else if (!searchEntityId.getType().equals(entityId.getType())) {
                return false;
            }

            // Match pattern if any
            if (pattern != null) {
                // Match two patterns by equality
                if (entityId.getIsPattern()) {
                    return searchEntityId.getId().equals(entityId.getId());
                }
                return pattern.matcher(entityId.getId()).find();
            } else {
                if (entityId.getIsPattern()) {
                    return getPattern(entityId).matcher(searchEntityId.getId()).find();
                }
                // Match two ids by equality
                return searchEntityId.getId().equals(entityId.getId());
            }
        };

        return filterEntityId;
    }
}
