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
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.ConstructorUtils;
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

    /** The base target entity. */
    private final @NotNull EntityPath<?> targetEntity;
    /** The base entities. */
    private final @NotNull Map<Class<? extends EntityPath<?>>, EntityPath<?>> baseEntities;
    /** The registered projections, by entity and type. */
    private final @NotNull Map<ProjectionKey, Expression<?>> projections;

    /**
     * Creates a new instance.
     * 
     * @param targetEntity The base target entity
     * @param supportedEntities The supported base entities
     */
    @SafeVarargs
    protected AbstractWrappedClauseProvider(
            final @NotNull EntityPath<?> targetEntity,
            final @NotNull EntityPath<?>... supportedEntities) {
        this(targetEntity, Arrays.asList(Validate.notNull(supportedEntities)));
    }

    /**
     * Creates a new instance.
     * 
     * @param targetEntity The base target entity
     * @param supportedEntities The supported base entities
     */
    protected AbstractWrappedClauseProvider(
            final @NotNull EntityPath<?> targetEntity,
            final @NotNull Collection<EntityPath<?>> supportedEntities) {
        super();
        this.targetEntity =  Validate.notNull(targetEntity);
        Validate.notNull(supportedEntities);
        Validate.noNullElements(supportedEntities);
        Validate.isTrue(!supportedEntities.isEmpty());
        this.baseEntities = new HashMap<>(supportedEntities.size());
        for (final EntityPath<?> baseEntity : supportedEntities) {
            @SuppressWarnings("unchecked")
            final Class<? extends EntityPath<?>> supportedEntityType =
                    (Class<? extends EntityPath<?>>) baseEntity.getClass();
            this.baseEntities.put(supportedEntityType, baseEntity);
        }
        this.projections = new HashMap<>();
    }

    /**
     * Returns the base target entity as returned by the expression
     * transformer and used in projections.
     * 
     * @return The base target entity
     */
    protected @NotNull EntityPath<?> getTargetEntity() {
        return this.targetEntity;
    }

    /**
     * Returns a new target entity with the same variable name as
     * the entity passed as argument.
     * <p>
     * Expects a constructor with a single {@code String} argument.
     * 
     * @param alias The desired entity alias
     * @return The aliased target entity
     */
    protected @NotNull EntityPath<?> getTargetEntity(
            final @NotNull EntityPath<?> alias) {
        return getTargetEntity(getEntityAlias(alias));
    }

    /**
     * Returns a new target entity with the specified variable name.
     * 
     * @param alias The desired entity alias
     * @return The aliased target entity
     */
    protected @NotNull EntityPath<?> getTargetEntity(
            final @NotNull String alias) {
        return createEntity(this.targetEntity.getClass(), alias);
    }

    /**
     * Returns the base (without alias) entities used in the expression transformer.
     * 
     * @return The base entities, by type
     */
    protected @NotNull Map<Class<? extends EntityPath<?>>, EntityPath<?>> getBaseEntities() {
        return this.baseEntities;
    }

    /**
     * Returns the base (without alias) entity used in the expression transformer.
     * 
     * @param <T> The type of entity
     * @param entityType The type of entity
     * @return The base entity
     */
    protected <T extends EntityPath<?>> @NotNull T getBaseEntity(
            final @NotNull Class<T> entityType) {
        validateSupported(entityType);
        return entityType.cast(this.baseEntities.get(entityType));
    }

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
        return this.baseEntities.containsKey(entityType);
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
     * projections returned by {@code getProjection()} and should replace
     * base target entity with aliased target entity.
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
            final EntityPath<?> targetBase = getTargetEntity();
            final EntityPath<?> targetAlias = getTargetEntity(entity);
            result = new EntityAliasReplacer(targetBase, targetAlias);
        }
        return result;
    }

    /**
     * Creates an expression transformer to apply to expressions passed to the
     * a wrapped clause over the specified entity.
     * 
     * @param entity The clause entity, potentially aliased
     * @return The expression transformer to apply
     */
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
     * Returns a new instance of the specified entity type with the specified
     * variable name.
     * <p>
     * Expects a constructor with a single {@code String} argument.
     * 
     * @param <T> The entity type
     * @param entityType The entity type
     * @param alias The desired entity alias
     * @return The aliased target entity
     */
    protected @NotNull <T extends EntityPath<?>> T createEntity(
            final @NotNull Class<T> entityType,
            final @NotNull String alias) {
        try {
            return ConstructorUtils.invokeConstructor(entityType, Validate.notNull(alias));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ExpressionTransformationException(
                    String.format("Error creating entity of type %s.", entityType),
                    e);
        }
    }

    /**
     * Returns the variable name of the specified entity.
     * 
     * @param entity The entity path
     * @return The entity path's variable name
     */
    protected @NotNull String getEntityAlias(
            final @NotNull EntityPath<?> entity) {
        return entity.getMetadata().getName();
    }

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
