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

import org.apache.commons.lang3.Validate;
import org.apiguardian.api.API;

import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;

/**
 * Visitor that replaces appearances of the source entity path with of
 * the target entity path. Both entities must be of the same type.
 * Used to apply or remove aliases from expressions.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public class EntityAliasReplacer
extends ReplaceVisitor<Void> {

    /** The source entity path. */
    private final @NotNull EntityPath<?> source;
    /** The target entity path. */
    private final @NotNull EntityPath<?> target;

    /**
     * Creates a new instance.
     * 
     * @param source The source entity path
     * @param target The target entity path
     */
    public EntityAliasReplacer(
            final @NotNull EntityPath<?> source,
            final @NotNull EntityPath<?> target) {
        super();
        this.source = Validate.notNull(source);
        this.target = Validate.notNull(target);
        Validate.isTrue(
                source.getClass().equals(target.getClass()),
                "Both entities must be of same type: %s vs %s",
                source,
                target);
    }

    /**
     * Returns the source entity path.
     * 
     * @return The source entity path
     */
    protected @NotNull EntityPath<?> getSource() {
        return this.source;
    }

    /**
     * Returns the target entity path.
     * 
     * @return The target entity path
     */
    protected @NotNull EntityPath<?> getTarget() {
        return this.target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Path<?> expr,
            final Void context) {
        if (this.source.equals(expr)) {
            return this.target;
        } else {
            return super.visit(expr, context);
        }
    }

    /**
     * Replaces appearances of the source entity path in the specified
     * expression with the target entity path.
     * 
     * @param <T> The type of the expression
     * @param expr The source expression
     * @return The resulting expression
     */
    @SuppressWarnings("unchecked")
    public <T extends Expression<?>> T apply(
            T expr) {
        if (expr == null) {
            return null;
        }
        return (T) expr.accept(this, null);
    }
}
