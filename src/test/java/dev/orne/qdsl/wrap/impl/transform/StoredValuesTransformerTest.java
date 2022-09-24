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
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

import dev.orne.qdsl.TestTypes;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code StoredValuesTransformer}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see StoredValuesTransformer
 */
@Tag("ut")
class StoredValuesTransformerTest {

    private final static PathBuilder<Object> builder =
            new PathBuilder<>(Object.class, "bean");

    /**
     * Test for {@link StoredValuesTransformer#transform(Path, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void transformSimpleTest() {
        final Path<TestTypes.SimpleType> path = builder.getSimple("source", TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final Expression<TestTypes.SimpleType> newValue = mock(Expression.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.SimpleType> exprTranslator =
                mock(SimpleExpressionTransformer.class);
        willReturn(newValue).given(exprTranslator).apply(oldValue);
        final StoredValuesTransformer transformer = StoredValuesTransformer.transform(path, exprTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(path, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(newValue, values.get(path));
        then(exprTranslator).should().apply(oldValue);
        then(exprTranslator).shouldHaveNoMoreInteractions();
    }

    /**
     * Test for {@link StoredValuesTransformer#transform(Path, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void transformSimpleTest_Missing() {
        final Path<TestTypes.SimpleType> path = builder.getSimple("source", TestTypes.SimpleType.class);
        final Path<TestTypes.SimpleType> path2 = builder.getSimple("other", TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.SimpleType> exprTranslator =
                mock(SimpleExpressionTransformer.class);
        final StoredValuesTransformer transformer = StoredValuesTransformer.transform(path, exprTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(path2, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(oldValue, values.get(path2));
        then(exprTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link StoredValuesTransformer#transform(Path, ValueTransformer, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void transformCompositeExprTest() {
        final Path<TestTypes.SimpleType> path = builder.getSimple("source", TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final Expression<TestTypes.SimpleType> newValue = mock(Expression.class);
        final ValueTransformer<TestTypes.SimpleType, TestTypes.SimpleType> valueTranslator =
                mock(ValueTransformer.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.SimpleType> exprTranslator =
                mock(SimpleExpressionTransformer.class);
        willReturn(newValue).given(exprTranslator).apply(oldValue);
        final StoredValuesTransformer transformer = StoredValuesTransformer.transform(path, valueTranslator, exprTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(path, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(newValue, values.get(path));
        then(exprTranslator).should().apply(oldValue);
        then(exprTranslator).shouldHaveNoMoreInteractions();
        then(valueTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link StoredValuesTransformer#transform(Path, ValueTransformer, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void transformCompositeValueTest() {
        final Path<TestTypes.SimpleType> path = builder.getSimple("source", TestTypes.SimpleType.class);
        final TestTypes.SimpleType oldValue = mock(TestTypes.SimpleType.class);
        final TestTypes.SimpleType newValue = mock(TestTypes.SimpleType.class);
        final ValueTransformer<TestTypes.SimpleType, TestTypes.SimpleType> valueTranslator =
                mock(ValueTransformer.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.SimpleType> exprTranslator =
                mock(SimpleExpressionTransformer.class);
        willReturn(newValue).given(valueTranslator).apply(oldValue);
        final StoredValuesTransformer transformer = StoredValuesTransformer.transform(path, valueTranslator, exprTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(path, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(Expressions.constant(newValue), values.get(path));
        then(valueTranslator).should().apply(oldValue);
        then(valueTranslator).shouldHaveNoMoreInteractions();
        then(exprTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link StoredValuesTransformer#move(Path, Path)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void moveTest() {
        final Path<TestTypes.SimpleType> source = builder.getSimple("source", TestTypes.SimpleType.class);
        final Path<TestTypes.SimpleType> target = builder.getSimple("target", TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final StoredValuesTransformer transformer = StoredValuesTransformer.move(source, target);
        final StoredValues values = StoredValues.with(StoredValue.of(source, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(oldValue, values.get(target));
    }

    /**
     * Test for {@link StoredValuesTransformer#move(Path, Path)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void moveTest_Missing() {
        final Path<TestTypes.SimpleType> source = builder.getSimple("source", TestTypes.SimpleType.class);
        final Path<TestTypes.SimpleType> other = builder.getSimple("other", TestTypes.SimpleType.class);
        final Path<TestTypes.SimpleType> target = builder.getSimple("target", TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final StoredValuesTransformer transformer = StoredValuesTransformer.move(source, target);
        final StoredValues values = StoredValues.with(StoredValue.of(other, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(oldValue, values.get(other));
    }

    /**
     * Test for {@link StoredValuesTransformer#replace(Path, Path, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void replaceSimpleTest() {
        final Path<TestTypes.SimpleType> source = builder.getSimple("source", TestTypes.SimpleType.class);
        final Path<TestTypes.UnrelatedType> target = builder.getSimple("target", TestTypes.UnrelatedType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final Expression<TestTypes.UnrelatedType> newValue = mock(Expression.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.UnrelatedType> valueTranslator =
                mock(SimpleExpressionTransformer.class);
        willReturn(newValue).given(valueTranslator).apply(oldValue);
        final StoredValuesTransformer transformer = StoredValuesTransformer.replace(source, target, valueTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(source, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(newValue, values.get(target));
        then(valueTranslator).should().apply(oldValue);
        then(valueTranslator).shouldHaveNoMoreInteractions();
    }

    /**
     * Test for {@link StoredValuesTransformer#replace(Path, Path, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void replaceSimpleTest_Missing() {
        final Path<TestTypes.SimpleType> source = builder.getSimple("source", TestTypes.SimpleType.class);
        final Path<TestTypes.SimpleType> other = builder.getSimple("other", TestTypes.SimpleType.class);
        final Path<TestTypes.UnrelatedType> target = builder.getSimple("target", TestTypes.UnrelatedType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.UnrelatedType> valueTranslator =
                mock(SimpleExpressionTransformer.class);
        final StoredValuesTransformer transformer = StoredValuesTransformer.replace(source, target, valueTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(other, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(oldValue, values.get(other));
        then(valueTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link StoredValuesTransformer#replace(Path, Path, ValueTransformer, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void replaceCompositeExprTest() {
        final Path<TestTypes.SimpleType> source = builder.getSimple("source", TestTypes.SimpleType.class);
        final Path<TestTypes.UnrelatedType> target = builder.getSimple("target", TestTypes.UnrelatedType.class);
        final Expression<TestTypes.SimpleType> oldValue = mock(Expression.class);
        final Expression<TestTypes.UnrelatedType> newValue = mock(Expression.class);
        final ValueTransformer<TestTypes.SimpleType, TestTypes.UnrelatedType> valueTranslator =
                mock(ValueTransformer.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.UnrelatedType> exprTranslator =
                mock(SimpleExpressionTransformer.class);
        willReturn(newValue).given(exprTranslator).apply(oldValue);
        final StoredValuesTransformer transformer = StoredValuesTransformer.replace(source, target, valueTranslator, exprTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(source, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(newValue, values.get(target));
        then(exprTranslator).should().apply(oldValue);
        then(exprTranslator).shouldHaveNoMoreInteractions();
        then(valueTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link StoredValuesTransformer#replace(Path, Path, ValueTransformer, SimpleExpressionTransformer)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void replaceCompositeValueTest() {
        final Path<TestTypes.SimpleType> source = builder.getSimple("source", TestTypes.SimpleType.class);
        final Path<TestTypes.UnrelatedType> target = builder.getSimple("target", TestTypes.UnrelatedType.class);
        final TestTypes.SimpleType oldValue = mock(TestTypes.SimpleType.class);
        final TestTypes.UnrelatedType newValue = mock(TestTypes.UnrelatedType.class);
        final ValueTransformer<TestTypes.SimpleType, TestTypes.UnrelatedType> valueTranslator =
                mock(ValueTransformer.class);
        final SimpleExpressionTransformer<TestTypes.SimpleType, TestTypes.UnrelatedType> exprTranslator =
                mock(SimpleExpressionTransformer.class);
        willReturn(newValue).given(valueTranslator).apply(oldValue);
        final StoredValuesTransformer transformer = StoredValuesTransformer.replace(source, target, valueTranslator, exprTranslator);
        final StoredValues values = StoredValues.with(StoredValue.of(source, oldValue));
        transformer.visit(values, null);
        assertEquals(1, values.size());
        assertEquals(Expressions.constant(newValue), values.get(target));
        then(valueTranslator).should().apply(oldValue);
        then(valueTranslator).shouldHaveNoMoreInteractions();
        then(exprTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_AllEqual() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(path).given(path).accept(visitor, null);
        willReturn(expr).given(expr).accept(visitor, null);
        final StoredValue<?> result =
                StoredValuesTransformer.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(assignment, result);
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_PathTranslated() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(expr).given(expr).accept(visitor, null);
        final StoredValue<?> result =
                StoredValuesTransformer.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertSame(expr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_ValueTranslated() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(path).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        final StoredValue<?> result =
                StoredValuesTransformer.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(path, result.getPath());
        assertSame(translatedExpr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_AllTranslated() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        final StoredValue<?> result =
                StoredValuesTransformer.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertSame(translatedExpr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NullPath() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(null).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        final StoredValue<?> result =
                StoredValuesTransformer.translateFromComponents(assignment, visitor);
        assertNull(result);
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NullValue() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(null).given(expr).accept(visitor, null);
        final StoredValue<?> result =
                StoredValuesTransformer.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertNull(result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NotAPath() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        final Expression<TestTypes.SimpleType> translatedPath =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        assertThrows(IllegalArgumentException.class, () -> {
            StoredValuesTransformer.translateFromComponents(assignment, visitor);
        });
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link StoredValueTransformer#translateFromComponents(StoredValue, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NotAssignable() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> assignment =
                StoredValue.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.UnrelatedType> translatedExpr =
                TestTypes.expressionOf(TestTypes.UnrelatedType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        assertThrows(IllegalArgumentException.class, () -> {
            StoredValuesTransformer.translateFromComponents(assignment, visitor);
        });
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }
}
