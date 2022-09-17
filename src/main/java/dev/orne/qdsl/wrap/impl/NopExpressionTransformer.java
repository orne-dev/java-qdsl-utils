package dev.orne.qdsl.wrap.impl;

import java.util.Collections;
import java.util.List;

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

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import dev.orne.qdsl.NopReplaceVisitor;
import dev.orne.qdsl.OrderSpecifierReplaceVisitor;
import dev.orne.qdsl.wrap.ReferenceProjection;
import dev.orne.qdsl.wrap.ReferenceProjectionReplaceVisitor;
import dev.orne.qdsl.wrap.StoredValues;
import dev.orne.qdsl.wrap.StoredValuesReplaceVisitor;

/**
 * No operation implementation of {@code Translator}.
 * Returns the provided parameters.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
public class NopExpressionTransformer
extends NopReplaceVisitor
implements ExpressionTransformer,
        ReferenceProjectionReplaceVisitor<Void>,
        OrderSpecifierReplaceVisitor<Void>,
        StoredValuesReplaceVisitor<Void> {

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull ReferenceProjection<?, ?> value,
            final Void context) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<OrderSpecifier<?>> visit(
            final @NotNull OrderSpecifier<?> expr,
            final Void context) {
        return Collections.singletonList(expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull StoredValues visit(
            final @NotNull StoredValues expr,
            final Void context) {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?>[] translateProjections(
            final @NotNull Expression<?>... exprs) {
        return exprs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> @NotNull Expression<U> translateProjection(
            final @NotNull Expression<U> expr) {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Predicate[] translatePredicates(
            final @NotNull Predicate... exprs) {
        return exprs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull OrderSpecifier<?>[] translateOrderSpecifiers(
            final @NotNull OrderSpecifier<?>... orders) {
        return orders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?>[] translateGroupByExpressions(
            final @NotNull Expression<?>... exprs) {
        return exprs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull StoredValues translateStoredValues(
            final @NotNull StoredValues assigments) {
        return assigments;
    }
}
