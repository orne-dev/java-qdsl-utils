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

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

import dev.orne.qdsl.TranslateVisitor.Context;

/**
 * Value assignment bean for store (create, update) operations.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-08
 * @param <V> The value type
 * @since 0.1
 */
public class ValueAssigment<V> {

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
    protected ValueAssigment(
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
    public static <V> @NotNull ValueAssigment<V> of(
            final @NotNull Path<V> path,
            final V value) {
        return new ValueAssigment<V>(
                path,
                Expressions.constant(value));
    }

    /**
     * Creates a new instance for the specified path and value expression.
     * 
     * @param <V> The type of the path and value
     * @param path The path of the property to assign
     * @param value The value to be assigned
     * @return The created instance
     */
    public static <V> @NotNull ValueAssigment<V> of(
            final @NotNull Path<V> path,
            final @NotNull Expression<? extends V> value) {
        return new ValueAssigment<V>(path, value);
    }

    /**
     * Creates a new array with the specified value assignments.
     * <p>
     * To be passed to {@code Translator} and returned by
     * {@code TranslateVisitor}.
     * 
     * @param assignments The assignments of the array
     * @return The assignments array
     */
    public static @NotNull ValueAssigment<?>[] array(
            final @NotNull ValueAssigment<?>... assignments) {
        Validate.notNull(assignments);
        return assignments;
    }

    /**
     * Creates a new array with the specified value assignments.
     * <p>
     * To be passed to {@code Translator} and returned by
     * {@code TranslateVisitor}.
     * 
     * @param assignments The assignments of the array
     * @return The assignments array
     */
    public static @NotNull ValueAssigment<?>[] array(
            final Collection<ValueAssigment<?>> assignments) {
        return assignments == null ?
                new ValueAssigment<?>[0] :
                assignments.toArray(new ValueAssigment<?>[assignments.size()]);
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
    public @NotNull Expression<? extends V> getValue() {
        return this.value;
    }

    /**
     * Accept the visitor with the given context.
     *
     * @param v The translation visitor
     * @param context The context of visit
     * @return The result of the visit
     */
    public @NotNull ValueAssigment<?>[] accept(
            final @NotNull TranslateVisitor visitor,
            final Context context) {
        return visitor.visit(this, context);
    }
}
