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
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.querydsl.collections.CollQuery;
import com.querydsl.collections.QueryEngine;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.support.ExtendedSubQuery;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;

import dev.orne.qdsl.TestTypes;

/**
 * Unit tests for {@code WrappedCollQueryClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedCollQueryClause
 */
@Tag("ut")
class WrappedCollQueryClauseTest
extends WrappedQueryClauseTest {

    protected @Mock QueryMetadata metadata;
    protected @Mock CollQuery<TestTypes.SimpleType> delegate;

    /**
     * Unit test for {@link WrappedCollQueryClause#WrappedCollQueryClause(Path, QueryEngine, Object...)}.
     */
    @Test
    void testConstructorVarargs() {
        final Path<TestTypes.SimpleType> source = new PathBuilder<>(TestTypes.SimpleType.class, "source");
        final QueryEngine engine = mock(QueryEngine.class);
        final TestTypes.SimpleType[] values = new TestTypes.SimpleType[] {
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class)
        };
        final WrappedCollQueryClause<?> clause = new WrappedCollQueryClause<>(source, engine, values);
        assertInstanceOf(CollQuery.class, clause.getDelegate());
        assertNotNull(clause.getMetadata());
        assertNotNull(clause.getMetadata().getJoins());
        assertEquals(1, clause.getMetadata().getJoins().size());
        assertSame(source, clause.getMetadata().getJoins().get(0).getTarget());
        assertSame(JoinType.DEFAULT, clause.getMetadata().getJoins().get(0).getType());
        assertSame(source, clause.getMetadata().getProjection());
        assertSame(TestTypes.SimpleType.class, clause.getType());
        assertSame(ExpressionTransformer.NOP, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(null, engine, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(source, null, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(source, engine, (List<TestTypes.SimpleType>) null);
        });
        final TestTypes.SimpleType[] nullValues = new TestTypes.SimpleType[] {
                mock(TestTypes.SimpleType.class),
                null,
                mock(TestTypes.SimpleType.class)
        };
        assertThrows(IllegalArgumentException.class, () -> {
            new WrappedCollQueryClause<>(source, engine, nullValues);
        });
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#WrappedCollQueryClause(Path, QueryEngine, Iterable)}.
     */
    @Test
    void testConstructorIterable() {
        final Path<TestTypes.SimpleType> source = new PathBuilder<>(TestTypes.SimpleType.class, "source");
        final QueryEngine engine = mock(QueryEngine.class);
        final List<TestTypes.SimpleType> values = Arrays.asList(
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class));
        final WrappedCollQueryClause<?> clause = new WrappedCollQueryClause<>(source, engine, values);
        assertInstanceOf(CollQuery.class, clause.getDelegate());
        assertNotNull(clause.getMetadata());
        assertNotNull(clause.getMetadata().getJoins());
        assertEquals(1, clause.getMetadata().getJoins().size());
        assertSame(source, clause.getMetadata().getJoins().get(0).getTarget());
        assertSame(JoinType.DEFAULT, clause.getMetadata().getJoins().get(0).getType());
        assertSame(source, clause.getMetadata().getProjection());
        assertSame(TestTypes.SimpleType.class, clause.getType());
        assertSame(ExpressionTransformer.NOP, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(null, engine, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(source, null, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(source, engine, (List<TestTypes.SimpleType>) null);
        });
        final List<TestTypes.SimpleType> nullValues = Arrays.asList(
                mock(TestTypes.SimpleType.class),
                null,
                mock(TestTypes.SimpleType.class));
        assertThrows(IllegalArgumentException.class, () -> {
            new WrappedCollQueryClause<>(source, engine, nullValues);
        });
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#WrappedCollQueryClause(ExpressionTransformer, Path, QueryEngine, Object...)}.
     */
    @Test
    void testTransformerConstructorVarargs() {
        final Path<TestTypes.SimpleType> source = new PathBuilder<>(TestTypes.SimpleType.class, "source");
        final QueryEngine engine = mock(QueryEngine.class);
        final TestTypes.SimpleType[] values = new TestTypes.SimpleType[] {
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class)
        };
        final WrappedCollQueryClause<?> clause = new WrappedCollQueryClause<>(transformer, source, engine, values);
        assertInstanceOf(CollQuery.class, clause.getDelegate());
        assertNotNull(clause.getMetadata());
        assertNotNull(clause.getMetadata().getJoins());
        assertEquals(1, clause.getMetadata().getJoins().size());
        assertSame(source, clause.getMetadata().getJoins().get(0).getTarget());
        assertSame(JoinType.DEFAULT, clause.getMetadata().getJoins().get(0).getType());
        assertSame(source, clause.getMetadata().getProjection());
        assertSame(TestTypes.SimpleType.class, clause.getType());
        assertSame(transformer, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(transformer, null, engine, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(transformer, source, null, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(transformer, source, engine, (List<TestTypes.SimpleType>) null);
        });
        final TestTypes.SimpleType[] nullValues = new TestTypes.SimpleType[] {
                mock(TestTypes.SimpleType.class),
                null,
                mock(TestTypes.SimpleType.class)
        };
        assertThrows(IllegalArgumentException.class, () -> {
            new WrappedCollQueryClause<>(transformer, source, engine, nullValues);
        });
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#WrappedCollQueryClause(ExpressionTransformer, Path, QueryEngine, Iterable)}.
     */
    @Test
    void testTransformerConstructorIterable() {
        final Path<TestTypes.SimpleType> source = new PathBuilder<>(TestTypes.SimpleType.class, "source");
        final QueryEngine engine = mock(QueryEngine.class);
        final List<TestTypes.SimpleType> values = Arrays.asList(
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class),
                mock(TestTypes.SimpleType.class));
        final WrappedCollQueryClause<?> clause = new WrappedCollQueryClause<>(transformer, source, engine, values);
        assertInstanceOf(CollQuery.class, clause.getDelegate());
        assertNotNull(clause.getMetadata());
        assertNotNull(clause.getMetadata().getJoins());
        assertEquals(1, clause.getMetadata().getJoins().size());
        assertSame(source, clause.getMetadata().getJoins().get(0).getTarget());
        assertSame(JoinType.DEFAULT, clause.getMetadata().getJoins().get(0).getType());
        assertSame(source, clause.getMetadata().getProjection());
        assertSame(TestTypes.SimpleType.class, clause.getType());
        assertSame(transformer, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(transformer, null, engine, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(transformer, source, null, values);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(transformer, source, engine, (List<TestTypes.SimpleType>) null);
        });
        final List<TestTypes.SimpleType> nullValues = Arrays.asList(
                mock(TestTypes.SimpleType.class),
                null,
                mock(TestTypes.SimpleType.class));
        assertThrows(IllegalArgumentException.class, () -> {
            new WrappedCollQueryClause<>(transformer, source, engine, nullValues);
        });
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#WrappedCollQueryClause(ExpressionTransformer, QueryMetadata, CollQuery, Class)}.
     */
    @Test
    void testInternalConstructor() {
        final WrappedCollQueryClause<?> clause = new WrappedCollQueryClause<>(
                transformer,
                metadata,
                delegate,
                TestTypes.SimpleType.class);
        assertSame(transformer, clause.getTransformer());
        assertSame(delegate, clause.getDelegate());
        assertSame(metadata, clause.getMetadata());
        assertSame(TestTypes.SimpleType.class, clause.getType());
        then(transformer).shouldHaveNoInteractions();
        then(metadata).shouldHaveNoInteractions();
        then(delegate).shouldHaveNoInteractions();
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(
                    transformer,
                    null,
                    delegate,
                    TestTypes.SimpleType.class);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(
                    transformer,
                    metadata,
                    null,
                    TestTypes.SimpleType.class);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedCollQueryClause<>(
                    transformer,
                    metadata,
                    delegate,
                    null);
        });
    }

    @Override
    protected @NotNull WrappedCollQueryClause<?> getClause() {
        return new WrappedCollQueryClause<>(transformer, metadata, delegate, TestTypes.SimpleType.class);
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#changeProjection(ExpressionTransformer, QueryMetadata, CollQuery, Class)}.
     */
    @Test
    void testChangeProjection() {
        final ExpressionTransformer newTransformer = mock(ExpressionTransformer.class);
        final QueryMetadata newMetadata = mock(QueryMetadata.class);
        @SuppressWarnings("unchecked")
        final CollQuery<TestTypes.UnrelatedType> newDelegate = mock(CollQuery.class);
        final WrappedCollQueryClause<?> clause = getClause();
        final WrappedCollQueryClause<?> result = clause.changeProjection(
                newTransformer,
                newMetadata,
                newDelegate,
                TestTypes.UnrelatedType.class);
        assertNotNull(result);
        assertSame(newTransformer, result.getTransformer());
        assertSame(newMetadata, result.getMetadata());
        assertSame(newDelegate, result.getDelegate());
        assertSame(TestTypes.UnrelatedType.class, result.getType());
        then(transformer).shouldHaveNoInteractions();
        then(metadata).shouldHaveNoInteractions();
        then(delegate).shouldHaveNoInteractions();
        then(newTransformer).shouldHaveNoInteractions();
        then(newMetadata).shouldHaveNoInteractions();
        then(newDelegate).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#select(Expression)}.
     */
    @Test
    void testSelectExpression() {
        final Expression<TestTypes.UnrelatedType> expr = TestTypes.expressionOf(TestTypes.UnrelatedType.class);
        final Expression<TestTypes.UnrelatedType> texpr = TestTypes.expressionOf(TestTypes.UnrelatedType.class);
        @SuppressWarnings("unchecked")
        final CollQuery<TestTypes.UnrelatedType> tdelegate = mock(CollQuery.class);
        @SuppressWarnings("unchecked")
        final WrappedCollQueryClause<TestTypes.UnrelatedType> tclause = mock(WrappedCollQueryClause.class);
        final WrappedCollQueryClause<?> clause = spy(getClause());
        given(transformer.translateProjection(expr)).willReturn(texpr);
        given(delegate.select(texpr)).willReturn(tdelegate);
        willReturn(tclause).given(clause).changeProjection(transformer, metadata, tdelegate, TestTypes.UnrelatedType.class);
        final InOrder order = inOrder(transformer, delegate, clause);
        final WrappedCollQueryClause<?> result = clause.select(expr);
        assertSame(tclause, result);
        then(transformer).should(order).translateProjection(expr);
        then(delegate).should(order).select(texpr);
        then(clause).should(order).changeProjection(transformer, metadata, tdelegate, TestTypes.UnrelatedType.class);
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#select(Expression...)}.
     */
    @Test
    void testSelectExpressions() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] texprs = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = TestTypes.randomExpression();
            texprs[i] = TestTypes.randomExpression();
        }
        @SuppressWarnings("unchecked")
        final CollQuery<Tuple> tdelegate = mock(CollQuery.class);
        @SuppressWarnings("unchecked")
        final WrappedCollQueryClause<Tuple> tclause = mock(WrappedCollQueryClause.class);
        final WrappedCollQueryClause<?> clause = spy(getClause());
        given(transformer.translateProjections(exprs)).willReturn(texprs);
        given(delegate.select(texprs)).willReturn(tdelegate);
        willReturn(tclause).given(clause).changeProjection(transformer, metadata, tdelegate, Tuple.class);
        final InOrder order = inOrder(transformer, delegate, clause);
        final WrappedCollQueryClause<?> result = clause.select(exprs);
        assertSame(tclause, result);
        then(transformer).should(order).translateProjections(exprs);
        then(delegate).should(order).select(texprs);
        then(clause).should(order).changeProjection(transformer, metadata, tdelegate, Tuple.class);
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#asSubQuery()}.
     */
    @Test
    void testAsSubQuery() {
        final List<SerializableType> values = Arrays.asList(
                mock(SerializableType.class),
                mock(SerializableType.class),
                mock(SerializableType.class));
        @SuppressWarnings("unchecked")
        final CollQuery<SerializableType> sdelegate = mock(CollQuery.class);
        final WrappedCollQueryClause<?> clause = new WrappedCollQueryClause<>(
                transformer,
                metadata,
                sdelegate,
                SerializableType.class);
        given(sdelegate.fetch()).willReturn(values);
        final ExtendedSubQuery<?> result = clause.asSubQuery();
        assertNotNull(result);
        then(sdelegate).should().fetch();
        final PreFetchedSubQueryExpression<?> tresult =
                assertInstanceOf(PreFetchedSubQueryExpression.class, result);
        assertEquals(metadata, tresult.getMetadata());
        assertEquals(SerializableType.class, tresult.getType());
        assertEquals(values, tresult.getValues());
    }

    /**
     * Unit test for {@link WrappedCollQueryClause#asSubQuery()}.
     */
    @Test
    void testAsSubQueryNoSerializable() {
        final WrappedCollQueryClause<?> clause = getClause();
        assertThrows(IllegalArgumentException.class, () -> {
            clause.asSubQuery();
        });
        then(delegate).shouldHaveNoInteractions();
    }

    private static interface SerializableType
    extends Serializable {
        
    }
}
