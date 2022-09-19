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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import dev.orne.qdsl.QBeanBuilder;

/**
 * Unit tests for {@code ReferenceEntityProjection}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 * @see ReferenceProjectionImpl
 */
@Tag("ut")
class ReferenceProjectionImplTest {

    /**
     * Test for {@link ReferenceProjectionImpl#ReferenceEntityProjection(Expression, Expression)}.
     */
    @Test
    void testConstructor() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjectionImpl<?, ?> instance = new ReferenceProjectionImpl<>(projection, reference);
        assertSame(projection, instance.getProjection());
        assertSame(reference, instance.getReference());
        then(projection).shouldHaveNoInteractions();
        then(reference).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl#getType()}.
     */
    @Test
    void testGetType() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjectionImpl<?, ?> instance = new ReferenceProjectionImpl<>(projection, reference);
        willReturn(MyType.class).given(projection).getType();
        assertSame(MyType.class, instance.getType());
        then(projection).should().getType();
        then(projection).shouldHaveNoMoreInteractions();
        then(reference).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl#getReferenceType()}.
     */
    @Test
    void testGetReferenceType() {
        final Expression<?> projection = mock(Expression.class);
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjectionImpl<?, ?> instance = new ReferenceProjectionImpl<>(projection, reference);
        willReturn(MyType.class).given(reference).getType();
        assertSame(MyType.class, instance.getReferenceType());
        then(projection).shouldHaveNoInteractions();
        then(reference).should().getType();
        then(reference).shouldHaveNoMoreInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl#of(Expression)}.
     */
    @Test
    void testOfExpression() {
        final Expression<?> projection = mock(Expression.class);
        final ReferenceProjectionImpl.Builder<?> result = ReferenceProjectionImpl.of(projection);
        assertNotNull(result);
        final ReferenceProjectionImpl.ReferenceBuilderImpl<?> rresult =
                assertInstanceOf(ReferenceProjectionImpl.ReferenceBuilderImpl.class, result);
        assertSame(projection, rresult.getProjection());
        then(projection).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl#of(Class)}.
     */
    @Test
    void testOfClass() {
        final ReferenceProjectionImpl.BeanBuilder<?> result = ReferenceProjectionImpl.of(MyType.class);
        assertNotNull(result);
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> rresult =
                assertInstanceOf(ReferenceProjectionImpl.QBeanBuilderImpl.class, result);
        final QBeanBuilder<?> delegated = rresult.getDelegated();
        assertNotNull(delegated);
        assertSame(MyType.class, delegated.getType());
        assertTrue(delegated.getBindings().isEmpty());
    }

    /**
     * Test for {@link ReferenceProjectionImpl.ReferenceBuilderImpl#withSimpleReference(Expression)}.
     */
    @Test
    void testReferenceBuilderWithSimpleReference() {
        final Expression<?> projection = mock(Expression.class);
        final ReferenceProjectionImpl.ReferenceBuilderImpl<?> builder =
                new ReferenceProjectionImpl.ReferenceBuilderImpl<>(projection);
        assertSame(projection, builder.getProjection());
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjectionImpl<?, ?> result = builder.withSimpleReference(reference);
        assertNotNull(result);
        assertSame(projection, result.getProjection());
        assertSame(reference, result.getReference());
        then(projection).shouldHaveNoInteractions();
        then(reference).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl.ReferenceBuilderImpl#withComposedReference(Expression...)}.
     */
    @Test
    void testReferenceBuilderWithComposedReference() {
        final Expression<?> projection = mock(Expression.class);
        final ReferenceProjectionImpl.ReferenceBuilderImpl<?> builder =
                new ReferenceProjectionImpl.ReferenceBuilderImpl<>(projection);
        assertSame(projection, builder.getProjection());
        final Expression<?> expr0 = mock(Expression.class);
        final Expression<?> expr1 = mock(Expression.class);
        final ReferenceProjectionImpl<?, ?> result = builder.withComposedReference(expr0, expr1);
        assertNotNull(result);
        assertSame(projection, result.getProjection());
        final Expression<?> resultRef = result.getReference();
        assertNotNull(resultRef);
        assertEquals(Projections.tuple(expr0, expr1), resultRef);
        then(projection).shouldHaveNoInteractions();
        then(expr0).shouldHaveNoInteractions();
        then(expr1).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl.QBeanBuilderImpl#withFieldAccess(boolean)}.
     */
    @Test
    void testBeanBuilderWithFieldAccess() {
        final QBeanBuilder<?> delegated = mock(QBeanBuilder.class);
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> builder =
                new ReferenceProjectionImpl.QBeanBuilderImpl<>(delegated);
        assertSame(delegated, builder.getDelegated());
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> result = builder.withFieldAccess(true);
        assertSame(builder, result);
        then(delegated).should().withFieldAccess(true);
        then(delegated).shouldHaveNoMoreInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl.QBeanBuilderImpl#bind(String, Expression)}.
     */
    @Test
    void testBeanBuilderBind() {
        final QBeanBuilder<?> delegated = mock(QBeanBuilder.class);
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> builder =
                new ReferenceProjectionImpl.QBeanBuilderImpl<>(delegated);
        assertSame(delegated, builder.getDelegated());
        final String prop = "mockProperty";
        final Expression<?> expr = mock(Expression.class);
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> result = builder.bind(prop, expr);
        assertSame(builder, result);
        then(delegated).should().bind(prop, expr);
        then(delegated).shouldHaveNoMoreInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl.QBeanBuilderImpl#withSimpleReference(Expression)}.
     */
    @Test
    void testBeanBuilderWithSimpleReference() {
        final QBean<?> projection = mock(QBean.class);
        final QBeanBuilder<?> delegated = mock(QBeanBuilder.class);
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> builder =
                new ReferenceProjectionImpl.QBeanBuilderImpl<>(delegated);
        assertSame(delegated, builder.getDelegated());
        willReturn(projection).given(delegated).build();
        final Expression<?> reference = mock(Expression.class);
        final ReferenceProjectionImpl<?, ?> result = builder.withSimpleReference(reference);
        assertNotNull(result);
        assertSame(projection, result.getProjection());
        assertSame(reference, result.getReference());
        then(delegated).should().build();
        then(delegated).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(reference).shouldHaveNoInteractions();
    }

    /**
     * Test for {@link ReferenceProjectionImpl.QBeanBuilderImpl#withComposedReference(Expression...)}.
     */
    @Test
    void testBeanBuilderWithComposedReference() {
        final QBean<?> projection = mock(QBean.class);
        final QBeanBuilder<?> delegated = mock(QBeanBuilder.class);
        final ReferenceProjectionImpl.QBeanBuilderImpl<?> builder =
                new ReferenceProjectionImpl.QBeanBuilderImpl<>(delegated);
        assertSame(delegated, builder.getDelegated());
        willReturn(projection).given(delegated).build();
        final Expression<?> expr0 = mock(Expression.class);
        final Expression<?> expr1 = mock(Expression.class);
        final ReferenceProjectionImpl<?, ?> result = builder.withComposedReference(expr0, expr1);
        assertNotNull(result);
        assertSame(projection, result.getProjection());
        final Expression<?> resultRef = result.getReference();
        assertNotNull(resultRef);
        assertEquals(Projections.tuple(expr0, expr1), resultRef);
        then(delegated).should().build();
        then(delegated).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(expr0).shouldHaveNoInteractions();
        then(expr1).shouldHaveNoInteractions();
    }

    private static interface MyType {}
}
