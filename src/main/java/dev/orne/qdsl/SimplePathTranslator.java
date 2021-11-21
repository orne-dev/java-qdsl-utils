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

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;

/**
 * Visitor that translates references to the source path with references to
 * the target path of the same type.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-10
 * @since 0.1
 */
public class SimplePathTranslator<S>
extends AbstractPathTranslator<S, S> {

    /**
     * Builder constructor.
     * 
     * @param builder The builder
     */
    protected SimplePathTranslator(
            final @NotNull Builder<S> builder) {
        super(builder);
    }

    public static <V> TargetBuilder<V> fromPath(
            final @NotNull Path<V> source) {
        return new Builder<>(source);
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
        @NotNull FinalBuilder<S> toPath(
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
         * @param target The target path
         * @param translator The value expression translator
         * @return The next step builder
         */
        @NotNull <T> FinalBuilder<S> storingTo(
                @NotNull Path<T> target,
                @NotNull ExpressionTranslator<S, T> translator);
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
        @NotNull SimplePathTranslator<S> build();
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
    extends AbstractPathTranslator.Builder<S, S>
    implements TargetBuilder<S>, StoreBuilder<S>, FinalBuilder<S> {

        /**
         * Creates a new instance.
         * 
         * @param source The source path
         */
        public Builder(
                final @NotNull Path<S> source) {
            super(source);
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
            setAssignmentTranslator(AssignmentTranslator.identity(target));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> @NotNull Builder<S> storingTo(
                final @NotNull Path<T> target,
                final @NotNull ExpressionTranslator<S, T> translator) {
            setAssignmentTranslator(AssignmentTranslator.forPath(target, translator));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull SimplePathTranslator<S> build() {
            return new SimplePathTranslator<>(this);
        }
    }
}
