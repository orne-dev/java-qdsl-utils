package dev.orne.qdsl.wrap.impl;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2022 Orne Developments
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Unit tests for {@code PreFetchedSubQueryExpression}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see PreFetchedSubQueryExpression
 */
@Tag("ut")
class PreFetchedSubQueryExpressionTest {

    private @Mock QueryMetadata metadata;
    private @Mock TestResultType value1;
    private @Mock TestResultType value2;
    private @Mock TestResultType value3;
    private List<TestResultType> values;
    private @Mock TestResultType otherValue;
    private @Mock TestResultType otherValue2;
    private @Mock Expression<TestResultType> expr;

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
        values = Arrays.asList(value1, value2, value3);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#PreFetchedSubQueryExpression(QueryMetadata, Class, List)}.
     */
    @Test
    void testConstructor() {
        final PreFetchedSubQueryExpression<TestResultType> result =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        assertSame(metadata, result.getMetadata());
        assertSame(TestResultType.class, result.getType());
        assertNotSame(values, result.getValues());
        assertEquals(values, result.getValues());
        assertThrows(NullPointerException.class, () -> {
            new PreFetchedSubQueryExpression<>(null, TestResultType.class, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new PreFetchedSubQueryExpression<>(metadata, null, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, null);
        });
        then(metadata).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#fromUnchecked(QueryMetadata, Class, List)}.
     */
    @Test
    void testFromUnchecked() {
        final List<Object> uvalues = Arrays.<Object>asList(value1, value2, value3);
        final PreFetchedSubQueryExpression<TestResultType> result =
                PreFetchedSubQueryExpression.fromUnchecked(metadata, TestResultType.class, uvalues);
        assertSame(metadata, result.getMetadata());
        assertSame(TestResultType.class, result.getType());
        assertNotSame(values, result.getValues());
        assertEquals(values, result.getValues());
        assertThrows(NullPointerException.class, () -> {
            PreFetchedSubQueryExpression.fromUnchecked(null, TestResultType.class, uvalues);
        });
        assertThrows(NullPointerException.class, () -> {
            PreFetchedSubQueryExpression.fromUnchecked(metadata, null, uvalues);
        });
        assertThrows(NullPointerException.class, () -> {
            PreFetchedSubQueryExpression.fromUnchecked(metadata, TestResultType.class, null);
        });
        then(metadata).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#asSingleValue()}.
     */
    @Test
    void testAsSingleValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final Expression<TestResultType> result = subquery.asSingleValue();
        assertNotNull(result);
        final Constant<?> cresult = assertInstanceOf(Constant.class, result);
        assertSame(value1, cresult.getConstant());
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#asSingleValue()}.
     */
    @Test
    void testAsSingleValueEmpty() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, Collections.emptyList());
        final Expression<TestResultType> result = subquery.asSingleValue();
        assertNotNull(result);
        assertInstanceOf(NullExpression.class, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#asValues()}.
     */
    @Test
    void testAsValues() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final Expression<List<TestResultType>> result = subquery.asValues();
        assertNotNull(result);
        assertTrue(List.class.isAssignableFrom(result.getType()));
        final Constant<?> cresult = assertInstanceOf(Constant.class, result);
        assertEquals(values, cresult.getConstant());
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#eq(Serializable)}.
     */
    @Test
    void testEqValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.eq(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.EQ,
                Expressions.constant(value1),
                Expressions.constant(otherValue));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#eq(Expression)}.
     */
    @Test
    void testEqExpression() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.eq(expr);
        final BooleanExpression expected = Expressions.predicate(
                Ops.EQ,
                Expressions.constant(value1),
                expr);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#ne(Serializable)}.
     */
    @Test
    void testNeValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.ne(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.NE,
                Expressions.constant(value1),
                Expressions.constant(otherValue));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#ne(Expression)}.
     */
    @Test
    void testNeExpression() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.ne(expr);
        final BooleanExpression expected = Expressions.predicate(
                Ops.NE,
                Expressions.constant(value1),
                expr);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#contains(Serializable)}.
     */
    @Test
    void testContainsValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.contains(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.IN,
                Expressions.constant(otherValue),
                Expressions.constant(values));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#contains(Expression)}.
     */
    @Test
    void testContainsExpression() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.contains(expr);
        final BooleanExpression expected = Expressions.predicate(
                Ops.IN,
                expr,
                Expressions.constant(values));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#exists()}.
     */
    @Test
    void testExists() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.exists();
        final BooleanExpression expected = Expressions.predicate(
                Ops.COL_IS_EMPTY,
                Expressions.constant(values));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#notExists()}.
     */
    @Test
    void testNotExists() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.notExists();
        final BooleanExpression expected = Expressions.predicate(
                Ops.NOT,
                Expressions.predicate(
                    Ops.COL_IS_EMPTY,
                    Expressions.constant(values)));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#lt(Serializable)}.
     */
    @Test
    void testLtValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.lt(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.LT,
                Expressions.constant(value1),
                Expressions.constant(otherValue));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#lt(Expression)}.
     */
    @Test
    void testLtExpression() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.lt(expr);
        final BooleanExpression expected = Expressions.predicate(
                Ops.LT,
                Expressions.constant(value1),
                expr);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#gt(Serializable)}.
     */
    @Test
    void testGtValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.gt(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.GT,
                Expressions.constant(value1),
                Expressions.constant(otherValue));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#gt(Expression)}.
     */
    @Test
    void testGtExpression() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.gt(expr);
        final BooleanExpression expected = Expressions.predicate(
                Ops.GT,
                Expressions.constant(value1),
                expr);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#loe(Serializable)}.
     */
    @Test
    void testLoeValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.loe(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.LOE,
                Expressions.constant(value1),
                Expressions.constant(otherValue));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#loe(Expression)}.
     */
    @Test
    void testLoeExpression() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.loe(expr);
        final BooleanExpression expected = Expressions.predicate(
                Ops.LOE,
                Expressions.constant(value1),
                expr);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#goe(Serializable)}.
     */
    @Test
    void testGoeValue() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.goe(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.GOE,
                Expressions.constant(value1),
                Expressions.constant(otherValue));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#goe(Expression)}.
     */
    @Test
    void testGoeExpression() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.goe(expr);
        final BooleanExpression expected = Expressions.predicate(
                Ops.GOE,
                Expressions.constant(value1),
                expr);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#isNull()}.
     */
    @Test
    void testIsNull() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.isNull();
        final BooleanExpression expected = Expressions.predicate(
                Ops.IS_NULL,
                Expressions.constant(value1));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#isNotNull()}.
     */
    @Test
    void testIsNotNull() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.isNotNull();
        final BooleanExpression expected = Expressions.predicate(
                Ops.IS_NOT_NULL,
                Expressions.constant(value1));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#in(Collection)}.
     */
    @Test
    void testInCollection() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final Collection<TestResultType> otherValues = Arrays.asList(otherValue, otherValue2);
        final BooleanExpression result = subquery.in(otherValues);
        final BooleanExpression expected = Expressions.predicate(
                Ops.IN,
                Expressions.constant(value1),
                Expressions.constant(otherValues));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#in(Collection)}.
     */
    @Test
    void testInOnItemCollection() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final Collection<TestResultType> otherValues = Arrays.asList(otherValue);
        final BooleanExpression result = subquery.in(otherValues);
        final BooleanExpression expected = Expressions.predicate(
                Ops.IN,
                Expressions.constant(otherValue),
                Expressions.constant(values));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#in(Serializable...)}.
     */
    @Test
    void testInArray() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.in(otherValue, otherValue2);
        final BooleanExpression expected = Expressions.predicate(
                Ops.IN,
                Expressions.constant(value1),
                Expressions.constant(Arrays.asList(otherValue, otherValue2)));
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#in(Serializable...)}.
     */
    @Test
    void testInOnItemArray() {
        final PreFetchedSubQueryExpression<TestResultType> subquery =
                new PreFetchedSubQueryExpression<>(metadata, TestResultType.class, values);
        final BooleanExpression result = subquery.in(otherValue);
        final BooleanExpression expected = Expressions.predicate(
                Ops.IN,
                Expressions.constant(otherValue),
                Expressions.constant(values));
        assertEquals(expected, result);
    }

    private static interface TestResultType
    extends Serializable {}
}
