package dev.orne.qdsl.wrap.impl;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2022 Orne Developments
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import javax.cache.Cache;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.querydsl.core.types.EntityPath;

import dev.orne.qdsl.wrap.WrappedClauseProvider;
import dev.orne.qdsl.wrap.WrappedClauseProviderRegistry;

/**
 * Delegated based implementation of {@code WrappedClauseProviderRegistry}
 * that can be cached through JCache (JSR-107) implementations.
 * <p>
 * Supports both internally managed caches (through {{@link #setCache(Cache)})
 * and annotation based cache.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 */
public class CacheableWrappedClauseProviderRegistry
implements WrappedClauseProviderRegistry {

    /** The delegated wrapped clause provider registry. */
    private final @NotNull WrappedClauseProviderRegistry delegated;
    /** The internally managed cache. */
    private Cache<CacheKey, WrappedClauseProvider> cache;

    /**
     * Creates a new instance.
     * 
     * @param delegated The delegated wrapped clause provider registry
     */
    public CacheableWrappedClauseProviderRegistry(
            final @NotNull WrappedClauseProviderRegistry delegated) {
        super();
        this.delegated = delegated;
    }

    /**
     * Returns the delegated wrapped clause provider registry.
     * 
     * @return The delegated wrapped clause provider registry
     */
    protected @NotNull WrappedClauseProviderRegistry getDelegated() {
        return this.delegated;
    }

    /**
     * Returns the internally managed cache.
     * 
     * @return The internally managed cache
     */
    protected synchronized Cache<CacheKey, WrappedClauseProvider> getCache() {
        return this.cache;
    }

    /**
     * Sets the internally managed cache.
     * 
     * @param cache The internally managed cache
     */
    public synchronized void setCache(
            final Cache<CacheKey, WrappedClauseProvider> cache) {
        this.cache = cache;
    }

    /**
     * Resets the cache.
     */
    @CacheRemoveAll
    public synchronized void reset() {
        if (this.cache != null) {
            this.cache.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheResult
    public synchronized <T extends WrappedClauseProvider> T find(
            final @NotNull Class<T> type,
            final @NotNull Class<? extends EntityPath<?>> entityType) {
        T result = null;
        if (this.cache == null) {
            result = this.delegated.find(type, entityType);
        } else {
            final CacheKey key = new CacheKey(
                    Validate.notNull(type),
                    Validate.notNull(entityType));
            if (cache.containsKey(key)) {
                result = type.cast(cache.get(key));
            } else {
                result = this.delegated.find(type, entityType);
                cache.put(key, result);
            }
        }
        return result;
    }

    /**
     * Cache key type for internally managed caches.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2022-08
     * @since AbstractCacheableWrappedClauseProviderRegistry 1.0
     */
    public static class CacheKey {

        /** The wrapped clause provider type. */
        private final @NotNull Class<? extends WrappedClauseProvider> providerType;
        /** The wrapped clause entity type. */
        private final @NotNull Class<? extends EntityPath<?>> entityType;

        /**
         * Creates a new instance.
         * 
         * @param providerType The wrapped clause provider type
         * @param entityType The wrapped clause entity type
         */
        public CacheKey(
                final @NotNull Class<? extends WrappedClauseProvider> providerType,
                final @NotNull Class<? extends EntityPath<?>> entityType) {
            super();
            this.providerType = providerType;
            this.entityType = entityType;
        }

        /**
         * Returns the wrapped clause provider type.
         * 
         * @return The wrapped clause provider type
         */
        public @NotNull Class<? extends WrappedClauseProvider> getProviderType() {
            return this.providerType;
        }

        /**
         * Returns the wrapped clause entity type.
         * 
         * @return The wrapped clause entity type
         */
        public @NotNull Class<? extends EntityPath<?>> getEntityType() {
            return this.entityType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(this.providerType)
                    .append(this.entityType)
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(
                final Object obj) {
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            final CacheKey other = (CacheKey) obj;
            return new EqualsBuilder()
                    .append(this.providerType, other.providerType)
                    .append(this.entityType, other.entityType)
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        }
    }
}
