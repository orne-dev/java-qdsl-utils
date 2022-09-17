package dev.orne.qdsl.wrap.impl.transform;

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

import dev.orne.qdsl.ChainedReplaceVisitor;
import dev.orne.qdsl.OrderSpecifierReplaceVisitor;
import dev.orne.qdsl.wrap.ReferenceProjection;
import dev.orne.qdsl.wrap.ReferenceProjectionReplaceVisitor;
import dev.orne.qdsl.wrap.StoredValues;
import dev.orne.qdsl.wrap.StoredValuesReplaceVisitor;
import dev.orne.qdsl.wrap.impl.ExpressionTransformationException;
import dev.orne.qdsl.wrap.impl.ExpressionTransformer;

/**
 * QueryDSL expression translator that applies delegated translators in chain..
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
public class ChainedExpressionTransformer
extends ChainedReplaceVisitor
implements ExpressionTransformer,
        ReferenceProjectionReplaceVisitor<Void>,
        OrderSpecifierReplaceVisitor<Void>,
        StoredValuesReplaceVisitor<Void> {

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     */
    @SafeVarargs
    public ChainedExpressionTransformer(
            final Visitor<Expression<?>, ?>... visitors) {
        super(visitors);
    }

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     */
    public ChainedExpressionTransformer(
            final Collection<Visitor<Expression<?>, ?>> visitors) {
        super(visitors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final @NotNull ReferenceProjection<?, ?> value,
            final Void context) {
        Expression<?> result = value;
        for (final Visitor<Expression<?>, ?> visitor : getVisitors()) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderSpecifier<?>> visit(
            final @NotNull OrderSpecifier<?> order,
            final Void context) {
        List<OrderSpecifier<?>> result = Collections.singletonList(order);
        for (final Visitor<Expression<?>, ?> visitor : getVisitors()) {
            if (visitor instanceof OrderSpecifierReplaceVisitor) {
                final OrderSpecifierReplaceVisitor<?> ovisitor =
                        (OrderSpecifierReplaceVisitor<?>) visitor;
                result = result.parallelStream()
                        .map(e -> ovisitor.visit(e, null))
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            } else {
                result = result.parallelStream()
                        .map(p -> OrderSpecifierReplaceVisitor.fromComponents(p, visitor))
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull StoredValues visit(
            final @NotNull StoredValues expr,
            final Void context) {
        StoredValues result = expr;
        for (final Visitor<Expression<?>, ?> visitor : getVisitors()) {
            if (visitor instanceof StoredValuesTransformer) {
                ((StoredValuesTransformer) visitor).visit(result, null);
            } else if (visitor instanceof StoredValuesReplaceVisitor) {
                final StoredValuesReplaceVisitor<?> vvisitor =
                        (StoredValuesReplaceVisitor<?>) visitor;
                result = vvisitor.visit(result, null);
            } else {
                result = result.parallelStream()
                        .map(p -> StoredValuesTransformer.translateFromComponents(p, visitor))
                        .collect(
                                StoredValues::new,
                                StoredValues::addAll,
                                StoredValues::addAll);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull Expression<?>[] translateProjections(
            final @NotNull Expression<?>... exprs) {
        return Arrays.asList(exprs)
                .parallelStream()
                .map(this::translateProjection)
                .toArray(Expression<?>[]::new);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public @NotNull <U> Expression<U> translateProjection(
            final Expression<U> expr) {
        final Expression<?> result = expr.accept(this, null);
        if (result == null) {
            throw new ExpressionTransformationException(String.format(
                    "Illegal null projection after translation of '%s'.",
                    expr));
        }
        if (!expr.getType().isAssignableFrom(result.getType())) {
            throw new ExpressionTransformationException(String.format(
                    "Translation of projection '%s' is not of a compatible type: %s vs %s",
                    expr,
                    expr.getType(),
                    result.getType()));
        }
        return (Expression<U>) result;
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull Predicate[] translatePredicates(
            final @NotNull Predicate... exprs) {
        return Arrays.asList(exprs)
                .parallelStream()
                .map(this::translatePredicate)
                .toArray(Predicate[]::new);
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull Predicate translatePredicate(
            final @NotNull Predicate expr) {
        final Expression<?> result = expr.accept(this, null);
        if (result == null) {
            throw new ExpressionTransformationException(String.format(
                    "Illegal null predicate after translation of '%s'.",
                    expr));
        }
        if (!(result instanceof Predicate)) {
            throw new ExpressionTransformationException(String.format(
                    "Translation of predicate '%s' is not a predicate: %s",
                    expr,
                    result.getType()));
        }
        return (Predicate) result;
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull OrderSpecifier<?>[] translateOrderSpecifiers(
            final @NotNull OrderSpecifier<?>... orders) {
        return Arrays.asList(orders)
                .parallelStream()
                .map(this::translateOrderSpecifier)
                .flatMap(List::stream)
                .toArray(OrderSpecifier<?>[]::new);
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull List<OrderSpecifier<?>> translateOrderSpecifier(
            final @NotNull OrderSpecifier<?> order) {
        final List<OrderSpecifier<?>> result = this.visit(order, null);
        if (result == null) {
            throw new ExpressionTransformationException(String.format(
                    "Illegal null order specifiers list after translation of '%s'.",
                    order));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?>[] translateGroupByExpressions(
            final @NotNull Expression<?>... exprs) {
        return Arrays.asList(exprs)
                .parallelStream()
                .map(this::translateGroupByExpression)
                .toArray(Expression<?>[]::new);
    }

    public @NotNull Expression<?> translateGroupByExpression(
            final @NotNull Expression<?> expr) {
        return  expr.accept(this, null);
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull StoredValues translateStoredValues(
            final @NotNull StoredValues assigments) {
        return assigments.accept(this, null);
    }
}
