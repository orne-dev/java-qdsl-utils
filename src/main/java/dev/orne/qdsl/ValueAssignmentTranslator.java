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

import java.util.Objects;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Functional interface for assigned value translation methods.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-10
 * @param <S> The source value type
 * @since 0.1
 */
@FunctionalInterface
public interface ValueAssignmentTranslator<S>
extends Function<Expression<S>, ValueAssignments> {

    /**
     * Creates a new identity assigned value translator that assigns the passed
     * value to the specified property path.
     * 
     * @param <T> The value type
     * @param target The target property path
     * @return The created assigned value translator
     */
    static <T> ValueAssignmentTranslator<T> identity(
            final @NotNull Path<T> target) {
        Validate.notNull(target);
        return e -> ValueAssignments.of(ValueAssignment.of(target, e));
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
     * AssigmentTranslator{@literal <}S{@literal >} translator = e -{@literal >} ValueAssigment.array(
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
    static <S, T> ValueAssignment<?> toSingleValue(
            final @NotNull Path<T> target,
            final @NotNull ValueTranslator<S, T> valueTranslator,
            final @NotNull ExpressionTranslator<S, T> expressionTranslator,
            final @NotNull Expression<S> valueExpr) {
        Validate.notNull(target);
        Validate.notNull(valueTranslator);
        Validate.notNull(expressionTranslator);
        final Expression<T> newValue;
        if (valueExpr instanceof Constant) {
            final S value = valueExpr.getType().cast(
                    ((Constant<?>) valueExpr).getConstant());
            newValue = Expressions.constant(valueTranslator.apply(value));
        } else {
            newValue = expressionTranslator.apply(valueExpr);
        }
        return ValueAssignment.of(target, newValue);
    }

    /**
     * Creates a new assigned value translator that converts the passed
     * value to a single value to be assigned to the specified property path.
     * 
     * @param <S> The source value type
     * @param <T> The target value type
     * @param target The target property path
     * @param expressionTranslator The expression translator
     * @return The created assigned value translator
     */
    static <S, T> ValueAssignmentTranslator<S> forPath(
            final @NotNull Path<T> target,
            final @NotNull ExpressionTranslator<S, T> expressionTranslator) {
        Validate.notNull(target);
        Validate.notNull(expressionTranslator);
        return e -> ValueAssignments.of(ValueAssignment.of(
                target,
                expressionTranslator.apply(e)));
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
    static <S, T> ValueAssignmentTranslator<S> forPath(
            final @NotNull Path<T> target,
            final @NotNull ValueTranslator<S, T> valueTranslator,
            final @NotNull ExpressionTranslator<S, T> expressionTranslator) {
        Validate.notNull(target);
        Validate.notNull(valueTranslator);
        Validate.notNull(expressionTranslator);
        return e -> ValueAssignments.of(toSingleValue(
                target,
                valueTranslator,
                expressionTranslator,
                e));
    }

    /**
     * Convert the specified simple translator to a complete translator.
     * 
     * @param <S> The single assignment type
     * @param simple The single assignment translator
     * @return A lambda that implements {@code AssigmentTranslator}
     */
    static <S> ValueAssignmentTranslator<S> fromSimple(
            final @NotNull Simple<S> simple) {
        return e -> ValueAssignments.of(simple.apply(e));
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
    static ValueAssignment<?> translateFromComponents(
            final ValueAssignment<?> from,
            final Visitor<Expression<?>, ?> visitor) {
        final Expression<?> newTarget = from.getPath().accept(visitor, null);
        final Expression<?> newValue = from.getValue().accept(visitor, null);
        final ValueAssignment<?> result;
        if (newTarget == null) {
            result = null;
        } else if (from.getPath().equals(newTarget) &&
                Objects.equals(from.getValue(), newValue)) {
            result = from;
        } else if (newTarget instanceof Path) {
            return ValueAssignment.ofUntyped((Path<?>) newTarget, newValue);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Replacement expression for path %s is not a path: %s",
                    from.getPath(),
                    newTarget));
        }
        return result;
    }

    /**
     * Functional interface for assigned value translation methods that
     * translates to a single assignment.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-10
     * @param <S> The source value type
     * @since 0.1
     */
    @FunctionalInterface
    interface Simple<S>
    extends Function<Expression<S>, ValueAssignment<?>> {}
}
