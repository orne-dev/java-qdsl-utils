package dev.orne.qdsl.wrap;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
 * Container for multiple value assignments for store (create, update)
 * operations.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 */
public class StoredValues
implements Cloneable,
        Iterable<StoredValue<?>> {

    /** The assigned values indexed by paths. */
    private final Map<Path<?>, Expression<?>> assignments;

    /**
     * Creates a new instance with the default initial capacity (16).
     */
    public StoredValues() {
        super();
        this.assignments = new HashMap<>();
    }

    /**
     * Creates a new instance with the specified initial capacity.
     * 
     * @param initialCapacity The initial capacity
     * @throws IllegalArgumentException If the initial capacity is negative
     */
    public StoredValues(
            final int initialCapacity) {
        super();
        this.assignments = new HashMap<>(initialCapacity);
    }

    /**
     * Copy constructor.
     * 
     * @param copy The instance to copy
     */
    public StoredValues(
            final @NotNull StoredValues copy) {
        super();
        this.assignments = new HashMap<>(Validate.notNull(copy).assignments);
    }

    /**
     * Creates a new instance with the specified initial assignments.
     * 
     * @param assignments The initial assignments of the new instance
     * @return The new instance
     */
    public static @NotNull StoredValues with(
            final @NotNull StoredValue<?>... assignments) {
        Validate.notNull(assignments);
        Validate.noNullElements(assignments);
        final StoredValues result = new StoredValues(assignments.length);
        for (final StoredValue<?> assignment : assignments) {
            result.add(assignment);
        }
        return result;
    }

    /**
     * Creates a new instance with the specified initial assignments.
     * 
     * @param assignments The initial assignments of the new instance
     * @return The new instance
     */
    public static @NotNull StoredValues with(
            final @NotNull Collection<StoredValue<?>> assignments) {
        Validate.notNull(assignments);
        Validate.noNullElements(assignments);
        final StoredValues result = new StoredValues(assignments.size());
        for (final StoredValue<?> assignment : assignments) {
            result.add(assignment);
        }
        return result;
    }

    /**
     * Returns {@code true} if this instance contains no assignments.
     * 
     * @return {@code true} if this instance contains no assignments
     */
    public boolean isEmpty() {
        return this.assignments.isEmpty();
    }

    /**
     * Returns the number of assignments in this instance.
     * If contains more than Integer.MAX_VALUE elements,
     * returns Integer.MAX_VALUE.
     * 
     * @return The number of assignments in this instance
     */
    public int size() {
        return this.assignments.size();
    }

    /**
     * Returns {@code true} if this instance contains an assignment for the
     * specified path.
     * 
     * @param path Path whose presence in this instance is to be tested
     * @return {@code true} if this instance contains an assignment for the
     * specified path
     */
    public boolean contains(
            final @NotNull Path<?> path) {
        return this.assignments.containsKey(path);
    }

    /**
     * Returns a Set view of the paths to be updated by this instance.
     * The set is backed by this instance, so changes to this instance are
     * reflected in the set, and vice-versa.
     * If this instance is modified while an iteration over the set is in
     * progress (except through the iterator's own remove operation), the
     * results of the iteration are undefined.
     * The set supports element removal, which removes the corresponding
     * assignment from the map, via the Iterator.remove, Set.remove, removeAll,
     * retainAll, and clear operations.
     * It does not support the add or addAll operations.
     * 
     * @return A set view of the paths assigned by this instance
     */
    public @NotNull Set<Path<?>> paths() {
        return this.assignments.keySet();
    }

    /**
     * Returns the value to which the specified path will be updated,
     * or null if this instance contains no value for the path.
     * <p>
     * This map permits null values, so a return value of null does not
     * necessarily indicate that the path will not be updated;
     * It's also possible that this instance explicitly assigns {@code null}
     * to the path.
     * The {@code contains} operation may be used to distinguish these two
     * cases.
     * 
     * @param <T> The type of the path
     * @param path The path whose assigned value is to be returned
     * @return The value to which the specified path is assigned,
     * or {@code null} if this instance contains no assignment for the path
     */
    public <T> Expression<T> get(
            final @NotNull Path<T> path) {
        @SuppressWarnings("unchecked")
        final Expression<T> result = (Expression<T>) this.assignments.get(path);
        return result;
    }

    /**
     * Returns the value to which the specified path is will be updated,
     * or {@code defaultValue} if this instance contains no assignment for the
     * path.
     * 
     * @param <T> The type of the path
     * @param path The path whose assigned value is to be returned
     * @param defaultValue The default mapping of the path
     * @return The value to which the specified path is assigned,
     * or {@code defaultValue} if this map contains no assignment for the path
     */
    public <T> Expression<T> getOrDefault(
            final @NotNull Path<T> path,
            final Expression<T> defaultValue) {
        @SuppressWarnings("unchecked")
        final Expression<T> result = (Expression<T>) this.assignments.getOrDefault(path, defaultValue);
        return result;
    }

    /**
     * Returns the value assignment for the specified path,
     * or null if this instance contains no assignment for the path.
     * 
     * @param <T> The type of the path
     * @param path The path whose value assignment is to be returned
     * @return The value assignment for the specified path,
     * or null if this instance contains no assignment for the path
     */
    public <T> StoredValue<T> getAssignment(
            final @NotNull Path<T> path) {
        if (this.contains(path)) {
            return StoredValue.of(path, get(path));
        } else {
            return null;
        }
    }

    /**
     * Returns the value assignment for the specified path,
     * or {@code defaultValue} if this instance contains no assignment for the
     * path.
     * 
     * @param <T> The type of the path
     * @param path The path whose value assignment is to be returned
     * @param defaultValue The default value to return if this instance
     * contains no assignment for the path
     * @return The value assignment for the specified path,
     * with {@code defaultValue} if this map contains no assignment for the
     * path
     */
    public <T> StoredValue<T> getAssignmentOrDefault(
            final @NotNull Path<T> path,
            final Expression<T> defaultValue) {
        return StoredValue.of(path, getOrDefault(path, defaultValue));
    }

    /**
     * Removes all of the assignments from this instance.
     * The instance will be empty after this call returns.
     */
    public void clear() {
        this.assignments.clear();
    }

    /**
     * Adds the specified assignment to this instance.
     * If this instance previously contained an assignment for the path,
     * the old value is replaced by the specified value.
     * 
     * @param <T> The type of the path
     * @param assignment The assignment to add to this instance
     * @return The previous value assigned to the path,
     * or {@code null} if there was no assignment for the path
     */
    public <T> Expression<T> add(
           final @NotNull StoredValue<T> assignment) {
        Validate.notNull(assignment);
        return add(assignment.getPath(), assignment.getValue());
    }

    /**
     * Associates the specified value with the specified path in this instance.
     * If this instance previously contained an assignment for the path,
     * the old value is replaced by the specified value.
     * 
     * @param <T> The type of the path
     * @param path The path to which the specified value is to be assigned
     * @param value The value to be assigned to the specified path
     * @return The previous value assigned to the path,
     * or {@code null} if there was no assignment for the path
     */
    public <T> Expression<T> add(
           final @NotNull Path<T> path,
           final T value) {
        return add(path, value == null ? null : Expressions.constant(value));
    }

    /**
     * Associates the specified value with the specified path in this instance.
     * If this instance previously contained an assignment for the path,
     * the old value is replaced by the specified value.
     * 
     * @param <T> The type of the path
     * @param path The path to which the specified value is to be assigned
     * @param value The value to be assigned to the specified path
     * @return The previous value assigned to the path,
     * or {@code null} if there was no assignment for the path
     */
    public <T> Expression<T> add(
           final @NotNull Path<T> path,
           final Expression<? extends T> value) {
        Validate.notNull(path);
        @SuppressWarnings("unchecked")
        final Expression<T> oldValue = (Expression<T>) this.assignments.put(path, value);
        return oldValue;
    }

    /**
     * If the path of the given assignment is not already assigned with a value
     * assigns it with the value of the given assignment and returns null,
     * else returns the current assigned value.
     * 
     * @param <T> The type of the path
     * @param assignment The assignment to add to this instance
     * @return The previous value assigned to the path,
     * or {@code null} if there was no assignment for the path
     */
    public <T> Expression<T> addIfAbsent(
           final @NotNull StoredValue<T> assignment) {
        Validate.notNull(assignment);
        return addIfAbsent(assignment.getPath(), assignment.getValue());
    }

    /**
     * If the specified path is not already assigned with a value assigns it
     * with the given value and returns null,
     * else returns the current assigned value.
     * 
     * @param <T> The type of the path
     * @param path The path to which the specified value is to be assigned
     * @param value The value to be assigned to the specified path
     * @return The previous value assigned to the path,
     * or {@code null} if there was no assignment for the path
     */
    public <T> Expression<T> addIfAbsent(
           final @NotNull Path<T> path,
           final T value) {
        return addIfAbsent(path, value == null ? null : Expressions.constant(value));
    }

    /**
     * If the specified path is not already assigned with a value assigns it
     * with the given value and returns null,
     * else returns the current assigned value.
     * 
     * @param <T> The type of the path
     * @param path The path to which the specified value is to be assigned
     * @param value The value to be assigned to the specified path
     * @return The previous value assigned to the path,
     * or {@code null} if there was no assignment for the path
     */
    public <T> Expression<T> addIfAbsent(
           final @NotNull Path<T> path,
           final Expression<? extends T> value) {
        Validate.notNull(path);
        final Expression<T> oldValue;
        if (contains(path)) {
            oldValue = get(path);
        } else {
            oldValue = add(path, value);
        }
        return oldValue;
    }

    /**
     * Copies all of the assignments from the specified instance to this instance.
     * The effect of this call is equivalent to that of calling {@code add(assignment)}
     * on this map once for each assignment in the specified instance.
     * The behavior of this operation is undefined if the specified instance is
     * modified while the operation is in progress.
     * 
     * @param assignments The instance whose assignments copy to this instance
     */
    public void addAll(
            final @NotNull StoredValues assignments) {
        for (final StoredValue<?> assignment : assignments) {
            add(assignment);
        }
    }

    /**
     * Copies all of the assignments from the specified instance to this instance.
     * The effect of this call is equivalent to that of calling {@code add(assignment)}
     * on this map once for each assignment in the specified instance.
     * The behavior of this operation is undefined if the specified instance is
     * modified while the operation is in progress.
     * 
     * @param assignments The instance whose assignments copy to this instance
     */
    public void addAll(
            final @NotNull StoredValue<?>... assignments) {
        for (final StoredValue<?> assignment : assignments) {
            add(assignment);
        }
    }

    /**
     * Copies all of the assignments from the specified instance to this instance.
     * The effect of this call is equivalent to that of calling {@code add(assignment)}
     * on this map once for each assignment in the specified instance.
     * The behavior of this operation is undefined if the specified instance is
     * modified while the operation is in progress.
     * 
     * @param assignments The instance whose assignments copy to this instance
     */
    public void addAll(
            final @NotNull Collection<StoredValue<?>> assignments) {
        for (final StoredValue<?> assignment : assignments) {
            add(assignment);
        }
    }

    /**
     * 
     * @param <T> The type of the path
     * @param path The path whose assignment remove
     * @return The previous value assigned to the path,
     * or {@code null} if there was no assignment for the path
     */
    public <T> Expression<T> remove(
           final @NotNull Path<T> path) {
        Validate.notNull(path);
        @SuppressWarnings("unchecked")
        final Expression<T> oldValue = (Expression<T>) this.assignments.remove(path);
        return oldValue;
    }

    /**
     * Applies the value assignments of this instance to the specified storage
     * clause (INSERT, UPDATE).
     * 
     * @param <C> The type of the storage clause
     * @param clause The storage clause
     * @return The storage clause
     */
    public @NotNull <C extends StoreClause<? extends C>> C apply(
            final @NotNull C clause) {
        for (final StoredValue<?> assignment : this) {
            assignment.apply(clause);
        }
        return clause;
    }

    /**
     * Accept the visitor with the given context.
     *
     * @param <R> return type
     * @param <C> context type
     * @param visitor The translation visitor
     * @param context The context of visit
     * @return The result of the visit
     */
    public <R, C> R accept(
            final @NotNull StoredValuesVisitor<R, C> visitor,
            final C context) {
        return visitor.visit(this, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Iterator<StoredValue<?>> iterator() {
        return new ValueStoreClauseIterator();
    }

    /**
     * Returns a sequential {@code Stream} with the assignments of this
     * instance as its source.
     *
     * @return A sequential {@code Stream} over the assignments in this
     * instance
     */
    public @NotNull Stream<StoredValue<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a parallel {@code Stream} with the assignments of this
     * instance as its source.
     *
     * @return A parallel {@code Stream} over the assignments in this
     * instance
     */
    public @NotNull Stream<StoredValue<?>> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull StoredValues clone() {
        return new StoredValues(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.assignments)
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
        final StoredValues other = (StoredValues) obj;
        return new EqualsBuilder()
                .append(this.assignments, other.assignments)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * Internal implementation of {@code Iterator} that returns
     * stored {@code ValueStoreClause}s.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-11
     * @since ValuesStoreClause 0.1
     */
    protected class ValueStoreClauseIterator
    implements Iterator<StoredValue<?>> {

        /** The iterator over the assigned paths. */
        private final Iterator<Path<?>> pathsIt;

        /**
         * Creates a new instance.
         */
        public ValueStoreClauseIterator() {
            this.pathsIt = assignments.keySet().iterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return this.pathsIt.hasNext();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StoredValue<?> next() {
            return getAssignment(this.pathsIt.next());
        }
    }
}
