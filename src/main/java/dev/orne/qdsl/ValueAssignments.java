package dev.orne.qdsl;

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
 * Container for multiple store (create, update) operations.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-11
 * @since 0.1
 */
public class ValueAssignments
implements Cloneable,
        Iterable<ValueAssignment<?>> {

    /** The assigned values indexed by paths. */
    private final Map<Path<?>, Expression<?>> assignments;

    /**
     * Creates a new instance with the default initial capacity (16).
     */
    public ValueAssignments() {
        super();
        this.assignments = new HashMap<>();
    }

    /**
     * Creates a new instance with the specified initial capacity.
     * 
     * @param initialCapacity The initial capacity
     * @throws IllegalArgumentException If the initial capacity is negative
     */
    public ValueAssignments(
            final int initialCapacity) {
        super();
        this.assignments = new HashMap<>(initialCapacity);
    }

    /**
     * Copy constructor.
     * 
     * @param copy The instance to copy
     */
    public ValueAssignments(
            final @NotNull ValueAssignments copy) {
        super();
        this.assignments = new HashMap<>(Validate.notNull(copy).assignments);
    }

    /**
     * Creates a new instance with the specified initial assignments.
     * 
     * @param assignments The initial assignments of the new instance
     * @return The new instance
     */
    public static @NotNull ValueAssignments of(
            final @NotNull ValueAssignment<?>... assignments) {
        Validate.notNull(assignments);
        Validate.noNullElements(assignments);
        final ValueAssignments result = new ValueAssignments(assignments.length);
        for (final ValueAssignment<?> assignment : assignments) {
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
    public static @NotNull ValueAssignments of(
            final @NotNull Collection<ValueAssignment<?>> assignments) {
        Validate.notNull(assignments);
        Validate.noNullElements(assignments);
        final ValueAssignments result = new ValueAssignments(assignments.size());
        for (final ValueAssignment<?> assignment : assignments) {
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
     * Returns a Set view of the paths assigned in this instance.
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
     * Returns the value to which the specified path is assigned,
     * or null if this instance contains no assignment for the path.
     * <p>
     * This map permits null values, so a return value of null does not
     * necessarily indicate that there is no assignment for the path;
     * It's also possible that this instance explicitly assigns the path to
     * null.
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
     * Returns the value to which the specified path is assigned,
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
    public <T> ValueAssignment<T> getAssignment(
            final @NotNull Path<T> path) {
        if (this.contains(path)) {
            return ValueAssignment.of(path, get(path));
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
     * @return The value assignment for the specified path,
     * with {@code defaultValue} if this map contains no assignment for the
     * path
     */
    public <T> ValueAssignment<T> getAssignmentOrDefault(
            final @NotNull Path<T> path,
            final Expression<T> defaultValue) {
        return ValueAssignment.of(path, getOrDefault(path, defaultValue));
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
           final @NotNull ValueAssignment<T> assignment) {
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
           final @NotNull ValueAssignment<T> assignment) {
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
            final @NotNull ValueAssignments assignments) {
        for (final ValueAssignment<?> assignment : assignments) {
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
            final @NotNull ValueAssignment<?>... assignments) {
        for (final ValueAssignment<?> assignment : assignments) {
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
            final @NotNull Collection<ValueAssignment<?>> assignments) {
        for (final ValueAssignment<?> assignment : assignments) {
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
        for (final ValueAssignment<?> assignment : this) {
            assignment.apply(clause);
        }
        return clause;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Iterator<ValueAssignment<?>> iterator() {
        return new ValueAssignmentIterator();
    }

    /**
     * Returns a sequential {@code Stream} with the assignments of this
     * instance as its source.
     *
     * @return A sequential {@code Stream} over the assignments in this
     * instance
     */
    public @NotNull Stream<ValueAssignment<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a parallel {@code Stream} with the assignments of this
     * instance as its source.
     *
     * @return A parallel {@code Stream} over the assignments in this
     * instance
     */
    public @NotNull Stream<ValueAssignment<?>> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssignments clone() {
        return new ValueAssignments(this);
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
        final ValueAssignments other = (ValueAssignments) obj;
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
     * stored {@code ValueAssigment}s.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-11
     * @since ValueAssignments 0.1
     */
    protected class ValueAssignmentIterator
    implements Iterator<ValueAssignment<?>> {

        /** The iterator over the assigned paths. */
        private final Iterator<Path<?>> pathsIt;

        /**
         * Creates a new instance.
         */
        public ValueAssignmentIterator() {
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
        public ValueAssignment<?> next() {
            return getAssignment(this.pathsIt.next());
        }
    }
}
