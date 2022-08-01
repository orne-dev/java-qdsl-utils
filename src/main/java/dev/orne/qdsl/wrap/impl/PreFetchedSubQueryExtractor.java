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

import java.util.Arrays;

import javax.validation.constraints.NotNull;

import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;

import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Visitor that replaces {@code PreFetchedSubQueryExpression} instances
 * with constant expression of the query result values.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
public class PreFetchedSubQueryExtractor
extends ReplaceVisitor<Void> {

    /** The shared instance. */
    public static final PreFetchedSubQueryExtractor INSTANCE =
            new PreFetchedSubQueryExtractor();

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
    public <T> Expression<T> extract(
            final Expression<T> expr) {
        final Expression<?> uresult = expr.accept(this, null);
        if (!expr.getType().isAssignableFrom(uresult.getType())) {
            throw new ExpressionTransformationException(String.format(
                    "Unexpected expression incompatible type change: %s vs %s",
                    expr.getType(),
                    uresult.getType()));
        }
        @SuppressWarnings("unchecked")
        final Expression<T> result = (Expression<T>) uresult;
        return result;
    }

    /**
     * Transforms the specified expressions, replacing
     * {@code PreFetchedSubQueryExpression} instances with constant expression
     * of the query result values.
     * 
     * @param exprs The original expressions
     * @return The resulting expressions
     */
    public Expression<?>[] extract(
            final Expression<?>... exprs) {
        return Arrays.asList(exprs)
                .parallelStream()
                .map(p -> p.accept(this, null))
                .toArray(Expression<?>[]::new);
    }

    /**
     * Transforms the specified predicates, replacing
     * {@code PreFetchedSubQueryExpression} instances with constant expression
     * of the query result values.
     * 
     * @param exprs The original predicates
     * @return The resulting predicates
     */
    public Predicate[] extract(
            final Predicate... predicates) {
        return Arrays.asList(predicates)
                .parallelStream()
                .map(p -> p.accept(this, null))
                .toArray(Predicate[]::new);
    }

    /**
     * Transforms the specified value assignments, replacing
     * {@code PreFetchedSubQueryExpression} instances in the values
     * with constant expression of the query result values.
     * 
     * @param exprs The original value assignments
     * @return The resulting value assignments
     */
    public StoredValues extract(
            final StoredValues assignments) {
        final StoredValues result = new StoredValues();
        for (final StoredValue<?> assignment : assignments) {
            result.add(extract(assignment));
        }
        return result;
    }

    /**
     * Transforms the specified value assignment, replacing
     * {@code PreFetchedSubQueryExpression} instances in the value
     * with constant expression of the query result values.
     * 
     * @param exprs The original value assignment
     * @return The resulting value assignment
     */
    public <T> StoredValue<T> extract(
            final StoredValue<T> assignment) {
        return StoredValue.ofUntyped(
                assignment.getPath(),
                extract(assignment.getValue()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Replaces {@code PreFetchedSubQueryExpression} instances
     * with constant expression of the query result values
     */
    @Override
    public Expression<?> visit(
            final @NotNull SubQueryExpression<?> expr,
            final Void context) {
        if (expr instanceof PreFetchedSubQueryExpression) {
            return Expressions.constant(
                    ((PreFetchedSubQueryExpression<?>) expr).getValues());
        } else {
            return super.visit(expr, context);
        }
    }
}
