package dev.orne.qdsl.wrap;

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

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheResult;
import javax.validation.constraints.NotNull;

import org.apiguardian.api.API;

import com.querydsl.core.types.EntityPath;

/**
 * Registry of wrapped clause providers.
 * Implementation will expose providers discovered through different methods
 * (SPI, Spring context, etc.) to the wrapped clause factories.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedClauseFactory
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
@CacheDefaults(cacheName="dev.orne.qdsl.wrap.WrappedClauseProviderRegistry")
public interface WrappedClauseProviderRegistry {

    /**
     * Finds a wrapped clause provider of the specified type that supports the
     * specified entity path type.
     * 
     * @param <T> The wrapped clause provider type
     * @param type The wrapped clause provider type
     * @param entityType The entity path type
     * @return The registered wrapped clause provider that supports the  entity
     * path type, or {@code null} if no provider is available
     */
    @CachePut
    @CacheResult
    <T extends WrappedClauseProvider> T find(
            @NotNull Class<T> type,
            @NotNull Class<? extends EntityPath<?>> entityType);

    /**
     * Adds this registry to the registries used by {@code WrappedClauseFactory}
     * utility class.
     */
    default void addToWrappedClauseFactory() {
        WrappedClauseFactory.addRegistry(this);
    }
}
