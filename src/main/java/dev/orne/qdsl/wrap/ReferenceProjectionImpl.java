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

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;

import dev.orne.qdsl.QBeanBuilder;

/**
 * Default implementation of {@code ReferenceProjection} that encapsulates the
 * bean projection expression and the reference expression.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @param <T> The bean type
 * @param <I> The reference type
 * @since 0.1
 */
public class ReferenceProjectionImpl<T, I>
implements ReferenceProjection<T, I> {

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The projection expression. */
    private final @NotNull Expression<T> projection;
    /** The reference expression. */
    private final @NotNull Expression<I> reference;

    /**
     * Creates a new instance.
     * 
     * @param delegated The projection expression
     * @param reference The reference expression
     */
    public ReferenceProjectionImpl(
            final @NotNull Expression<T> projection,
            final @NotNull Expression<I> reference) {
        super();
        this.projection = Validate.notNull(projection);
        this.reference = Validate.notNull(reference);
    }

    /**
     * Factory method for creation of projections of unknown types.
     * <p>
     * Allows chained writing
     * <pre>
     * Expression<?> projection;
     * Expression<?> reference;
     * BaseReferenceProjection<?, ?> result = BaseReferenceProjection.of(projection, reference);
     * </pre>
     * 
     * @param <T> The bean type
     * @param <T> The reference type
     * @param projection The bean projection expression
     * @param projection The reference expression
     * @return The created instance
     */
    public static <T, I> @NotNull ReferenceProjectionImpl<T, I> of(
            final @NotNull Expression<T> projection,
            final @NotNull Expression<I> reference) {
        return new ReferenceProjectionImpl<>(
                Validate.notNull(projection),
                Validate.notNull(reference));
    }

    /**
     * Utility factory method for clearer syntax.
     * <p>
     * Allows chained writing
     * <pre>
     * ReferenceEntityProjection.of(Projections.someMethod())
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
     * @param projection The bean projection expression
     * @return The created builder, for method chaining
     */
    public static <T> @NotNull Builder<T> of(
            final @NotNull Expression<T> projection) {
        return new ReferenceBuilderImpl<>(Validate.notNull(projection));
    }

    /**
     * Utility factory method for QBean based projections for clearer syntax.
     * <p>
     * Allows chained writing
     * <pre>
     * ReferenceEntityProjection.of(Type.class)
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
     * @return The created builder, for method chaining
     */
    public static <T> @NotNull BeanBuilder<T> of(
            final @NotNull Class<T> type) {
        return new QBeanBuilderImpl<>(Validate.notNull(type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<T> getProjection() {
        return this.projection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends T> getType() {
        return this.projection.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<I> getReference() {
        return this.reference;
    }

    /**
     * Final {@code ReferenceEntityProjection} builder interface.
     * <p>
     * Provides methods to specify the reference expression through
     * method chaining.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2022-09
     * @param <T> The bean type
     * @since ReferenceEntityProjection, 1.0
     */
    public interface Builder<T> {

        /**
         * Creates a new {@code ReferenceEntityProjection} with the configured
         * delegated factory expression and the specified reference expression.
         * 
         * @param <I> The reference expression type
         * @param reference The reference expression
         * @return The created {@code ReferenceEntityProjection}
         */
        <I> dev.orne.qdsl.wrap.ReferenceProjectionImpl<T, I> withSimpleReference(
                final @NotNull Expression<I> reference);

        /**
         * 
         * @param reference The expressions that compose the reference expression
         * @return The created {@code ReferenceEntityProjection}
         */
        @NotNull ReferenceProjectionImpl<T, Tuple> withComposedReference(
                final @NotNull Expression<?>... reference);
    }

    /**
     * Final {@code ReferenceEntityProjection} builder interface for
     * bean projections.
     * <p>
     * Provides methods to configure the bean projection and the reference
     * expression through method chaining.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2022-09
     * @param <T> The bean type
     * @since ReferenceEntityProjection, 1.0
     */
    public interface BeanBuilder<T>
    extends Builder<T> {

        /**
         * Sets if direct field access must be used.
         * 
         * @param fieldAccess {@code true} for field access and
         * {@code false} for property access
         * @return This instance for method chaining
         */
        @NotNull BeanBuilder<T> withFieldAccess(
                boolean fieldAccess);

        /**
         * Binds the specified property with the specified expression.
         * 
         * @param property The property to bind to
         * @param expr The binded expression
         * @return This instance for method chaining
         */
        @NotNull BeanBuilder<T> bind(
                @NotNull String property,
                @NotNull Expression<?> expr);
    }

    /**
     * Default implementation of {@code Builder}.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2022-09
     * @param <T> The bean type
     * @since ReferenceEntityProjection, 1.0
     */
    protected static class ReferenceBuilderImpl<T>
    implements Builder<T> {

        /** The projection expression. */
        private final @NotNull Expression<T> projection;

        /**
         * Creates a new instance.
         * 
         * @param type The bean type
         * @param reference The reference expression
         */
        public ReferenceBuilderImpl(
                final @NotNull Expression<T> projection) {
            super();
            this.projection = projection;
        }

        /**
         * Returns the bean type.
         * 
         * @return The bean type
         */
        public @NotNull Expression<T> getProjection() {
            return this.projection;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <I> dev.orne.qdsl.wrap.ReferenceProjectionImpl<T, I> withSimpleReference(
                final @NotNull Expression<I> reference) {
            return new ReferenceProjectionImpl<>(this.projection, reference);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull ReferenceProjectionImpl<T, Tuple> withComposedReference(
                final @NotNull Expression<?>... reference) {
            return new ReferenceProjectionImpl<>(
                    this.projection,
                    Projections.tuple(reference));
        }
    }

    /**
     * Default implementation of {@code BeanBuilder}.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2022-09
     * @param <T> The bean type
     * @since ReferenceEntityProjection, 1.0
     */
    protected static class QBeanBuilderImpl<T>
    implements BeanBuilder<T> {

        /** The delegated {@code QBean} builder. */
        private final @NotNull QBeanBuilder<T> delegated;

        /**
         * Creates a new instance.
         * 
         * @param type The bean type
         * @param reference The reference expression
         */
        public QBeanBuilderImpl(
                final @NotNull Class<T> type) {
            this(QBeanBuilder.of(type));
        }

        /**
         * Creates a new instance.
         * 
         * @param delegated The delegated {@code QBean} builder
         * @param reference The reference expression
         */
        protected QBeanBuilderImpl(
                final @NotNull QBeanBuilder<T> delegated) {
            super();
            this.delegated = delegated;
        }

        /**
         * Returns the delegated {@code QBean} builder.
         * 
         * @return The delegated {@code QBean} builder
         */
        protected @NotNull QBeanBuilder<T> getDelegated() {
            return this.delegated;
        }

        /**
         * {@inheritDoc}
         */
        public @NotNull QBeanBuilderImpl<T> withFieldAccess(
                final boolean fieldAccess) {
            this.delegated.withFieldAccess(fieldAccess);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public @NotNull QBeanBuilderImpl<T> bind(
                final @NotNull String property,
                final @NotNull Expression<?> expr) {
            this.delegated.bind(property, expr);
           return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <I> dev.orne.qdsl.wrap.ReferenceProjectionImpl<T, I> withSimpleReference(
                final @NotNull Expression<I> reference) {
            return new ReferenceProjectionImpl<>(
                    this.delegated.build(),
                    reference);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull ReferenceProjectionImpl<T, Tuple> withComposedReference(
                final @NotNull Expression<?>... reference) {
            return new ReferenceProjectionImpl<>(
                    this.delegated.build(),
                    Projections.tuple(reference));
        }
    }
}
