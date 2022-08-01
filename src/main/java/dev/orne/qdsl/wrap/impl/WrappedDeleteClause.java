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

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.types.Predicate;

/**
 * Wrapped QueryDSL delete clause.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
public class WrappedDeleteClause
extends WrappedClause<WrappedDeleteClause>
implements DeleteClause<WrappedDeleteClause> {

    /** The delegate QueryDSL delete clause. */
    private final @NotNull DeleteClause<?> delegate;

    /**
     * Creates a new instance with no-operation transformer.
     * 
     * @param delegate The delegate QueryDSL delete clause
     */
    public WrappedDeleteClause(
            final @NotNull DeleteClause<?> delegate) {
        this(delegate, null);
    }

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param delegate The delegate QueryDSL delete clause
     * @param transformer The expression transformer to use
     */
    public WrappedDeleteClause(
            final @NotNull DeleteClause<?> delegate,
            final ExpressionTransformer transformer) {
        super(transformer);
        this.delegate = Validate.notNull(delegate, "Delegate clause is required");
    }

    /**
     * Returns the delegate QueryDSL delete clause.
     * 
     * @return The delegate QueryDSL delete clause
     */
    protected @NotNull DeleteClause<?> getDelegate() {
        return this.delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedDeleteClause where(
            final @NotNull Predicate... exprs) {
        getDelegate().where(getTransformer().translatePredicates(
                extractPreFetched(exprs)));
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long execute() {
        return getDelegate().execute();
    }
}
