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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.apiguardian.api.API;

import com.querydsl.core.Tuple;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;

/**
 * Main entry point for wrapped clauses system.
 * Allows abstraction of underlying DB system to components based on public API
 * model and query types.
 * <p>
 * Different wrapped clause providers can be used in runtime without changes in
 * the calling code to transform public API based query expressions into query
 * expressions based on the concrete DB system.
 * <p>
 * Queries can be made based on the public API query types with zero knowledge
 * of the real implementation:
 * <pre>
 * class QType extends EntityPathBase&ltIFace&gt; ...
 * QType entity = ...;
 * List&lt;? extends IFace&gt; result = WrappedClauseFactory
 *     .select(entity, DefaultProjectionTypes.DEFAULT)
 *     .where(entity.status.in(Status.ENABLED, Status.DISABLED))
 *     .orderBy(entity.code.asc())
 *     .fetch();
 * </pre>
 * Supports basic DML clauses:
 * <pre>
 * WrappedClauseFactory.insert(entity)
 *     .set(entity.code, 1000)
 *     .set(entity.name, "My new bean")
 *     .set(entity.status, Status.ENABLED)
 *     .execute();
 * WrappedClauseFactory.update(entity)
 *     .set(entity.status, Status.READY_DO_DELETE)
 *     .where(entity.code.eq(1001))
 *     .execute();
 * WrappedClauseFactory.delete(entity)
 *     .where(entity.status.eq(Status.READY_DO_DELETE))
 *     .execute();
 * </pre>
 * <p>
 * <b>Warning:</b> The expression transformation has a computational cost.
 * If the underlying DB system is known classic QueryDSL usage based on query
 * types generated from real DB entities should be used instead.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedClauseProviderRegistry
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public final class WrappedClauseFactory {

    /** The wrapped clause provider registries. */
    private static final @NotNull List<WrappedClauseProviderRegistry> REGISTRIES =
            new ArrayList<>();

    /**
     * Private constructor.
     */
    private WrappedClauseFactory() {
        // Utility class
    }

    /**
     * Returns the wrapped clause provider registries.
     * 
     * @return The wrapped clause provider registries
     */
    static List<WrappedClauseProviderRegistry> getRegistries() {
        return Collections.unmodifiableList(REGISTRIES);
    }

    /**
     * Adds the specified wrapped clause provider registry to the registries
     * used to find providers for entity path types.
     * <p>
     * Called from {@code WrappedClauseProviderRegistry.addToWrappedClauseFactory()}.
     * 
     * @param registry The wrapped clause provider registry
     * @see WrappedClauseProviderRegistry#addToWrappedClauseFactory()
     */
    static void addRegistry(
            final @NotNull WrappedClauseProviderRegistry registry) {
        REGISTRIES.add(Validate.notNull(registry));
    }

    /**
     * Clears the wrapped clause provider registries used.
     * <p>
     * New calls to {@code WrappedClauseProviderRegistry.addToWrappedClauseFactory()}
     * are required to add provider registries.
     * 
     * @see WrappedClauseProviderRegistry#addToWrappedClauseFactory()
     */
    public static void reset() {
        REGISTRIES.clear();
    }

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
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static <T> @NotNull Expression<? extends T> getProjection(
            @NotNull EntityPath<T> entity,
            @NotNull ProjectionType type) {
        return findProvider(WrappedQueryClauseProvider.class, entity).getProjection(entity, type);
    }

    /**
     * Creates a new wrapped Query clause for the specified entity path.
     * 
     * @param entity The target entity path
     * @return The created wrapped Query clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static @NotNull ExtendedQueryClause<?, ?> query(
            @NotNull EntityPath<?> entity) {
        return findProvider(WrappedQueryClauseProvider.class, entity).query(entity);
    }

    /**
     * Creates a new wrapped Query clause for the specified entity path
     * with support for grouping of results.
     * 
     * @param entity The target entity path
     * @return The created wrapped Query clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static @NotNull ExtendedGroupableQueryClause<?, ?> queryGroupable(
            @NotNull EntityPath<?> entity) {
        return findProvider(WrappedGroupableQueryClauseProvider.class, entity).query(entity);
    }

    /**
     * Creates a new wrapped Query clause for the specified entity path
     * with the default entity projection.
     * 
     * @param <T> The entity type returned by the Query clause
     * @param entity The target entity path
     * @return The created wrapped Query clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static <T> @NotNull ExtendedQueryClause<T, ?> select(
            @NotNull EntityPath<T> entity) {
        return select(entity, ProjectionType.Basic.DEFAULT);
    }

    /**
     * Creates a new wrapped Query clause for the specified entity path
     * with an entity projection of the specified type.
     * <p>
     * If no projection is available for the specified type the default entity
     * projection is applied.
     * 
     * @param <T> The entity type returned by the Query clause
     * @param entity The target entity path
     * @param projectionType The projection type
     * @return The created wrapped Query clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static <T> @NotNull ExtendedQueryClause<T, ?> select(
            @NotNull EntityPath<T> entity,
            @NotNull ProjectionType projectionType) {
        final WrappedQueryClauseProvider provider = findProvider(
                WrappedQueryClauseProvider.class,
                entity);
        return provider.query(entity).select(
                provider.getProjection(entity, projectionType));
    }

    /**
     * Creates a new wrapped Query clause for the specified entity path
     * with the specified projection expression.
     * 
     * @param <U> The result type returned by the Query clause
     * @param entity The target entity path
     * @param expr The projection expression
     * @return The created wrapped Query clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static <U> @NotNull ExtendedQueryClause<U, ?> select(
            @NotNull EntityPath<?> entity,
            @NotNull Expression<U> expr) {
        return query(entity).select(expr);
    }

    /**
     * Creates a new wrapped Query clause for the specified entity path
     * that fetches tuples with the values of specified expressions.
     * 
     * @param entity The target entity path
     * @param exprs The projection expressions
     * @return The created wrapped Query clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static @NotNull ExtendedQueryClause<Tuple, ?> select(
            @NotNull EntityPath<?> entity,
            @NotNull Expression<?>... exprs) {
        return query(entity).select(exprs);
    }

    /**
     * Creates a new wrapped Insert clause for the specified entity path.
     * 
     * @param entity The target entity path
     * @return The created wrapped Insert clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static @NotNull ExtendedInsertClause<?> insert(
            final @NotNull EntityPath<?> entity) {
        return findProvider(WrappedInsertClauseProvider.class, entity).insert(entity);
    }

    /**
     * Creates a new wrapped Update clause for the specified entity path.
     * 
     * @param entity The target entity path
     * @return The created wrapped Update clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static @NotNull ExtendedUpdateClause<?> update(
            final @NotNull EntityPath<?> entity) {
        return findProvider(WrappedUpdateClauseProvider.class, entity).update(entity);
    }

    /**
     * Creates a new wrapped Delete clause for the specified entity path.
     * 
     * @param entity The target entity path
     * @return The created wrapped Delete clause
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    public static @NotNull DeleteClause<?> delete(
            final @NotNull EntityPath<?> entity) {
        return findProvider(WrappedDeleteClauseProvider.class, entity).delete(entity);
        
    }

    /**
     * Finds a wrapped clause provider of the specified type that supports
     * 
     * @param <T> The wrapped clause provider type
     * @param type The wrapped clause provider type
     * @param entity The target entity path
     * @return The wrapped clause provider that supports the entity path type
     * @throws ClauseProviderNotFoundException If no provider is available
     * for the requested entity path type
     */
    private static <T extends WrappedClauseProvider> @NotNull T findProvider(
            final @NotNull Class<T> type,
            final @NotNull EntityPath<?> entity) {
        if (REGISTRIES.isEmpty()) {
            throw new ClauseProviderNotFoundException(
                    "No wrapped clause provider registry configured");
        }
        Validate.notNull(type, "The clause provider type is required");
        Validate.notNull(entity, "The entity path is required");
        final Class<? extends EntityPath<?>> entityType =
                WrappedClauseProvider.getEntityPathType(entity);
        T provider = null;
        for (final WrappedClauseProviderRegistry registry : REGISTRIES) {
            provider = registry.find(type, entityType);
            if (provider != null) {
                break;
            }
        }
        if (provider == null) {
            throw new ClauseProviderNotFoundException(String.format(
                    "Clause provider of type %s not found for entity %s",
                    type,
                    entity));
        }
        return provider;
    }
}
