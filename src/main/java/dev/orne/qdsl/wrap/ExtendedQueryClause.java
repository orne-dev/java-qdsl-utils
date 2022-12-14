package dev.orne.qdsl.wrap;

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

import com.querydsl.core.FetchableQuery;
import com.querydsl.core.Tuple;
import com.querydsl.core.support.ExtendedSubQuery;
import com.querydsl.core.types.Expression;

/**
 * Extension of {@code FetchableQuery} that provides an additional method to
 * convert the query to a sub query expression.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <T> The query results type
 * @param <C> The clause type
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public interface ExtendedQueryClause<T, C extends ExtendedQueryClause<T, C>>
extends FetchableQuery<T, C> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> @NotNull ExtendedQueryClause<U, ?> select(
            @NotNull Expression<U> expr);

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull ExtendedQueryClause<Tuple, ?> select(
            @NotNull Expression<?>... exprs);

    /**
     * Returns a sub query expression usable in other clauses.
     * 
     * @return The sub query expression
     */
    @NotNull ExtendedSubQuery<T> asSubQuery();
}
