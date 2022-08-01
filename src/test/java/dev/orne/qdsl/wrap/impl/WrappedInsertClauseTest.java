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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;

import dev.orne.qdsl.TestTypes;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code WrappedInsertClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedInsertClause
 */
@Tag("ut")
class WrappedInsertClauseTest
extends WrappedStoreClauseTest {

    protected @Mock InsertClause<?> delegate;

    /**
     * Unit test for {@link WrappedInsertClause#WrappedInsertClause(DeleteClause)}.
     */
    @Test
    void testConstructor() {
        final WrappedInsertClause clause = new WrappedInsertClause(delegate);
        assertSame(delegate, clause.getDelegate());
        assertSame(ExpressionTransformer.NOP, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedInsertClause(null);
        });
    }

    /**
     * Unit test for {@link WrappedInsertClause#WrappedInsertClause(DeleteClause, ExpressionTransformer)}.
     */
    @Test
    void testTransformerConstructor() {
        final WrappedInsertClause clause = new WrappedInsertClause(delegate, transformer);
        assertSame(delegate, clause.getDelegate());
        assertSame(transformer, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedInsertClause(null, transformer);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedInsertClause(null, null);
        });
        final WrappedInsertClause nullTransformerClause = new WrappedInsertClause(delegate, null);
        assertSame(delegate, nullTransformerClause.getDelegate());
        assertSame(ExpressionTransformer.NOP, nullTransformerClause.getTransformer());
    }

    @Override
    protected @NotNull WrappedInsertClause getClause() {
        return new WrappedInsertClause(delegate, transformer);
    }

    /**
     * Unit test for {@link WrappedInsertClause#columns(Path...)}.
     */
    @Test
    void testColumns() {
        final int count = RandomUtils.nextInt(2, 10);
        final Path<?>[] paths = new Path<?>[count];
        for (int i = 0; i < count; i++) {
            paths[i] = mock(Path.class);
        }
        final WrappedInsertClause clause = getClause();
        final WrappedInsertClause result = clause.columns(paths);
        assertSame(clause, result);
        assertTrue(clause.getAssignments().isEmpty());
        assertArrayEquals(paths, clause.getColumns());
        then(transformer).shouldHaveNoInteractions();
        then(delegate).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#values(Object...)}.
     */
    @Test
    void testValues() {
        final int count = RandomUtils.nextInt(2, 10);
        final Path<?>[] paths = new Path<?>[count];
        final Object[] values = new Object[count];
        for (int i = 0; i < count; i++) {
            final Class<?> type = TestTypes.randomPathType();
            paths[i] = TestTypes.pathOf(type);
            if (RandomUtils.nextBoolean()) {
                values[i] = TestTypes.expressionOf(type);
            } else {
                values[i] = TestTypes.randomValue(type);
            }
        }
        final WrappedInsertClause clause = getClause();
        clause.columns(paths);
        final WrappedInsertClause result = clause.values(values);
        assertSame(clause, result);
        assertFalse(clause.isEmpty());
        assertEquals(count, clause.getAssignments().size());
        for (int i = 0; i < count; i++) {
            assertTrue(clause.getAssignments().contains(paths[i]));
            final Expression<?> value = clause.getAssignments().get(paths[i]);
            if (values[i] instanceof Expression) {
                assertSame(values[i], value);
            } else {
                assertTrue(value instanceof Constant);
                assertSame(values[i], ((Constant<?>) value).getConstant());
            }
        }
        assertNull(clause.getColumns());
    }

    /**
     * Unit test for {@link WrappedInsertClause#values(Object...)}.
     */
    @Test
    void testValuesPriorColumns() {
        final int count = RandomUtils.nextInt(2, 10);
        final Object[] values = new Object[count];
        for (int i = 0; i < count; i++) {
            final Class<?> type = TestTypes.randomPathType();
            if (RandomUtils.nextBoolean()) {
                values[i] = TestTypes.expressionOf(type);
            } else {
                values[i] = TestTypes.randomValue(type);
            }
        }
        final WrappedInsertClause clause = getClause();
        assertThrows(IllegalStateException.class, () -> {
            clause.values(values);
        });
    }

    /**
     * Unit test for {@link WrappedInsertClause#values(Object...)}.
     */
    @Test
    void testValuesWrongSize() {
        final Path<?>[] paths = new Path<?>[RandomUtils.nextInt(2, 10)];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = TestTypes.randomPath();
        }
        final Object[] values = new Object[RandomUtils.nextInt(1, paths.length)];
        for (int i = 0; i < values.length; i++) {
            final Class<?> type = paths[i].getType();
            if (RandomUtils.nextBoolean()) {
                values[i] = TestTypes.expressionOf(type);
            } else {
                values[i] = TestTypes.randomValue(type);
            }
        }
        final WrappedInsertClause clause = getClause();
        clause.columns(paths);
        assertThrows(IllegalArgumentException.class, () -> {
            clause.values(values);
        });
    }

    /**
     * Unit test for {@link WrappedInsertClause#projectionToStoredValues(Expression)}.
     */
    @Test
    void testProjectionToStoredValuesPriorColumns() {
        final Expression<?> expr = TestTypes.randomExpression();
        final WrappedInsertClause clause = getClause();
        assertThrows(IllegalStateException.class, () -> {
            clause.projectionToStoredValues(expr);
        });
    }

    /**
     * Unit test for {@link WrappedInsertClause#projectionToStoredValues(Expression)}.
     */
    @Test
    void testProjectionToStoredValuesOneColumn() {
        final StoredValue<?> value = TestTypes.randomStoredValue();
        final Path<?> path = value.getPath();
        final Expression<?> projection = value.getValue();
        final StoredValues expected = StoredValues.with(value);
        final WrappedInsertClause clause = getClause();
        clause.columns(path);
        final StoredValues result = clause.projectionToStoredValues(projection);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link WrappedInsertClause#projectionToStoredValues(Expression)}.
     */
    @Test
    void testProjectionToStoredValuesOneColumnTuple() {
        final StoredValue<?> value = TestTypes.randomStoredValue();
        final Path<?> path = value.getPath();
        final Expression<?> projection = Projections.tuple(value.getValue());
        final StoredValues expected = StoredValues.with(value);
        final WrappedInsertClause clause = getClause();
        clause.columns(path);
        final StoredValues result = clause.projectionToStoredValues(projection);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link WrappedInsertClause#projectionToStoredValues(Expression)}.
     */
    @Test
    void testProjectionToStoredValuesOneColumnWrongType() {
        final Path<?> path = TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<?> projection = TestTypes.expressionOf(TestTypes.UnrelatedType.class);
        final WrappedInsertClause clause = getClause();
        clause.columns(path);
        assertThrows(IllegalArgumentException.class, () -> {
            clause.projectionToStoredValues(projection);
        });
    }

    /**
     * Unit test for {@link WrappedInsertClause#projectionToStoredValues(Expression)}.
     */
    @Test
    void testProjectionToStoredValuesTuple() {
        final int count = RandomUtils.nextInt(2, 10);
        final Path<?>[] paths = new Path<?>[count];
        final Expression<?>[] values = new Expression<?>[count];
        final StoredValues expected = new StoredValues(count);
        for (int i = 0; i < count; i++) {
            final StoredValue<?> value = TestTypes.randomStoredValue();
            paths[i] = value.getPath();
            values[i] = value.getValue();
            expected.add(value);
        }
        final Expression<?> projection = Projections.tuple(values);
        final WrappedInsertClause clause = getClause();
        clause.columns(paths);
        final StoredValues result = clause.projectionToStoredValues(projection);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link WrappedInsertClause#projectionToStoredValues(Expression)}.
     */
    @Test
    void testProjectionToStoredValuesTupleWrongSize() {
        final Path<?>[] paths = new Path<?>[RandomUtils.nextInt(2, 5)];
        final Expression<?>[] values = new Expression<?>[RandomUtils.nextInt(1, paths.length)];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = TestTypes.randomPath();
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = TestTypes.expressionOf(paths[i].getType());
        }
        final Expression<?> projection = Projections.tuple(values);
        final WrappedInsertClause clause = getClause();
        clause.columns(paths);
        assertThrows(IllegalArgumentException.class, () -> {
            clause.projectionToStoredValues(projection);
        });
    }

    /**
     * Unit test for {@link WrappedInsertClause#projectionToStoredValues(Expression)}.
     */
    @Test
    void testProjectionToStoredValuesTupleWrongType() {
        final Path<?>[] paths = new Path<?>[RandomUtils.nextInt(2, 5)];
        final Expression<?>[] values = new Expression<?>[RandomUtils.nextInt(1, paths.length)];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = TestTypes.randomPath();
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = TestTypes.expressionOf(TestTypes.UnrelatedType.class);
        }
        final Expression<?> projection = Projections.tuple(values);
        final WrappedInsertClause clause = getClause();
        clause.columns(paths);
        assertThrows(IllegalArgumentException.class, () -> {
            clause.projectionToStoredValues(projection);
        });
    }

    /**
     * Unit test for {@link WrappedInsertClause#transformQuery(SubQueryExpression, List)}.
     */
    @Test
    void testTransformQueryEmpty() {
        final QueryMetadata metadata = mock(QueryMetadata.class);
        final QueryMetadata cmetadata = mock(QueryMetadata.class);
        final SubQueryExpression<?> original = mock(SubQueryExpression.class);
        given(original.getMetadata()).willReturn(metadata);
        given(metadata.clone()).willReturn(cmetadata);
        final WrappedInsertClause clause = getClause();
        final List<Expression<?>> values = Collections.emptyList();
        assertThrows(IllegalArgumentException.class, () -> {
            clause.transformQuery(original, values);
        });
        then(metadata).should(atMostOnce()).clone();
        then(metadata).shouldHaveNoMoreInteractions();
        then(cmetadata).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#transformQuery(SubQueryExpression, List)}.
     */
    @Test
    void testTransformQueryOneColumn() {
        final QueryMetadata metadata = mock(QueryMetadata.class);
        final QueryMetadata cmetadata = mock(QueryMetadata.class);
        final SubQueryExpression<?> original = mock(SubQueryExpression.class);
        final Expression<?> value = TestTypes.expressionOf(TestTypes.SimpleType.class);
        given(original.getMetadata()).willReturn(metadata);
        given(metadata.clone()).willReturn(cmetadata);
        final WrappedInsertClause clause = getClause();
        final SubQueryExpression<?> result = clause.transformQuery(original, Arrays.asList(value));
        assertNotNull(result);
        assertSame(cmetadata, result.getMetadata());
        assertSame(TestTypes.SimpleType.class, result.getType());
        then(metadata).should().clone();
        then(metadata).shouldHaveNoMoreInteractions();
        then(cmetadata).should().setProjection(value);
        then(cmetadata).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#transformQuery(SubQueryExpression, List)}.
     */
    @Test
    void testTransformQueryTuple() {
        final QueryMetadata metadata = mock(QueryMetadata.class);
        final QueryMetadata cmetadata = mock(QueryMetadata.class);
        final SubQueryExpression<?> original = mock(SubQueryExpression.class);
        final Expression<?>[] values = new Expression<?>[RandomUtils.nextInt(2, 10)];
        for (int i = 0; i < values.length; i++) {
            values[i] = TestTypes.randomExpression();
        }
        given(original.getMetadata()).willReturn(metadata);
        given(metadata.clone()).willReturn(cmetadata);
        final WrappedInsertClause clause = getClause();
        final SubQueryExpression<?> result = clause.transformQuery(original, Arrays.asList(values));
        assertNotNull(result);
        assertSame(cmetadata, result.getMetadata());
        assertSame(Tuple.class, result.getType());
        then(metadata).should().clone();
        then(metadata).shouldHaveNoMoreInteractions();
        then(cmetadata).should().setProjection(Projections.tuple(values));
        then(cmetadata).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedInsertClause#select(SubQueryExpression)}.
     */
    @Test
    void testSelect() {
        final SubQueryExpression<?> subquery = mock(SubQueryExpression.class);
        final QueryMetadata metadata = mock(QueryMetadata.class);
        final Expression<?> projection = mock(Expression.class);
        final StoredValues ovalues = mock(StoredValues.class);
        final StoredValues tvalues = TestTypes.randomStoredValues();
        final Path<?>[] opaths = new Path<?>[tvalues.size()];
        for (int i = 0; i < tvalues.size(); i++) {
            opaths[i] = TestTypes.randomPath();
        }
        final List<Path<?>> paths = new ArrayList<>(tvalues.paths());
        final List<Expression<?>> values = new ArrayList<>(tvalues.size());
        for (final Path<?> path : paths) {
            values.add(tvalues.get(path));
        }
        final SubQueryExpression<?> tsubquery = mock(SubQueryExpression.class);
        final WrappedInsertClause clause = spy(getClause());
        willReturn(metadata).given(subquery).getMetadata();
        willReturn(projection).given(metadata).getProjection();
        willReturn(ovalues).given(clause).projectionToStoredValues(projection);
        willReturn(tvalues).given(transformer).translateStoredValues(ovalues);
        willReturn(tsubquery).given(clause).transformQuery(subquery, values);
        final InOrder order = inOrder(delegate);
        clause.columns(opaths);
        final WrappedInsertClause result = clause.select(subquery);
        assertSame(clause, result);
        then(clause).should().projectionToStoredValues(projection);
        then(transformer).should().translateStoredValues(ovalues);
        then(clause).should().transformQuery(subquery, values);
        then(delegate).should(order).columns(paths.toArray(new Path<?>[paths.size()]));
        then(delegate).should(order).select(tsubquery);
        assertNull(clause.getColumns());
    }

    /**
     * Unit test for {@link WrappedInsertClause#select(SubQueryExpression)}.
     */
    @Test
    void testSelectNull() {
        final WrappedInsertClause clause = getClause();
        assertThrows(NullPointerException.class, () -> {
            clause.select(null);
        });
    }
}
