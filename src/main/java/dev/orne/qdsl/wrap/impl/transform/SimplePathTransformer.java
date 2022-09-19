package dev.orne.qdsl.wrap.impl.transform;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021 - 2022 Orne Developments
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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;

import dev.orne.qdsl.wrap.StoredValues;

/**
 * Visitor that replaces references to the source path with references to
 * the target expression.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-10
 * @param <S> The source path type
 * @since 0.1
 */
public class SimplePathTransformer<S>
extends BaseExpressionTransformer<Void> {

    /** The source path. */
    private final @NotNull Path<S> source;
    /** The target expression. */
    private final @NotNull Expression<S> target;
    /** The value assignment translator. */
    private final StoredValuesTransformer assignmentTranslator;

    /**
     * Creates a new instance.
     * 
     * @param source The source path
     * @param target The target expression
     * @param assignmentTranslator The value assignment translator
     */
    protected SimplePathTransformer(
            final @NotNull Path<S> source,
            final @NotNull Expression<S> target,
            final StoredValuesTransformer assignmentTranslator) {
        super();
        this.source = Validate.notNull(source);
        this.target = Validate.notNull(target);
        this.assignmentTranslator = Validate.notNull(assignmentTranslator);
    }

    /**
     * Builder constructor.
     * 
     * @param builder The builder
     */
    protected SimplePathTransformer(
            final @NotNull Builder<S> builder) {
        super();
        Validate.notNull(builder);
        this.source = builder.getSource();
        this.target = builder.getTarget();
        this.assignmentTranslator = builder.getAssignmentTranslator();
    }

    /**
     * Starts the construction of a new instance.
     * 
     * @param <V> The source path type
     * @param source The source path
     * @return The next step builder
     */
    public static <V> TargetBuilder<V> fromPath(
            final @NotNull Path<V> source) {
        return new Builder<>(source);
    }

    /**
     * Returns the source path.
     * 
     * @return The source path
     */
    public @NotNull Path<S> getSource() {
        return this.source;
    }

    /**
     * Returns the target expression.
     * 
     * @return The target expression
     */
    public @NotNull Expression<S> getTarget() {
        return this.target;
    }

    /**
     * Returns the value assignment translator.
     * 
     * @return The value assignment translator
     */
    public StoredValuesTransformer getAssignmentTranslator() {
        return this.assignmentTranslator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Path<?> expr,
            final Void context) {
        if (this.source.equals(expr))  {
            return this.target;
        } else {
            return super.visit(expr, context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull StoredValues visit(
            final @NotNull StoredValues expr,
            final Void context) {
        if (expr.contains(this.source)) {
            if (this.assignmentTranslator == null) {
                throw new UnsupportedOperationException(String.format(
                        "Assignments to path %s are not allowed",
                        this.source));
            }
            this.assignmentTranslator.visit(expr, null);
        }
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("source", this.source)
                .append("target", this.target)
                .build();
    }

    /**
     * Builder for single path translators that require target expression.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface TargetBuilder<S> {

        /**
         * Set the target expression.
         * 
         * @param target The target expression
         * @return The next step builder
         */
        @NotNull StoreBuilder<S> toExpression(
                @NotNull Expression<S> target);

        /**
         * Set the target path.
         * 
         * @param target The target path
         * @return The next step builder
         */
        @NotNull PathFinalBuilder<S> toPath(
                @NotNull Path<S> target);
    }

    /**
     * Builder for single path translators that require target paths.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface StoreBuilder<S> {

        /**
         * Set a simple value assignment translation.
         * 
         * @param <T> The target path type
         * @param target The target path
         * @param translator The value expression translator
         * @return The next step builder
         */
        @NotNull <T> FinalBuilder<S> storingTo(
                @NotNull Path<T> target,
                @NotNull SimpleExpressionTransformer<S, T> translator);

        /**
         * Set a value assignment translator.
         * 
         * @param translator The value assignment translator
         * @return The next step builder
         */
        @NotNull FinalBuilder<S> storingWith(
                @NotNull StoredValuesTransformer translator);

        /**
         * Disallows thw storage of values to the path.
         * 
         * @return The next step builder
         */
        @NotNull FinalBuilder<S> withDisallowedStorage();
    }

    /**
     * Terminal builder for single path translators.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface FinalBuilder<S> {

        /**
         * Builds the path translator.
         * 
         * @return The created path translator
         */
        @NotNull SimplePathTransformer<S> build();
    }

    /**
     * Terminal builder for single path translators.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface PathFinalBuilder<S>
    extends FinalBuilder<S> {

        /**
         * Disallows thw storage of values to the path.
         * 
         * @return The next step builder
         */
        @NotNull FinalBuilder<S> withDisallowedStorage();
    }

    /**
     * Internal implementation of builder interfaces.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source path type
     * @since 0.1
     */
    protected static class Builder<S>
    implements TargetBuilder<S>, StoreBuilder<S>, PathFinalBuilder<S> {

        /** The source path. */
        private final @NotNull Path<S> source;
        /** The target expression. */
        private Expression<S> target;
        /** The value assignment translator. */
        private StoredValuesTransformer assignmentTranslator;

        /**
         * Creates a new instance.
         * 
         * @param source The source path
         */
        protected Builder(
                final @NotNull Path<S> source) {
            super();
            this.source = Validate.notNull(source);
        }

        /**
         * Returns the source path.
         * 
         * @return The source path
         */
        protected Path<S> getSource() {
            return this.source;
        }

        /**
         * Returns the target expression.
         * 
         * @return The target expression
         */
        protected Expression<S> getTarget() {
            return this.target;
        }

        /**
         * Sets the target expression.
         * 
         * @param target The target expression
         */
        protected void setTarget(
                final @NotNull Expression<S> target) {
            this.target = target;
        }

        /**
         * Returns the value assignment translator.
         * 
         * @return The value assignment translator
         */
        protected StoredValuesTransformer getAssignmentTranslator() {
            return this.assignmentTranslator;
        }

        /**
         * Sets the value assignment translator.
         * 
         * @param translator The value assignment translator
         */
        protected void setAssignmentTranslator(
                final StoredValuesTransformer translator) {
            this.assignmentTranslator = translator;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull Builder<S> toExpression(
                final @NotNull Expression<S> target) {
            setTarget(target);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull Builder<S> toPath(
                final @NotNull Path<S> target) {
            setTarget(target);
            setAssignmentTranslator(StoredValuesTransformer.move(this.source, target));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> @NotNull Builder<S> storingTo(
                final @NotNull Path<T> target,
                final @NotNull SimpleExpressionTransformer<S, T> translator) {
            setAssignmentTranslator(StoredValuesTransformer.replace(this.source, target, translator));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull Builder<S> storingWith(
                final @NotNull StoredValuesTransformer translator) {
            setAssignmentTranslator(translator);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull Builder<S> withDisallowedStorage() {
            setAssignmentTranslator(null);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull SimplePathTransformer<S> build() {
            return new SimplePathTransformer<>(this);
        }
    }
}
