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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang3.Validate;

/**
 * Functional interface for value translation methods.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @param <S> The source value type
 * @param <T> The target value type
 * @since 0.1
 */
@FunctionalInterface
public interface ValueTranslator<S, T>
extends Function<S, T> {

    /** Value translator from {@code String} to {@code Byte}. */
    ValueTranslator<String, Byte> STR_TO_BYTE = Byte::valueOf;
    /** Value translator from {@code String} to {@code Short}. */
    ValueTranslator<String, Short> STR_TO_SHORT = Short::valueOf;
    /** Value translator from {@code String} to {@code Integer}. */
    ValueTranslator<String, Integer> STR_TO_INT = Integer::valueOf;
    /** Value translator from {@code String} to {@code Long}. */
    ValueTranslator<String, Long> STR_TO_LONG = Long::valueOf;
    /** Value translator from {@code String} to {@code Float}. */
    ValueTranslator<String, Float> STR_TO_FLOAT = Float::valueOf;
    /** Value translator from {@code String} to {@code Double}. */
    ValueTranslator<String, Double> STR_TO_DOUBLE = Double::valueOf;
    /** Value translator from {@code String} to {@code BigInteger}. */
    ValueTranslator<String, BigInteger> STR_TO_BIG_INT = BigInteger::new;
    /** Value translator from {@code String} to {@code BigDecimal}. */
    ValueTranslator<String, BigDecimal> STR_TO_BIG_DEC = BigDecimal::new;
    /** Value translator from {@code Number} to {@code String}. */
    ValueTranslator<Number, String> NUMBER_TO_STR = Number::toString;
    /** Value translator from {@code String} to {@code Boolean}. */
    ValueTranslator<String, Boolean> STR_TO_BOOL = stringToBoolean("true");
    /** Value translator from {@code Boolean} to {@code String}. */
    ValueTranslator<Boolean, String> BOOL_TO_STR = booleanToString("true", "false");

    /**
     * Creates a new identity value translator that returns the passed value.
     * 
     * @param <T> The value type
     * @return The identity value translator
     */
    static <T> ValueTranslator<T, T> identity() {
        return e -> e;
    }

    /**
     * Creates a new value translator from {@code String} to {@code Boolean}
     * that uses the specified value as true value (case insensitive).
     * 
     * @param trueValue The {@code String} representation of {@code true} values
     * @return The created value translator
     */
    static ValueTranslator<String, Boolean> stringToBoolean(
            final @NotNull String trueValue) {
        Validate.notNull(trueValue);
        return e -> e.equalsIgnoreCase(trueValue);
    }

    /**
     * Creates a new value translator from {@code String} to {@code Boolean}
     * that uses the specified {@code String} values.
     * 
     * @param trueValue The {@code String} representation of {@code true} values
     * @param falseValue The {@code String} representation of {@code false} values
     * @return The created value translator
     */
    static ValueTranslator<Boolean, String> booleanToString(
            final @NotNull String trueValue,
            final @NotNull String falseValue) {
        Validate.notNull(trueValue);
        Validate.notNull(falseValue);
        return e -> e ? trueValue : falseValue;
    }

    /**
     * Creates a new value translator that uses Apache BeanUtils
     * shared {@code ConvertUtilsBean} instance to convert the values.
     * 
     * @param <S> The source value type
     * @param <T> The target value type
     * @param type The target value type
     * @return The created value translator
     */
    static <S, T> ValueTranslator<S, T> beanUtilsBased(
            final Class<T> type) {
        return beanUtilsBased(BeanUtilsBean.getInstance().getConvertUtils(), type);
    }

    /**
     * Creates a new value translator that uses specified Apache BeanUtils
     * {@code ConvertUtilsBean} instance to convert the values.
     * 
     * @param <S> The source value type
     * @param <T> The target value type
     * @param converter The instance to use to convert the values 
     * @param type The target value type
     * @return The created value translator
     */
    static <S, T> ValueTranslator<S, T> beanUtilsBased(
            final ConvertUtilsBean converter,
            final Class<T> type) {
        return e -> type.cast(converter.convert(e, type));
    }
}
