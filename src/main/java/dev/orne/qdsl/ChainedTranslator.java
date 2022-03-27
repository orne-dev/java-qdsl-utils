package dev.orne.qdsl;

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
import java.util.List;

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

/**
 * QueryDSL expression translator that applies delegated translators in chain..
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-03
 * @since 0.1
 */
public class ChainedTranslator
extends ChainedReplaceVisitor
implements Translator {

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     */
    @SafeVarargs
    public ChainedTranslator(
            final Visitor<Expression<?>, ?>... visitors) {
        super(visitors);
    }

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     */
    public ChainedTranslator(
            final Collection<Visitor<Expression<?>, ?>> visitors) {
        super(visitors);
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
            throw new QueryTranslationException(String.format(
                    "Illegal null projection after translation of '%s'.",
                    expr));
        }
        if (!expr.getType().isAssignableFrom(result.getType())) {
            throw new QueryTranslationException(String.format(
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
            throw new QueryTranslationException(String.format(
                    "Illegal null predicate after translation of '%s'.",
                    expr));
        }
        if (!(result instanceof Predicate)) {
            throw new QueryTranslationException(String.format(
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
            throw new QueryTranslationException(String.format(
                    "Illegal null order specifiers list after translation of '%s'.",
                    order));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull ValueAssignments translateAssigments(
            final @NotNull ValueAssignment<?>... assigments) {
        return translateAssigments(ValueAssignments.of(assigments));
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull ValueAssignments translateAssigments(
            final @NotNull ValueAssignments assigments) {
        return assigments.parallelStream()
                .map(this::translateAssigment)
                .collect(
                    ValueAssignments::new,
                    ValueAssignments::addAll,
                    ValueAssignments::addAll);
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull ValueAssignments translateAssigment(
            final @NotNull ValueAssignment<?> assigment) {
        final ValueAssignments result = assigment.accept(this, null);
        if (result == null) {
            throw new QueryTranslationException(String.format(
                    "Illegal null value assignments after translation of '%s'.",
                    assigment));
        }
        return result;
    }
}
