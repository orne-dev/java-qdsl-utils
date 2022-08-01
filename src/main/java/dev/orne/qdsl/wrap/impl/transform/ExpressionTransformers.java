package dev.orne.qdsl.wrap.impl.transform;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021 - 2022 Orne Developments
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

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;

/**
 * Factory methods for creation of built in {@code ExpressionTransformer}
 * instances.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see ExpressionTransformer
 */
public final class ExpressionTransformers {

    /**
     * Private constructor.
     */
    private ExpressionTransformers() {
        // Utility class
    }

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     * @return The created instance
     */
    @SafeVarargs
    public static ChainedExpressionTransformer with(
            final Visitor<Expression<?>, ?>... visitors) {
        return new ChainedExpressionTransformer(visitors);
    }

    /**
     * Starts the creation of a expression translator for the specified source
     * path.
     * 
     * @param <S> The source path type
     * @param path The source path
     * @return A builder, to chain configuration calls
     */
    public static <S> SimplePathTransformer.TargetBuilder<S> fromPath(
            final @NotNull Path<S> path) {
        return SimplePathTransformer.fromPath(path);
    }

    /**
     * Creates a property path translator that translates source path
     * references to the target path of the same type.
     * 
     * @param <V> The properties type
     * @param source The source property path
     * @param target The target property path
     * @return The property path translator
     */
    public static <V> SimplePathTransformer<V> renamePath(
            final @NotNull Path<V> source,
            final @NotNull Path<V> target) {
        return SimplePathTransformer.fromPath(source)
                .toPath(target)
                .build();
    }
}
