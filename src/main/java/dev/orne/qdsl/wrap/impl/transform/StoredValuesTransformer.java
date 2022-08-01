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

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;

import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValuesVisitor;

/**
 * Functional interface for stored values transformation methods.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <S> The source value type
 * @since 0.1
 */
@FunctionalInterface
public interface StoredValuesTransformer
extends StoredValuesVisitor<Void, Void> {

    /**
     * Apply the specified expression transformation to the value assigned
     * to the specified path.
     * 
     * @param <S> The path type
     * @param path The affected path
     * @param transformation The value transformation to apply
     * @return A lambda that implements {@code ValueAssignmentsTranslator}
     */
    static <S> StoredValuesTransformer transform(
            final @NotNull Path<S> path,
            final @NotNull SimpleExpressionTransformer<S, S> transformation) {
        Validate.notNull(path, "The path cannot be null");
        Validate.notNull(transformation, "The transformation cannot be null");
        return (assignments, context) -> {
            if (assignments.contains(path)) {
                final Expression<S> value = assignments.get(path);
                assignments.add(path, transformation.apply(value));
            }
            return null;
        };
    }

    /**
     * Apply the specified expression transformation to the value assigned
     * to the specified path.
     * 
     * @param <S> The path type
     * @param path The affected path
     * @param transformation The value transformation to apply
     * @return A lambda that implements {@code ValueAssignmentsTranslator}
     */
    static <S> StoredValuesTransformer transform(
            final @NotNull Path<S> path,
            final @NotNull ValueTransformer<S, S> valueTransformer,
            final @NotNull SimpleExpressionTransformer<S, S> exprTransformer) {
        Validate.notNull(path, "The path cannot be null");
        Validate.notNull(valueTransformer, "The value transformer cannot be null");
        Validate.notNull(exprTransformer, "The expression transformer cannot be null");
        return transform(path, createValueTransformation(valueTransformer, exprTransformer));
    }

    /**
     * If the values to be updated contains the source path assigns the same
     * value to the target path.
     * 
     * @param <T> The value type
     * @param source The source property path
     * @param target The target property path
     * @return The created assigned values transformer
     */
    static <T> StoredValuesTransformer move(
            final @NotNull Path<T> source,
            final @NotNull Path<T> target) {
        Validate.notNull(source);
        Validate.notNull(target);
        return (assignments, context) -> {
            if (assignments.contains(source)) {
                assignments.add(target, assignments.remove(source));
            }
            return null;
        };
    }

    /**
     * Apply the specified expression transformation to the value assigned
     * to the specified path.
     * 
     * @param <S> The path type
     * @param path The affected path
     * @param transformation The value transformation to apply
     * @return A lambda that implements {@code ValueAssignmentsTranslator}
     */
    static <S, T> StoredValuesTransformer replace(
            final @NotNull Path<S> source,
            final @NotNull Path<T> target,
            final @NotNull SimpleExpressionTransformer<S, T> transformation) {
        Validate.notNull(source, "The source path cannot be null");
        Validate.notNull(target, "The target path cannot be null");
        Validate.notNull(transformation, "The transformation cannot be null");
        return (assignments, context) -> {
            if (assignments.contains(source)) {
                final Expression<S> value = assignments.remove(source);
                assignments.add(target, transformation.apply(value));
            }
            return null;
        };
    }

    /**
     * Apply the specified expression transformation to the value assigned
     * to the specified path.
     * 
     * @param <S> The path type
     * @param path The affected path
     * @param transformation The value transformation to apply
     * @return A lambda that implements {@code ValueAssignmentsTranslator}
     */
    static <S, T> StoredValuesTransformer replace(
            final @NotNull Path<S> source,
            final @NotNull Path<T> target,
            final @NotNull ValueTransformer<S, T> valueTransformer,
            final @NotNull SimpleExpressionTransformer<S, T> exprTransformer) {
        Validate.notNull(source, "The source path cannot be null");
        Validate.notNull(target, "The target path cannot be null");
        Validate.notNull(valueTransformer, "The value transformer cannot be null");
        Validate.notNull(exprTransformer, "The expression transformer cannot be null");
        return replace(source, target, createValueTransformation(valueTransformer, exprTransformer));
    }

    static <S, T> @NotNull SimpleExpressionTransformer<S, T> createValueTransformation(
            final @NotNull ValueTransformer<S, T> valueTransformer,
            final @NotNull SimpleExpressionTransformer<S, T> exprTransformer) {
        return valueExpr -> {
            final Expression<T> newValue;
            if (valueExpr instanceof Constant) {
                final S value = valueExpr.getType().cast(
                        ((Constant<?>) valueExpr).getConstant());
                newValue = Expressions.constant(valueTransformer.apply(value));
            } else {
                final Expression<S> tvalue = valueExpr;
                newValue = exprTransformer.apply(tvalue);
            }
            return newValue;
        };
    }

    /**
     * Tries to translate the value assignment translating its target path and
     * value with the specified replace visitor.
     * 
     * @param from The source value assignment
     * @param visitor The replace visitor to use
     * @return The resulting value assignment
     * @throws IllegalArgumentException If the assigned value is not of a
     * compatible type
     */
    static StoredValue<?> translateFromComponents(
            final StoredValue<?> from,
            final Visitor<Expression<?>, ?> visitor) {
        final Expression<?> newTarget = from.getPath().accept(visitor, null);
        final Expression<?> newValue = from.getValue().accept(visitor, null);
        final StoredValue<?> result;
        if (newTarget == null) {
            result = null;
        } else if (from.getPath().equals(newTarget) &&
                Objects.equals(from.getValue(), newValue)) {
            result = from;
        } else if (newTarget instanceof Path) {
            return StoredValue.ofUntyped((Path<?>) newTarget, newValue);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Replacement expression for path %s is not a path: %s",
                    from.getPath(),
                    newTarget));
        }
        return result;
    }
}
