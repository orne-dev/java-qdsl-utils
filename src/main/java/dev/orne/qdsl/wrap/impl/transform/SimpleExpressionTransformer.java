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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Functional interface for expression transformation methods.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @param <S> The source value type
 * @param <T> The target value type
 * @since 0.1
 */
@FunctionalInterface
public interface SimpleExpressionTransformer<S, T>
extends Function<Expression<S>, Expression<T>> {

    /** Expression translator from {@code String} to {@code Byte}. */
    SimpleExpressionTransformer<String, Byte> STR_TO_BYTE = stringToNumber(Byte.class);
    /** Expression translator from {@code String} to {@code Short}. */
    SimpleExpressionTransformer<String, Short> STR_TO_SHORT = stringToNumber(Short.class);
    /** Expression translator from {@code String} to {@code Integer}. */
    SimpleExpressionTransformer<String, Integer> STR_TO_INT = stringToNumber(Integer.class);
    /** Expression translator from {@code String} to {@code Long}. */
    SimpleExpressionTransformer<String, Long> STR_TO_LONG = stringToNumber(Long.class);
    /** Expression translator from {@code String} to {@code Float}. */
    SimpleExpressionTransformer<String, Float> STR_TO_FLOAT = stringToNumber(Float.class);
    /** Expression translator from {@code String} to {@code Double}. */
    SimpleExpressionTransformer<String, Double> STR_TO_DOUBLE = stringToNumber(Double.class);
    /** Expression translator from {@code String} to {@code BigInteger}. */
    SimpleExpressionTransformer<String, BigInteger> STR_TO_BIG_INT = stringToNumber(BigInteger.class);
    /** Expression translator from {@code String} to {@code BigDecimal}. */
    SimpleExpressionTransformer<String, BigDecimal> STR_TO_BIG_DEC = stringToNumber(BigDecimal.class);
    /** Expression translator from {@code Boolean} to {@code String}. */
    SimpleExpressionTransformer<Byte, String> BYTE_TO_STR = numberToString(Byte.class);
    /** Expression translator from {@code Short} to {@code String}. */
    SimpleExpressionTransformer<Short, String> SHORT_TO_STR = numberToString(Short.class);
    /** Expression translator from {@code Integer} to {@code String}. */
    SimpleExpressionTransformer<Integer, String> INT_TO_STR = numberToString(Integer.class);
    /** Expression translator from {@code Long} to {@code String}. */
    SimpleExpressionTransformer<Long, String> LONG_TO_STR = numberToString(Long.class);
    /** Expression translator from {@code Float} to {@code String}. */
    SimpleExpressionTransformer<Float, String> FLOAT_TO_STR = numberToString(Float.class);
    /** Expression translator from {@code Double} to {@code String}. */
    SimpleExpressionTransformer<Double, String> DOUBLE_TO_STR = numberToString(Double.class);
    /** Expression translator from {@code BigInteger} to {@code String}. */
    SimpleExpressionTransformer<BigInteger, String> BIG_INT_TO_STR = numberToString(BigInteger.class);
    /** Expression translator from {@code BigDecimal} to {@code String}. */
    SimpleExpressionTransformer<BigDecimal, String> BIG_DEC_TO_STR = numberToString(BigDecimal.class);
    
    /** Expression translator from {@code String} to {@code Byte}. */
    SimpleExpressionTransformer<String, Boolean> STR_TO_BOOL = stringToBoolean("true");
    /** Expression translator from {@code Boolean} to {@code String}. */
    SimpleExpressionTransformer<Boolean, String> BOOL_TO_STR = booleanToString("true", "false");

    /**
     * Creates a new identity expression translator that returns the passed
     * expression.
     * 
     * @param <T> The value type
     * @return The identity expression translator
     */
    static <T> SimpleExpressionTransformer<T, T> identity() {
        return e -> e;
    }

    /**
     * Creates a new expression translator that translates a {@code String}
     * expression in a number expression of the given type.
     * 
     * @param <T> The target {@code Number} type
     * @param type The target {@code Number} type
     * @return The created expression translator
     */
    static <T extends Number & Comparable<? super T>> SimpleExpressionTransformer<String, T> stringToNumber(
            final @NotNull Class<T> type) {
        Validate.notNull(type);
        return e -> Expressions.asString(e).castToNum(type);
    }

    /**
     * Creates a new expression translator that translates a number of the
     * given type expression in a {@code String} expression.
     * 
     * @param <T> The source {@code Number} type
     * @param type The source {@code Number} type
     * @return The created expression translator
     */
    static <T extends Number & Comparable<? super T>> SimpleExpressionTransformer<T, String> numberToString(
            final @NotNull Class<T> type) {
        Validate.notNull(type);
        return e -> Expressions.asNumber(e).stringValue();
    }

    /**
     * Creates a new expression translator from {@code String} to
     * {@code Boolean} that uses the specified value as true value (case
     * insensitive).
     * 
     * @param trueValue The {@code String} representation of {@code true} values
     * @return The created expression translator
     */
    static SimpleExpressionTransformer<String, Boolean> stringToBoolean(
            final @NotNull String trueValue) {
        Validate.notNull(trueValue);
        return e -> Expressions.asString(e).equalsIgnoreCase(trueValue);
    }

    /**
     * Creates a new expression translator from {@code String} to
     * {@code Boolean} that uses the specified expression as true value
     * (case insensitive).
     * 
     * @param trueValue The {@code String} representation of {@code true} values
     * @return The created expression translator
     */
    static SimpleExpressionTransformer<String, Boolean> stringToBoolean(
            final @NotNull Expression<String> trueValue) {
        Validate.notNull(trueValue);
        return e -> Expressions.asString(e).equalsIgnoreCase(trueValue);
    }

    /**
     * Creates a new expression translator from {@code String} to
     * {@code Boolean} that uses the specified {@code String} values.
     * 
     * @param trueValue The {@code String} representation of {@code true} values
     * @param falseValue The {@code String} representation of {@code false} values
     * @return The created expression translator
     */
    static SimpleExpressionTransformer<Boolean, String> booleanToString(
            final @NotNull String trueValue,
            final @NotNull String falseValue) {
        Validate.notNull(trueValue);
        Validate.notNull(falseValue);
        return e -> Expressions.asBoolean(e).when(true).then(trueValue).otherwise(falseValue);
    }

    /**
     * Creates a new expression translator from {@code String} to
     * {@code Boolean} that uses the specified {@code String} expressions.
     * 
     * @param trueValue The {@code String} representation of {@code true} values
     * @param falseValue The {@code String} representation of {@code false} values
     * @return The created expression translator
     */
    static SimpleExpressionTransformer<Boolean, String> booleanToString(
            final @NotNull Expression<String> trueValue,
            final @NotNull Expression<String> falseValue) {
        Validate.notNull(trueValue);
        Validate.notNull(falseValue);
        return e -> Expressions.asBoolean(e).when(true).then(trueValue).otherwise(falseValue);
    }
}
