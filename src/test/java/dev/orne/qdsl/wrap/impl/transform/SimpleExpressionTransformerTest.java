package dev.orne.qdsl.wrap.impl.transform;

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
 * Unit tests for {@code SimpleExpressionTransformer}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-11
 * @since 0.1
 * @see SimpleExpressionTransformer
 */
@Tag("ut")
class SimpleExpressionTransformerTest {

    private final static PathBuilder<Object> bean =
            new PathBuilder<>(Object.class, "bean");

    /**
     * Test for {@link SimpleExpressionTransformer#identity()}.
     */
    @Test
    void identityTest() {
        final SimpleExpressionTransformer<Object, Object> translator = SimpleExpressionTransformer.identity();
        assertNotNull(translator);
        final Expression<Object> value = bean.getSimple("prop", Object.class);
        final Expression<?> result = translator.apply(value);
        assertNotNull(result);
        assertSame(value, result);
    }

    /**
     * Test for {@link SimpleExpressionTransformer#booleanToString(String, String)}.
     */
    @Test
    void booleanToStringConstantTest() {
        final String trueValue = "someStr";
        final String falseValue = "negated";
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.booleanToString((String) null, falseValue));
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.booleanToString(trueValue, (String) null));
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.booleanToString((String) null, (String) null));
        final SimpleExpressionTransformer<Boolean, String> translator = SimpleExpressionTransformer.booleanToString(trueValue, falseValue);
        assertNotNull(translator);
        final Expression<Boolean> value = bean.getBoolean("prop");
        assertEquals(
                Expressions.asBoolean(value)
                    .when(true).then(trueValue)
                    .otherwise(falseValue),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#booleanToString(Expression, Expression)}.
     */
    @Test
    void booleanToStringExpressionTest() {
        final Expression<String> trueValue = bean.getString("trueValue");
        final Expression<String> falseValue = bean.getString("falseValue");
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.booleanToString((Expression<String>) null, falseValue));
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.booleanToString(trueValue, (Expression<String>) null));
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.booleanToString((Expression<String>) null, (Expression<String>) null));
        final SimpleExpressionTransformer<Boolean, String> translator = SimpleExpressionTransformer.booleanToString(trueValue, falseValue);
        assertNotNull(translator);
        final Expression<Boolean> value = bean.getBoolean("prop");
        assertEquals(
                Expressions.asBoolean(value)
                    .when(true).then(trueValue)
                    .otherwise(falseValue),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#stringToBoolean(String)}.
     */
    @Test
    void stringToBooleanConstantTest() {
        final String trueValue = "someStr";
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.stringToBoolean((String) null));
        final SimpleExpressionTransformer<String, Boolean> translator = SimpleExpressionTransformer.stringToBoolean(trueValue);
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase(trueValue),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#stringToBoolean(String)}.
     */
    @Test
    void stringToBooleanExpressionTest() {
        final Expression<String> trueValue = bean.getString("trueValue");
        assertThrows(NullPointerException.class, () -> SimpleExpressionTransformer.stringToBoolean((Expression<String>) null));
        final SimpleExpressionTransformer<String, Boolean> translator = SimpleExpressionTransformer.stringToBoolean(trueValue);
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase(trueValue),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#BOOL_TO_STR}.
     */
    @Test
    void boolToStrTest() {
        final SimpleExpressionTransformer<Boolean, String> translator = SimpleExpressionTransformer.BOOL_TO_STR;
        assertNotNull(translator);
        final Expression<Boolean> value = bean.getBoolean("prop");
        assertEquals(
                Expressions.asBoolean(value)
                    .when(true).then("true")
                    .otherwise("false"),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_BOOL}.
     */
    @Test
    void strToBoolTest() {
        final SimpleExpressionTransformer<String, Boolean> translator = SimpleExpressionTransformer.STR_TO_BOOL;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase("true"),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#numberToString(Class)}.
     */
    @Test
    void numberToStringTest() {
        final SimpleExpressionTransformer<Integer, String> translator = SimpleExpressionTransformer.numberToString(Integer.class);
        assertNotNull(translator);
        final Expression<Integer> value = bean.getNumber("prop", Integer.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#stringToNumber(Class)}.
     */
    @Test
    void stringToNumberTest() {
        final SimpleExpressionTransformer<String, Integer> translator = SimpleExpressionTransformer.stringToNumber(Integer.class);
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Integer.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#BYTE_TO_STR}.
     */
    @Test
    void byteToStrTest() {
        final SimpleExpressionTransformer<Byte, String> translator = SimpleExpressionTransformer.BYTE_TO_STR;
        assertNotNull(translator);
        final Expression<Byte> value = bean.getNumber("prop", Byte.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_BYTE}.
     */
    @Test
    void strToByteTest() {
        final SimpleExpressionTransformer<String, Byte> translator = SimpleExpressionTransformer.STR_TO_BYTE;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Byte.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#BYTE_TO_STR}.
     */
    @Test
    void shortToStrTest() {
        final SimpleExpressionTransformer<Short, String> translator = SimpleExpressionTransformer.SHORT_TO_STR;
        assertNotNull(translator);
        final Expression<Short> value = bean.getNumber("prop", Short.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_BYTE}.
     */
    @Test
    void strToShortTest() {
        final SimpleExpressionTransformer<String, Short> translator = SimpleExpressionTransformer.STR_TO_SHORT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Short.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#INT_TO_STR}.
     */
    @Test
    void intToStrTest() {
        final SimpleExpressionTransformer<Integer, String> translator = SimpleExpressionTransformer.INT_TO_STR;
        assertNotNull(translator);
        final Expression<Integer> value = bean.getNumber("prop", Integer.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_INT}.
     */
    @Test
    void strToIntTest() {
        final SimpleExpressionTransformer<String, Integer> translator = SimpleExpressionTransformer.STR_TO_INT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Integer.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#LONG_TO_STR}.
     */
    @Test
    void longToStrTest() {
        final SimpleExpressionTransformer<Long, String> translator = SimpleExpressionTransformer.LONG_TO_STR;
        assertNotNull(translator);
        final Expression<Long> value = bean.getNumber("prop", Long.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_LONG}.
     */
    @Test
    void strToLongTest() {
        final SimpleExpressionTransformer<String, Long> translator = SimpleExpressionTransformer.STR_TO_LONG;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Long.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#FLOAT_TO_STR}.
     */
    @Test
    void floatToStrTest() {
        final SimpleExpressionTransformer<Float, String> translator = SimpleExpressionTransformer.FLOAT_TO_STR;
        assertNotNull(translator);
        final Expression<Float> value = bean.getNumber("prop", Float.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_FLOAT}.
     */
    @Test
    void strToFloatTest() {
        final SimpleExpressionTransformer<String, Float> translator = SimpleExpressionTransformer.STR_TO_FLOAT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Float.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#DOUBLE_TO_STR}.
     */
    @Test
    void doubleToStrTest() {
        final SimpleExpressionTransformer<Double, String> translator = SimpleExpressionTransformer.DOUBLE_TO_STR;
        assertNotNull(translator);
        final Expression<Double> value = bean.getNumber("prop", Double.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_DOUBLE}.
     */
    @Test
    void strToDoubleTest() {
        final SimpleExpressionTransformer<String, Double> translator = SimpleExpressionTransformer.STR_TO_DOUBLE;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Double.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#BIG_INT_TO_STR}.
     */
    @Test
    void bigIntToStrTest() {
        final SimpleExpressionTransformer<BigInteger, String> translator = SimpleExpressionTransformer.BIG_INT_TO_STR;
        assertNotNull(translator);
        final Expression<BigInteger> value = bean.getNumber("prop", BigInteger.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_BIG_INT}.
     */
    @Test
    void strToBigIntTest() {
        final SimpleExpressionTransformer<String, BigInteger> translator = SimpleExpressionTransformer.STR_TO_BIG_INT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(BigInteger.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#BIG_DEC_TO_STR}.
     */
    @Test
    void bigDecToStrTest() {
        final SimpleExpressionTransformer<BigDecimal, String> translator = SimpleExpressionTransformer.BIG_DEC_TO_STR;
        assertNotNull(translator);
        final Expression<BigDecimal> value = bean.getNumber("prop", BigDecimal.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link SimpleExpressionTransformer#STR_TO_BIG_DEC}.
     */
    @Test
    void strToBigDecTest() {
        final SimpleExpressionTransformer<String, BigDecimal> translator = SimpleExpressionTransformer.STR_TO_BIG_DEC;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(BigDecimal.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }
}
