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
 * @version 1.0, 2021-11
 * @since 0.1
 * @see ExpressionTranslator
 */
@Tag("ut")
class ExpressionTranslatorTest {

    private final static PathBuilder<Object> bean =
            new PathBuilder<>(Object.class, "bean");

    /**
     * Test for {@link ExpressionTranslator#identity()}.
     */
    @Test
    void identityTest() {
        final ExpressionTranslator<Object, Object> translator = ExpressionTranslator.identity();
        assertNotNull(translator);
        final Expression<Object> value = bean.getSimple("prop", Object.class);
        final Expression<?> result = translator.apply(value);
        assertNotNull(result);
        assertSame(value, result);
    }

    /**
     * Test for {@link ExpressionTranslator#booleanToString(String, String)}.
     */
    @Test
    void booleanToStringConstantTest() {
        final String trueValue = "someStr";
        final String falseValue = "negated";
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.booleanToString((String) null, falseValue));
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.booleanToString(trueValue, (String) null));
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.booleanToString((String) null, (String) null));
        final ExpressionTranslator<Boolean, String> translator = ExpressionTranslator.booleanToString(trueValue, falseValue);
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
     * Test for {@link ExpressionTranslator#booleanToString(Expression, Expression)}.
     */
    @Test
    void booleanToStringExpressionTest() {
        final Expression<String> trueValue = bean.getString("trueValue");
        final Expression<String> falseValue = bean.getString("falseValue");
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.booleanToString((Expression<String>) null, falseValue));
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.booleanToString(trueValue, (Expression<String>) null));
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.booleanToString((Expression<String>) null, (Expression<String>) null));
        final ExpressionTranslator<Boolean, String> translator = ExpressionTranslator.booleanToString(trueValue, falseValue);
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
     * Test for {@link ExpressionTranslator#stringToBoolean(String)}.
     */
    @Test
    void stringToBooleanConstantTest() {
        final String trueValue = "someStr";
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.stringToBoolean((String) null));
        final ExpressionTranslator<String, Boolean> translator = ExpressionTranslator.stringToBoolean(trueValue);
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase(trueValue),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#stringToBoolean(String)}.
     */
    @Test
    void stringToBooleanExpressionTest() {
        final Expression<String> trueValue = bean.getString("trueValue");
        assertThrows(NullPointerException.class, () -> ExpressionTranslator.stringToBoolean((Expression<String>) null));
        final ExpressionTranslator<String, Boolean> translator = ExpressionTranslator.stringToBoolean(trueValue);
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase(trueValue),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#BOOL_TO_STR}.
     */
    @Test
    void boolToStrTest() {
        final ExpressionTranslator<Boolean, String> translator = ExpressionTranslator.BOOL_TO_STR;
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
     * Test for {@link ExpressionTranslator#STR_TO_BOOL}.
     */
    @Test
    void strToBoolTest() {
        final ExpressionTranslator<String, Boolean> translator = ExpressionTranslator.STR_TO_BOOL;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).equalsIgnoreCase("true"),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#numberToString(Class)}.
     */
    @Test
    void numberToStringTest() {
        final ExpressionTranslator<Integer, String> translator = ExpressionTranslator.numberToString(Integer.class);
        assertNotNull(translator);
        final Expression<Integer> value = bean.getNumber("prop", Integer.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#stringToNumber(Class)}.
     */
    @Test
    void stringToNumberTest() {
        final ExpressionTranslator<String, Integer> translator = ExpressionTranslator.stringToNumber(Integer.class);
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Integer.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#BYTE_TO_STR}.
     */
    @Test
    void byteToStrTest() {
        final ExpressionTranslator<Byte, String> translator = ExpressionTranslator.BYTE_TO_STR;
        assertNotNull(translator);
        final Expression<Byte> value = bean.getNumber("prop", Byte.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_BYTE}.
     */
    @Test
    void strToByteTest() {
        final ExpressionTranslator<String, Byte> translator = ExpressionTranslator.STR_TO_BYTE;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Byte.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#BYTE_TO_STR}.
     */
    @Test
    void shortToStrTest() {
        final ExpressionTranslator<Short, String> translator = ExpressionTranslator.SHORT_TO_STR;
        assertNotNull(translator);
        final Expression<Short> value = bean.getNumber("prop", Short.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_BYTE}.
     */
    @Test
    void strToShortTest() {
        final ExpressionTranslator<String, Short> translator = ExpressionTranslator.STR_TO_SHORT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Short.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#INT_TO_STR}.
     */
    @Test
    void intToStrTest() {
        final ExpressionTranslator<Integer, String> translator = ExpressionTranslator.INT_TO_STR;
        assertNotNull(translator);
        final Expression<Integer> value = bean.getNumber("prop", Integer.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_INT}.
     */
    @Test
    void strToIntTest() {
        final ExpressionTranslator<String, Integer> translator = ExpressionTranslator.STR_TO_INT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Integer.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#LONG_TO_STR}.
     */
    @Test
    void longToStrTest() {
        final ExpressionTranslator<Long, String> translator = ExpressionTranslator.LONG_TO_STR;
        assertNotNull(translator);
        final Expression<Long> value = bean.getNumber("prop", Long.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_LONG}.
     */
    @Test
    void strToLongTest() {
        final ExpressionTranslator<String, Long> translator = ExpressionTranslator.STR_TO_LONG;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Long.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#FLOAT_TO_STR}.
     */
    @Test
    void floatToStrTest() {
        final ExpressionTranslator<Float, String> translator = ExpressionTranslator.FLOAT_TO_STR;
        assertNotNull(translator);
        final Expression<Float> value = bean.getNumber("prop", Float.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_FLOAT}.
     */
    @Test
    void strToFloatTest() {
        final ExpressionTranslator<String, Float> translator = ExpressionTranslator.STR_TO_FLOAT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Float.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#DOUBLE_TO_STR}.
     */
    @Test
    void doubleToStrTest() {
        final ExpressionTranslator<Double, String> translator = ExpressionTranslator.DOUBLE_TO_STR;
        assertNotNull(translator);
        final Expression<Double> value = bean.getNumber("prop", Double.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_DOUBLE}.
     */
    @Test
    void strToDoubleTest() {
        final ExpressionTranslator<String, Double> translator = ExpressionTranslator.STR_TO_DOUBLE;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(Double.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#BIG_INT_TO_STR}.
     */
    @Test
    void bigIntToStrTest() {
        final ExpressionTranslator<BigInteger, String> translator = ExpressionTranslator.BIG_INT_TO_STR;
        assertNotNull(translator);
        final Expression<BigInteger> value = bean.getNumber("prop", BigInteger.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_BIG_INT}.
     */
    @Test
    void strToBigIntTest() {
        final ExpressionTranslator<String, BigInteger> translator = ExpressionTranslator.STR_TO_BIG_INT;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(BigInteger.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#BIG_DEC_TO_STR}.
     */
    @Test
    void bigDecToStrTest() {
        final ExpressionTranslator<BigDecimal, String> translator = ExpressionTranslator.BIG_DEC_TO_STR;
        assertNotNull(translator);
        final Expression<BigDecimal> value = bean.getNumber("prop", BigDecimal.class);
        assertEquals(
                Expressions.asNumber(value).stringValue(),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }

    /**
     * Test for {@link ExpressionTranslator#STR_TO_BIG_DEC}.
     */
    @Test
    void strToBigDecTest() {
        final ExpressionTranslator<String, BigDecimal> translator = ExpressionTranslator.STR_TO_BIG_DEC;
        assertNotNull(translator);
        final Expression<String> value = bean.getString("prop");
        assertEquals(
                Expressions.asString(value).castToNum(BigDecimal.class),
                translator.apply(value));
        assertThrows(NullPointerException.class, () -> translator.apply(null));
    }
}
