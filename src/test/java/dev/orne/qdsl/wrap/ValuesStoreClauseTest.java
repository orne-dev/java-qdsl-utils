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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Unit tests for {@code ValuesStoreClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see StoredValues
 */
@Tag("ut")
class ValuesStoreClauseTest {

    private static final String ENTITY = "entity";
    private static final String PROPERTY_A = "propertyA";
    private static final String PROPERTY_B = "propertyB";
    private static final String PROPERTY_C = "propertyC";

    private static final PathBuilder<Object> ENTITY_PATH =
            new PathBuilder<Object>(Object.class, ENTITY);
    private static final StringPath PROPERTY_A_PATH =
            ENTITY_PATH.getString(PROPERTY_A);
    private static final StringPath PROPERTY_B_PATH =
            ENTITY_PATH.getString(PROPERTY_B);
    private static final StringPath PROPERTY_C_PATH =
            ENTITY_PATH.getString(PROPERTY_C);
    private static final String STR_A =
            "SomeValue";
    private static final String STR_B =
            "OtherValue";
    private static final Expression<String> VALUE_A =
            Expressions.constant(STR_A);
    private static final Expression<String> VALUE_B =
            Expressions.constant(STR_B);

    private static final StoredValue<String> NULL_ASSIGNMENT_A =
            StoredValue.of(PROPERTY_A_PATH, (String) null);
    private static final StoredValue<String> NULL_ASSIGNMENT_B =
            StoredValue.of(PROPERTY_B_PATH, (String) null);
    private static final StoredValue<String> VALUE_A_ASSIGNMENT_A =
            StoredValue.of(PROPERTY_A_PATH, VALUE_A);
    private static final StoredValue<String> VALUE_A_ASSIGNMENT_B =
            StoredValue.of(PROPERTY_B_PATH, VALUE_A);
    private static final StoredValue<String> VALUE_B_ASSIGNMENT_A =
            StoredValue.of(PROPERTY_A_PATH, VALUE_B);
    private static final StoredValue<String> VALUE_B_ASSIGNMENT_B =
            StoredValue.of(PROPERTY_B_PATH, VALUE_B);
    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    @Test
    void testEmptyConstructor() {
        final StoredValues result = new StoredValues();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        assertNotNull(result.toString());
    }

    @Test
    void testCopyConstructor() {
        final StoredValues initial = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        final StoredValues result = new StoredValues(initial);
        assertNotNull(result);
        assertEquals(initial, result);
        assertEquals(initial.hashCode(), result.hashCode());
    }

    @Test
    void testClone() {
        final StoredValues initial = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        final StoredValues result = initial.clone();
        assertNotNull(result);
        assertEquals(initial, result);
        assertEquals(initial.hashCode(), result.hashCode());
    }

    @Test
    void testEquals() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        assertFalse(bean.equals(null));
        assertTrue(bean.equals(bean));
        assertFalse(bean.equals(new Object()));
        assertFalse(bean.equals(StoredValues.with(
                VALUE_A_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B)));
    }

    @Test
    void testCreateEmpty() {
        final StoredValues result = StoredValues.with();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        assertNotNull(result.toString());
    }

    @Test
    void testCreateVarargs() {
        final StoredValues result = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertNull(result.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, result.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, result.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, result.getAssignment(PROPERTY_B_PATH));
        assertNotNull(result.toString());
    }

    @Test
    void testCreateCollection() {
        final StoredValues result = StoredValues.with(Arrays.asList(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertNull(result.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, result.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, result.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, result.getAssignment(PROPERTY_B_PATH));
        assertNotNull(result.toString());
    }

    @Test
    void testPaths() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        final Set<Path<?>> result = bean.paths();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(PROPERTY_A_PATH));
        assertTrue(result.contains(PROPERTY_B_PATH));
    }

    @Test
    void testGetOrDefault() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        assertNull(bean.get(PROPERTY_A_PATH));
        assertNull(bean.getOrDefault(PROPERTY_A_PATH, VALUE_B));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A, bean.getOrDefault(PROPERTY_B_PATH, VALUE_B));
        assertNull(bean.get(PROPERTY_C_PATH));
        assertEquals(VALUE_B, bean.getOrDefault(PROPERTY_C_PATH, VALUE_B));
        
    }

    @Test
    void testGetAssignment() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testGetAssignmentOrDefault() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_A, bean.getAssignmentOrDefault(PROPERTY_A_PATH, VALUE_B));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignmentOrDefault(PROPERTY_B_PATH, VALUE_B));
    }

    @Test
    void testClear() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        bean.clear();
        assertTrue(bean.isEmpty());
        assertEquals(0, bean.size());
        assertNotNull(bean.toString());
    }

    @Test
    void testAddAssignment() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<String> result = bean.add(VALUE_B_ASSIGNMENT_A);
        assertNull(result);
        assertEquals(2, bean.size());
        assertEquals(VALUE_B, bean.get(PROPERTY_A_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testReplaceAssignment() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<String> result = bean.add(VALUE_B_ASSIGNMENT_B);
        assertEquals(VALUE_A, result);
        assertEquals(1, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_B, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddPathValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<String> result = bean.add(PROPERTY_A_PATH, STR_B);
        assertNull(result);
        assertEquals(2, bean.size());
        assertEquals(VALUE_B, bean.get(PROPERTY_A_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddPathNullValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<String> result = bean.add(PROPERTY_A_PATH, (String) null);
        assertNull(result);
        assertEquals(2, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testReplacePathValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<String> result = bean.add(PROPERTY_B_PATH, STR_B);
        assertEquals(VALUE_A, result);
        assertEquals(1, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_B, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testReplacePathNullValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<String> result = bean.add(PROPERTY_B_PATH, (String) null);
        assertEquals(VALUE_A, result);
        assertEquals(1, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertNull(bean.get(PROPERTY_B_PATH));
        assertEquals(NULL_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddIfAbsentAssignment() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<? extends String> result = bean.addIfAbsent(VALUE_B_ASSIGNMENT_A);
        assertNull(result);
        assertEquals(2, bean.size());
        assertEquals(VALUE_B, bean.get(PROPERTY_A_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddIfAbsentExistingAssignment() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<? extends String> result = bean.addIfAbsent(VALUE_B_ASSIGNMENT_B);
        assertEquals(VALUE_A, result);
        assertEquals(1, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddIfAbsentPathValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<? extends String> result = bean.addIfAbsent(PROPERTY_A_PATH, STR_B);
        assertNull(result);
        assertEquals(2, bean.size());
        assertEquals(VALUE_B, bean.get(PROPERTY_A_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddIfAbsentPathNullValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<? extends String> result = bean.addIfAbsent(PROPERTY_A_PATH, (String) null);
        assertNull(result);
        assertEquals(2, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddIfAbsentExitingPathValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<? extends String> result = bean.addIfAbsent(PROPERTY_B_PATH, STR_B);
        assertEquals(VALUE_A, result);
        assertEquals(1, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddIfAbsentExitingPathNullValue() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        final Expression<? extends String> result = bean.addIfAbsent(PROPERTY_B_PATH, (String) null);
        assertEquals(VALUE_A, result);
        assertEquals(1, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertNull(bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_A, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddAllAssignments() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        bean.addAll(StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B));
        assertEquals(2, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_B, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddAllVarargs() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        bean.addAll(
                NULL_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B);
        assertEquals(2, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_B, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testAddAllCollection() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_B);
        bean.addAll(Arrays.asList(
                NULL_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B));
        assertEquals(2, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertEquals(VALUE_B, bean.get(PROPERTY_B_PATH));
        assertEquals(VALUE_B_ASSIGNMENT_B, bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testRemove() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        final Expression<String> result = bean.remove(PROPERTY_B_PATH);
        assertEquals(VALUE_A, result);
        assertEquals(1, bean.size());
        assertNull(bean.get(PROPERTY_A_PATH));
        assertEquals(NULL_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertNull(bean.get(PROPERTY_B_PATH));
        assertNull(bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testRemoveMissing() {
        final StoredValues bean = StoredValues.with(
                VALUE_A_ASSIGNMENT_A);
        final Expression<String> result = bean.remove(PROPERTY_B_PATH);
        assertNull(result);
        assertEquals(1, bean.size());
        assertEquals(VALUE_A, bean.get(PROPERTY_A_PATH));
        assertEquals(VALUE_A_ASSIGNMENT_A, bean.getAssignment(PROPERTY_A_PATH));
        assertNull(bean.get(PROPERTY_B_PATH));
        assertNull(bean.getAssignment(PROPERTY_B_PATH));
    }

    @Test
    void testApply() {
        final StoreClause<?> clause = mock(StoreClause.class);
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B);
        final StoreClause<?> result = bean.apply(clause);
        assertSame(result, clause);
        then(clause).should(times(1)).setNull(PROPERTY_A_PATH);
        then(clause).should(times(1)).set(PROPERTY_B_PATH, VALUE_B);
    }

    @Test
    void testAccept() {
        final StoredValues bean = spy(StoredValues.class);
        final StoredValuesVisitor<?, ?> visitor = mock(StoredValuesVisitor.class);
        final Object expected = new Object();
        willReturn(expected).given(visitor).visit(bean, null);
        final Object result = bean.accept(visitor, null);
        then(visitor).should().visit(bean, null);
        assertSame(expected, result);
    }

    @Test
    void testIterator() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B);
        final Iterator<StoredValue<?>> result = bean.iterator();
        assertNotNull(result);
        assertTrue(result.hasNext());
        assertEquals(NULL_ASSIGNMENT_A, result.next());
        assertTrue(result.hasNext());
        assertEquals(VALUE_B_ASSIGNMENT_B, result.next());
        assertFalse(result.hasNext());
    }

    @Test
    void testStream() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B);
        final List<StoredValue<?>> result = new ArrayList<>();
        bean.stream().forEach(a -> result.add(a));
        assertNotNull(result);
        assertTrue(result.contains(NULL_ASSIGNMENT_A));
        assertTrue(result.contains(VALUE_B_ASSIGNMENT_B));
    }

    @Test
    void testParalellStream() {
        final StoredValues bean = StoredValues.with(
                NULL_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B);
        final List<StoredValue<?>> result = new ArrayList<>();
        bean.parallelStream().forEach(a -> result.add(a));
        assertNotNull(result);
        assertTrue(result.contains(NULL_ASSIGNMENT_A));
        assertTrue(result.contains(VALUE_B_ASSIGNMENT_B));
    }
}
