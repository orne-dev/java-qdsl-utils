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

import org.apache.commons.lang3.ObjectUtils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;

import dev.orne.qdsl.wrap.StoredValues;

/**
 * Base class for wrapped QueryDSL clauses.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <C> The clause type
 * @since 0.1
 */
public abstract class WrappedClause<C extends WrappedClause<C>> {

    /** The expression transformer. */
    private final @NotNull ExpressionTransformer transformer;

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param transformer The expression transformer to use
     */
    protected WrappedClause(
            final ExpressionTransformer transformer) {
        super();
        this.transformer = ObjectUtils.defaultIfNull(transformer, ExpressionTransformer.NOP);
    }

    /**
     * Returns used the expression transformer.
     * 
     * @return The expression transformer
     */
    protected @NotNull ExpressionTransformer getTransformer() {
        return this.transformer;
    }

    /**
     * Returns the prefetched subquery extractor.
     * 
     * @return The prefetched subquery extractor
     */
    protected @NotNull PreFetchedSubQueryExtractor getPreFetchedExtractor() {
        return PreFetchedSubQueryExtractor.INSTANCE;
    }

    /**
     * Returns this instance as the clause type.
     * <p>
     * To avoid "unchecked" warnings in method chaining methods.
     * 
     * @return This instance as clause type
     */
    @SuppressWarnings("unchecked")
    protected @NotNull C self() {
        return (C) this;
    }

    /**
     * Transforms the specified expression, replacing
     * {@code PreFetchedSubQueryExpression} instances with constant expression
     * of the query result values.
     * <p>
     * Verifies that the resulting expression is of a type assignable to the
     * original expression type.
     * 
     * @param <T> The expression type
     * @param expr The original expression
     * @return The resulting expression
     */
    protected <T> @NotNull Expression<T> extractPreFetched(
            final @NotNull Expression<T> expr) {
        return getPreFetchedExtractor().extract(expr);
    }

    /**
     * Transforms the specified expressions, replacing
     * {@code PreFetchedSubQueryExpression} instances with constant expression
     * of the query result values.
     * 
     * @param exprs The original expressions
     * @return The resulting expressions
     */
    protected @NotNull Expression<?>[] extractPreFetched(
            final @NotNull Expression<?>... exprs) {
        return getPreFetchedExtractor().extract(exprs);
    }

    /**
     * Transforms the specified predicates, replacing
     * {@code PreFetchedSubQueryExpression} instances with constant expression
     * of the query result values.
     * 
     * @param exprs The original predicates
     * @return The resulting predicates
     */
    protected @NotNull Predicate[] extractPreFetched(
            final @NotNull Predicate... predicates) {
        return getPreFetchedExtractor().extract(predicates);
    }

    /**
     * Transforms the specified value assignments, replacing
     * {@code PreFetchedSubQueryExpression} instances in the values
     * with constant expression of the query result values.
     * 
     * @param exprs The original value assignments
     * @return The resulting value assignments
     */
    protected @NotNull StoredValues extractPreFetched(
            final @NotNull StoredValues assignments) {
        return getPreFetchedExtractor().extract(assignments);
    }
}
