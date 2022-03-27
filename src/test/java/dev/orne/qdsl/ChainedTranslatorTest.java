package dev.orne.qdsl;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Unit tests for {@code ChainedTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-03
 * @since 0.1
 * @see ChainedTranslator
 */
@Tag("ut")
class ChainedTranslatorTest {

    private static final PathBuilder<Object> ENTITY_PATH =
            new PathBuilder<Object>(Object.class, "entity");

    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_A =
            mock(Visitor.class);
    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_B =
            mock(Visitor.class);

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    /**
     * Unit test for {@link ChainedTranslator#ChainedTranslator(Visitor...)} with no arguments.
     */
    @Test
    void testEmptyVarargsConstructor() {
        final ChainedTranslator result = new ChainedTranslator();
        assertNotNull(result.getVisitors());
        assertEquals(0, result.getVisitors().size());
    }

    /**
     * Unit test for {@link ChainedTranslator#ChainedTranslator(Visitor...)}.
     */
    @Test
    void testVarargsConstructor() {
        assertThrows(IllegalArgumentException.class, ()-> {
            new ChainedTranslator(VISITOR_A, null);
        });
        final ChainedTranslator result = new ChainedTranslator(
                VISITOR_A, VISITOR_B);
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
    }

    /**
     * Unit test for {@link ChainedTranslator#ChainedTranslator(Collection)}.
     */
    @Test
    void testCollectionConstructor() {
        assertThrows(NullPointerException.class, ()-> {
            new ChainedTranslator((Collection<Visitor<Expression<?>, ?>>) null);
        });
        final ChainedTranslator result = new ChainedTranslator(
                Arrays.asList(VISITOR_A, VISITOR_B));
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
    }

    /**
     * Unit test for {@link ChainedTranslator#translateProjection(Expression)}.
     */
    @Test
    void testTranslateProjection() {
        final Expression<?> expr = mock(Expression.class);
        final Expression<?> mockResult = mock(Expression.class);
        final ChainedTranslator translator = new ChainedTranslator();
        willReturn(mockResult).given(expr).accept(translator, null);
        willReturn(TYPE.class).given(expr).getType();
        willReturn(TYPE.class).given(mockResult).getType();
        final Expression<?> result = translator.translateProjection(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link ChainedTranslator#translateProjection(Expression)}.
     */
    @Test
    void testTranslateProjectionNullResult() {
        final Expression<?> expr = mock(Expression.class);
        final ChainedTranslator translator = new ChainedTranslator();
        given(expr.accept(translator, null)).willReturn(null);
        assertThrows(QueryTranslationException.class, () -> {
            translator.translateProjection(expr);
        });
    }

    /**
     * Unit test for {@link ChainedTranslator#translateProjection(Expression)}.
     */
    @Test
    void testTranslateProjectionWrongType() {
        final Expression<?> expr = mock(Expression.class);
        final Expression<?> mockResult = mock(Expression.class);
        final ChainedTranslator translator = new ChainedTranslator();
        willReturn(mockResult).given(expr).accept(translator, null);
        willReturn(TYPE.class).given(expr).getType();
        willReturn(WRONG_TYPE.class).given(mockResult).getType();
        assertThrows(QueryTranslationException.class, () -> {
            translator.translateProjection(expr);
        });
    }

    /**
     * Unit test for {@link ChainedTranslator#translateProjections(Expression...)}.
     */
    @Test
    void testTranslateProjections() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] mockResults = new Expression<?>[count];
        final ChainedTranslator translator = spy(new ChainedTranslator());
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
            mockResults[i] = mock(Expression.class);
            willReturn(mockResults[i]).given(translator).translateProjection(exprs[i]);
        }
        final Expression<?>[] result = translator.translateProjections(exprs);
        assertArrayEquals(mockResults, result);
    }

    /**
     * Unit test for {@link ChainedTranslator#translatePredicate(Predicate)}.
     */
    @Test
    void testTranslatePredicate() {
        final Predicate expr = mock(Predicate.class);
        final Predicate mockResult = mock(Predicate.class);
        final ChainedTranslator translator = new ChainedTranslator();
        willReturn(mockResult).given(expr).accept(translator, null);
        final Expression<?> result = translator.translatePredicate(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link ChainedTranslator#translatePredicate(Predicate)}.
     */
    @Test
    void testTranslatePredicateNullResult() {
        final Predicate expr = mock(Predicate.class);
        final ChainedTranslator translator = new ChainedTranslator();
        given(expr.accept(translator, null)).willReturn(null);
        assertThrows(QueryTranslationException.class, () -> {
            translator.translatePredicate(expr);
        });
    }

    /**
     * Unit test for {@link ChainedTranslator#translatePredicate(Predicate)}.
     */
    @Test
    void testTranslatePredicateWrongType() {
        final Predicate expr = mock(Predicate.class);
        final Expression<?> mockResult = mock(Expression.class);
        final ChainedTranslator translator = new ChainedTranslator();
        willReturn(mockResult).given(expr).accept(translator, null);
        assertThrows(QueryTranslationException.class, () -> {
            translator.translatePredicate(expr);
        });
    }

    /**
     * Unit test for {@link ChainedTranslator#translatePredicates(Predicate...)}.
     */
    @Test
    void testTranslatePredicates() {
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        final Predicate[] mockResults = new Predicate[count];
        final ChainedTranslator translator = spy(new ChainedTranslator());
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
            mockResults[i] = mock(Predicate.class);
            willReturn(mockResults[i]).given(translator).translatePredicate(exprs[i]);
        }
        final Predicate[] result = translator.translatePredicates(exprs);
        assertArrayEquals(mockResults, result);
    }

    /**
     * Unit test for {@link ChainedTranslator#translateOrderSpecifier(OrderSpecifier)}.
     */
    @Test
    void testTranslateOrderSpecifier() {
        final OrderSpecifier<?> expr = mock(OrderSpecifier.class);
        final List<OrderSpecifier<?>> mockResult = Arrays.asList(
                mock(OrderSpecifier.class));
        final ChainedTranslator translator = spy(new ChainedTranslator());
        willReturn(mockResult).given(translator).visit(expr, null);
        final List<OrderSpecifier<?>> result = translator.translateOrderSpecifier(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link ChainedTranslator#translateOrderSpecifier(OrderSpecifier)}.
     */
    @Test
    void testTranslateOrderSpecifierNullResult() {
        final OrderSpecifier<?> expr = mock(OrderSpecifier.class);
        final ChainedTranslator translator = spy(new ChainedTranslator());
        willReturn(null).given(translator).visit(expr, null);
        assertThrows(QueryTranslationException.class, () -> {
            translator.translateOrderSpecifier(expr);
        });
    }

    /**
     * Unit test for {@link ChainedTranslator#translateOrderSpecifiers(OrderSpecifier...)}.
     */
    @Test
    void testTranslateOrderSpecifiers() {
        final int count = RandomUtils.nextInt(2, 10);
        final OrderSpecifier<?>[] exprs = new OrderSpecifier<?>[count];
        final List<List<OrderSpecifier<?>>> partialResults = new ArrayList<>(count);
        final List<OrderSpecifier<?>> expectedResult = new ArrayList<>();
        final ChainedTranslator translator = spy(new ChainedTranslator());
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(OrderSpecifier.class);
            final List<OrderSpecifier<?>> pResult = new ArrayList<>();
            final int pResultElems = RandomUtils.nextInt(1, 3);
            for (int j = 0; j < pResultElems; j++) {
                final OrderSpecifier<?> pResultElem = mock(OrderSpecifier.class);
                pResult.add(pResultElem);
                expectedResult.add(pResultElem);
            }
            partialResults.add(pResult);
            willReturn(pResult).given(translator).translateOrderSpecifier(exprs[i]);
        }
        final OrderSpecifier<?>[] result = translator.translateOrderSpecifiers(exprs);
        assertEquals(expectedResult, Arrays.asList(result));
    }

    /**
     * Unit test for {@link NopTranslator#translateAssigment(ValueAssignment)}.
     */
    @Test
    void testTranslateAssigment() {
        final Path<String> path = ENTITY_PATH.getString("path");
        final Expression<String> value = Expressions.asString("value");
        final ValueAssignment<?> expr = spy(ValueAssignment.of(path, value));
        final ValueAssignments mockResult = mock(ValueAssignments.class);
        final ChainedTranslator translator = new ChainedTranslator();
        willReturn(mockResult).given(expr).accept(translator, null);
        final ValueAssignments result = translator.translateAssigment(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link NopTranslator#translateAssigment(ValueAssignment)}.
     */
    @Test
    void testTranslateAssigmentNullResult() {
        final Path<String> path = ENTITY_PATH.getString("path");
        final Expression<String> value = Expressions.asString("value");
        final ValueAssignment<?> expr = spy(ValueAssignment.of(path, value));
        final ChainedTranslator translator = new ChainedTranslator();
        willReturn(null).given(expr).accept(translator, null);
        assertThrows(QueryTranslationException.class, () -> {
            translator.translateAssigment(expr);
        });
    }

    /**
     * Unit test for {@link NopTranslator#translateAssigments(ValueAssignment...)}.
     */
    @Test
    void testTranslateAssigmentArray() {
        final int count = RandomUtils.nextInt(2, 10);
        final ValueAssignment<?>[] exprs = new ValueAssignment<?>[count];
        final List<ValueAssignments> partialResults = new ArrayList<>(count);
        final ValueAssignments expectedResult = new ValueAssignments();
        final ChainedTranslator translator = spy(new ChainedTranslator());
        for (int i = 0; i < count; i++) {
            final Path<String> path = ENTITY_PATH.getString("path" + i);
            final Expression<String> value = Expressions.asString("value" + i);
            final ValueAssignment<?> expr = ValueAssignment.of(path, value);
            exprs[i] = expr;
            final ValueAssignments pResult = new ValueAssignments();
            final int pResultElems = RandomUtils.nextInt(1, 3);
            for (int j = 0; j < pResultElems; j++) {
                final Path<String> pResultElemPath = ENTITY_PATH.getString("path" + i + "result" + j);
                final Expression<String> pResultElemValue = Expressions.asString("value" + 1);
                final ValueAssignment<?> pResultElem = ValueAssignment.of(pResultElemPath, pResultElemValue);
                pResult.add(pResultElem);
                expectedResult.add(pResultElem);
            }
            partialResults.add(pResult);
            willReturn(pResult).given(translator).translateAssigment(expr);
        }
        final ValueAssignments result = translator.translateAssigments(exprs);
        assertEquals(expectedResult, result);
    }

    /**
     * Unit test for {@link NopTranslator#translateAssigments(ValueAssignments)}.
     */
    @Test
    void testTranslateAssigments() {
        final int count = RandomUtils.nextInt(2, 10);
        final ValueAssignments exprs = new ValueAssignments(count);
        final List<ValueAssignments> partialResults = new ArrayList<>(count);
        final ValueAssignments expectedResult = new ValueAssignments();
        final ChainedTranslator translator = spy(new ChainedTranslator());
        for (int i = 0; i < count; i++) {
            final Path<String> path = ENTITY_PATH.getString("path" + i);
            final Expression<String> value = Expressions.asString("value" + i);
            final ValueAssignment<?> expr = ValueAssignment.of(path, value);
            exprs.add(expr);
            final ValueAssignments pResult = new ValueAssignments();
            final int pResultElems = RandomUtils.nextInt(1, 3);
            for (int j = 0; j < pResultElems; j++) {
                final Path<String> pResultElemPath = ENTITY_PATH.getString("path" + i + "result" + j);
                final Expression<String> pResultElemValue = Expressions.asString("value" + 1);
                final ValueAssignment<?> pResultElem = ValueAssignment.of(pResultElemPath, pResultElemValue);
                pResult.add(pResultElem);
                expectedResult.add(pResultElem);
            }
            partialResults.add(pResult);
            willReturn(pResult).given(translator).translateAssigment(expr);
        }
        final ValueAssignments result = translator.translateAssigments(exprs);
        assertEquals(expectedResult, result);
    }

    private interface TYPE {}
    private interface WRONG_TYPE {}
}
