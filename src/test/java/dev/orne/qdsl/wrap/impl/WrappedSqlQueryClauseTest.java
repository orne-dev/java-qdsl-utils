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

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.ProjectableSQLQuery;

/**
 * Unit tests for {@code WrappedSqlQueryClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 * @see WrappedSqlQueryClause
 */
@Tag("ut")
class WrappedSqlQueryClauseTest
extends WrappedQueryClauseTest {

    @Override
    protected @NotNull WrappedSqlQueryClause<?> getClause() {
        @SuppressWarnings("unchecked")
        final WrappedSqlQueryClause<?> result = new WrappedSqlQueryClause<>(
                transformer,
                mock(ProjectableSQLQuery.class));
        return result;
    }

    /**
     * Unit test for {@link WrappedSqlQueryClause#getDelegate()}.
     */
    @Test
    void testGetDelegate() {
        final ProjectableSQLQuery<?, ?> delegate = mock(ProjectableSQLQuery.class);
        final WrappedSqlQueryClause<?> result = new WrappedSqlQueryClause<>(
                transformer,
                delegate);
        assertSame(delegate, result.getDelegate());
    }

    /**
     * Unit test for {@link WrappedSqlQueryClause#select(Expression...)}.
     */
    @Test
    void testSelectProjection() {
        final WrappedSqlQueryClause<?> clause = spy(getClause());
        final ProjectableSQLQuery<?, ?> delegate = clause.getDelegate();
        final ProjectableSQLQuery<?, ?> newDelegate = mock(ProjectableSQLQuery.class);
        final Expression<?> expr = mock(Expression.class);
        final Expression<?> eexpr = mock(Expression.class);
        final Expression<?> texpr = mock(Expression.class);
        willReturn(eexpr).given(clause).extractPreFetched(expr);
        willReturn(texpr).given(transformer).translateProjection(eexpr);
        willReturn(newDelegate).given(delegate).select(texpr);
        final WrappedSqlQueryClause<?> result = clause.select(expr);
        assertNotSame(clause, result);
        assertSame(newDelegate, result.getDelegate());
        then(clause).should().extractPreFetched(expr);
        then(transformer).should().translateProjection(eexpr);
        then(delegate).should().select(texpr);
        then(delegate).shouldHaveNoMoreInteractions();
        then(newDelegate).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedSqlQueryClause#select(Expression...)}.
     */
    @Test
    void testSelectTuple() {
        final WrappedSqlQueryClause<?> clause = spy(getClause());
        final ProjectableSQLQuery<?, ?> delegate = clause.getDelegate();
        @SuppressWarnings("unchecked")
        final ProjectableSQLQuery<Tuple, ?> newDelegate = mock(ProjectableSQLQuery.class);
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] eexprs = new Expression<?>[count];
        final Expression<?>[] texprs = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
            eexprs[i] = mock(Expression.class);
            texprs[i] = mock(Expression.class);
        }
        willReturn(eexprs).given(clause).extractPreFetched(exprs);
        given(transformer.translateProjections(eexprs)).willReturn(texprs);
        willReturn(newDelegate).given(delegate).select(texprs);
        final WrappedSqlQueryClause<Tuple> result = clause.select(exprs);
        assertNotSame(clause, result);
        assertSame(newDelegate, result.getDelegate());
        then(clause).should().extractPreFetched(exprs);
        then(transformer).should().translateProjections(eexprs);
        then(delegate).should().select(texprs);
        then(delegate).shouldHaveNoMoreInteractions();
        then(newDelegate).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedSqlQueryClause#groupBy(Expression...)}.
     */
    @Test
    void testGroupBy() {
        final WrappedSqlQueryClause<?> clause = spy(getClause());
        final ProjectableSQLQuery<?, ?> delegate = clause.getDelegate();
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] eexprs = new Expression<?>[count];
        final Expression<?>[] texprs = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
            eexprs[i] = mock(Expression.class);
            texprs[i] = mock(Expression.class);
        }
        willReturn(eexprs).given(clause).extractPreFetched(exprs);
        given(transformer.translateGroupByExpressions(eexprs)).willReturn(texprs);
        final WrappedSqlQueryClause<?> result = clause.groupBy(exprs);
        assertSame(clause, result);
        then(clause).should().extractPreFetched(exprs);
        then(transformer).should().translateGroupByExpressions(eexprs);
        then(delegate).should().groupBy(texprs);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedSqlQueryClause#having(Predicate...)}.
     */
    @Test
    void testHaving() {
        final WrappedSqlQueryClause<?> clause = spy(getClause());
        final ProjectableSQLQuery<?, ?> delegate = clause.getDelegate();
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
        final WrappedSqlQueryClause<?> result = clause.having(exprs);
        assertSame(clause, result);
        then(clause).should().extractPreFetched(exprs);
        then(transformer).should().translatePredicates(eexprs);
        then(delegate).should().having(texprs);
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedSqlQueryClause#asSubQuery()}.
     */
    @Test
    void testAsSubQuery() {
        final WrappedSqlQueryClause<?> clause = getClause();
        final ProjectableSQLQuery<?, ?> delegate = clause.getDelegate();
        assertSame(delegate, clause.asSubQuery());
        then(delegate).shouldHaveNoInteractions();
    }
}
