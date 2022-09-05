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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apiguardian.api.API;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Visitor;

import dev.orne.qdsl.NopReplaceVisitor;
import dev.orne.qdsl.wrap.ExtendedInsertClause;
import dev.orne.qdsl.wrap.ExtendedUpdateClause;
import dev.orne.qdsl.wrap.ProjectionType;
import dev.orne.qdsl.wrap.WrappedClauseProvider;
import dev.orne.qdsl.wrap.WrappedDeleteClauseProvider;
import dev.orne.qdsl.wrap.WrappedInsertClauseProvider;
import dev.orne.qdsl.wrap.WrappedQueryClauseProvider;
import dev.orne.qdsl.wrap.WrappedUpdateClauseProvider;
import dev.orne.qdsl.wrap.impl.transform.ChainedExpressionTransformer;

/**
 * Abstract base implementation for wrapped QueryDSL clause providers.
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
public abstract class AbstractWrappedClauseProvider
implements WrappedClauseProvider {

    /** The supported entity types. */
    private final @NotNull Set<Class<? extends EntityPath<?>>> supportedEntityTypes;
    /** The registered projections, by entity and type. */
    private final @NotNull Map<ProjectionKey, Expression<?>> projections;

    /**
     * Creates a new instance.
     * 
     * @param supportedEntityTypes The supported entity types
     */
    @SafeVarargs
    protected AbstractWrappedClauseProvider(
            final @NotNull Class<? extends EntityPath<?>>... supportedEntityTypes) {
        this(new HashSet<>(Arrays.asList(Validate.notNull(supportedEntityTypes))));
    }

    /**
     * Creates a new instance.
     * 
     * @param supportedEntityTypes The supported entity types
     */
    protected AbstractWrappedClauseProvider(
            final @NotNull Set<Class<? extends EntityPath<?>>> supportedEntityTypes) {
        super();
        this.supportedEntityTypes = Validate.notNull(supportedEntityTypes);
        Validate.noNullElements(supportedEntityTypes);
        Validate.isTrue(!supportedEntityTypes.isEmpty());
        this.projections = new HashMap<>();
    }

    /**
     * Returns the supported entity types.
     * 
     * @return The supported entity types
     */
    protected @NotNull Set<Class<? extends EntityPath<?>>> getSupportedEntityTypes() {
        return Collections.unmodifiableSet(this.supportedEntityTypes);
    }

    protected abstract <T extends EntityPath<?>> T getBaseEntity(
            @NotNull Class<T> entityType);

    /**
     * Returns the registered projections, by entity and type.
     * 
     * @return The registered projections, by entity and type
     */
    protected @NotNull Map<ProjectionKey, Expression<?>> getProjections() {
        return Collections.unmodifiableMap(this.projections);
    }

    /**
     * Returns the transformer to apply to the expressions added to the
     * wrapped clauses before adding them to the delegated clauses.
     * 
     * @param entity The target entity
     * @return The expression transformer to apply
     */
    protected abstract ExpressionTransformer getTransformer(
            final @NotNull EntityPath<?> entity);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(
            final @NotNull Class<? extends EntityPath<?>> entityType) {
        return this.supportedEntityTypes.contains(entityType);
    }

    /**
     * Implementation of {@code WrappedQueryClauseProvider.getProjection()}.
     * <p>
     * Note that {@code AbstractWrappedClauseProvider} does not
     * inherit {@code WrappedQueryClauseProvider}.
     * Implementations must declare which provider subtypes exposes inheriting
     * appropriate interfaces.
     * 
     * @param <T> The entity type of the returned expression
     * @param entity The target entity path
     * @param type The projection type
     * @return The entity projection expression
     * @see WrappedQueryClauseProvider#getProjection(EntityPath, ProjectionType)
     */
    @SuppressWarnings("unchecked")
    public <T> @NotNull Expression<? extends T> getProjection(
            final @NotNull EntityPath<T> entity,
            final @NotNull ProjectionType projectionType) {
        validateSupported(entity);
        final Class<? extends EntityPath<T>> entityType =
                WrappedClauseProvider.getEntityPathType(entity);
        final ProjectionKey key = new ProjectionKey(entityType, projectionType);
        final Expression<? extends T> result;
        if (this.projections.containsKey(key)) {
            result = (Expression<? extends T>) this.projections.get(key);
        } else {
            result = (Expression<? extends T>) this.projections.get(
                    new ProjectionKey(entityType, ProjectionType.Basic.DEFAULT));
        }
        Validate.validState(result != null,
                "No default projection registered for entities of type %s",
                entityType);
        return applyAlias(entity, result);
    }

    /**
     * Registers the projection for the specified entity path and projection
     * type.
     * 
     * @param <T> The entity type
     * @param entityType The entity path type
     * @param projectionType The projection type
     * @param projection The projection expression
     */
    protected <T> void setProjection(
            final @NotNull Class<? extends EntityPath<T>> entityType,
            final @NotNull ProjectionType projectionType,
            final @NotNull Expression<? extends T> projection) {
        Validate.notNull(entityType);
        Validate.notNull(projectionType);
        Validate.notNull(projection);
        this.projections.put(
                new ProjectionKey(entityType, projectionType),
                projection);
    }

    /**
     * Creates the visitor to remove the alias of the entities of the
     * expressions incoming to the wrapped clauses.
     * <p>
     * This visitor is applied before the expression transformer.
     * 
     * @param entity The entity to which alias remove
     * @return The entity alias remover visitor
     */
    protected @NotNull Visitor<Expression<?>, ?> createAliasRemover(
            final @NotNull EntityPath<?> entity) {
        final Visitor<Expression<?>, ?> result;
        final EntityPath<?> base = getBaseEntity(entity.getClass());
        if (entity.getMetadata().getName().equals(
                base.getMetadata().getName())) {
            result = NopReplaceVisitor.INSTANCE;
        } else {
            result = new EntityAliasReplacer(entity, base);
        }
        return result;
    }

    /**
     * Creates the visitor to set/restore the alias of the entities of the
     * expressions to be applied to the delegated clauses.
     * <p>
     * This visitor is applied after the expression transformer end to the
     * projections returned by {@code getProjection()}.
     * 
     * @param entity The entity to which alias restore
     * @return The entity alias restorer visitor
     */
    protected @NotNull Visitor<Expression<?>, ?> createAliasRestorer(
            final @NotNull EntityPath<?> entity) {
        final Visitor<Expression<?>, ?> result;
        final EntityPath<?> base = getBaseEntity(entity.getClass());
        if (entity.getMetadata().getName().equals(
                base.getMetadata().getName())) {
            result = NopReplaceVisitor.INSTANCE;
        } else {
            result = new EntityAliasReplacer(base, entity);
        }
        return result;
    }

    protected @NotNull ExpressionTransformer createTransformerForAlias(
            final @NotNull EntityPath<?> entity) {
        final EntityPath<?> base = getBaseEntity(entity.getClass());
        if (entity.getMetadata().getName().equals(
                base.getMetadata().getName())) {
            return getTransformer(entity);
        } else {
            return new ChainedExpressionTransformer(
                    createAliasRemover(entity),
                    getTransformer(entity),
                    createAliasRestorer(entity));
        }
    }

    /**
     * Applies the required transformation to the specified expression
     * to apply the alias of the target entity to the base entity of
     * 
     * @param <T> The type of the expression
     * @param alias The (potentially) aliased entity
     * @param expr The expression to transform
     * @return The expression with the alias applied
     */
    @SuppressWarnings("unchecked")
    protected <T> Expression<T> applyAlias(
            final @NotNull EntityPath<?> alias,
            final Expression<T> expr) {
        Validate.notNull(alias);
        if (expr == null) {
            return null;
        } else {
            return (Expression<T>) expr.accept(
                    createAliasRestorer(alias),
                    null);
        }
    }

    /**
     * Implementation of {@code WrappedInsertClauseProvider.insert()}.
     * <p>
     * Note that {@code AbstractWrappedClauseProvider} does not
     * inherit {@code WrappedInsertClauseProvider}.
     * Implementations must declare which provider subtypes exposes inheriting
     * appropriate interfaces.
     * 
     * @param entity The target entity path
     * @return The created wrapped Insert clause
     * @see WrappedInsertClauseProvider#insert(EntityPath)
     */
    public @NotNull ExtendedInsertClause<?> insert(
            final @NotNull EntityPath<?> entity) {
        validateSupported(entity);
        return new WrappedInsertClause(
                createDelegatedInsertClause(entity),
                createTransformerForAlias(entity));
    }

    /**
     * Creates the delegated Insert clause for the specified entity.
     * 
     * @param <T> The target entity type
     * @param entity The target entity path
     * @return The created Insert clause
     */
    protected abstract <T> @NotNull InsertClause<?> createDelegatedInsertClause(
            final @NotNull EntityPath<T> entity);

    /**
     * Implementation of {@code WrappedUpdateClauseProvider.update()}.
     * <p>
     * Note that {@code AbstractWrappedClauseProvider} does not
     * inherit {@code WrappedUpdateClauseProvider}.
     * Implementations must declare which provider subtypes exposes inheriting
     * appropriate interfaces.
     * 
     * @param entity The target entity path
     * @return The created wrapped Update clause
     * @see WrappedUpdateClauseProvider#update(EntityPath)
     */
    public @NotNull ExtendedUpdateClause<?> update(
            final @NotNull EntityPath<?> entity) {
        validateSupported(entity);
        return new WrappedUpdateClause(
                createDelegatedUpdateClause(entity),
                createTransformerForAlias(entity));
    }

    /**
     * Creates the delegated Update clause for the specified entity.
     * 
     * @param <T> The target entity type
     * @param entity The target entity path
     * @return The created Update clause
     */
    protected abstract <T> @NotNull UpdateClause<?> createDelegatedUpdateClause(
            final @NotNull EntityPath<T> entity);

    /**
     * Implementation of {@code WrappedDeleteClauseProvider.delete()}.
     * <p>
     * Note that {@code AbstractWrappedClauseProvider} does not
     * inherit {@code WrappedDeleteClauseProvider}.
     * Implementations must declare which provider subtypes exposes inheriting
     * appropriate interfaces.
     * 
     * @param entity The target entity path
     * @return The created wrapped Delete clause
     * @see WrappedDeleteClauseProvider#delete(EntityPath)
     */
    public @NotNull DeleteClause<?> delete(
            final @NotNull EntityPath<?> entity) {
        validateSupported(entity);
        return new WrappedDeleteClause(
                createDelegatedDeleteClause(entity),
                createTransformerForAlias(entity));
    }

    /**
     * Creates the delegated Delete clause for the specified entity.
     * 
     * @param <T> The target entity type
     * @param entity The target entity path
     * @return The created Delete clause
     */
    protected abstract <T> @NotNull DeleteClause<?> createDelegatedDeleteClause(
            final @NotNull EntityPath<T> entity);

    /**
     * Immutable container bean for registered projections keys.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2022-08
     * @since AbstractWrappedClauseProvider 1.0
     */
    protected static class ProjectionKey
    implements Serializable {

        /** The serial version UID. */
        private static final long serialVersionUID = 1L;

        /** The entity type. */
        private final @NotNull Class<? extends EntityPath<?>> entityType;
        /** The projection type code. */
        private final @NotNull String projectionType;

        /**
         * Creates a new instance.
         * 
         * @param entityType The entity type
         * @param projectionType The projection type
         */
        public ProjectionKey(
                final @NotNull Class<? extends EntityPath<?>> entityType,
                final @NotNull ProjectionType projectionType) {
            super();
            this.entityType = Validate.notNull(entityType);
            this.projectionType = Validate.notNull(projectionType).getCode();
        }

        /**
         * Returns the entity type.
         * 
         * @return The entity type
         */
        public Class<? extends EntityPath<?>> getEntityType() {
            return this.entityType;
        }

        /**
         * Returns the projection type code.
         * 
         * @return The projection type code
         */
        public String getProjectionType() {
            return this.projectionType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(this.entityType)
                    .append(this.projectionType)
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(
                final Object obj) {
            if (!(obj instanceof ProjectionKey)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            final ProjectionKey other = (ProjectionKey) obj;
            return new EqualsBuilder()
                    .append(this.entityType, other.entityType)
                    .append(this.projectionType, other.projectionType)
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
