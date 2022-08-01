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

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;

import dev.orne.qdsl.wrap.ExtendedGroupableQueryClause;

/**
 * Wrapped QueryDSL JPA query clause.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @param <T> The query results type
 * @since 0.1
 */
public class WrappedJpaQueryClause<T>
extends WrappedQueryClause<T, WrappedJpaQueryClause<T>>
implements ExtendedGroupableQueryClause<T, WrappedJpaQueryClause<T>> {

    /** The delegated QueryDSL JPA query clause. */
    private final @NotNull JPQLQuery<T> delegate;

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param transformer The expression transformer to use
     * @param delegate The delegated JPA query
     */
    public WrappedJpaQueryClause(
            final ExpressionTransformer transformer,
            final @NotNull JPQLQuery<T> delegate) {
        super(transformer);
        this.delegate = Validate.notNull(delegate);
    }

    /**
     * Returns the delegate QueryDSL JPA query clause.
     * 
     * @return The delegate QueryDSL JPA query clause
     */
    @Override
    protected @NotNull JPQLQuery<T> getDelegate() {
        return this.delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> WrappedJpaQueryClause<U> select(
            final @NotNull Expression<U> expr) {
        final Expression<U> proj = getTransformer().translateProjection(
                extractPreFetched(expr));
        return new WrappedJpaQueryClause<>(
                getTransformer(),
                delegate.select(proj));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedJpaQueryClause<Tuple> select(
            final @NotNull Expression<?>... exprs) {
        final Expression<?>[] projs = getTransformer().translateProjections(
                extractPreFetched(exprs));
        return new WrappedJpaQueryClause<>(
                getTransformer(),
                delegate.select(projs));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WrappedJpaQueryClause<T> groupBy(
            final @NotNull Expression<?>... exprs) {
        getDelegate().groupBy(getTransformer().translateGroupByExpressions(
                extractPreFetched(exprs)));
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WrappedJpaQueryClause<T> having(
            final @NotNull Predicate... conditions) {
        getDelegate().having(getTransformer().translatePredicates(
                extractPreFetched(conditions)));
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull JPQLQuery<T> asSubQuery() {
        return this.delegate;
    }
}
