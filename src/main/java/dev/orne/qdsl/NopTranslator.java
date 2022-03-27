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

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

/**
 * No operation implementation of {@code Translator}.
 * Returns the provided parameters.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-03
 * @since 0.1
 */
public class NopTranslator
implements Translator {

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
    public @NotNull Predicate translatePredicate(
            final @NotNull Predicate expr) {
        return expr;
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
    public @NotNull List<OrderSpecifier<?>> translateOrderSpecifier(
            final @NotNull OrderSpecifier<?> order) {
        return Collections.singletonList(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssignments translateAssigments(
            final @NotNull ValueAssignment<?>... assigments) {
        return ValueAssignments.of(assigments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssignments translateAssigments(
            final @NotNull ValueAssignments assigments) {
        return assigments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssignments translateAssigment(
            final @NotNull ValueAssignment<?> assigment) {
        return ValueAssignments.of(assigment);
    }
}
