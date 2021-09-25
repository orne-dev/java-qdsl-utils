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

import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Functional interface for assigned value translation methods.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-08
 * @param <S> The source value type
 * @since 0.1
 */
@FunctionalInterface
public interface AssigmentTranslator<S>
extends Function<Expression<S>, ValueAssigment<?>[]> {

    /**
     * Creates a new identity assigned value translator that assigns the passed
     * value to the specified property path.
     * 
     * @param <T> The value type
     * @param target The target property path
     * @return The created assigned value translator
     */
    static <T> AssigmentTranslator<T> identity(
            final @NotNull Path<T> target) {
        Validate.notNull(target);
        return e -> ValueAssigment.array(ValueAssigment.of(target, e));
    }

    /**
     * 
     * Creates a new single value assignment for the specified property path
     * converting the assigned value to the target type.
     * <p>
     * If the assigned value is a constant the {@code valueTranslator} is used
     * to convert the value. Otherwise the {@code expressionTranslator} is used.
     * <p>
     * This method is a utility method used by
     * {@link #forPath(Path, ValueTranslator, ExpressionTranslator)} to
     * translate a value assignment to a single value assignment.
     * <p>
     * Translators for multiple assignments can be implemented easily based on
     * this method:
     * <pre>
     * AssigmentTranslator{@literal<}S{@literal>} translator = e -> ValueAssigment.array(
     *             toSingleValue(pathA, valueTransA, exprTransA, e),
     *             toSingleValue(pathB, valueTransB, exprTransB, e)
     *         );
     * </pre>
     * 
     * @param <S> The source value type
     * @param <T> The target value type
     * @param target The target property path
     * @param valueTranslator The value translator
     * @param expressionTranslator The expression translator
     * @param valueExpr The assigned value expression
     * @return The value assignment
     */
    static <S, T> ValueAssigment<?> toSingleValue(
            final @NotNull Path<T> target,
            final @NotNull ValueTranslator<S, ? extends T> valueTranslator,
            final @NotNull ExpressionTranslator<S, ? extends T> expressionTranslator,
            final @NotNull Expression<S> valueExpr) {
        Validate.notNull(target);
        Validate.notNull(valueTranslator);
        Validate.notNull(expressionTranslator);
        final Expression<? extends T> newValue;
        if (valueExpr instanceof Constant) {
            final S value = valueExpr.getType().cast(
                    ((Constant<?>) valueExpr).getConstant());
            newValue = Expressions.constant(valueTranslator.apply(value));
        } else {
            newValue = expressionTranslator.apply(valueExpr);
        }
        return ValueAssigment.of(target, newValue);
    }

    /**
     * Creates a new assigned value translator that converts the passed
     * value to a single value to be assigned to the specified property path.
     * 
     * @param <S> The source value type
     * @param <T> The target value type
     * @param target The target property path
     * @param valueTranslator The value translator
     * @param expressionTranslator The expression translator
     * @return The created assigned value translator
     */
    static <S, T> AssigmentTranslator<S> forPath(
            final @NotNull Path<T> target,
            final @NotNull ValueTranslator<S, ? extends T> valueTranslator,
            final @NotNull ExpressionTranslator<S, ? extends T> expressionTranslator) {
        Validate.notNull(target);
        Validate.notNull(valueTranslator);
        Validate.notNull(expressionTranslator);
        return e -> ValueAssigment.array(toSingleValue(
                target,
                valueTranslator,
                expressionTranslator,
                e));
    }
}
