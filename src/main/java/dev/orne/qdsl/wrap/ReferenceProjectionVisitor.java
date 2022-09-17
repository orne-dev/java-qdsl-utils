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

import com.querydsl.core.types.Visitor;

/**
 * {@code ReferenceProjectionVisitor} defines a visitor signature for
 * {@link ReferenceProjection} expression values.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @param <R> The result type
 * @param <C> The context type
 * @since 0.1
 */
public interface ReferenceProjectionVisitor<R, C>
extends Visitor<R, C> {

    /**
     * Visit a {@code ReferenceProjection} instance with the given context.
     *
     * @param expr The reference projection to visit
     * @param context The context of the visit or null, if not used
     * @return The visit result
     */
    R visit(
            @NotNull ReferenceProjection<?, ?> value,
            C context);
}
