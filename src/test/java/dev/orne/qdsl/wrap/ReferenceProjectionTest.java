package dev.orne.qdsl.wrap;

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

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Visitor;

import dev.orne.qdsl.QBeanBuilder;

/**
 * Unit tests for {@code ReferenceProjection}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 * @see ReferenceProjection
 */
@Tag("ut")
class ReferenceProjectionTest {

    /**
     * Test for {@link ReferenceProjection#accept(Visitor, Object)}.
     */
    @Test
    void testAccept() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> instance = spy(ReferenceProjection.class);
        willReturn(projection).given(instance).getProjection();
        willReturn(reference).given(instance).getReference();
        @SuppressWarnings("unchecked")
        final Visitor<?, Object> visitor = mock(Visitor.class);
        final Object context = mock(Object.class);
        final Object expected = mock(Object.class);
        final Object refResult = mock(Object.class);
        willReturn(expected).given(projection).accept(visitor, context);
        willReturn(refResult).given(reference).accept(visitor, context);
        assertSame(expected, instance.accept(visitor, context));
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(visitor).shouldHaveNoInteractions();
        then(context).shouldHaveNoInteractions();
        then(refResult).shouldHaveNoInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#accept(Visitor, Object)}.
     */
    @Test
    void testAccept_ReplaceVisitor_Unchanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> instance = spy(ReferenceProjection.class);
        willReturn(projection).given(instance).getProjection();
        willReturn(reference).given(instance).getReference();
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, Object> visitor = mock(Visitor.class);
        final Object context = mock(Object.class);
        willReturn(projection).given(projection).accept(visitor, context);
        willReturn(reference).given(reference).accept(visitor, context);
        final Expression<?> result = instance.accept(visitor, context);
        assertSame(instance, result);
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(visitor).shouldHaveNoInteractions();
        then(context).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#accept(Visitor, Object)}.
     */
    @Test
    void testAccept_ReplaceVisitor_ProjectionChanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> instance = spy(ReferenceProjection.class);
        willReturn(projection).given(instance).getProjection();
        willReturn(reference).given(instance).getReference();
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, Object> visitor = mock(Visitor.class);
        final Object context = mock(Object.class);
        final Expression<?> projResult = mock(Expression.class);
        willReturn(projResult).given(projection).accept(visitor, context);
        willReturn(reference).given(reference).accept(visitor, context);
        final Expression<?> result = instance.accept(visitor, context);
        final ReferenceProjection<?, ?> tresult = assertInstanceOf(ReferenceProjection.class, result);
        assertSame(projResult, tresult.getProjection());
        assertSame(reference, tresult.getReference());
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(visitor).shouldHaveNoInteractions();
        then(context).shouldHaveNoInteractions();
        then(projResult).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#accept(Visitor, Object)}.
     */
    @Test
    void testAccept_ReplaceVisitor_ReferenceChanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> instance = spy(ReferenceProjection.class);
        willReturn(projection).given(instance).getProjection();
        willReturn(reference).given(instance).getReference();
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, Object> visitor = mock(Visitor.class);
        final Object context = mock(Object.class);
        final Expression<?> refResult = mock(Expression.class);
        willReturn(projection).given(projection).accept(visitor, context);
        willReturn(refResult).given(reference).accept(visitor, context);
        final Expression<?> result = instance.accept(visitor, context);
        final ReferenceProjection<?, ?> tresult = assertInstanceOf(ReferenceProjection.class, result);
        assertSame(projection, tresult.getProjection());
        assertSame(refResult, tresult.getReference());
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(visitor).shouldHaveNoInteractions();
        then(context).shouldHaveNoInteractions();
        then(refResult).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#accept(Visitor, Object)}.
     */
    @Test
    void testAccept_ReplaceVisitor_BothChanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> instance = spy(ReferenceProjection.class);
        willReturn(projection).given(instance).getProjection();
        willReturn(reference).given(instance).getReference();
        @SuppressWarnings("unchecked")
        final Visitor<Expression<?>, Object> visitor = mock(Visitor.class);
        final Object context = mock(Object.class);
        final Expression<?> projResult = mock(Expression.class);
        final Expression<?> refResult = mock(Expression.class);
        willReturn(projResult).given(projection).accept(visitor, context);
        willReturn(refResult).given(reference).accept(visitor, context);
        final Expression<?> result = instance.accept(visitor, context);
        final ReferenceProjection<?, ?> tresult = assertInstanceOf(ReferenceProjection.class, result);
        assertSame(projResult, tresult.getProjection());
        assertSame(refResult, tresult.getReference());
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(visitor).shouldHaveNoInteractions();
        then(context).shouldHaveNoInteractions();
        then(projResult).shouldHaveNoInteractions();
        then(refResult).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#accept(Visitor, Object)}.
     */
    @Test
    void testAccept_MixedVisitor() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> instance = spy(ReferenceProjection.class);
        willReturn(projection).given(instance).getProjection();
        willReturn(reference).given(instance).getReference();
        @SuppressWarnings("unchecked")
        final Visitor<?, Object> visitor = mock(Visitor.class);
        final Object context = mock(Object.class);
        final Expression<?> expected = mock(Expression.class);
        final Object refResult = mock(Object.class);
        willReturn(expected).given(projection).accept(visitor, context);
        willReturn(refResult).given(reference).accept(visitor, context);
        assertSame(expected, instance.accept(visitor, context));
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(visitor).shouldHaveNoInteractions();
        then(context).shouldHaveNoInteractions();
        then(refResult).shouldHaveNoInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#accept(Visitor, Object)}.
     */
    @Test
    void testAccept_ReferenceProjectionVisitor() {
        final ReferenceProjection<?, ?> instance = spy(ReferenceProjection.class);
        @SuppressWarnings("unchecked")
        final ReferenceProjectionVisitor<?, Object> visitor = mock(ReferenceProjectionVisitor.class);
        final Object context = mock(Object.class);
        final Object expected = mock(Object.class);
        willReturn(expected).given(visitor).visit(instance, context);
        assertSame(expected, instance.accept(visitor, context));
        then(visitor).should().visit(instance, context);
        then(visitor).shouldHaveNoMoreInteractions();
        then(context).shouldHaveNoInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#of(Expression, Expression)}.
     */
    @Test
    void testOfExpressionExpression() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> result = ReferenceProjection.of(projection, reference);
        assertNotNull(result);
        assertSame(projection, result.getProjection());
        assertSame(reference, result.getReference());
        then(projection).shouldHaveNoInteractions();
        then(reference).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#of(Expression)}.
     */
    @Test
    void testOfExpression() {
        final Expression<?> projection = mock(Expression.class);
        final ReferenceProjectionImpl.Builder<?> result = ReferenceProjection.of(projection);
        assertNotNull(result);
        final ReferenceProjectionImpl.ReferenceBuilderImpl<?> rresult =
                assertInstanceOf(ReferenceProjectionImpl.ReferenceBuilderImpl.class, result);
        assertSame(projection, rresult.getProjection());
        then(projection).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjection#of(Class)}.
     */
    @Test
    void testOfClass() {
        final ReferenceProjectionImpl.BeanBuilder<?> result = ReferenceProjection.of(MyType.class);
        assertNotNull(result);
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> rresult =
                assertInstanceOf(ReferenceProjectionImpl.QBeanBuilderImpl.class, result);
        final QBeanBuilder<?> delegated = rresult.getDelegated();
        assertNotNull(delegated);
        assertSame(MyType.class, delegated.getType());
        assertTrue(delegated.getBindings().isEmpty());
    }

    private static interface MyType {}
}
