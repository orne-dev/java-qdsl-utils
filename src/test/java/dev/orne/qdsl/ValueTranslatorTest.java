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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.orne.qdsl.TestTypes.SimpleType;

/**
 * Unit tests for {@code ValueTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-01
 * @since 0.1
 * @see ValueTranslator
 */
@Tag("ut")
public class ValueTranslatorTest {

    @Test
    void testIdentity() {
        final ValueTranslator<Object, Object> translator =
                ValueTranslator.identity();
        final Object value = new Object();
        final Object result = translator.apply(value);
        assertNotNull(result);
        assertSame(value, result);
    }

    @Test
    void testStringToBoolean() {
        final ValueTranslator<String, Boolean> translator =
                ValueTranslator.stringToBoolean("trueValue");
        assertTrue(translator.apply("truevalue"));
        assertTrue(translator.apply("trueValue"));
        assertTrue(translator.apply("TrUeVaLuE"));
        assertTrue(translator.apply("TRUEVALUE"));
        assertNull(translator.apply(null));
        assertFalse(translator.apply("true"));
        assertFalse(translator.apply("false"));
        assertFalse(translator.apply("other value"));
    }

    @Test
    void testBooleanToString() {
        final String trueValue = "trueValue";
        final String falseValue = "falseValue";
        final ValueTranslator<Boolean, String> translator =
                ValueTranslator.booleanToString(trueValue, falseValue);
        assertEquals(trueValue, translator.apply(true));
        assertEquals(falseValue, translator.apply(false));
        assertNull(translator.apply(null));
    }

    @Test
    void testBeanUtilsBased() {
        final ConvertUtilsBean convertUtils = mock(ConvertUtilsBean.class);
        final BeanUtilsBean beanUtils =  new BeanUtilsBean(convertUtils);
        BeanUtilsBean beanUtilsBck = BeanUtilsBean.getInstance();
        final Object value = new Object();
        final SimpleType expectedResult = mock(SimpleType.class);
        willReturn(expectedResult).given(convertUtils).convert(value, SimpleType.class);
        BeanUtilsBean.setInstance(beanUtils);
        try {
            final ValueTranslator<Object, SimpleType> translator =
                    ValueTranslator.beanUtilsBased(SimpleType.class);
            assertSame(expectedResult, translator.apply(value));
        } finally {
            BeanUtilsBean.setInstance(beanUtilsBck);
        }
        then(convertUtils).should().convert(value, SimpleType.class);
    }

    @Test
    void testBeanUtilsBased_Bean() {
        final ConvertUtilsBean convertUtils = mock(ConvertUtilsBean.class);
        final ValueTranslator<Object, SimpleType> translator =
                ValueTranslator.beanUtilsBased(convertUtils, SimpleType.class);
        final Object value = new Object();
        final SimpleType expectedResult = mock(SimpleType.class);
        willReturn(expectedResult).given(convertUtils).convert(value, SimpleType.class);
        assertSame(expectedResult, translator.apply(value));
        then(convertUtils).should().convert(value, SimpleType.class);
    }

    @Test
    void testSTR_TO_BOOL() {
        final ValueTranslator<String, Boolean> translator =
                ValueTranslator.STR_TO_BOOL;
        assertTrue(translator.apply("true"));
        assertTrue(translator.apply("TrUe"));
        assertTrue(translator.apply("TRUE"));
        assertNull(translator.apply(null));
        assertFalse(translator.apply("false"));
        assertFalse(translator.apply("fAlSe"));
        assertFalse(translator.apply("FALSE"));
        assertFalse(translator.apply("other value"));
    }

    @Test
    void testBOOL_TO_STR() {
        final ValueTranslator<Boolean, String> translator =
                ValueTranslator.BOOL_TO_STR;
        assertEquals("true", translator.apply(true));
        assertEquals("false", translator.apply(false));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_BYTE() {
        final ValueTranslator<String, Byte> translator =
                ValueTranslator.STR_TO_BYTE;
        assertEquals(Byte.MAX_VALUE, translator.apply(Byte.toString(Byte.MAX_VALUE)));
        assertEquals((byte) 100, translator.apply("100"));
        assertEquals((byte) 10, translator.apply("10"));
        assertEquals((byte) 0, translator.apply("0"));
        assertEquals((byte) -10, translator.apply("-10"));
        assertEquals((byte) -100, translator.apply("-100"));
        assertEquals(Byte.MIN_VALUE, translator.apply(Byte.toString(Byte.MIN_VALUE)));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_SHORT() {
        final ValueTranslator<String, Short> translator =
                ValueTranslator.STR_TO_SHORT;
        assertEquals(Short.MAX_VALUE, translator.apply(Short.toString(Short.MAX_VALUE)));
        assertEquals((short) 10000, translator.apply("10000"));
        assertEquals((short) 10, translator.apply("10"));
        assertEquals((short) 0, translator.apply("0"));
        assertEquals((short) -10, translator.apply("-10"));
        assertEquals((short) -10000, translator.apply("-10000"));
        assertEquals(Short.MIN_VALUE, translator.apply(Short.toString(Short.MIN_VALUE)));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_INT() {
        final ValueTranslator<String, Integer> translator =
                ValueTranslator.STR_TO_INT;
        assertEquals(Integer.MAX_VALUE, translator.apply(Integer.toString(Integer.MAX_VALUE)));
        assertEquals((int) 1000000, translator.apply("1000000"));
        assertEquals((int) 10, translator.apply("10"));
        assertEquals((int) 0, translator.apply("0"));
        assertEquals((int) -10, translator.apply("-10"));
        assertEquals((int) -1000000, translator.apply("-1000000"));
        assertEquals(Integer.MIN_VALUE, translator.apply(Integer.toString(Integer.MIN_VALUE)));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_LONG() {
        final ValueTranslator<String, Long> translator =
                ValueTranslator.STR_TO_LONG;
        assertEquals(Long.MAX_VALUE, translator.apply(Long.toString(Long.MAX_VALUE)));
        assertEquals(100000000000L, translator.apply("100000000000"));
        assertEquals(10L, translator.apply("10"));
        assertEquals(0L, translator.apply("0"));
        assertEquals(-10L, translator.apply("-10"));
        assertEquals(-100000000000L, translator.apply("-100000000000"));
        assertEquals(Long.MIN_VALUE, translator.apply(Long.toString(Long.MIN_VALUE)));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_FLOAT() {
        final ValueTranslator<String, Float> translator =
                ValueTranslator.STR_TO_FLOAT;
        assertEquals(Float.MAX_VALUE, translator.apply(Float.toString(Float.MAX_VALUE)));
        assertEquals(100000000000f, translator.apply("100000000000"));
        assertEquals(10f, translator.apply("10"));
        assertEquals(0f, translator.apply("0"));
        assertEquals(-10f, translator.apply("-10"));
        assertEquals(-100000000000f, translator.apply("-100000000000"));
        assertEquals(Float.MIN_VALUE, translator.apply(Float.toString(Float.MIN_VALUE)));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_DOUBLE() {
        final ValueTranslator<String, Double> translator =
                ValueTranslator.STR_TO_DOUBLE;
        assertEquals(Double.MAX_VALUE, translator.apply(Double.toString(Double.MAX_VALUE)));
        assertEquals(100000000000d, translator.apply("100000000000"));
        assertEquals(10d, translator.apply("10"));
        assertEquals(0d, translator.apply("0"));
        assertEquals(-10d, translator.apply("-10"));
        assertEquals(-100000000000d, translator.apply("-100000000000"));
        assertEquals(Double.MIN_VALUE, translator.apply(Double.toString(Double.MIN_VALUE)));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_BIG_INT() {
        final ValueTranslator<String, BigInteger> translator =
                ValueTranslator.STR_TO_BIG_INT;
        assertEquals(BigInteger.valueOf(100000000000L), translator.apply("100000000000"));
        assertEquals(BigInteger.valueOf(10L), translator.apply("10"));
        assertEquals(BigInteger.valueOf(0L), translator.apply("0"));
        assertEquals(BigInteger.valueOf(-10L), translator.apply("-10"));
        assertEquals(BigInteger.valueOf(-100000000000L), translator.apply("-100000000000"));
        assertNull(translator.apply(null));
    }

    @Test
    void testSTR_TO_BIG_DEC() {
        final ValueTranslator<String, BigDecimal> translator =
                ValueTranslator.STR_TO_BIG_DEC;
        assertEquals(BigDecimal.valueOf(100000000000L), translator.apply("100000000000"));
        assertEquals(BigDecimal.valueOf(10L), translator.apply("10"));
        assertEquals(BigDecimal.valueOf(0L), translator.apply("0"));
        assertEquals(BigDecimal.valueOf(-10L), translator.apply("-10"));
        assertEquals(BigDecimal.valueOf(-100000000000L), translator.apply("-100000000000"));
        assertNull(translator.apply(null));
    }

    @Test
    void testNUMBER_TO_STR() {
        final ValueTranslator<Number, String> translator =
                ValueTranslator.NUMBER_TO_STR;
        assertEquals("100", translator.apply((byte) 100));
        assertEquals("10000", translator.apply((short) 10000));
        assertEquals("1000000", translator.apply((int) 1000000));
        assertEquals("100000000000", translator.apply(100000000000L));
        assertNull(translator.apply(null));
    }
}
