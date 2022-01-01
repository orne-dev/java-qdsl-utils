package dev.orne.qdsl;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021 Orne Developments
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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Value assignment bean for store (create, update) operations.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @param <V> The value type
 * @since 0.1
 */
public class ValueAssignment<V> {

    /** The path of the property to assign. */
    private final @NotNull Path<V> path;
    /** The value to be assigned. */
    private final Expression<? extends V> value;

    /**
     * Creates a new instance.
     * 
     * @param path The path of the property to assign
     * @param value The value to be assigned
     */
    protected ValueAssignment(
            final @NotNull Path<V> path,
            final Expression<? extends V> value) {
        super();
        this.path = Validate.notNull(path);
        this.value = value;
    }

    /**
     * Creates a new instance for the specified path and value.
     * 
     * @param <V> The type of the path and value
     * @param path The path of the property to assign
     * @param value The value to be assigned
     * @return The created instance
     */
    public static @NotNull <V> ValueAssignment<V> of(
            final @NotNull Path<V> path,
            final V value) {
        return of(path, value == null ? null : Expressions.constant(value));
    }

    /**
     * Creates a new instance for the specified path and value expression.
     * 
     * @param <V> The type of the path and value
     * @param path The path of the property to assign
     * @param value The value to be assigned
     * @return The created instance
     */
    public static @NotNull <V> ValueAssignment<V> of(
            final @NotNull Path<V> path,
            final Expression<? extends V> value) {
        return new ValueAssignment<>(path, value);
    }

    /**
     * Creates a new typed instance for the specified target path
     * validating that the assigned value is of a compatible type.
     * 
     * @param <T> The path type
     * @param target The path of the property to assign
     * @param value The assigned value
     * @return The value to be assigned
     * @throws IllegalArgumentException If the assigned value is not of a
     * compatible type
     */
    @SuppressWarnings("unchecked")
    public static <T> ValueAssignment<T> ofUntyped(
            final @NotNull Path<T> target,
            final Expression<?> value) {
        if (value == null || target.getType().isAssignableFrom(value.getType())) {
            return of(target, (Expression<? extends T>) value);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Replaced assigned expression for path %s is not of expected type: %s",
                    target,
                    value));
        }
    }

    /**
     * Returns the path of the property to assign.
     * 
     * @return The path of the property to assign
     */
    public @NotNull Path<V> getPath() {
        return this.path;
    }

    /**
     * Returns the value to be assigned.
     * 
     * @return The value to be assigned
     */
    public Expression<? extends V> getValue() {
        return this.value;
    }

    /**
     * Accept the visitor with the given context.
     *
     * @param <R> return type
     * @param <C> context type
     * @param v The translation visitor
     * @param context The context of visit
     * @return The result of the visit
     */
    public @NotNull <R, C> R accept(
            final @NotNull ValueAssignmentVisitor<R, C> visitor,
            final C context) {
        return visitor.visit(this, context);
    }

    /**
     * Applies this value assignment to the specified storage clause (INSERT,
     * UPDATE).
     * 
     * @param <C> The type of the storage clause
     * @param clause The storage clause
     * @return The storage clause
     */
    public @NotNull <C extends StoreClause<? extends C>> C apply(
            final @NotNull C clause) {
        if (this.getValue() == null) {
            clause.setNull(this.getPath());
        } else {
            clause.set(this.getPath(), this.getValue());
        }
        return clause;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.path)
                .append(this.value)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final ValueAssignment<?> other = (ValueAssignment<?>) obj;
        return new EqualsBuilder()
                .append(this.path, other.path)
                .append(this.value, other.value)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
