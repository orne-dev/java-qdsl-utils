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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;


/**
 * Unit tests for {@code OrderSpecifierReplaceVisitor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-01
 * @since 0.1
 * @see OrderSpecifierReplaceVisitor
 */
@Tag("ut")
public class OrderSpecifierReplaceVisitorTest {

    @Test
    void testFromComponents_Unchanged() {
        final Expression<TestTypes.ComparableType> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> value =
                TestTypes.randomOrderSpecifier(expr);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        willReturn(expr).given(expr).accept(visitor, null);
        final OrderSpecifier<?>[] result = OrderSpecifierReplaceVisitor
                .fromComponents(value, visitor );
        assertNotNull(result);
        assertEquals(1, result.length);
        assertSame(value, result[0]);
    }

    @Test
    void testFromComponents_Replaced() {
        final Expression<TestTypes.ComparableType> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> value =
                TestTypes.randomOrderSpecifier(expr);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        final Expression<TestTypes.ComparableType> replacedExpr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        willReturn(replacedExpr).given(expr).accept(visitor, null);
        final OrderSpecifier<?>[] result = OrderSpecifierReplaceVisitor
                .fromComponents(value, visitor );
        assertNotNull(result);
        assertEquals(1, result.length);
        assertSame(replacedExpr, result[0].getTarget());
        assertSame(value.getOrder(), result[0].getOrder());
        assertSame(value.getNullHandling(), result[0].getNullHandling());
    }

    @Test
    void testFromComponents_Replaced_NonComparable() {
        final Expression<TestTypes.ComparableType> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> value =
                TestTypes.randomOrderSpecifier(expr);
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, ?> visitor = mock(Visitor.class);
        final Expression<?> replacedExpr = mock(Expression.class);
        willReturn(Object.class).given(replacedExpr).getType();
        willReturn(replacedExpr).given(expr).accept(visitor, null);
        final OrderSpecifier<?>[] result = OrderSpecifierReplaceVisitor
                .fromComponents(value, visitor );
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testAsComparable() {
        final Expression<?> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Expression<? extends Comparable<?>> result = OrderSpecifierReplaceVisitor
                .asComparable(expr);
        assertNotNull(result);
        assertSame(expr, result);
    }

    @Test
    void testAsComparable_Null() {
        final Expression<?> result = OrderSpecifierReplaceVisitor
                .asComparable(null);
        assertNull(result);
    }

    @Test
    void testAsComparable_NonComparable() {
        final Expression<?> expr = mock(Expression.class);
        willReturn(Object.class).given(expr).getType();
        final Expression<?> result = OrderSpecifierReplaceVisitor
                .asComparable(null);
        assertNull(result);
    }

    @Test
    void testResultFrom() {
        final Expression<TestTypes.ComparableType> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> template =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> result = OrderSpecifierReplaceVisitor
                .resultFrom(template, expr);
        assertNotNull(result);
        assertSame(expr, result.getTarget());
        assertSame(template.getOrder(), result.getOrder());
        assertSame(template.getNullHandling(), result.getNullHandling());
    }

    @Test
    void testResultFrom_Order() {
        final Expression<TestTypes.ComparableType> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Order order =
                TestTypes.randomEnum(Order.class);
        final OrderSpecifier<?> template =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> result = OrderSpecifierReplaceVisitor
                .resultFrom(template, expr, order);
        assertNotNull(result);
        assertSame(expr, result.getTarget());
        assertSame(order, result.getOrder());
        assertSame(template.getNullHandling(), result.getNullHandling());
    }

    @Test
    void testCreateResult() {
        final Expression<TestTypes.ComparableType> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Order order =
                TestTypes.randomEnum(Order.class);
        final OrderSpecifier.NullHandling nullHandling =
                TestTypes.randomEnum(OrderSpecifier.NullHandling.class);
        final OrderSpecifier<TestTypes.ComparableType> result = OrderSpecifierReplaceVisitor
                .createResult(expr, order, nullHandling);
        assertNotNull(result);
        assertSame(expr, result.getTarget());
        assertSame(order, result.getOrder());
        assertSame(nullHandling, result.getNullHandling());
    }

    @Test
    void testCreateResult_NullNullHandling() {
        final Expression<TestTypes.ComparableType> expr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Order order =
                TestTypes.randomEnum(Order.class);
        final OrderSpecifier<TestTypes.ComparableType> result = OrderSpecifierReplaceVisitor
                .createResult(expr, order, null);
        assertNotNull(result);
        assertSame(expr, result.getTarget());
        assertSame(order, result.getOrder());
        assertSame(OrderSpecifier.NullHandling.Default, result.getNullHandling());
    }
}
