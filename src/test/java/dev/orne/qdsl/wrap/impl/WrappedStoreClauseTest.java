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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;

import dev.orne.qdsl.TestTypes;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code WrappedStoreClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedStoreClause
 */
@Tag("ut")
class WrappedStoreClauseTest
extends WrappedClauseTest {

    @Override
    protected @NotNull WrappedStoreClause<?> getClause() {
        return new TestWrappedClause(transformer, mock(StoreClause.class));
    }

    /**
     * Unit test for {@link WrappedInsertClause#isEmpty()}.
     */
    @Test
    void testIsEmpty() {
        final WrappedStoreClause<?> clause = getClause();
        assertTrue(clause.isEmpty());
    }

    /**
     * Unit test for {@link WrappedInsertClause#set(Path, Object)}.
     */
    @Test
    void testSetValue() {
        final Path<TestTypes.SimpleType> path = TestTypes.pathOf(TestTypes.SimpleType.class);
        final TestTypes.SimpleType value = mock(TestTypes.SimpleType.class);
        final WrappedStoreClause<?> clause = getClause();
        final StoreClause<?> delegate = clause.getDelegate();
        final WrappedStoreClause<?> result = clause.set(path, value);
        assertSame(clause, result);
        assertFalse(clause.isEmpty());
        assertEquals(1, clause.getAssignments().size());
        assertTrue(clause.getAssignments().contains(path));
        final Expression<?> evalue = clause.getAssignments().get(path);
        assertTrue(evalue instanceof Constant);
        assertSame(value, ((Constant<?>) evalue).getConstant());
        assertThrows(NullPointerException.class, () -> {
            clause.set((Path<TestTypes.SimpleType>) null, value);
        });
        assertThrows(NullPointerException.class, () -> {
            clause.set((Path<TestTypes.SimpleType>) null, (TestTypes.SimpleType) null);
        });
        clause.set(path, (TestTypes.SimpleType) null);
        assertEquals(1, clause.getAssignments().size());
        assertTrue(clause.getAssignments().contains(path));
        assertNull(clause.getAssignments().get(path));
        then(delegate).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#set(Path, Expression)}.
     */
    @Test
    void testSetExpression() {
        final Path<TestTypes.SimpleType> path = TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> value = TestTypes.expressionOf(TestTypes.SimpleType.class);
        final WrappedStoreClause<?> clause = getClause();
        final StoreClause<?> delegate = clause.getDelegate();
        final WrappedStoreClause<?> result = clause.set(path, value);
        assertSame(clause, result);
        assertFalse(clause.isEmpty());
        assertEquals(1, clause.getAssignments().size());
        assertTrue(clause.getAssignments().contains(path));
        assertSame(value, clause.getAssignments().get(path));
        then(delegate).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
        assertThrows(NullPointerException.class, () -> {
            clause.set((Path<TestTypes.SimpleType>) null, value);
        });
        assertThrows(NullPointerException.class, () -> {
            clause.set((Path<TestTypes.SimpleType>) null, (Expression<TestTypes.SimpleType>) null);
        });
        clause.set(path, (Expression<TestTypes.SimpleType>) null);
        assertEquals(1, clause.getAssignments().size());
        assertTrue(clause.getAssignments().contains(path));
        assertNull(clause.getAssignments().get(path));
        then(delegate).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#setNull(Path)}.
     */
    @Test
    void testSetNull() {
        final Path<?> path = mock(Path.class);
        final WrappedStoreClause<?> clause = getClause();
        final StoreClause<?> delegate = clause.getDelegate();
        final WrappedStoreClause<?> result = clause.setNull(path);
        assertSame(clause, result);
        assertFalse(clause.isEmpty());
        assertEquals(1, clause.getAssignments().size());
        assertTrue(clause.getAssignments().contains(path));
        assertNull(clause.getAssignments().get(path));
        then(delegate).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#set(StoredValue...)}.
     */
    @Test
    void testSetStoredValue() {
        final Path<TestTypes.SimpleType> path = TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> value = TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<TestTypes.SimpleType> bean = StoredValue.of(path, value);
        final WrappedStoreClause<?> clause = getClause();
        final StoreClause<?> delegate = clause.getDelegate();
        final WrappedStoreClause<?> result = clause.set(bean);
        assertSame(clause, result);
        assertFalse(clause.isEmpty());
        assertEquals(1, clause.getAssignments().size());
        assertTrue(clause.getAssignments().contains(path));
        assertSame(value, clause.getAssignments().get(path));
        assertThrows(NullPointerException.class, () -> {
            clause.set((StoredValue<?>) null);
        });
        then(delegate).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#set(StoredValues)}.
     */
    @Test
    void testSetStoredValues() {
        final StoredValues bean = new StoredValues();
        final int count = RandomUtils.nextInt(2, 10);
        final Path<?>[] paths = new Path<?>[count];
        final Expression<?>[] values = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            paths[i] = TestTypes.pathOf(TestTypes.SimpleType.class);
            values[i] = TestTypes.expressionOf(TestTypes.SimpleType.class);
            bean.add(StoredValue.ofUntyped(paths[i], values[i]));
        }
        final WrappedStoreClause<?> clause = getClause();
        final StoreClause<?> delegate = clause.getDelegate();
        final WrappedStoreClause<?> result = clause.set(bean);
        assertSame(clause, result);
        assertFalse(clause.isEmpty());
        assertEquals(count, clause.getAssignments().size());
        for (int i = 0; i < count; i++) {
            assertTrue(clause.getAssignments().contains(paths[i]));
            assertSame(values[i], clause.getAssignments().get(paths[i]));
        }
        assertThrows(NullPointerException.class, () -> {
            clause.set((StoredValues) null);
        });
        then(delegate).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#execute()}.
     */
    @Test
    void testExecute() {
        final StoredValues evalues = mock(StoredValues.class);
        final StoredValues tvalues = mock(StoredValues.class);
        final WrappedStoreClause<?> clause = spy(getClause());
        final StoredValues rvalues = clause.getAssignments();
        clause.set(TestTypes.randomStoredValues());
        willReturn(evalues).given(clause).extractPreFetched(rvalues);
        final StoreClause<?> delegate = clause.getDelegate();
        final long expected = RandomUtils.nextLong();
        given(transformer.translateStoredValues(evalues)).willReturn(tvalues);
        given(delegate.execute()).willReturn(expected);
        final InOrder order = inOrder(clause, transformer, delegate, tvalues);
        final long result = clause.execute();
        assertEquals(expected, result);
        then(clause).should(order).extractPreFetched(rvalues);
        then(transformer).should(order).translateStoredValues(evalues);
        then(transformer).shouldHaveNoMoreInteractions();
        then(tvalues).should(order).apply(delegate);
        then(tvalues).shouldHaveNoMoreInteractions();
        then(delegate).should(order).execute();
        then(delegate).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#execute()}.
     */
    @Test
    void testExecuteEmpty() {
        final WrappedStoreClause<?> clause = getClause();
        final StoreClause<?> delegate = clause.getDelegate();
        final long result = clause.execute();
        assertEquals(0, result);
        then(delegate).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    private class TestWrappedClause
    extends WrappedStoreClause<TestWrappedClause> {
        private final @NotNull StoreClause<?> delegate;
        public TestWrappedClause(
                ExpressionTransformer transformer,
                StoreClause<?> delegate) {
            super(transformer);
            this.delegate = delegate;
        }
        @Override
        protected @NotNull StoreClause<?> getDelegate() {
            return this.delegate;
        }
    }
}
