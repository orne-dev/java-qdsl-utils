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
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@code ValueTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-11
 * @since 0.1
 * @see ValueTranslator
 */
@Tag("ut")
public class ValueTranslatorTest {

    /**
     * Test for {@link ValueTranslator#identity()}.
     */
    @Test
    void testIdentity() {
        final ValueTranslator<Object, Object> translator = ValueTranslator.identity();
        assertNotNull(translator);
        final Object value = new Object();
        final Object result = translator.apply(value);
        assertNotNull(result);
        assertSame(value, result);
    }

    /**
     * Test for {@link ValueTranslator#booleanToString(String, String)}.
     */
    @Test
    void booleanToStringTest() {
        final String trueValue = "someStr";
        final String falseValue = "negated";
        assertThrows(NullPointerException.class, () -> ValueTranslator.booleanToString(null, falseValue));
        assertThrows(NullPointerException.class, () -> ValueTranslator.booleanToString(trueValue, null));
        assertThrows(NullPointerException.class, () -> ValueTranslator.booleanToString(null, null));
        final ValueTranslator<Boolean, String> translator = ValueTranslator.booleanToString(trueValue, falseValue);
        assertNotNull(translator);
        assertSame(trueValue, translator.apply(true));
        assertSame(falseValue, translator.apply(false));
        assertNull(translator.apply(null));
    }

    /**
     * Test for {@link ValueTranslator#stringToBoolean(String)}.
     */
    @Test
    void stringToBooleanTest() {
        final String trueValue = "someStr";
        final String falseValue = "negatedValue";
        assertThrows(NullPointerException.class, () -> ValueTranslator.stringToBoolean(null));
        final ValueTranslator<String, Boolean> translator = ValueTranslator.stringToBoolean(trueValue);
        assertNotNull(translator);
        assertTrue(translator.apply(trueValue));
        assertTrue(translator.apply(trueValue.toLowerCase()));
        assertTrue(translator.apply(trueValue.toUpperCase()));
        assertFalse(translator.apply(falseValue));
        assertFalse(translator.apply(falseValue.toLowerCase()));
        assertFalse(translator.apply(falseValue.toUpperCase()));
        assertNull(translator.apply(null));
    }

    /**
     * Test for {@link ValueTranslator#BOOL_TO_STR}.
     */
    @Test
    void boolToStrTest() {
        final ValueTranslator<Boolean, String> translator = ValueTranslator.BOOL_TO_STR;
        assertNotNull(translator);
        assertEquals("true", translator.apply(true));
        assertEquals("false", translator.apply(false));
        assertNull(translator.apply(null));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_BOOL}.
     */
    @Test
    void strToBoolTest() {
        final ValueTranslator<String, Boolean> translator = ValueTranslator.STR_TO_BOOL;
        assertNotNull(translator);
        assertTrue(translator.apply("true"));
        assertTrue(translator.apply("TRUE"));
        assertTrue(translator.apply("tRuE"));
        assertFalse(translator.apply("false"));
        assertFalse(translator.apply("FALSE"));
        assertFalse(translator.apply("fAlSe"));
        assertFalse(translator.apply("Something else"));
        assertNull(translator.apply(null));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_BYTE}.
     */
    @Test
    void strToByteTest() {
        final ValueTranslator<String, Byte> translator = ValueTranslator.STR_TO_BYTE;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(Byte.MIN_VALUE, translator.apply(Byte.toString(Byte.MIN_VALUE)));
        assertEquals(Byte.MAX_VALUE, translator.apply(Byte.toString(Byte.MAX_VALUE)));
        assertEquals((byte) 0, translator.apply("0"));
        assertEquals((byte) 1, translator.apply("1"));
        assertEquals((byte) -1, translator.apply("-1"));
        byte value = (byte) RandomUtils.nextInt();
        assertEquals(value, translator.apply(Byte.toString(value)));
        assertThrows(NumberFormatException.class, () -> translator.apply("128"));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_SHORT}.
     */
    @Test
    void strToShortTest() {
        final ValueTranslator<String, Short> translator = ValueTranslator.STR_TO_SHORT;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(Short.MIN_VALUE, translator.apply(Short.toString(Short.MIN_VALUE)));
        assertEquals(Short.MAX_VALUE, translator.apply(Short.toString(Short.MAX_VALUE)));
        assertEquals((short) 0, translator.apply("0"));
        assertEquals((short) 1, translator.apply("1"));
        assertEquals((short) -1, translator.apply("-1"));
        short value = (short) RandomUtils.nextInt();
        assertEquals(value, translator.apply(Short.toString(value)));
        assertThrows(NumberFormatException.class, () -> translator.apply("32768"));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_INT}.
     */
    @Test
    void strToIntTest() {
        final ValueTranslator<String, Integer> translator = ValueTranslator.STR_TO_INT;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(Integer.MIN_VALUE, translator.apply(Integer.toString(Integer.MIN_VALUE)));
        assertEquals(Integer.MAX_VALUE, translator.apply(Integer.toString(Integer.MAX_VALUE)));
        assertEquals(0, translator.apply("0"));
        assertEquals(1, translator.apply("1"));
        assertEquals(-1, translator.apply("-1"));
        int value = RandomUtils.nextInt();
        assertEquals(value, translator.apply(Integer.toString(value)));
        assertThrows(NumberFormatException.class, () -> translator.apply(String.valueOf(Long.MAX_VALUE)));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_LONG}.
     */
    @Test
    void strToLongTest() {
        final ValueTranslator<String, Long> translator = ValueTranslator.STR_TO_LONG;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(Long.MIN_VALUE, translator.apply(Long.toString(Long.MIN_VALUE)));
        assertEquals(Long.MAX_VALUE, translator.apply(Long.toString(Long.MAX_VALUE)));
        assertEquals(0l, translator.apply("0"));
        assertEquals(1l, translator.apply("1"));
        assertEquals(-1l, translator.apply("-1"));
        long value = RandomUtils.nextLong();
        assertEquals(value, translator.apply(Long.toString(value)));
        assertThrows(NumberFormatException.class, () -> translator.apply(String.valueOf(
                BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2)))));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_FLOAT}.
     */
    @Test
    void strToFloatTest() {
        final ValueTranslator<String, Float> translator = ValueTranslator.STR_TO_FLOAT;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(Float.MIN_VALUE, translator.apply(Float.toString(Float.MIN_VALUE)));
        assertEquals(Float.MAX_VALUE, translator.apply(Float.toString(Float.MAX_VALUE)));
        assertEquals(0f, translator.apply("0"));
        assertEquals(1f, translator.apply("1"));
        assertEquals(-1f, translator.apply("-1"));
        float value = RandomUtils.nextFloat();
        assertEquals(value, translator.apply(Float.toString(value)));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_DOUBLE}.
     */
    @Test
    void strToDoubleTest() {
        final ValueTranslator<String, Double> translator = ValueTranslator.STR_TO_DOUBLE;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(Double.MIN_VALUE, translator.apply(Double.toString(Double.MIN_VALUE)));
        assertEquals(Double.MAX_VALUE, translator.apply(Double.toString(Double.MAX_VALUE)));
        assertEquals(0d, translator.apply("0"));
        assertEquals(1d, translator.apply("1"));
        assertEquals(-1d, translator.apply("-1"));
        double value = RandomUtils.nextDouble();
        assertEquals(value, translator.apply(Double.toString(value)));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_BIG_INT}.
     */
    @Test
    void strToBigIntTest() {
        final ValueTranslator<String, BigInteger> translator = ValueTranslator.STR_TO_BIG_INT;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(BigInteger.ZERO, translator.apply("0"));
        assertEquals(BigInteger.ONE, translator.apply("1"));
        assertEquals(BigInteger.valueOf(-1), translator.apply("-1"));
        BigInteger value = new BigInteger(RandomUtils.nextBytes(100));
        assertEquals(value, translator.apply(value.toString()));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#STR_TO_BIG_DEC}.
     */
    @Test
    void strToBigDecTest() {
        final ValueTranslator<String, BigDecimal> translator = ValueTranslator.STR_TO_BIG_DEC;
        assertNotNull(translator);
        assertNull(translator.apply(null));
        assertEquals(BigDecimal.ZERO, translator.apply("0"));
        assertEquals(BigDecimal.ONE, translator.apply("1"));
        assertEquals(BigDecimal.valueOf(-1), translator.apply("-1"));
        BigDecimal value = new BigDecimal(
                new BigInteger(RandomUtils.nextBytes(100)),
                RandomUtils.nextInt());
        assertEquals(value, translator.apply(value.toString()));
        assertThrows(NumberFormatException.class, () -> translator.apply("NotANumber"));
    }

    /**
     * Test for {@link ValueTranslator#NUMBER_TO_STR}.
     */
    @Test
    void numberToStrTest() {
        final ValueTranslator<Number, String> translator = ValueTranslator.NUMBER_TO_STR;
        assertNull(translator.apply(null));
        assertEquals(String.valueOf(Byte.MIN_VALUE), translator.apply(Byte.MIN_VALUE));
        assertEquals(String.valueOf(Short.MIN_VALUE), translator.apply(Short.MIN_VALUE));
        assertEquals(String.valueOf(Integer.MIN_VALUE), translator.apply(Integer.MIN_VALUE));
        assertEquals(String.valueOf(Float.MIN_VALUE), translator.apply(Float.MIN_VALUE));
        assertEquals(String.valueOf(Double.MIN_VALUE), translator.apply(Double.MIN_VALUE));
        assertEquals(String.valueOf(Byte.MAX_VALUE), translator.apply(Byte.MAX_VALUE));
        assertEquals(String.valueOf(Short.MAX_VALUE), translator.apply(Short.MAX_VALUE));
        assertEquals(String.valueOf(Integer.MAX_VALUE), translator.apply(Integer.MAX_VALUE));
        assertEquals(String.valueOf(Float.MAX_VALUE), translator.apply(Float.MAX_VALUE));
        assertEquals(String.valueOf(Double.MAX_VALUE), translator.apply(Double.MAX_VALUE));
    }

    /**
     * Test for {@link ValueTranslator#beanUtilsBased(Class)}.
     */
    @Test
    void beanUtilsBasedTest() {
        final BeanUtilsBean backup = BeanUtilsBean.getInstance();
        try {
            final ConvertUtilsBean sharedInstance = mock(ConvertUtilsBean.class);
            BeanUtilsBean.setInstance(new BeanUtilsBean(sharedInstance));
            final ValueTranslator<SourceType, TargetType> translator = ValueTranslator.beanUtilsBased(TargetType.class);
            final SourceType value = mock(SourceType.class);
            final TargetType expected = mock(TargetType.class);
            willReturn(expected).given(sharedInstance).convert(value, TargetType.class);
            final TargetType result = translator.apply(value);
            assertSame(expected, result);
            then(sharedInstance).should().convert(value, TargetType.class);
        } finally {
            BeanUtilsBean.setInstance(backup);
        }
    }

    /**
     * Test for {@link ValueTranslator#beanUtilsBased(ConvertUtilsBean, Class)}.
     */
    @Test
    void beanUtilsBasedInstanceTest() {
        final ConvertUtilsBean instance = mock(ConvertUtilsBean.class);
        final ValueTranslator<SourceType, TargetType> translator = ValueTranslator.beanUtilsBased(instance, TargetType.class);
        final SourceType value = mock(SourceType.class);
        final TargetType expected = mock(TargetType.class);
        willReturn(expected).given(instance).convert(value, TargetType.class);
        final TargetType result = translator.apply(value);
        assertSame(expected, result);
        then(instance).should().convert(value, TargetType.class);
    }

    /**
     * Test for {@link ValueTranslator#beanUtilsBased(ConvertUtilsBean2, Class)}.
     */
    @Test
    void beanUtilsBasedInstance2Test() {
        final ConvertUtilsBean2 instance = mock(ConvertUtilsBean2.class);
        final ValueTranslator<SourceType, TargetType> translator = ValueTranslator.beanUtilsBased(instance, TargetType.class);
        final SourceType value = mock(SourceType.class);
        final TargetType expected = mock(TargetType.class);
        willReturn(expected).given(instance).convert(value, TargetType.class);
        final TargetType result = translator.apply(value);
        assertSame(expected, result);
        then(instance).should().convert(value, TargetType.class);
    }

    private static interface SourceType {}
    private static interface TargetType {}
}
