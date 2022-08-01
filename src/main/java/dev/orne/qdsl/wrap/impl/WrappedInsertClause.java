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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.SubQueryExpressionImpl;
import com.querydsl.core.types.dsl.Expressions;

import dev.orne.qdsl.wrap.ExtendedInsertClause;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Wrapped QueryDSL insert clause.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
public class WrappedInsertClause
extends WrappedStoreClause<WrappedInsertClause>
implements ExtendedInsertClause<WrappedInsertClause> {

    /** The delegate QueryDSL insert clause. */
    private final @NotNull InsertClause<?> delegate;
    /** The columns to be populated. */
    private Path<?>[] columns;

    /**
     * Creates a new instance with no-operation transformer.
     * 
     * @param delegate The delegate QueryDSL insert clause
     */
    public WrappedInsertClause(
            final @NotNull InsertClause<?> delegate) {
        this(delegate, null);
    }

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param delegate The delegate QueryDSL insert clause
     * @param transformer The expression transformer to use
     */
    public WrappedInsertClause(
            final @NotNull InsertClause<?> delegate,
            final ExpressionTransformer transformer) {
        super(transformer);
        this.delegate = Validate.notNull(delegate, "Delegate clause is required");
    }

    /**
     * Returns the delegate QueryDSL insert clause.
     * 
     * @return The delegate QueryDSL insert clause
     */
    @Override
    protected @NotNull InsertClause<?> getDelegate() {
        return this.delegate;
    }

    /**
     * Returns the columns to be populated.
     * 
     * @return The columns to be populated
     */
    protected Path<?>[] getColumns() {
        return this.columns == null ? null : Arrays.copyOf(this.columns, this.columns.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedInsertClause columns(
            final @NotNull Path<?>... columns) {
        this.columns = Arrays.copyOf(Validate.notNull(columns), columns.length);
        Validate.noNullElements(columns);
        return self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedInsertClause values(
            final @NotNull Object... values) {
        Validate.validState(columns != null);
        Validate.notNull(values);
        Validate.isTrue(columns.length == values.length);
        final StoredValues assignments = new StoredValues();
        for (int i = 0; i < columns.length; i++) {
            final Path<?> path = columns[i];
            final Object uValue = values[i];
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
        this.columns = null;
        return set(assignments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedInsertClause select(
            final @NotNull SubQueryExpression<?> subQuery) {
        Validate.notNull(subQuery);
        final StoredValues assignments = projectionToStoredValues(
                subQuery.getMetadata().getProjection());
        final StoredValues translated = getTransformer().translateStoredValues(assignments);
        final List<Path<?>> paths = new ArrayList<>(translated.paths());
        final List<Expression<?>> values = new ArrayList<>(paths.size());
        for (final Path<?> path : paths) {
            values.add(translated.get(path));
        }
        getDelegate().columns(paths.toArray(new Path<?>[paths.size()]));
        getDelegate().select(transformQuery(subQuery, values));
        this.columns = null;
        return self();
    }

    /**
     * Converts the query projection into a {@code StoredValues} instance.
     * <p>
     * If a single column is declared uses the projection expression as is.
     * Otherwise expects a {@code FactoryExpression} of type {@code Tuple}
     * verifies that the number of expression of the tuple matches the number
     * of columns and that their types are assignable.
     * 
     * @param projection The query projection
     * @return The {@code StoredValues} instance
     */
    protected @NotNull StoredValues projectionToStoredValues(
            final @NotNull Expression<?> projection) {
        Validate.validState(this.columns != null);
        final StoredValues result = new StoredValues();
        if (this.columns.length == 1) {
            if (projection instanceof FactoryExpression &&
                    Tuple.class.isAssignableFrom(projection.getType()) &&
                    ((FactoryExpression<?>) projection).getArgs().size() == 1) {
                result.add(StoredValue.ofUntyped(columns[0], ((FactoryExpression<?>) projection).getArgs().get(0)));
            } else {
                result.add(StoredValue.ofUntyped(columns[0], projection));
            }
        } else {
            Validate.isInstanceOf(FactoryExpression.class, projection);
            Validate.isAssignableFrom(Tuple.class, projection.getType());
            final List<Expression<?>> values = ((FactoryExpression<?>) projection).getArgs();
            Validate.isTrue(this.columns.length == values.size());
            for (int i = 0; i < this.columns.length; i++) {
                result.add(StoredValue.ofUntyped(this.columns[i], values.get(i)));
            }
        }
        return result;
    }

    /**
     * Creates a new subquery based on the original query with a Tuple with
     * the specified values as its projection.
     * 
     * @param subquery The original subquery
     * @param values The projection tuple's values
     * @return The new subquery
     */
    protected @NotNull SubQueryExpression<?> transformQuery(
            final @NotNull SubQueryExpression<?> subquery,
            final @NotNull List<Expression<?>> values) {
        Validate.isTrue(!values.isEmpty());
        final QueryMetadata tmetadata = subquery.getMetadata().clone();
        final SubQueryExpression<?> result;
        if (values.size() == 1) {
            tmetadata.setProjection(values.get(0));
            result = new SubQueryExpressionImpl<>(values.get(0).getType(), tmetadata);
        } else {
            tmetadata.setProjection(Projections.tuple(values));
            result = new SubQueryExpressionImpl<>(Tuple.class, tmetadata);
        }
        return result;
    }
}
