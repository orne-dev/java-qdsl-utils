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

import javax.validation.constraints.NotNull;

import org.apiguardian.api.API;

import com.querydsl.core.types.EntityPath;

/**
 * Root interface for providers of wrapped QueryDSL clauses.
 * <p>
 * Wrapped clauses allow limited build time transformation between expressions
 * based on query types based on API entity types and query types based the DB
 * representations of those entities.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public interface WrappedClauseProvider {

    /**
     * Tests if the specified entity path is supported by this clause
     * provider.
     * 
     * @param entity The entity path
     * @return If the entity path type is supported
     */
    default boolean supports(
            @NotNull EntityPath<?> entity) {
        return supports(getEntityPathType(entity));
    }

    /**
     * Tests if the specified entity path type is supported by this clause
     * provider.
     * 
     * @param entityType The entity path type
     * @return If the entity path type is supported
     */
    boolean supports(
            @NotNull Class<? extends EntityPath<?>> entityType);

    /**
     * Validates that the specified entity path is supported by this provider.
     * <p>
     * Equivalent to {@code validateSupported(getEntityPathType(entity))}.
     * 
     * @param entity The entity path to test
     * @throws EntityPathNotSupportedException If the entity path is not
     * supported
     */
    default void validateSupported(
            @NotNull EntityPath<?> entity) {
        validateSupported(getEntityPathType(entity));
    }

    /**
     * Validates that the specified entity path type is supported by this
     * provider.
     * 
     * @param entityType The entity path type to test
     * @throws EntityPathNotSupportedException If the entity path is not
     * supported
     */
    default void validateSupported(
            @NotNull Class<? extends EntityPath<?>> entityType) {
        if (!supports(entityType)) {
            throw new EntityPathNotSupportedException(String.format(
                    "Entity type %s is not supported on wrapped clause provider %s",
                    entityType,
                    getClass()));
        }
    }

    /**
     * Returns the entity path type of the specified entity path.
     * <p>
     * Hides unchecked (but known) casting of class with type erasure to
     * typed class.
     * 
     * @param <T> The entity path type
     * @param entity The entity path
     * @return The entity path type
     */
    @SuppressWarnings("unchecked")
    static <T> @NotNull Class<? extends EntityPath<T>> getEntityPathType(
            final @NotNull EntityPath<T> entity) {
        return (Class<? extends EntityPath<T>>) entity.getClass();
    }
}
