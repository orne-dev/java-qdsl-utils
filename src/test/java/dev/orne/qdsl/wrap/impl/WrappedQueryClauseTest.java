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

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.support.ExtendedSubQuery;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;

import dev.orne.qdsl.TestTypes;

/**
 * Unit tests for {@code WrappedQueryClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedQueryClause
 */
@Tag("ut")
class WrappedQueryClauseTest
extends WrappedClauseTest {

    @Override
    protected @NotNull WrappedQueryClause<?, ?> getClause() {
        @SuppressWarnings("unchecked")
        final WrappedQueryClause<?, ?> result = new TestWrappedClause<>(
                transformer,
                mock(FetchableQuery.class));
        return result;
    }

    /**
     * Unit test for {@link WrappedQueryClause#distinct()}.
     */
    @Test
    void testDistinct() {
        final WrappedQueryClause<?, ?> clause = getClause();
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final WrappedQueryClause<?, ?> result = clause.distinct();
        assertSame(clause, result);
        then(delegate).should().distinct();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#limit(long)}.
     */
    @Test
    void testLimit() {
        final WrappedQueryClause<?, ?> clause = getClause();
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final long limit = RandomUtils.nextLong();
        final WrappedQueryClause<?, ?> result = clause.limit(limit);
        assertSame(clause, result);
        then(delegate).should().limit(limit);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#offset(long)}.
     */
    @Test
    void testOffset() {
        final WrappedQueryClause<?, ?> clause = getClause();
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final long offset = RandomUtils.nextLong();
        final WrappedQueryClause<?, ?> result = clause.offset(offset);
        assertSame(clause, result);
        then(delegate).should().offset(offset);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#restrict(QueryModifiers)}.
     */
    @Test
    void testRestrict() {
        final WrappedQueryClause<?, ?> clause = getClause();
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final QueryModifiers mods = new QueryModifiers(
                RandomUtils.nextLong(),
                RandomUtils.nextLong());
        final WrappedQueryClause<?, ?> result = clause.restrict(mods);
        assertSame(clause, result);
        then(delegate).should().restrict(mods);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#where(Predicate...)}.
     */
    @Test
    void testWhere() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        final Predicate[] eexprs = new Predicate[count];
        final Predicate[] texprs = new Predicate[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
            eexprs[i] = mock(Predicate.class);
            texprs[i] = mock(Predicate.class);
        }
        willReturn(eexprs).given(clause).extractPreFetched(exprs);
        given(transformer.translatePredicates(eexprs)).willReturn(texprs);
        final WrappedQueryClause<?, ?> result = clause.where(exprs);
        assertSame(clause, result);
        then(clause).should().extractPreFetched(exprs);
        then(transformer).should().translatePredicates(eexprs);
        then(delegate).should().where(texprs);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#orderBy(OrderSpecifier...)}.
     */
    @Test
    void testOrderBy() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final int count = RandomUtils.nextInt(2, 10);
        final OrderSpecifier<?>[] exprs = new OrderSpecifier<?>[count];
        final OrderSpecifier<?>[] texprs = new OrderSpecifier<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(OrderSpecifier.class);
            texprs[i] = mock(OrderSpecifier.class);
        }
        given(transformer.translateOrderSpecifiers(exprs)).willReturn(texprs);
        final WrappedQueryClause<?, ?> result = clause.orderBy(exprs);
        assertSame(clause, result);
        then(transformer).should().translateOrderSpecifiers(exprs);
        then(delegate).should().orderBy(texprs);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#set(ParamExpression, Object)}.
     */
    @Test
    void testSet() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        @SuppressWarnings("unchecked")
        final ParamExpression<TestTypes.SimpleType> param = mock(ParamExpression.class);
        final TestTypes.SimpleType value = mock(TestTypes.SimpleType.class);
        final WrappedQueryClause<?, ?> result = clause.set(param, value);
        assertSame(clause, result);
        then(delegate).should().set(param, value);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#fetch()}.
     */
    @Test
    void testFetch() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final List<?> expected = mock(List.class);
        willReturn(expected).given(delegate).fetch();
        final List<?> result = clause.fetch();
        assertSame(expected, result);
        then(delegate).should().fetch();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#fetchFirst()}.
     */
    @Test
    void testFetchFirst() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final Object expected = new Object();
        if (delegate instanceof QueryBase) {
            willReturn(delegate).given(delegate).limit(1);
            willReturn(expected).given(delegate).fetchOne();
        } else {
            willReturn(expected).given(delegate).fetchFirst();
        }
        final Object result = clause.fetchFirst();
        assertSame(expected, result);
        if (delegate instanceof QueryBase) {
            then(delegate).should().limit(1);
            then(delegate).should().fetchOne();
        } else {
            then(delegate).should().fetchFirst();
        }
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#fetchOne()}.
     */
    @Test
    void testFetchOne() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final Object expected = new Object();
        willReturn(expected).given(delegate).fetchOne();
        final Object result = clause.fetchOne();
        assertSame(expected, result);
        then(delegate).should().fetchOne();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#fetchOne()}.
     */
    @Test
    void testFetchOneException() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final NonUniqueResultException expected = new NonUniqueResultException();
        willThrow(expected).given(delegate).fetchOne();
        final NonUniqueResultException result = assertThrows(NonUniqueResultException.class, () -> {
            clause.fetchOne();
        });
        assertSame(expected, result);
        then(delegate).should().fetchOne();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#iterate()}.
     */
    @Test
    void testIterate() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final CloseableIterator<?> expected = mock(CloseableIterator.class);
        willReturn(expected).given(delegate).iterate();
        final CloseableIterator<?> result = clause.iterate();
        assertSame(expected, result);
        then(delegate).should().iterate();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#fetchResults()}.
     */
    @Test
    void testFetchResults() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final QueryResults<?> expected = new QueryResults<>(Collections.emptyList(), null, null, 0);
        willReturn(expected).given(delegate).fetchResults();
        final QueryResults<?> result = clause.fetchResults();
        assertSame(expected, result);
        then(delegate).should().fetchResults();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#fetchCount()}.
     */
    @Test
    void testFetchCount() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final long expected = RandomUtils.nextLong();
        willReturn(expected).given(delegate).fetchCount();
        final long result = clause.fetchCount();
        assertEquals(expected, result);
        then(delegate).should().fetchCount();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedQueryClause#transform(ResultTransformer)}.
     */
    @Test
    void testTransform() {
        final WrappedQueryClause<?, ?> clause = spy(getClause());
        final FetchableQuery<?, ?> delegate = clause.getDelegate();
        final ResultTransformer<?> transformer = mock(ResultTransformer.class);
        final Object expected = new Object();
        willReturn(expected).given(delegate).transform(transformer);
        final Object result = clause.transform(transformer);
        assertSame(expected, result);
        then(delegate).should().transform(transformer);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    private class TestWrappedClause<T>
    extends WrappedQueryClause<T, TestWrappedClause<T>> {
        private final @NotNull FetchableQuery<T, ?> delegate;
        public TestWrappedClause(
                ExpressionTransformer transformer,
                FetchableQuery<T, ?> delegate) {
            super(transformer);
            this.delegate = delegate;
        }
        @Override
        protected @NotNull FetchableQuery<T, ?> getDelegate() {
            return this.delegate;
        }
        @Override
        public @NotNull ExtendedSubQuery<T> asSubQuery() {
            throw new NotImplementedException();
        }
        @Override
        public <U> WrappedQueryClause<U, ?> select(@NotNull Expression<U> expr) {
            throw new NotImplementedException();
        }
        @Override
        public @NotNull WrappedQueryClause<Tuple, ?> select(@NotNull Expression<?>... exprs) {
            throw new NotImplementedException();
        }
    }
}
