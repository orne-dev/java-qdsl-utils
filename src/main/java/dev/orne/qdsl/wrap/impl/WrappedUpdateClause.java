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

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

import dev.orne.qdsl.wrap.ExtendedUpdateClause;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Wrapped QueryDSL update clause.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
public class WrappedUpdateClause
extends WrappedStoreClause<WrappedUpdateClause>
implements ExtendedUpdateClause<WrappedUpdateClause> {

    /** The delegate QueryDSL update clause. */
    private final @NotNull UpdateClause<?> delegate;

    /**
     * Creates a new instance with no-operation transformer.
     * 
     * @param delegate The delegate QueryDSL update clause
     */
    public WrappedUpdateClause(
            final @NotNull UpdateClause<?> delegate) {
        this(delegate, null);
    }

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param delegate The delegate QueryDSL update clause
     * @param transformer The expression transformer to use
     */
    public WrappedUpdateClause(
            final @NotNull UpdateClause<?> delegate,
            final ExpressionTransformer transformer) {
        super(transformer);
        this.delegate = Validate.notNull(delegate, "Delegate clause is required");
    }

    /**
     * Returns the delegate QueryDSL update clause.
     * 
     * @return The delegate QueryDSL update clause
     */
    @Override
    protected @NotNull UpdateClause<?> getDelegate() {
        return this.delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedUpdateClause set(
            final @NotNull List<? extends Path<?>> paths,
            final @NotNull List<?> values) {
        Validate.notNull(paths);
        Validate.noNullElements(paths);
        Validate.notNull(values);
        Validate.isTrue(paths.size() == values.size());
        final StoredValues assignments = new StoredValues();
        for (int i = 0; i < paths.size(); i++) {
            final Path<?> path = paths.get(i);
            final Object uValue = values.get(i);
            final StoredValue<?> assignment;
            if (uValue instanceof Expression) {
                assignment = StoredValue.ofUntyped(
                        path,
                        (Expression<?>) uValue);
            } else {
                assignment = StoredValue.ofUntyped(
                        path,
                        Expressions.constant(uValue));
            }
            assignments.add(assignment);
        }
        return set(assignments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedUpdateClause where(
            final Predicate... conds) {
        getDelegate().where(getTransformer().translatePredicates(
                extractPreFetched(conds)));
        return self();
    }
}
