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
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Unit tests for {@code ValueAssignmentTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-11
 * @since 0.1
 * @see ValueAssignmentTranslator
 */
@Tag("ut")
class ValueAssignmentTranslatorTest {

    private final static PathBuilder<Object> builder =
            new PathBuilder<>(Object.class, "bean");

    /**
     * Test for {@link ValueAssignmentTranslator#identity(Path)}.
     */
    @Test
    void identityTest() {
        final Path<Object> target = builder.getSimple("target", Object.class);
        final ValueAssignmentTranslator<Object> translator = ValueAssignmentTranslator.identity(target);
        assertNotNull(translator);
        final Expression<Object> value = builder.getSimple("value", Object.class);
        final ValueAssignments result = translator.apply(value);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(value, result.get(target));
    }

    /**
     * Test for {@link ValueAssignmentTranslator#toSingleValue(Path, ValueTranslator, ExpressionTranslator, Expression)}.
     */
    @Test
    void toSingleValueConstantTest() {
        final Path<Object> target = builder.getSimple("target", Object.class);
        final Object value = new Object();
        final Expression<Object> valueConst = Expressions.constant(value);
        @SuppressWarnings("unchecked")
        final ValueTranslator<Object, Object> valueTranslator = mock(ValueTranslator.class);
        final Object expectedValue = new Object();
        willReturn(expectedValue).given(valueTranslator).apply(value);
        @SuppressWarnings("unchecked")
        final ExpressionTranslator<Object, Object> expressionTranslator = mock(ExpressionTranslator.class);
        final ValueAssignment<?> result = ValueAssignmentTranslator.toSingleValue(
                target,
                valueTranslator,
                expressionTranslator,
                valueConst);
        assertNotNull(result);
        assertEquals(target, result.getPath());
        assertInstanceOf(Constant.class, result.getValue());
        assertEquals(expectedValue, ((Constant<?>) result.getValue()).getConstant());
        then(valueTranslator).should().apply(value);
        then(expressionTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ValueAssignmentTranslator#toSingleValue(Path, ValueTranslator, ExpressionTranslator, Expression)}.
     */
    @Test
    void toSingleValueExpressionTest() {
        final Path<Object> target = builder.getSimple("target", Object.class);
        final Expression<Object> value = builder.getSimple("value", Object.class);
        @SuppressWarnings("unchecked")
        final ValueTranslator<Object, Object> valueTranslator = mock(ValueTranslator.class);
        @SuppressWarnings("unchecked")
        final ExpressionTranslator<Object, Object> expressionTranslator = mock(ExpressionTranslator.class);
        final Expression<Object> expectedValue = builder.getSimple("expectedValue", Object.class);
        willReturn(expectedValue).given(expressionTranslator).apply(value);
        final ValueAssignment<?> result = ValueAssignmentTranslator.toSingleValue(
                target,
                valueTranslator ,
                expressionTranslator,
                value);
        assertNotNull(result);
        assertEquals(target, result.getPath());
        assertEquals(expectedValue, result.getValue());
        then(expressionTranslator).should().apply(value);
        then(valueTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ValueAssignmentTranslator#forPath(Path, ExpressionTranslator)}.
     */
    @Test
    void forPathSimpleTest() {
        final Path<Object> target = builder.getSimple("target", Object.class);
        @SuppressWarnings("unchecked")
        final ExpressionTranslator<Object, Object> expressionTranslator = mock(ExpressionTranslator.class);
        final Expression<Object> value = builder.getSimple("value", Object.class);
        final Expression<Object> expectedValue = builder.getSimple("expectedValue", Object.class);
        willReturn(expectedValue).given(expressionTranslator).apply(value);
        final ValueAssignmentTranslator<Object> translator = ValueAssignmentTranslator.forPath(target, expressionTranslator);
        assertNotNull(translator);
        final ValueAssignments result = translator.apply(value);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedValue, result.get(target));
        then(expressionTranslator).should().apply(value);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#forPath(Path, ValueTranslator, ExpressionTranslator)}.
     */
    @Test
    void forPathConstantTest() {
        final Path<Object> target = builder.getSimple("target", Object.class);
        final Object value = new Object();
        final Expression<Object> valueConst = Expressions.constant(value);
        @SuppressWarnings("unchecked")
        final ValueTranslator<Object, Object> valueTranslator = mock(ValueTranslator.class);
        final Object expectedValue = new Object();
        willReturn(expectedValue).given(valueTranslator).apply(value);
        @SuppressWarnings("unchecked")
        final ExpressionTranslator<Object, Object> expressionTranslator = mock(ExpressionTranslator.class);
        final ValueAssignmentTranslator<Object> translator = ValueAssignmentTranslator.forPath(target, valueTranslator, expressionTranslator);
        assertNotNull(translator);
        final ValueAssignments result = translator.apply(valueConst);
        assertNotNull(result);
        assertEquals(1, result.size());
        final Expression<?> resultValue = result.get(target);
        assertInstanceOf(Constant.class, resultValue);
        assertEquals(expectedValue, ((Constant<?>) resultValue).getConstant());
        then(valueTranslator).should().apply(value);
        then(expressionTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ValueAssignmentTranslator#forPath(Path, ValueTranslator, ExpressionTranslator)}.
     */
    @Test
    void forPathExpressionTest() {
        final Path<Object> target = builder.getSimple("target", Object.class);
        @SuppressWarnings("unchecked")
        final ValueTranslator<Object, Object> valueTranslator = mock(ValueTranslator.class);
        @SuppressWarnings("unchecked")
        final ExpressionTranslator<Object, Object> expressionTranslator = mock(ExpressionTranslator.class);
        final Expression<Object> value = builder.getSimple("value", Object.class);
        final Expression<Object> expectedValue = builder.getSimple("expectedValue", Object.class);
        willReturn(expectedValue).given(expressionTranslator).apply(value);
        final ValueAssignmentTranslator<Object> translator = ValueAssignmentTranslator.forPath(target, valueTranslator, expressionTranslator);
        assertNotNull(translator);
        final ValueAssignments result = translator.apply(value);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedValue, result.get(target));
        then(expressionTranslator).should().apply(value);
        then(valueTranslator).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ValueAssignmentTranslator#fromSimple(dev.orne.qdsl.ValueAssignmentTranslator.Simple)}.
     */
    @Test
    void fromSimpleTest() {
        @SuppressWarnings("unchecked")
        final ValueAssignmentTranslator.Simple<Object> simple = mock(ValueAssignmentTranslator.Simple.class);
        final Expression<Object> value = builder.getSimple("value", Object.class);
        final Path<String> expectedTarget = builder.getSimple("target", String.class);
        final Expression<String> expectedValue = builder.getSimple("expectedValue", String.class);
        final ValueAssignment<String> expectedResult = ValueAssignment.of(expectedTarget, expectedValue);
        willReturn(expectedResult).given(simple).apply(value);
        final ValueAssignmentTranslator<Object> translator = ValueAssignmentTranslator.fromSimple(simple);
        final ValueAssignments result = translator.apply(value);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedValue, result.get(expectedTarget));
        then(simple).should().apply(value);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_AllEqual() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(path).given(path).accept(visitor, null);
        willReturn(expr).given(expr).accept(visitor, null);
        final ValueAssignment<?> result =
                ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(assignment, result);
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_PathTranslated() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(expr).given(expr).accept(visitor, null);
        final ValueAssignment<?> result =
                ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertSame(expr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_ValueTranslated() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(path).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        final ValueAssignment<?> result =
                ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(path, result.getPath());
        assertSame(translatedExpr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_AllTranslated() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        final ValueAssignment<?> result =
                ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertSame(translatedExpr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NullPath() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(null).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        final ValueAssignment<?> result =
                ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNull(result);
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NullValue() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(null).given(expr).accept(visitor, null);
        final ValueAssignment<?> result =
                ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertNull(result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NotAPath() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        final Expression<TestTypes.SimpleType> translatedPath =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> translatedExpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        assertThrows(IllegalArgumentException.class, () -> {
            ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        });
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

    /**
     * Test for {@link ValueAssignmentTranslator#translateFromComponents(ValueAssignment, Visitor)}.
     */
    @Test
    void testTranslateFromComponents_NotAssignable() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> expr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> assignment =
                ValueAssignment.of(path, expr);
        final Path<TestTypes.SimpleType> translatedPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.UnrelatedType> translatedExpr =
                TestTypes.expressionOf(TestTypes.UnrelatedType.class);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(translatedPath).given(path).accept(visitor, null);
        willReturn(translatedExpr).given(expr).accept(visitor, null);
        assertThrows(IllegalArgumentException.class, () -> {
            ValueAssignmentTranslator.translateFromComponents(assignment, visitor);
        });
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }
}
