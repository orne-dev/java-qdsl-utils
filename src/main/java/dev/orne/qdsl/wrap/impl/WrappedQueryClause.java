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

import java.util.List;

import javax.validation.constraints.NotNull;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;

import dev.orne.qdsl.wrap.ExtendedQueryClause;

/**
 * Base class for wrapped QueryDSL query clauses.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <T> The query results type
 * @param <C> The clause type
 * @since 0.1
 */
public abstract class WrappedQueryClause<T, C extends WrappedQueryClause<T, C>>
extends WrappedClause<C>
implements ExtendedQueryClause<T, C> {

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param transformer The expression transformer to use
     */
    protected WrappedQueryClause(
            final ExpressionTransformer transformer) {
        super(transformer);
    }

    /**
     * Returns the delegate QueryDSL fetchable query clause.
     * 
     * @return The delegate QueryDSL fetchable query clause
     */
    protected abstract @NotNull FetchableQuery<T, ?> getDelegate();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract <U> WrappedQueryClause<U, ?> select(
            @NotNull Expression<U> expr);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract @NotNull WrappedQueryClause<Tuple, ?> select(
            @NotNull Expression<?>... exprs);

    /**
     * {@inheritDoc}
     */
    @Override
    public C distinct() {
        getDelegate().distinct();
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C limit(
            final long limit) {
        getDelegate().limit(limit);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C offset(
            final long offset) {
        getDelegate().offset(offset);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C restrict(
            final @NotNull QueryModifiers modifiers) {
        getDelegate().restrict(modifiers);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C where(
            final Predicate... conds) {
        getDelegate().where(getTransformer().translatePredicates(
                extractPreFetched(conds)));
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C orderBy(
            final OrderSpecifier<?>... orders) {
        getDelegate().orderBy(getTransformer().translateOrderSpecifiers(orders));
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <P> C set(
            final @NotNull ParamExpression<P> param,
            final P value) {
        getDelegate().set(param, value);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> fetch() {
        return getDelegate().fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T fetchFirst() {
        return getDelegate().fetchFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T fetchOne() throws NonUniqueResultException {
        return getDelegate().fetchOne();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<T> iterate() {
        return getDelegate().iterate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResults<T> fetchResults() {
        return getDelegate().fetchResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long fetchCount() {
        return getDelegate().fetchCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> S transform(
            final @NotNull ResultTransformer<S> transformer) {
        return getDelegate().transform(transformer);
    }
}
