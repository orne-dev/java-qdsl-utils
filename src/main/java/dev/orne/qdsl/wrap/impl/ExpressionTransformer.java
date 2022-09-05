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

import org.apiguardian.api.API;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

import dev.orne.qdsl.wrap.StoredValues;

/**
 * QueryDSL clause expression transformer that converts vanilla
 * expressions, order specifiers and value assignments of wrapped
 * clauses to expressions to apply in delegated clauses.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public interface ExpressionTransformer
extends Visitor<Expression<?>, Void> {

    /** Shared instance of no operation translator. */
    public static final ExpressionTransformer NOP = new NopExpressionTransformer();

    /**
     * Transforms the specified query projection expression if required.
     * <p>
     * Validates that the resulting expression type is assignable to the
     * original expression type.
     * 
     * @param <U> The type of the expression
     * @param expr The expression to transform
     * @return The resulting expression, transformed if required
     * @throws ExpressionTransformationException If an exception occurs
     */
    @NotNull <U> Expression<U> translateProjection(
            @NotNull Expression<U> expr);

    /**
     * Transforms the specified query projection expressions if required.
     * 
     * @param exprs The expressions to transform
     * @return The resulting expressions, transformed if required
     * @throws ExpressionTransformationException If an exception occurs
     */
    @NotNull Expression<?>[] translateProjections(
            @NotNull Expression<?>... exprs);

    /**
     * Transforms the specified predicate expressions if required.
     * 
     * @param exprs The expressions to transform
     * @return The resulting expressions, transformed if required
     * @throws ExpressionTransformationException If an exception occurs
     */
    @NotNull Predicate[] translatePredicates(
            @NotNull Predicate... exprs);

    /**
     * Transforms the specified order specifiers if required.
     * 
     * @param orders The order specifiers to transform
     * @return The resulting order specifiers, transformed if required
     * @throws ExpressionTransformationException If an exception occurs
     */
    @NotNull OrderSpecifier<?>[] translateOrderSpecifiers(
            @NotNull OrderSpecifier<?>... orders);

    /**
     * Transforms the specified query group by expressions if required.
     * 
     * @param exprs The expressions to transform
     * @return The resulting expressions, transformed if required
     * @throws ExpressionTransformationException If an exception occurs
     */
    @NotNull Expression<?>[] translateGroupByExpressions(
            @NotNull Expression<?>... exprs);

    /**
     * Transforms the specified value assignments if required.
     * 
     * @param assigments The value assignments to transform
     * @return The resulting value assignments, transformed if required
     * @throws ExpressionTransformationException If an exception occurs
     */
    @NotNull StoredValues translateStoredValues(
            @NotNull StoredValues assigments);
}
