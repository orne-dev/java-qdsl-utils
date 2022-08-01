package dev.orne.qdsl.wrap.impl.transform;

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
import org.mockito.InOrder;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

import dev.orne.qdsl.TestTypes;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;
import dev.orne.qdsl.wrap.impl.ExpressionTransformationException;

/**
 * Unit tests for {@code ChainedExpressionTransformer}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see ChainedExpressionTransformer
 */
@Tag("ut")
class ChainedExpressionTransformerTest {

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
     * Unit test for {@link ChainedExpressionTransformer#ChainedExpressionTransformer(Visitor...)} with no arguments.
     */
    @Test
    void testEmptyVarargsConstructor() {
        final ChainedExpressionTransformer result = new ChainedExpressionTransformer();
        assertNotNull(result.getVisitors());
        assertEquals(0, result.getVisitors().size());
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#ChainedExpressionTransformer(Visitor...)}.
     */
    @Test
    void testVarargsConstructor() {
        assertThrows(IllegalArgumentException.class, ()-> {
            new ChainedExpressionTransformer(VISITOR_A, null);
        });
        final ChainedExpressionTransformer result = new ChainedExpressionTransformer(
                VISITOR_A, VISITOR_B);
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#ChainedExpressionTransformer(Collection)}.
     */
    @Test
    void testCollectionConstructor() {
        assertThrows(NullPointerException.class, ()-> {
            new ChainedExpressionTransformer((Collection<Visitor<Expression<?>, ?>>) null);
        });
        final ChainedExpressionTransformer result = new ChainedExpressionTransformer(
                Arrays.asList(VISITOR_A, VISITOR_B));
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(OrderSpecifier, Void)}.
     */
    @Test
    void visitOrderSpecifier_Simple() {
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                VISITOR_A, VISITOR_B);
        final Expression<TestTypes.ComparableType> initialExpr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> initial =
                TestTypes.randomOrderSpecifier(initialExpr);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(TestTypes.ComparableType.class).given(partialResultA).getType();
        willReturn(TestTypes.ComparableType.class).given(partialResultB).getType();
        willReturn(partialResultA).given(initialExpr).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final List<OrderSpecifier<?>> result = translator.visit(initial, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(partialResultB, result.get(0).getTarget());
        assertSame(initial.getOrder(), result.get(0).getOrder());
        assertSame(initial.getNullHandling(), result.get(0).getNullHandling());
        then(initialExpr).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(OrderSpecifier, Void)}.
     */
    @Test
    void visitOrderSpecifier_OrderSpecifierReplaceVisitor() {
        final TestTypes.OrderSpecifierReplacer visitorA =
                mock(TestTypes.OrderSpecifierReplacer.class);
        final TestTypes.OrderSpecifierReplacer visitorB = mock(
                TestTypes.OrderSpecifierReplacer.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                visitorA, visitorB);
        final OrderSpecifier<?> initial =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultA1 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultA2 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB11 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB12 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB21 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB22 =
                TestTypes.randomOrderSpecifier();
        willReturn(Arrays.asList(
            partialResultA1, partialResultA2
        )).given(visitorA).visit(initial, null);
        willReturn(Arrays.asList(
            partialResultB11, partialResultB12
        )).given(visitorB).visit(partialResultA1, null);
        willReturn(Arrays.asList(
            partialResultB21, partialResultB22
        )).given(visitorB).visit(partialResultA2, null);
        final List<OrderSpecifier<?>> result = translator.visit(initial, null);
        assertNotNull(result);
        assertEquals(4, result.size());
        assertSame(partialResultB11, result.get(0));
        assertSame(partialResultB12, result.get(1));
        assertSame(partialResultB21, result.get(2));
        assertSame(partialResultB22, result.get(3));
        then(visitorA).should().visit(initial, null);
        then(visitorB).should().visit(partialResultA1, null);
        then(visitorB).should().visit(partialResultA2, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(OrderSpecifier, Void)}.
     */
    @Test
    void visitOrderSpecifier_Mixed() {
        final TestTypes.OrderSpecifierReplacer visitorA =
                mock(TestTypes.OrderSpecifierReplacer.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                visitorA, VISITOR_B);
        final OrderSpecifier<?> initial =
                TestTypes.randomOrderSpecifier();
        final Expression<TestTypes.ComparableType> partialExprA1 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Expression<TestTypes.ComparableType> partialExprA2 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> partialResultA1 =
                TestTypes.randomOrderSpecifier(partialExprA1);
        final OrderSpecifier<?> partialResultA2 =
                TestTypes.randomOrderSpecifier(partialExprA2);
        final Expression<TestTypes.ComparableType> partialExprB1 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Expression<TestTypes.ComparableType> partialExprB2 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        willReturn(Arrays.asList(
            partialResultA1, partialResultA2
        )).given(visitorA).visit(initial, null);
        willReturn(partialExprB1).given(partialExprA1).accept(VISITOR_B, null);
        willReturn(partialExprB2).given(partialExprA2).accept(VISITOR_B, null);
        final List<OrderSpecifier<?>> result = translator.visit(initial, null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(partialExprB1, result.get(0).getTarget());
        assertSame(partialResultA1.getOrder(), result.get(0).getOrder());
        assertSame(partialResultA1.getNullHandling(), result.get(0).getNullHandling());
        assertSame(partialExprB2, result.get(1).getTarget());
        assertSame(partialResultA2.getOrder(), result.get(1).getOrder());
        assertSame(partialResultA2.getNullHandling(), result.get(1).getNullHandling());
        then(visitorA).should().visit(initial, null);
        then(partialResultA1.getTarget()).should().accept(VISITOR_B, null);
        then(partialResultA2.getTarget()).should().accept(VISITOR_B, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(StoredValues, Void)}.
     */
    @Test
    void visitStoredValues_Components() {
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                VISITOR_A, VISITOR_B);
        final Path<TYPE> initialPath =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> initialValue =
                TestTypes.expressionOf(TYPE.class);
        final StoredValue<?> initial = StoredValue.of(initialPath, initialValue);
        final StoredValues initials = StoredValues.with(initial);
        final Path<TYPE> partialPathA =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> partialValueA =
                TestTypes.expressionOf(TYPE.class);
        final Path<TYPE> partialPathB =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> partialValueB =
                TestTypes.expressionOf(TYPE.class);
        willReturn(partialPathA).given(initialPath).accept(VISITOR_A, null);
        willReturn(partialValueA).given(initialValue).accept(VISITOR_A, null);
        willReturn(partialPathB).given(partialPathA).accept(VISITOR_B, null);
        willReturn(partialValueB).given(partialValueA).accept(VISITOR_B, null);
        final StoredValues result = translator.visit(initials, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(partialValueB, result.get(partialPathB));
        then(initialPath).should().accept(VISITOR_A, null);
        then(initialValue).should().accept(VISITOR_A, null);
        then(partialPathA).should().accept(VISITOR_B, null);
        then(partialValueA).should().accept(VISITOR_B, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(StoredValues, Void)}.
     */
    @Test
    void visitStoredValues_StoredValuesTransformer() {
        final TestTypes.StoredValuesTransformerVisitor visitorA = mock(TestTypes.StoredValuesTransformerVisitor.class);
        final TestTypes.StoredValuesTransformerVisitor visitorB = mock(TestTypes.StoredValuesTransformerVisitor.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                visitorA, visitorB);
        final StoredValues values = new StoredValues();
        final StoredValues result = translator.visit(values, null);
        assertSame(values, result);
        then(visitorA).should().visit(values, null);
        then(visitorB).should().visit(values, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(StoredValues, Void)}.
     */
    @Test
    void visitStoredValues_StoredValuesReplaceVisitor() {
        final TestTypes.StoredValuesReplacer visitorA = mock(TestTypes.StoredValuesReplacer.class);
        final TestTypes.StoredValuesReplacer visitorB = mock(TestTypes.StoredValuesReplacer.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                visitorA, visitorB);
        final StoredValues values = new StoredValues();
        final StoredValues partialA = new StoredValues();
        final StoredValues expected = new StoredValues();
        willReturn(partialA).given(visitorA).visit(values, null);
        willReturn(expected).given(visitorB).visit(partialA, null);
        final StoredValues result = translator.visit(values, null);
        assertSame(expected, result);
        then(visitorA).should().visit(values, null);
        then(visitorB).should().visit(partialA, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(StoredValues, Void)}.
     */
    @Test
    void visitStoredValues_StoredValueReplaceVisitor() {
        final TestTypes.StoredValueReplacer visitorA = spy(TestTypes.StoredValueReplacer.class);
        final TestTypes.StoredValueReplacer visitorB = spy(TestTypes.StoredValueReplacer.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                visitorA, visitorB);
        final Path<TYPE> initialPath =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> initialValue =
                TestTypes.expressionOf(TYPE.class);
        final StoredValue<?> initial = StoredValue.of(initialPath, initialValue);
        final StoredValues initials = StoredValues.with(initial);
        final Path<TYPE> partialPathA =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> partialValueA =
                TestTypes.expressionOf(TYPE.class);
        final Path<TYPE> partialPathB =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> partialValueB =
                TestTypes.expressionOf(TYPE.class);
        final StoredValue<?> partialA = StoredValue.of(partialPathA, partialValueA);
        final StoredValue<?> partialB = StoredValue.of(partialPathB, partialValueB);
        final StoredValues partials1 = StoredValues.with(partialA, partialB);
        final Path<TYPE> expectedPathA =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> expectedValueA =
                TestTypes.expressionOf(TYPE.class);
        final Path<OTHER_TYPE> expectedPathB1 =
                TestTypes.pathOf(OTHER_TYPE.class);
        final Expression<OTHER_TYPE> expectedValueB1 =
                TestTypes.expressionOf(OTHER_TYPE.class);
        final Path<OTHER_TYPE> expectedPathB2 =
                TestTypes.pathOf(OTHER_TYPE.class);
        final Expression<OTHER_TYPE> expectedValueB2 =
                TestTypes.expressionOf(OTHER_TYPE.class);
        final StoredValue<?> expectedA = StoredValue.of(expectedPathA, expectedValueA);
        final StoredValue<?> expectedB1 = StoredValue.of(expectedPathB1, expectedValueB1);
        final StoredValue<?> expectedB2 = StoredValue.of(expectedPathB2, expectedValueB2);
        final StoredValues partialsA2 = StoredValues.with(expectedA);
        final StoredValues partialsB2 = StoredValues.with(expectedB1, expectedB2);
        willReturn(partials1).given(visitorA).visit(initial, null);
        willReturn(partialsA2).given(visitorB).visit(partialA, null);
        willReturn(partialsB2).given(visitorB).visit(partialB, null);
        final StoredValues result = translator.visit(initials, null);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedValueA, result.get(expectedPathA));
        assertEquals(expectedValueB1, result.get(expectedPathB1));
        assertEquals(expectedValueB2, result.get(expectedPathB2));
        then(visitorA).should().visit(initial, null);
        then(visitorB).should().visit(partialA, null);
        then(visitorB).should().visit(partialB, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#visit(StoredValues, Void)}.
     */
    @Test
    void visitStoredValues_Mixed() {
        final TestTypes.StoredValuesReplacer visitorB = mock(TestTypes.StoredValuesReplacer.class);
        final TestTypes.StoredValuesTransformerVisitor visitorC = mock(TestTypes.StoredValuesTransformerVisitor.class);
        final TestTypes.StoredValueReplacer visitorD = spy(TestTypes.StoredValueReplacer.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer(
                VISITOR_A, visitorB, visitorC, visitorD);
        final Path<TYPE> initialPath =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> initialValue =
                TestTypes.expressionOf(TYPE.class);
        final StoredValues initials = StoredValues.with();
        initials.add(initialPath, initialValue);
        final Path<TYPE> partialPathA =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> partialValueA =
                TestTypes.expressionOf(TYPE.class);
        final StoredValues partialsA = StoredValues.with();
        partialsA.add(partialPathA, partialValueA);
        final Path<TYPE> partialPathB1 =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> partialValueB1 =
                TestTypes.expressionOf(TYPE.class);
        final StoredValue<?> partialB1 = StoredValue.of(partialPathB1, partialValueB1);
        final Path<OTHER_TYPE> partialPathB2 =
                TestTypes.pathOf(OTHER_TYPE.class);
        final Expression<OTHER_TYPE> partialValueB2 =
                TestTypes.expressionOf(OTHER_TYPE.class);
        final StoredValue<?> partialB2 = StoredValue.of(partialPathB2, partialValueB2);
        final StoredValues partialsB = StoredValues.with();
        partialsB.add(partialB1);
        partialsB.add(partialB2);
        final Path<TYPE> partialPathC1 =
                TestTypes.pathOf(TYPE.class);
        final Expression<TYPE> partialValueC1 =
                TestTypes.expressionOf(TYPE.class);
        final Path<OTHER_TYPE> partialPathC2 =
                TestTypes.pathOf(OTHER_TYPE.class);
        final Expression<OTHER_TYPE> partialValueC2 =
                TestTypes.expressionOf(OTHER_TYPE.class);
        final StoredValues partialsC1 = StoredValues.with(partialB1);
        final StoredValues partialsC2 = StoredValues.with();
        partialsC2.add(partialPathC1, partialValueC1);
        partialsC2.add(partialPathC2, partialValueC2);
        final StoredValues expected = StoredValues.with();
        expected.add(partialB1);
        expected.add(partialPathC1, partialValueC1);
        expected.add(partialPathC2, partialValueC2);
        willReturn(partialPathA).given(initialPath).accept(VISITOR_A, null);
        willReturn(partialValueA).given(initialValue).accept(VISITOR_A, null);
        willReturn(partialsB).given(visitorB).visit(partialsA, null);
        willReturn(partialsC1).given(visitorD).visit(partialB1, null);
        willReturn(partialsC2).given(visitorD).visit(partialB2, null);
        final InOrder order = inOrder(initialPath, initialValue, visitorB, visitorC, visitorD);
        final StoredValues result = translator.visit(initials, null);
        assertNotNull(result);
        assertEquals(expected, result);
        then(initialPath).should(order).accept(VISITOR_A, null);
        then(initialValue).should(order).accept(VISITOR_A, null);
        then(visitorB).should(order).visit(partialsA, null);
        then(visitorC).should(order).visit(partialsB, null);
        then(visitorD).should().visit(partialB1, null);
        then(visitorD).should().visit(partialB2, null);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateProjection(Expression)}.
     */
    @Test
    void testTranslateProjection() {
        final Expression<?> expr = TestTypes.expressionOf(TYPE.class);
        final Expression<?> mockResult = TestTypes.expressionOf(TYPE.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        willReturn(mockResult).given(expr).accept(translator, null);
        final Expression<?> result = translator.translateProjection(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateProjection(Expression)}.
     * <p>
     * Verifies that when the resulting expression is {@code null} an
     * {@code ExpressionTransformationException} is thrown.
     */
    @Test
    void testTranslateProjectionNullResult() {
        final Expression<?> expr = mock(Expression.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        given(expr.accept(translator, null)).willReturn(null);
        assertThrows(ExpressionTransformationException.class, () -> {
            translator.translateProjection(expr);
        });
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateProjection(Expression)}.
     * <p>
     * Verifies that when the resulting expression type is not assignable to the original
     * expression type an {@code ExpressionTransformationException} is thrown.
     */
    @Test
    void testTranslateProjectionWrongType() {
        final Expression<?> expr = TestTypes.expressionOf(TYPE.class);
        final Expression<?> mockResult = TestTypes.expressionOf(WRONG_TYPE.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        willReturn(mockResult).given(expr).accept(translator, null);
        assertThrows(ExpressionTransformationException.class, () -> {
            translator.translateProjection(expr);
        });
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateProjections(Expression...)}.
     */
    @Test
    void testTranslateProjections() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] mockResults = new Expression<?>[count];
        final ChainedExpressionTransformer translator = spy(new ChainedExpressionTransformer());
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
            mockResults[i] = mock(Expression.class);
            willReturn(mockResults[i]).given(translator).translateProjection(exprs[i]);
        }
        final Expression<?>[] result = translator.translateProjections(exprs);
        assertArrayEquals(mockResults, result);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translatePredicate(Predicate)}.
     */
    @Test
    void testTranslatePredicate() {
        final Predicate expr = mock(Predicate.class);
        final Predicate mockResult = mock(Predicate.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        willReturn(mockResult).given(expr).accept(translator, null);
        final Expression<?> result = translator.translatePredicate(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translatePredicate(Predicate)}.
     * <p>
     * Verifies that when the resulting predicate is {@code null} an
     * {@code ExpressionTransformationException} is thrown.
     */
    @Test
    void testTranslatePredicateNullResult() {
        final Predicate expr = mock(Predicate.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        given(expr.accept(translator, null)).willReturn(null);
        assertThrows(ExpressionTransformationException.class, () -> {
            translator.translatePredicate(expr);
        });
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translatePredicate(Predicate)}.
     * <p>
     * Verifies that when the resulting expression is not a {@code Predicate} an
     * {@code ExpressionTransformationException} is thrown.
     */
    @Test
    void testTranslatePredicateWrongType() {
        final Predicate expr = mock(Predicate.class);
        final Expression<?> mockResult = mock(Expression.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        willReturn(mockResult).given(expr).accept(translator, null);
        assertThrows(ExpressionTransformationException.class, () -> {
            translator.translatePredicate(expr);
        });
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translatePredicates(Predicate...)}.
     */
    @Test
    void testTranslatePredicates() {
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        final Predicate[] mockResults = new Predicate[count];
        final ChainedExpressionTransformer translator = spy(new ChainedExpressionTransformer());
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
            mockResults[i] = mock(Predicate.class);
            willReturn(mockResults[i]).given(translator).translatePredicate(exprs[i]);
        }
        final Predicate[] result = translator.translatePredicates(exprs);
        assertArrayEquals(mockResults, result);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateOrderSpecifier(OrderSpecifier)}.
     */
    @Test
    void testTranslateOrderSpecifier() {
        final OrderSpecifier<?> expr = mock(OrderSpecifier.class);
        final List<OrderSpecifier<?>> mockResult = Arrays.asList(
                mock(OrderSpecifier.class));
        final ChainedExpressionTransformer translator = spy(new ChainedExpressionTransformer());
        willReturn(mockResult).given(translator).visit(expr, null);
        final List<OrderSpecifier<?>> result = translator.translateOrderSpecifier(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateOrderSpecifier(OrderSpecifier)}.
     * <p>
     * Verifies that when the resulting order specifier is {@code null} an
     * {@code ExpressionTransformationException} is thrown.
     */
    @Test
    void testTranslateOrderSpecifierNullResult() {
        final OrderSpecifier<?> expr = mock(OrderSpecifier.class);
        final ChainedExpressionTransformer translator = spy(new ChainedExpressionTransformer());
        willReturn(null).given(translator).visit(expr, null);
        assertThrows(ExpressionTransformationException.class, () -> {
            translator.translateOrderSpecifier(expr);
        });
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateOrderSpecifiers(OrderSpecifier...)}.
     */
    @Test
    void testTranslateOrderSpecifiers() {
        final int count = RandomUtils.nextInt(2, 10);
        final OrderSpecifier<?>[] exprs = new OrderSpecifier<?>[count];
        final List<List<OrderSpecifier<?>>> partialResults = new ArrayList<>(count);
        final List<OrderSpecifier<?>> expectedResult = new ArrayList<>();
        final ChainedExpressionTransformer translator = spy(new ChainedExpressionTransformer());
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
     * Unit test for {@link ChainedExpressionTransformer#translateGroupByExpression(Expression)}.
     */
    @Test
    void testTranslateGroupByExpression() {
        final Expression<?> expr = TestTypes.expressionOf(TYPE.class);
        final Expression<?> mockResult = TestTypes.expressionOf(TYPE.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        willReturn(mockResult).given(expr).accept(translator, null);
        final Expression<?> result = translator.translateGroupByExpression(expr);
        assertSame(mockResult, result);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateGroupByExpressions(Expression...)}.
     */
    @Test
    void testGroupByExpressions() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] mockResults = new Expression<?>[count];
        final ChainedExpressionTransformer translator = spy(new ChainedExpressionTransformer());
        for (int i = 0; i < count; i++) {
            exprs[i] = TestTypes.expressionOf(TYPE.class);
            mockResults[i] = TestTypes.expressionOf(TYPE.class);
            willReturn(mockResults[i]).given(translator).translateGroupByExpression(exprs[i]);
        }
        final Expression<?>[] result = translator.translateGroupByExpressions(exprs);
        assertArrayEquals(mockResults, result);
    }

    /**
     * Unit test for {@link ChainedExpressionTransformer#translateStoredValues(StoredValue)}.
     */
    @Test
    void testTranslateStoredValues() {
        final StoredValues expr = spy(StoredValues.with());
        final StoredValues mockResult = mock(StoredValues.class);
        final ChainedExpressionTransformer translator = new ChainedExpressionTransformer();
        willReturn(mockResult).given(expr).accept(translator, null);
        final StoredValues result = translator.translateStoredValues(expr);
        assertSame(mockResult, result);
    }

    private interface TYPE {}
    private interface OTHER_TYPE {}
    private interface WRONG_TYPE {}
}
