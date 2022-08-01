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
import java.util.Arrays;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.collections.CollQuery;
import com.querydsl.collections.QueryEngine;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.support.ExtendedSubQuery;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;

/**
 * Wrapped QueryDSL update clause.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @param <T> The query results type
 * @since 0.1
 */
public class WrappedCollQueryClause<T>
extends WrappedQueryClause<T, WrappedCollQueryClause<T>> {

    /** The delegate QueryDSL collection based query clause. */
    private final @NotNull CollQuery<T> delegate;
    /**
     * The metadata of the delegate QueryDSL clause.
     * For prefetched subquery creation.
     */
    private final @NotNull QueryMetadata metadata;
    /**
     * The query results type.
     * For prefetched subquery creation.
     */
    private final @NotNull Class<? extends T> type;

    /**
     * Creates a new instance with no-operation transformer.
     * 
     * @param path The path for the source
     * @param engine The query engine for query execution
     * @param values The content of the source
     */
    @SafeVarargs
    public WrappedCollQueryClause(
            final @NotNull Path<T> path,
            final @NotNull QueryEngine engine,
            final @NotNull T... values) {
        this(null, path, engine, Arrays.asList(values));
    }

    /**
     * Creates a new instance with no-operation transformer.
     * 
     * @param path The path for the source
     * @param engine The query engine for query execution
     * @param values The content of the source
     */
    public WrappedCollQueryClause(
            final @NotNull Path<T> path,
            final @NotNull QueryEngine engine,
            final @NotNull Iterable<T> values) {
        this(null, path, engine, values);
    }

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param transformer The expression transformer to use
     * @param path The path for the source
     * @param engine The query engine for query execution
     * @param values The content of the source
     */
    @SafeVarargs
    public WrappedCollQueryClause(
            final ExpressionTransformer transformer,
            final @NotNull Path<T> path,
            final @NotNull QueryEngine engine,
            final @NotNull T... values) {
        this(transformer, path, engine, Arrays.asList(values));
    }

    /**
     * Creates a new instance.
     * <p>
     * If {@code transformer} is {@code null} {@code ExpressionTransformer.NOP}
     * is used.
     * 
     * @param transformer The expression transformer to use
     * @param path The path for the source
     * @param engine The query engine for query execution
     * @param values The content of the source
     */
    public WrappedCollQueryClause(
            final ExpressionTransformer transformer,
            final @NotNull Path<T> path,
            final @NotNull QueryEngine engine,
            final @NotNull Iterable<T> values) {
        super(transformer);
        Validate.notNull(path);
        Validate.notNull(engine);
        Validate.notNull(values);
        Validate.noNullElements(values);
        this.metadata = new DefaultQueryMetadata();
        this.type = path.getType();
        this.delegate = new CollQuery<Void>(this.metadata, engine)
                .from(path, values)
                .select(path);
    }

    /**
     * Internal constructor for projection changes.
     * <p>
     * Extending classes must override this constructor and method
     * {@code changeProjection}.
     * 
     * @param transformer The expression transformer to use
     * @param metadata The metadata of the delegate QueryDSL clause
     * @param delegate The delegate QueryDSL query clause
     * @param type The type of the delegate QueryDSL clause
     * @see #changeProjection(ExpressionTransformer, QueryMetadata, CollQuery, Class)
     */
    protected WrappedCollQueryClause(
            final ExpressionTransformer transformer,
            final @NotNull QueryMetadata metadata,
            final @NotNull CollQuery<T> delegate,
            final @NotNull Class<? extends T> type) {
        super(transformer);
        this.metadata = Validate.notNull(metadata);
        this.delegate = Validate.notNull(delegate);
        this.type = Validate.notNull(type);
    }

    /**
     * Returns the delegate QueryDSL collection based query clause.
     * 
     * @return The delegate QueryDSL collection based query clause
     */
    @Override
    protected @NotNull CollQuery<T> getDelegate() {
        return this.delegate;
    }

    /**
     * Returns the query results type.
     * 
     * @return The query results type
     */
    protected Class<? extends T> getType() {
        return this.type;
    }

    /**
     * Returns the metadata of the delegate QueryDSL clause.
     * 
     * @return The metadata of the delegate QueryDSL clause
     */
    protected QueryMetadata getMetadata() {
        return this.metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> WrappedCollQueryClause<U> select(
            final @NotNull Expression<U> expr) {
        return changeProjection(
                getTransformer(),
                this.metadata,
                this.delegate.select(getTransformer().translateProjection(expr)),
                expr.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull WrappedCollQueryClause<Tuple> select(
            final @NotNull Expression<?>... exprs) {
        return changeProjection(
                getTransformer(),
                this.metadata,
                this.delegate.select(getTransformer().translateProjections(exprs)),
                Tuple.class);
    }

    /**
     * Creates a new instance after projection type change.
     * <p>
     * Extending classes must override this method and internal constructor.
     * 
     * @param <U> The new clause results type
     * @param transformer The expression transformer to use
     * @param metadata The metadata of the delegate QueryDSL clause
     * @param delegate The delegate QueryDSL query clause
     * @param type The new clause results type
     * @return The new instance
     * @see #WrappedCollQueryClause(ExpressionTransformer, QueryMetadata, CollQuery, Class)
     */
    protected <U> @NotNull WrappedCollQueryClause<U> changeProjection(
            final ExpressionTransformer transformer,
            final @NotNull QueryMetadata metadata,
            final @NotNull CollQuery<U> delegate,
            final @NotNull Class<? extends U> type) {
        return new WrappedCollQueryClause<>(
                transformer,
                metadata,
                delegate,
                type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public @NotNull ExtendedSubQuery<T> asSubQuery() {
        Validate.isAssignableFrom(Serializable.class, this.type);
        final Class<? extends Serializable> sType = (Class<? extends Serializable>) this.type;
        return (ExtendedSubQuery<T>) PreFetchedSubQueryExpression.fromUnchecked(
                this.metadata,
                sType,
                this.delegate.fetch());
    }
}
