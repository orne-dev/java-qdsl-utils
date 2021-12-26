package dev.orne.qdsl;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021 Orne Developments
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

import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.ProjectionRole;

/**
 * Translator of QueryDSL entity values and expressions of QueryDSL
 * unsupported types to supported types.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @param <S> The source entity path type
 * @param <I> The target identity path type
 * @since 0.1
 */
public class EntityPathTranslator<S, I>
extends ReplaceVisitor<Void>
implements ValueAssignmentReplaceVisitor<Void> {

    /** The source path. */
    private final @NotNull EntityPath<S> source;
    /** The source path type. */
    private final @NotNull Class<? extends S> sourceType;
    /** The target expression. */
    private final @NotNull Path<I> target;
    /** The projection expression. */
    private final @NotNull Expression<S> projection;
    /** The value assignment translator. */
    private final @NotNull AssignmentTranslator<S> assignmentTranslator;

    /**
     * Creates a new instance.
     * 
     * @param source The source path
     * @param target The target expression
     * @param assignmentTranslator The value assignment translator
     */
    protected EntityPathTranslator(
            final @NotNull EntityPath<S> source,
            final @NotNull Path<I> target,
            final @NotNull Expression<S> projection,
            final @NotNull AssignmentTranslator<S> assignmentTranslator) {
        super();
        this.source = Validate.notNull(source);
        this.sourceType = source.getType();
        this.target = Validate.notNull(target);
        this.projection = Validate.notNull(projection);
        this.assignmentTranslator = Validate.notNull(assignmentTranslator);
    }

    /**
     * Builder constructor.
     * 
     * @param builder The builder
     */
    protected EntityPathTranslator(
            final @NotNull Builder<S, I> builder) {
        super();
        Validate.notNull(builder);
        this.source = builder.source;
        this.sourceType = this.source.getType();
        this.target = builder.target;
        this.projection = builder.projection;
        this.assignmentTranslator = builder.assignmentTranslator;
    }

    public static <V> TargetBuilder<V> fromPath(
            final @NotNull EntityPath<V> source) {
        return new InitialBuilder<>(source);
    }

    /**
     * Returns the source path.
     * 
     * @return The source path
     */
    public @NotNull EntityPath<S> getSource() {
        return this.source;
    }

    /**
     * Returns the source path type.
     * 
     * @return The source path type
     */
    public @NotNull Class<? extends S> getSourceType() {
        return this.sourceType;
    }

    /**
     * Returns the target expression.
     * 
     * @return The target expression
     */
    public @NotNull Path<I> getTarget() {
        return this.target;
    }

    /**
     * Returns the projection expression.
     * 
     * @return The projection expression
     */
    public Expression<S> getProjection() {
        return this.projection;
    }

    /**
     * Checks if the expression is the source path.
     * 
     * @param expr The expression to check
     * @return If the expression is the source path
     */
    protected boolean isSource(
            final @NotNull Expression<?> expr) {
        return this.source.equals(expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Path<?> expr,
            final Void context) {
        if (isSource(expr))  {
            return this.target;
        } else {
            return super.visit(expr, context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssignments visit(
            final @NotNull ValueAssignment<?> vexpr,
            final Void context) {
        if (isSource(vexpr.getPath())) {
            @SuppressWarnings("unchecked")
            final Expression<S> value = (Expression<S>) vexpr.getValue();
            return this.assignmentTranslator.apply(value);
        } else {
            return ValueAssignments.of(vexpr);
        }
    }

    /**
     * Builder for single path translators that require target expression.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-09
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface TargetBuilder<S> {

        /**
         * Set the target identity path.
         * 
         * @param target The target identity path
         * @return The next step builder
         */
        @NotNull <I> ProjectionBuilder<S, I> toIdentity(
                @NotNull Path<I> target);
    }

    /**
     * Builder for single path translators that require a projection expression.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-09
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface ProjectionBuilder<S, I> {

        /**
         * Set a factory expression as projection.
         * 
         * @param projection The factory expression
         * @return The next step builder
         */
        @NotNull StoreBuilder<S, I> withProjection(
                @NotNull FactoryExpression<S> projection);

        /**
         * Set a projection as projection.
         * 
         * @param projection The factory expression
         * @return The next step builder
         */
        @NotNull StoreBuilder<S, I> withProjection(
                @NotNull ProjectionRole<S> projection);
    }

    /**
     * Builder for single path translators that require target paths.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-09
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface StoreBuilder<S, I> {

        /**
         * Set a simple value assignment translation.
         * 
         * @param target The target path
         * @param exprTranslator The value expression translator
         * @param valueTranslator The value translator
         * @return The next step builder
         */
        @NotNull FinalBuilder<S, I> storingAs(
                @NotNull ExpressionTranslator<S, I> exprTranslator,
                @NotNull ValueTranslator<S, I> valueTranslator);
    }

    /**
     * Terminal builder for single path translators.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source path type
     * @since 0.1
     */
    public static interface FinalBuilder<S, I> {

        /**
         * Builds the path translator.
         * 
         * @return The created path translator
         */
        @NotNull EntityPathTranslator<S, I> build();
    }

    /**
     * Internal implementation of builder partially typed interfaces.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source entity path type
     * @since 0.1
     */
    protected static class InitialBuilder<S>
    implements TargetBuilder<S> {

        protected final @NotNull EntityPath<S> source;

        /**
         * Initial constructor.
         * 
         * @param source The source entity path
         */
        protected InitialBuilder(
                final @NotNull EntityPath<S> source) {
            super();
            this.source = source;
        }

        @Override
        public <I> @NotNull ProjectionBuilder<S, I> toIdentity(
                final @NotNull Path<I> target) {
            return new Builder<>(this.source, target);
        }
    }

    /**
     * Internal implementation of builder interfaces.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-09
     * @param <S> The source path entity type
     * @param <I> The target identity path type
     * @since 0.1
     */
    protected static class Builder<S, I>
    implements ProjectionBuilder<S, I>, StoreBuilder<S, I>, FinalBuilder<S, I> {

        /** The source path. */
        private final @NotNull EntityPath<S> source;
        /** The target expression. */
        private Path<I> target;
        /** The value assignment translator. */
        private AssignmentTranslator<S> assignmentTranslator;
        /** The projection expression. */
        private Expression<S> projection;

        /**
         * Initial constructor.
         * 
         * @param source The source path
         * @param identity The identity path
         */
        protected Builder(
                final @NotNull EntityPath<S> source,
                final @NotNull Path<I> identity) {
            super();
            this.source = Validate.notNull(source);
            setTarget(identity);
        }

        /**
         * Returns the source path.
         * 
         * @return The source path
         */
        protected EntityPath<S> getSource() {
            return this.source;
        }

        /**
         * Returns the target expression.
         * 
         * @return The target expression
         */
        protected Path<I> getTarget() {
            return this.target;
        }

        /**
         * Sets the target expression.
         * 
         * @param target The target expression
         */
        protected void setTarget(
                final @NotNull Path<I> target) {
            this.target = target;
        }

        /**
         * Returns the value assignment translator.
         * 
         * @return The value assignment translator
         */
        protected AssignmentTranslator<S> getAssignmentTranslator() {
            return this.assignmentTranslator;
        }

        /**
         * Sets the value assignment translator.
         * 
         * @param translator The value assignment translator
         */
        protected void setAssignmentTranslator(
                final @NotNull AssignmentTranslator<S> translator) {
            this.assignmentTranslator = translator;
        }

        /**
         * Returns the projection expression.
         * 
         * @return The projection expression
         */
        protected Expression<S> getProjection() {
            return this.projection;
        }

        /**
         * Sets the projection expression.
         * 
         * @param projection The projection expression
         */
        protected void setProjection(
                final @NotNull Expression<S> projection) {
            this.projection = projection;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull StoreBuilder<S, I> withProjection(
                final @NotNull FactoryExpression<S> projection) {
            setProjection(projection);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull StoreBuilder<S, I> withProjection(
                final @NotNull ProjectionRole<S> projection) {
            setProjection(projection.getProjection());
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull FinalBuilder<S, I> storingAs(
                final @NotNull ExpressionTranslator<S, I> exprTranslator,
                final @NotNull ValueTranslator<S, I> valueTranslator) {
            setAssignmentTranslator(AssignmentTranslator.forPath(
                    this.target,
                    valueTranslator,
                    exprTranslator));
            return this;
        }

        /**
         * {@index}
         */
        @Override
        public @NotNull EntityPathTranslator<S, I> build() {
            return new EntityPathTranslator<>(this);
        }
    }
}
