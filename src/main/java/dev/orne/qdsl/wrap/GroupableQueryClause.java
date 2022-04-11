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

import com.querydsl.core.Query;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

/**
 * Extension of {@code QueryClause} that supports grouping of results.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <T> The query results type
 * @param <C> The clause type
 * @since 0.1
 * @see Query
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public interface GroupableQueryClause<T, C extends GroupableQueryClause<T, C>>
extends QueryClause<T, C>, Query<C> {

    /**
     * {@inheritDoc}
     */
    @Override
    <U> @NotNull GroupableQueryClause<U, ?> select(
            @NotNull Expression<U> expr);

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull GroupableQueryClause<Tuple, ?> select(
            @NotNull Expression<?>... exprs);
}
