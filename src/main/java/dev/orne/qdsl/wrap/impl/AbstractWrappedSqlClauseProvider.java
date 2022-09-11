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

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apiguardian.api.API;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQueryFactory;

import dev.orne.qdsl.wrap.ExtendedGroupableQueryClause;
import dev.orne.qdsl.wrap.WrappedGroupableQueryClauseProvider;

/**
 * Abstract implementation for wrapped QueryDSL clause providers based on
 * delegated SQL based QueryDSL clauses.
 * <p>
 * This class does not inherit subtypes of {@code WrappedClauseProvider} but
 * provides methods that allow implementations to declare such inheritance
 * without the need to implement any of the methods.
 * <p>
 * This allows creation of providers that expose only a subset of the CRUD
 * operations.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public abstract class AbstractWrappedSqlClauseProvider
extends AbstractWrappedClauseProvider {

    /**
     * Creates a new instance.
     * 
     * @param supportedEntityTypes The supported entity types
     */
    @SafeVarargs
    protected AbstractWrappedSqlClauseProvider(
            final @NotNull Class<? extends EntityPath<?>>... supportedEntityTypes) {
        super(supportedEntityTypes);
    }

    /**
     * Creates a new instance.
     * 
     * @param supportedEntityTypes The supported entity types
     */
    protected AbstractWrappedSqlClauseProvider(
            final @NotNull Set<Class<? extends EntityPath<?>>> supportedEntityTypes) {
        super(supportedEntityTypes);
    }

    /**
     * Returns the SQL query factory to use.
     * 
     * @return The SQL query factory
     */
    protected abstract @NotNull SQLCommonQueryFactory<? extends ProjectableSQLQuery<?, ?>, ?, ?, ?, ?> getQueryFactory();

    /**
     * Returns the real relational entity path for the specified entity path.
     * <p>
     * Passed to the delegated SQL query clause {@code from()} method.
     * 
     * @param entity The target entity path
     * @return The target relational entity path
     */
    protected abstract @NotNull RelationalPath<?> getSource(
            final @NotNull EntityPath<?> entity);

    /**
     * Implementation of {@code WrappedGroupableQueryClauseProvider.query()}.
     * <p>
     * Note that {@code AbstractWrappedSqlClauseProvider} does not
     * inherit {@code WrappedGroupableQueryClauseProvider}.
     * Implementations must declare which provider subtypes exposes inheriting
     * appropriate interfaces.
     * 
     * @param entity The target entity path
     * @return The created wrapped Query clause
     * @see WrappedGroupableQueryClauseProvider#query(EntityPath)
     */
    public @NotNull ExtendedGroupableQueryClause<?, ?> query(
            final @NotNull EntityPath<?> entity) {
        validateSupported(entity);
        return new WrappedSqlQueryClause<>(
                createTransformerForAlias(entity),
                createDelegatedQueryClause(entity));
    }

    /**
     * Creates the delegated SQL query clause for the specified entity.
     * 
     * @param <T> The target entity type
     * @param entity The target entity path
     * @return The created SQL query clause
     */
    protected <T> @NotNull ProjectableSQLQuery<?, ?> createDelegatedQueryClause(
            final @NotNull EntityPath<T> entity) {
        return getQueryFactory().from(getSource(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> @NotNull InsertClause<?> createDelegatedInsertClause(
            final @NotNull EntityPath<T> entity) {
        return getQueryFactory().insert(getSource(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> @NotNull UpdateClause<?> createDelegatedUpdateClause(
            final @NotNull EntityPath<T> entity) {
        return getQueryFactory().update(getSource(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> @NotNull DeleteClause<?> createDelegatedDeleteClause(
            final @NotNull EntityPath<T> entity) {
        return getQueryFactory().delete(getSource(entity));
    }
}
