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

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.core.types.Visitor;

import dev.orne.qdsl.wrap.ReferenceProjectionImpl.BeanBuilder;
import dev.orne.qdsl.wrap.ReferenceProjectionImpl.Builder;
import dev.orne.qdsl.wrap.ReferenceProjectionImpl.QBeanBuilderImpl;
import dev.orne.qdsl.wrap.ReferenceProjectionImpl.ReferenceBuilderImpl;

/**
 * Entity factory expression that can be translated to entity reference
 * (primary key in relational model) when transforming predicates over
 * external entities (foreign keys in relational model).
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @param <T> The bean type
 * @param <I> The reference type
 * @since 0.1
 */
public interface ReferenceProjection<T, I>
extends Expression<T>, ProjectionRole<T> {

    /**
     * Returns the reference expression of this entity.
     * 
     * @return The reference expression of this entity
     */
    @NotNull Expression<I> getReference();

    /**
     * Returns the reference type.
     * 
     * @return The reference type
     */
    @NotNull Class<? extends I> getReferenceType();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    default <R, C> R accept(
            final @NotNull Visitor<R, C> visitor,
            final C context) {
        final R result;
        if (visitor instanceof ReferenceProjectionVisitor) {
            result = ((ReferenceProjectionVisitor<R, C>) visitor).visit(this, context);
        } else {
            R projection = getProjection().accept(visitor, context);
            R reference = getReference().accept(visitor, context);
            if (projection instanceof Expression &&
                    reference instanceof Expression) {
                final Expression<?> projResult = (Expression<?>) projection;
                final Expression<?> refResult = (Expression<?>) reference;
                if (getProjection().equals(projResult) &&
                        getReference().equals(refResult)) {
                    result = (R) this;
                } else {
                    result = (R) ReferenceProjectionImpl.of(
                            (Expression<?>) projection,
                            (Expression<?>) reference);
                }
            } else {
                result = projection;
            }
        }
        return result;
    }

    /**
     * Factory method for creation of projections of unknown types.
     * <p>
     * Allows chained writing
     * <pre>
     * Expression<?> projection;
     * Expression<?> reference;
     * ReferenceProjection<?, ?> result = ReferenceProjection.of(projection, reference);
     * </pre>
     * 
     * @param <T> The bean type
     * @param <T> The reference type
     * @param projection The bean projection expression
     * @param projection The reference expression
     * @return The created instance
     */
    public static <T, I> @NotNull ReferenceProjection<T, I> of(
            final @NotNull Expression<T> projection,
            final @NotNull Expression<I> reference) {
        return ReferenceProjectionImpl.of(
                Validate.notNull(projection),
                Validate.notNull(reference));
    }

    /**
     * Utility factory method for clearer syntax.
     * <p>
     * Allows chained writing
     * <pre>
     * ReferenceProjection.of(Projections.someMethod())
     *     .withSimpleReference(reference);
     * </pre>
     * instead of
     * <pre>
     * new ReferenceEntityProjection{@literal <}{@literal >}(
     *     Projections.someMethod(),
     *     reference);
     * </pre>
     * 
     * @param <T> The bean type
     * @param type The bean type
     * @return The created instance
     */
    public static <T> @NotNull Builder<T> of(
            final @NotNull Expression<T> projection) {
        return new ReferenceBuilderImpl<>(Validate.notNull(projection));
    }

    /**
     * Utility factory method for clearer syntax.
     * <p>
     * Allows chained writing
     * <pre>
     * ReferenceProjection.of(Type.class)
     *     // Bindings
     *     .withSimpleReference(reference);
     * </pre>
     * instead of
     * <pre>
     * new ReferenceEntityProjection{@literal <}{@literal >}(
     *     QBeanBuilder.of(type)
     *         // Bindings
     *         build(),
     *     reference);
     * </pre>
     * 
     * @param <T> The bean type
     * @param type The bean type
     * @return The created instance
     */
    public static <T> @NotNull BeanBuilder<T> of(
            final @NotNull Class<T> type) {
        return new QBeanBuilderImpl<>(Validate.notNull(type));
    }
}
