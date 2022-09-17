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
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;

/**
 * Unit tests for {@code ReferenceProjectionReplaceVisitor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 * @see ReferenceProjectionReplaceVisitor
 */
@Tag("ut")
class ReferenceProjectionReplaceVisitorTest {

    /**
     * Test for {@link ReferenceProjectionReplaceVisitor#visit(ReferenceProjection, Object)}.
     */
    @Test
    void testVisit_Unchanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> expr = mock(ReferenceProjection.class);
        willReturn(projection).given(expr).getProjection();
        willReturn(reference).given(expr).getReference();
        @SuppressWarnings("unchecked")
        final ReferenceProjectionReplaceVisitor<Object> visitor = spy(ReferenceProjectionReplaceVisitor.class);
        final Object context = mock(Object.class);
        willReturn(projection).given(projection).accept(visitor, context);
        willReturn(reference).given(reference).accept(visitor, context);
        final Expression<?> result = visitor.visit(expr, context);
        assertSame(expr, result);
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(context).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionReplaceVisitor#visit(ReferenceProjection, Object)}.
     */
    @Test
    void testVisit_ProjectionChanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> expr = mock(ReferenceProjection.class);
        willReturn(projection).given(expr).getProjection();
        willReturn(reference).given(expr).getReference();
        @SuppressWarnings("unchecked")
        final ReferenceProjectionReplaceVisitor<Object> visitor = spy(ReferenceProjectionReplaceVisitor.class);
        final Object context = mock(Object.class);
        final Expression<?> projResult = mock(Expression.class);
        willReturn(projResult).given(projection).accept(visitor, context);
        willReturn(reference).given(reference).accept(visitor, context);
        final Expression<?> result = visitor.visit(expr, context);
        final ReferenceProjection<?, ?> tresult = assertInstanceOf(ReferenceProjection.class, result);
        assertSame(projResult, tresult.getProjection());
        assertSame(reference, tresult.getReference());
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(context).shouldHaveNoInteractions();
        then(projResult).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionReplaceVisitor#visit(ReferenceProjection, Object)}.
     */
    @Test
    void testVisit_ReferenceChanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> expr = mock(ReferenceProjection.class);
        willReturn(projection).given(expr).getProjection();
        willReturn(reference).given(expr).getReference();
        @SuppressWarnings("unchecked")
        final ReferenceProjectionReplaceVisitor<Object> visitor = spy(ReferenceProjectionReplaceVisitor.class);
        final Object context = mock(Object.class);
        final Expression<?> refResult = mock(Expression.class);
        willReturn(projection).given(projection).accept(visitor, context);
        willReturn(refResult).given(reference).accept(visitor, context);
        final Expression<?> result = visitor.visit(expr, context);
        final ReferenceProjection<?, ?> tresult = assertInstanceOf(ReferenceProjection.class, result);
        assertSame(projection, tresult.getProjection());
        assertSame(refResult, tresult.getReference());
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(context).shouldHaveNoInteractions();
        then(refResult).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionReplaceVisitor#visit(ReferenceProjection, Object)}.
     */
    @Test
    void testVisit_BothChanged() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjection<?, ?> expr = mock(ReferenceProjection.class);
        willReturn(projection).given(expr).getProjection();
        willReturn(reference).given(expr).getReference();
        @SuppressWarnings("unchecked")
        final ReferenceProjectionReplaceVisitor<Object> visitor = spy(ReferenceProjectionReplaceVisitor.class);
        final Object context = mock(Object.class);
        final Expression<?> projResult = mock(Expression.class);
        final Expression<?> refResult = mock(Expression.class);
        willReturn(projResult).given(projection).accept(visitor, context);
        willReturn(refResult).given(reference).accept(visitor, context);
        final Expression<?> result = visitor.visit(expr, context);
        final ReferenceProjection<?, ?> tresult = assertInstanceOf(ReferenceProjection.class, result);
        assertSame(projResult, tresult.getProjection());
        assertSame(refResult, tresult.getReference());
        then(projection).should().accept(visitor, context);
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).should().accept(visitor, context);
        then(reference).shouldHaveNoMoreInteractions();
        then(context).shouldHaveNoInteractions();
        then(projResult).shouldHaveNoInteractions();
        then(refResult).shouldHaveNoInteractions();
    }
}
