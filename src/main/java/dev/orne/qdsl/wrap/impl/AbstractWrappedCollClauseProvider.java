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

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.apiguardian.api.API;

import com.querydsl.collections.CollDeleteClause;
import com.querydsl.collections.CollUpdateClause;
import com.querydsl.collections.DefaultQueryEngine;
import com.querydsl.collections.QueryEngine;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;

import dev.orne.qdsl.wrap.ExtendedQueryClause;
import dev.orne.qdsl.wrap.WrappedQueryClauseProvider;

/**
 * Abstract implementation for wrapped QueryDSL clause providers based on
 * delegated collection based QueryDSL clauses.
 * <p>
 * This class does not inherit subtypes of {@code WrappedClauseProvider} but
 * provides methods that allow implementations to declare such inheritance
 * without the need to implement any of the methods.
 * <p>
 * This allows creation of providers that expose only a subset of the CRUD
 * operations.
 * <p>
 * Note that collection based QueryDSL clauses does not support insert clauses,
 * so neither does this class.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public abstract class AbstractWrappedCollClauseProvider
extends AbstractWrappedClauseProvider {

    /**
     * Creates a new instance.
     * 
     * @param targetEntity The base target entity
     * @param supportedEntities The supported base entities
     */
    @SafeVarargs
    protected AbstractWrappedCollClauseProvider(
            final @NotNull EntityPath<?> targetEntity,
            final @NotNull EntityPath<?>... supportedEntities) {
        super(targetEntity, supportedEntities);
    }

    /**
     * Creates a new instance.
     * 
     * @param targetEntity The base target entity
     * @param supportedEntities The supported base entities
     */
    protected AbstractWrappedCollClauseProvider(
            final @NotNull EntityPath<?> targetEntity,
            final @NotNull Collection<EntityPath<?>> supportedEntities) {
        super(targetEntity, supportedEntities);
    }

    /**
     * Returns the collection of source values for the specified entity path.
     * 
     * @param <T> The entity type
     * @param entity The entity path
     * @return The collection of source values
     */
    protected abstract <T> @NotNull Collection<T> getValues(
            @NotNull EntityPath<T> entity);

    /**
     * Implementation of {@code WrappedQueryClauseProvider.query()}.
     * <p>
     * Note that {@code AbstractWrappedCollectionClauseProvider} does not
     * inherit {@code WrappedQueryClauseProvider}.
     * Implementations must declare which provider subtypes exposes inheriting
     * appropriate interfaces.
     * 
     * @param entity The target entity path
     * @return The created wrapped Query clause
     * @see WrappedQueryClauseProvider#query(EntityPath)
     */
    public @NotNull ExtendedQueryClause<?, ?> query(
            final @NotNull EntityPath<?> entity) {
        validateSupported(entity);
        return createQueryClause(entity);
    }

    /**
     * Return the collection based query engine to use.
     * 
     * @return The collection based query engine
     */
    protected @NotNull QueryEngine getQueryEngine() {
        return DefaultQueryEngine.getDefault();
    }

    /**
     * Creates a new wrapped query clause for the specified entity.
     * 
     * @param <T> The target entity type
     * @param entity The target entity path
     * @return The created wrapped query clause
     */
    protected <T> @NotNull WrappedCollQueryClause<T> createQueryClause(
            final @NotNull EntityPath<T> entity) {
        @SuppressWarnings("unchecked")
        final EntityPath<T> target = (EntityPath<T>) getTargetEntity(entity);
        return new WrappedCollQueryClause<>(
                createTransformerForAlias(entity),
                target,
                getQueryEngine(),
                getValues(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> @NotNull InsertClause<?> createDelegatedInsertClause(
            final @NotNull EntityPath<T> entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> @NotNull UpdateClause<?> createDelegatedUpdateClause(
            final @NotNull EntityPath<T> entity) {
        return new CollUpdateClause<>(
                getQueryEngine(),
                entity,
                getValues(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> @NotNull DeleteClause<?> createDelegatedDeleteClause(
            final @NotNull EntityPath<T> entity) {
        return new CollDeleteClause<>(
                getQueryEngine(),
                entity,
                getValues(entity));
    }
}
