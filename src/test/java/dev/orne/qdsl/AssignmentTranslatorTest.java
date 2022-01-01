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
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Unit tests for {@code AssignmentTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-01
 * @since 0.1
 * @see AssignmentTranslator
 */
@Tag("ut")
public class AssignmentTranslatorTest {

    @Test
    void testIdentity() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final AssignmentTranslator<TestTypes.SimpleType> translator =
                AssignmentTranslator.identity(path);
        final Expression<TestTypes.SimpleType> value =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignments result = translator.apply(value);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(value, result.get(path));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSingleValue_Null() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final ValueTranslator<Object, TestTypes.SimpleType> valueTranslator =
                mock(ValueTranslator.class);
        final ExpressionTranslator<Object, TestTypes.SimpleType> expressionTranslator =
                mock(ExpressionTranslator.class);
        final Expression<TestTypes.SimpleType> expectedValue =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(expectedValue).given(expressionTranslator).apply(null);
        ValueAssignment<?> result = AssignmentTranslator.toSingleValue(
                path,
                valueTranslator,
                expressionTranslator,
                null);
        assertSame(path, result.getPath());
        assertSame(expectedValue, result.getValue());
        then(valueTranslator).shouldHaveNoInteractions();
        then(expressionTranslator).should().apply(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSingleValue_Constant() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final ValueTranslator<Object, TestTypes.SimpleType> valueTranslator =
                mock(ValueTranslator.class);
        final ExpressionTranslator<Object, TestTypes.SimpleType> expressionTranslator =
                mock(ExpressionTranslator.class);
        final Object value = new Object();
        final Expression<Object> valueExpr =
                Expressions.constant(value);
        final TestTypes.SimpleType expectedValue =
                mock(TestTypes.SimpleType.class);
        willReturn(expectedValue).given(valueTranslator).apply(value);
        ValueAssignment<?> result = AssignmentTranslator.toSingleValue(
                path,
                valueTranslator,
                expressionTranslator,
                valueExpr);
        assertSame(path, result.getPath());
        assertNotNull(result.getValue());
        assertTrue(result.getValue() instanceof Constant);
        assertSame(expectedValue, ((Constant<?>) result.getValue()).getConstant());
        then(valueTranslator).should().apply(value);
        then(expressionTranslator).shouldHaveNoInteractions();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSingleValue_Expression() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final ValueTranslator<Object, TestTypes.SimpleType> valueTranslator =
                mock(ValueTranslator.class);
        final ExpressionTranslator<Object, TestTypes.SimpleType> expressionTranslator =
                mock(ExpressionTranslator.class);
        final Expression<Object> value =
                TestTypes.expressionOf(Object.class);
        final Expression<TestTypes.SimpleType> expectedValue =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(expectedValue).given(expressionTranslator).apply(value);
        ValueAssignment<?> result = AssignmentTranslator.toSingleValue(
                path,
                valueTranslator,
                expressionTranslator,
                value);
        assertSame(path, result.getPath());
        assertSame(expectedValue, result.getValue());
        then(valueTranslator).shouldHaveNoInteractions();
        then(expressionTranslator).should().apply(value);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testForPath_Expression() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final ExpressionTranslator<Object, TestTypes.SimpleType> expressionTranslator =
                mock(ExpressionTranslator.class);
        final Expression<Object> value =
                TestTypes.expressionOf(Object.class);
        final Expression<TestTypes.SimpleType> expectedValue =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(expectedValue).given(expressionTranslator).apply(value);
        final AssignmentTranslator<Object> translator = AssignmentTranslator.forPath(
                path,
                expressionTranslator);
        final ValueAssignments result = translator.apply(value);
        assertEquals(1, result.size());
        assertEquals(expectedValue, result.get(path));
        then(expressionTranslator).should().apply(value);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testForPath_Complex_Null() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final ValueTranslator<Object, TestTypes.SimpleType> valueTranslator =
                mock(ValueTranslator.class);
        final ExpressionTranslator<Object, TestTypes.SimpleType> expressionTranslator =
                mock(ExpressionTranslator.class);
        final Expression<TestTypes.SimpleType> expectedValue =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(expectedValue).given(expressionTranslator).apply(null);
        final AssignmentTranslator<Object> translator = AssignmentTranslator.forPath(
                path,
                valueTranslator,
                expressionTranslator);
        final ValueAssignments result = translator.apply(null);
        assertEquals(1, result.size());
        assertEquals(expectedValue, result.get(path));
        then(valueTranslator).shouldHaveNoInteractions();
        then(expressionTranslator).should().apply(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testForPath_Complex_Constant() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final ValueTranslator<Object, TestTypes.SimpleType> valueTranslator =
                mock(ValueTranslator.class);
        final ExpressionTranslator<Object, TestTypes.SimpleType> expressionTranslator =
                mock(ExpressionTranslator.class);
        final Object value = new Object();
        final Expression<Object> valueExpr =
                Expressions.constant(value);
        final TestTypes.SimpleType expectedValue =
                mock(TestTypes.SimpleType.class);
        willReturn(expectedValue).given(valueTranslator).apply(value);
        final AssignmentTranslator<Object> translator = AssignmentTranslator.forPath(
                path,
                valueTranslator,
                expressionTranslator);
        final ValueAssignments result = translator.apply(valueExpr);
        assertEquals(1, result.size());
        final Expression<?> resultValue = result.get(path);
        assertNotNull(resultValue);
        assertTrue(resultValue instanceof Constant);
        assertSame(expectedValue, ((Constant<?>) resultValue).getConstant());
        then(valueTranslator).should().apply(value);
        then(expressionTranslator).shouldHaveNoInteractions();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testForPath_Complex_Expression() {
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final ValueTranslator<Object, TestTypes.SimpleType> valueTranslator =
                mock(ValueTranslator.class);
        final ExpressionTranslator<Object, TestTypes.SimpleType> expressionTranslator =
                mock(ExpressionTranslator.class);
        final Expression<Object> value =
                TestTypes.expressionOf(Object.class);
        final Expression<TestTypes.SimpleType> expectedValue =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(expectedValue).given(expressionTranslator).apply(value);
        final AssignmentTranslator<Object> translator = AssignmentTranslator.forPath(
                path,
                valueTranslator,
                expressionTranslator);
        final ValueAssignments result = translator.apply(value);
        assertEquals(1, result.size());
        assertEquals(expectedValue, result.get(path));
        then(valueTranslator).shouldHaveNoInteractions();
        then(expressionTranslator).should().apply(value);
    }

    @Test
    void testFromSimple() {
        @SuppressWarnings("unchecked")
        final AssignmentTranslator.Simple<TestTypes.SimpleType> simple =
                mock(AssignmentTranslator.Simple.class);
        final ValueAssignment<?> expected = TestTypes.randomValueAssignment();
        final Expression<TestTypes.SimpleType> value =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(expected).given(simple).apply(value);
        final AssignmentTranslator<TestTypes.SimpleType> translator =
                AssignmentTranslator.fromSimple(simple);
        final ValueAssignments result = translator.apply(value);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected.getValue(), result.get(expected.getPath()));
        then(simple).should().apply(value);
    }

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
                AssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(assignment, result);
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

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
                AssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertSame(expr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

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
                AssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(path, result.getPath());
        assertSame(translatedExpr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

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
                AssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertSame(translatedExpr, result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

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
                AssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNull(result);
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

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
                AssignmentTranslator.translateFromComponents(assignment, visitor);
        assertNotNull(result);
        assertSame(translatedPath, result.getPath());
        assertNull(result.getValue());
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

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
            AssignmentTranslator.translateFromComponents(assignment, visitor);
        });
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }

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
            AssignmentTranslator.translateFromComponents(assignment, visitor);
        });
        then(path).should().accept(visitor, null);
        then(expr).should().accept(visitor, null);
    }
}
