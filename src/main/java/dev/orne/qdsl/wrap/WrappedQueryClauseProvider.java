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
import com.querydsl.core.types.Expression;

/**
 * Provider of wrapped QueryDSL Query clauses and projection expressions.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public interface WrappedQueryClauseProvider
extends WrappedClauseProvider {

    /**
     * Returns the projection expression for the specified entity and type.
     * <p>
     * If no projection is available for the specified type the default entity
     * projection is returned.
     * 
     * @param <T> The entity type of the returned expression
     * @param entity The target entity path
     * @param type The projection type
     * @return The entity projection expression
     */
    <T> @NotNull Expression<? extends T> getProjection(
            @NotNull EntityPath<T> entity,
            @NotNull ProjectionType type);

    /**
     * Creates a new wrapped Query clause for the specified entity path.
     * 
     * @param entity The target entity path
     * @return The created wrapped Query clause
     */
    @NotNull ExtendedQueryClause<?, ?> query(
            @NotNull EntityPath<?> entity);
}
