package dev.orne.qdsl.wrap;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021-2022 Orne Developments
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

/**
 * Visitor signature for {@link ValueStoreClause} instances that
 * replaces the visited assignment with the returned assignments.
 * <p>
 * Provides a default {@code ValuesStoreClauseReplaceVisitor}
 * implementation that aggregates the result of visiting each
 * internal {@link ValueStoreClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <C> Context type
 * @since 0.1
 */
public interface ValueStoreClauseReplaceVisitor<C>
extends ValueStoreClauseVisitor<ValuesStoreClause, C>,
        ValuesStoreClauseReplaceVisitor<C> {

    @Override
    default ValuesStoreClause visit(
            final @NotNull ValuesStoreClause expr,
            final C context) {
        final ValuesStoreClause result = new ValuesStoreClause();
        for (final ValueStoreClause<?> value : expr) {
            result.addAll(value.accept(this, context));
        }
        if (!expr.equals(result)) {
            return result;
        }
        return expr;
    }
}
