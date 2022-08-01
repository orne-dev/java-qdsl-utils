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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.ExtendedSubQuery;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Implementation of {@code ExtendedSubQuery} based on prefetched
 * query results.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <T> The subquery result type
 * @since 0.1
 */
public class PreFetchedSubQueryExpression<T extends Serializable>
implements ExtendedSubQuery<T> {

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The original query metadata. */
    private final @NotNull QueryMetadata metadata;
    /** The original query return type. */
    private final @NotNull Class<? extends T> type;
    /** The result values. */
    private final @NotNull List<T> values;

    /**
     * Creates a new instance.
     * 
     * @param metadata The original query metadata
     * @param type The original query return type
     * @param values The result values
     */
    public PreFetchedSubQueryExpression(
            final @NotNull QueryMetadata metadata,
            final @NotNull Class<? extends T> type,
            final @NotNull List<T> values) {
        super();
        this.metadata = Validate.notNull(metadata);
        this.type = Validate.notNull(type);
        this.values = Collections.unmodifiableList(Validate.notNull(values));
    }

    /**
     * Creates a new instance from the specified query metadata and result type
     * that will return the passed unchecked values.
     * <p>
     * The type of the values is verified before creating the subquery.
     * 
     * @param <T> The subquery result type
     * @param metadata The original query metadata
     * @param type The original query return type
     * @param values The result values
     * @return The created prefetched subquery
     */
    public static <T extends Serializable> @NotNull PreFetchedSubQueryExpression<T> fromUnchecked(
            final @NotNull QueryMetadata metadata,
            final @NotNull Class<T> type,
            final @NotNull List<?> values) {
        Validate.notNull(metadata);
        Validate.notNull(type);
        Validate.notNull(values);
        final List<T> tValues = new ArrayList<>();
        for (final Object value : values) {
            tValues.add(type.cast(value));
        }
        return new PreFetchedSubQueryExpression<>(metadata, type, tValues);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the original query metadata.
     */
    @Override
    public @NotNull QueryMetadata getMetadata() {
        return this.metadata;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the original query return type.
     */
    @Override
    public @NotNull Class<? extends T> getType() {
        return this.type;
    }

    /**
     * Returns the pre-fetched result values.
     * 
     * @return The pre-fetched result values
     */
    public @NotNull List<T> getValues() {
        return this.values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression eq(
            final Expression<? extends T> expr) {
        return Expressions.predicate(
                Ops.EQ,
                asSingleValue(),
                expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression eq(
            final T constant) {
        return Expressions.predicate(
                Ops.EQ,
                asSingleValue(),
                Expressions.constant(constant));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression ne(
            final Expression<? extends T> expr) {
        return Expressions.predicate(
                Ops.NE,
                asSingleValue(),
                expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression ne(
            final T constant) {
        return Expressions.predicate(
                Ops.NE,
                asSingleValue(),
                Expressions.constant(constant));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression contains(
            final Expression<? extends T> right) {
        return Expressions.predicate(
                Ops.IN,
                right,
                asValues());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression contains(
            final T constant) {
        return Expressions.predicate(
                Ops.IN,
                Expressions.constant(constant),
                asValues());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression exists() {
        return Expressions.predicate(
                Ops.COL_IS_EMPTY,
                asValues());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression notExists() {
        return Expressions.predicate(
                Ops.NOT,
                Expressions.predicate(
                    Ops.COL_IS_EMPTY,
                    asValues()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression lt(
            final Expression<? extends T> expr) {
        return Expressions.predicate(
                Ops.LT,
                asSingleValue(),
                expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression lt(
            final T constant) {
        return Expressions.predicate(
                Ops.LT,
                asSingleValue(),
                Expressions.constant(constant));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression gt(
            final Expression<? extends T> expr) {
        return Expressions.predicate(
                Ops.GT,
                asSingleValue(),
                expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression gt(
            final T constant) {
        return Expressions.predicate(
                Ops.GT,
                asSingleValue(),
                Expressions.constant(constant));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression loe(
            final Expression<? extends T> expr) {
        return Expressions.predicate(
                Ops.LOE,
                asSingleValue(),
                expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression loe(
            final T constant) {
        return Expressions.predicate(
                Ops.LOE,
                asSingleValue(),
                Expressions.constant(constant));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression goe(
            final Expression<? extends T> expr) {
        return Expressions.predicate(
                Ops.GOE,
                asSingleValue(),
                expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression goe(
            final T constant) {
        return Expressions.predicate(
                Ops.GOE,
                asSingleValue(),
                Expressions.constant(constant));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanOperation isNull() {
        return Expressions.predicate(
                Ops.IS_NULL,
                asSingleValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanOperation isNotNull() {
        return Expressions.predicate(
                Ops.IS_NOT_NULL,
                asSingleValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression in(
            final Collection<? extends T> right) {
        if (right.size() == 1) {
            return Expressions.booleanOperation(
                    Ops.IN,
                    Expressions.constant(right.iterator().next()),
                    asValues());
        } else {
            return Expressions.booleanOperation(
                    Ops.IN,
                    asSingleValue(),
                    Expressions.constant(right));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked") 
    public BooleanExpression in(
            final T... right) {
        return this.in(Arrays.asList(right));
    }

    /**
     * Returns the subquery result as a single value.
     * <p>
     * If this subquery has multiple result values the first result value is
     * returned.
     * 
     * @return The subquery unique result
     */
    protected @NotNull Expression<T> asSingleValue() {
        if (values.isEmpty()) {
            return Expressions.nullExpression();
        } else {
            return Expressions.constant(values.get(0));
        }
    }

    /**
     * Returns the subquery result.
     * 
     * @return The subquery result
     */
    protected @NotNull Expression<List<T>> asValues() {
        return Expressions.constant(this.values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, C> R accept(
            final @NotNull Visitor<R, C> v,
            final C context) {
        return v.visit(this, context);
    }
}
