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

import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;

import dev.orne.qdsl.wrap.ExtendedStoreClause;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Base class for wrapped QueryDSL store clauses (insert, update).
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <C> The clause type
 * @since 0.1
 */
public abstract class WrappedStoreClause<C extends WrappedStoreClause<C>>
extends WrappedClause<C>
implements ExtendedStoreClause<C> {

    /** The values to store. */
    private @NotNull StoredValues assignments;

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param transformer The expression transformer to use
     */
    protected WrappedStoreClause(
            final ExpressionTransformer transformer) {
        super(transformer);
        this.assignments = new StoredValues();
    }

    /**
     * Returns the delegate QueryDSL store clause.
     * 
     * @return The delegate QueryDSL store clause
     */
    protected abstract @NotNull StoreClause<?> getDelegate();

    /**
     * Returns the values to store.
     * 
     * @return The values to store
     */
    protected @NotNull StoredValues getAssignments() {
        return this.assignments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> C set(
            final @NotNull Path<T> path,
            final T value) {
        this.assignments.add(path, value);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> C set(
            final @NotNull Path<T> path,
            final @NotNull Expression<? extends T> expression) {
        this.assignments.add(path, expression);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> C setNull(
            final @NotNull Path<T> path) {
        this.assignments.add(path, (T) null);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    public C set(
            final @NotNull StoredValues assignments) {
        this.assignments.addAll(assignments);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    public C set(
            final @NotNull StoredValue<?>... assignments) {
        this.assignments.addAll(assignments);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return this.assignments.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long execute() {
        if (this.assignments.isEmpty()) {
            return 0;
        } else {
            final StoredValues values = getTransformer().translateStoredValues(
                    extractPreFetched(assignments));
            values.apply(getDelegate());
            return getDelegate().execute();
        }
    }
}
