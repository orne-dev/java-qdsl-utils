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

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Unit tests for {@code ExpressionTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-01
 * @since 0.1
 * @see ExpressionTranslator
 */
@Tag("ut")
public class ExpressionTranslatorTest {

    private static final PathBuilder<Object> BUILDER =
            new PathBuilder<Object>(Object.class, "builder");

    @Test
    void testIdentity() {
        final ExpressionTranslator<Object, Object> translator =
                ExpressionTranslator.identity();
        final Expression<Object> value = BUILDER.getSimple("value", Object.class);
        final Expression<Object> result = translator.apply(value);
        assertNotNull(result);
        assertSame(value, result);
    }

    @Test
    void testStringToBoolean_Constant() {
        final String trueValue = "trueValue";
        final ExpressionTranslator<String, Boolean> translator =
                ExpressionTranslator.stringToBoolean(trueValue);
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase(trueValue),
                translator.apply(value));
    }

    @Test
    void testStringToBoolean_Expression() {
        final Expression<String> trueValue = BUILDER.get("trueValue", String.class);
        final ExpressionTranslator<String, Boolean> translator =
                ExpressionTranslator.stringToBoolean(trueValue);
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase(trueValue),
                translator.apply(value));
    }

    @Test
    void testBooleanToString_Constant() {
        final String trueValue = "trueValue";
        final String falseValue = "falseValue";
        final ExpressionTranslator<Boolean, String> translator =
                ExpressionTranslator.booleanToString(trueValue, falseValue);
        final Expression<Boolean> value = BUILDER.get("value", Boolean.class);
        assertEquals(
                Expressions.asBoolean(value).when(true).then(trueValue).otherwise(falseValue),
                translator.apply(value));
    }

    @Test
    void testBooleanToString_Expression() {
        final Expression<String> trueValue = BUILDER.get("trueValue", String.class);
        final Expression<String> falseValue = BUILDER.get("falseValue", String.class);
        final ExpressionTranslator<Boolean, String> translator =
                ExpressionTranslator.booleanToString(trueValue, falseValue);
        final Expression<Boolean> value = BUILDER.get("value", Boolean.class);
        assertEquals(
                Expressions.asBoolean(value).when(true).then(trueValue).otherwise(falseValue),
                translator.apply(value));
    }

    @Test
    void testStringToNumber() {
        final ExpressionTranslator<String, Long> translator =
                ExpressionTranslator.stringToNumber(Long.class);
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(Long.class),
                translator.apply(value));
    }

    @Test
    void testNumberToString() {
        final ExpressionTranslator<Long, String> translator =
                ExpressionTranslator.numberToString(Long.class);
        final Expression<Long> value = BUILDER.get("value", Long.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_BOOL() {
        final ExpressionTranslator<String, Boolean> translator =
                ExpressionTranslator.STR_TO_BOOL;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase("true"),
                translator.apply(value));
    }

    @Test
    void testBOOL_TO_STR() {
        final ExpressionTranslator<Boolean, String> translator =
                ExpressionTranslator.BOOL_TO_STR;
        final Expression<Boolean> value = BUILDER.get("value", Boolean.class);
        assertEquals(
                Expressions.asBoolean(value).when(true).then("true").otherwise("false"),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_BYTE() {
        final ExpressionTranslator<String, Byte> translator =
                ExpressionTranslator.STR_TO_BYTE;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(Byte.class),
                translator.apply(value));
    }

    @Test
    void testBYTE_TO_STR() {
        final ExpressionTranslator<Byte, String> translator =
                ExpressionTranslator.BYTE_TO_STR;
        final Expression<Byte> value = BUILDER.get("value", Byte.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_SHORT() {
        final ExpressionTranslator<String, Short> translator =
                ExpressionTranslator.STR_TO_SHORT;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(Short.class),
                translator.apply(value));
    }

    @Test
    void testSHORT_TO_STR() {
        final ExpressionTranslator<Short, String> translator =
                ExpressionTranslator.SHORT_TO_STR;
        final Expression<Short> value = BUILDER.get("value", Short.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_INT() {
        final ExpressionTranslator<String, Integer> translator =
                ExpressionTranslator.STR_TO_INT;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(Integer.class),
                translator.apply(value));
    }

    @Test
    void testINT_TO_STR() {
        final ExpressionTranslator<Integer, String> translator =
                ExpressionTranslator.INT_TO_STR;
        final Expression<Integer> value = BUILDER.get("value", Integer.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_LONG() {
        final ExpressionTranslator<String, Long> translator =
                ExpressionTranslator.STR_TO_LONG;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(Long.class),
                translator.apply(value));
    }

    @Test
    void testLONG_TO_STR() {
        final ExpressionTranslator<Long, String> translator =
                ExpressionTranslator.LONG_TO_STR;
        final Expression<Long> value = BUILDER.get("value", Long.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_FLOAT() {
        final ExpressionTranslator<String, Float> translator =
                ExpressionTranslator.STR_TO_FLOAT;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(Float.class),
                translator.apply(value));
    }

    @Test
    void testFLOAT_TO_STR() {
        final ExpressionTranslator<Float, String> translator =
                ExpressionTranslator.FLOAT_TO_STR;
        final Expression<Float> value = BUILDER.get("value", Float.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_DOUBLE() {
        final ExpressionTranslator<String, Double> translator =
                ExpressionTranslator.STR_TO_DOUBLE;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(Double.class),
                translator.apply(value));
    }

    @Test
    void testDOUBLE_TO_STR() {
        final ExpressionTranslator<Double, String> translator =
                ExpressionTranslator.DOUBLE_TO_STR;
        final Expression<Double> value = BUILDER.get("value", Double.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_BIG_INT() {
        final ExpressionTranslator<String, BigInteger> translator =
                ExpressionTranslator.STR_TO_BIG_INT;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(BigInteger.class),
                translator.apply(value));
    }

    @Test
    void testBIG_INT_TO_STR() {
        final ExpressionTranslator<BigInteger, String> translator =
                ExpressionTranslator.BIG_INT_TO_STR;
        final Expression<BigInteger> value = BUILDER.get("value", BigInteger.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }

    @Test
    void testSTR_TO_BIG_DEC() {
        final ExpressionTranslator<String, BigDecimal> translator =
                ExpressionTranslator.STR_TO_BIG_DEC;
        final Expression<String> value = BUILDER.get("value", String.class);
        assertEquals(
                Expressions.asString(value).castToNum(BigDecimal.class),
                translator.apply(value));
    }

    @Test
    void testBIG_DEC_TO_STR() {
        final ExpressionTranslator<BigDecimal, String> translator =
                ExpressionTranslator.BIG_DEC_TO_STR;
        final Expression<BigDecimal> value = BUILDER.get("value", BigDecimal.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
    }
}
