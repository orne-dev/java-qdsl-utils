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
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

import dev.orne.qdsl.TestTypes;

/**
 * Unit tests for {@code WrappedUpdateClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedUpdateClause
 */
@Tag("ut")
class WrappedUpdateClauseTest
extends WrappedStoreClauseTest {

    protected @Mock UpdateClause<?> delegate;

    /**
     * Unit test for {@link WrappedUpdateClause#WrappedUpdateClause(DeleteClause)}.
     */
    @Test
    void testConstructor() {
        final WrappedUpdateClause clause = new WrappedUpdateClause(delegate);
        assertSame(delegate, clause.getDelegate());
        assertSame(ExpressionTransformer.NOP, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedUpdateClause(null);
        });
    }

    /**
     * Unit test for {@link WrappedUpdateClause#WrappedUpdateClause(DeleteClause, ExpressionTransformer)}.
     */
    @Test
    void testTransformerConstructor() {
        final WrappedUpdateClause clause = new WrappedUpdateClause(delegate, transformer);
        assertSame(delegate, clause.getDelegate());
        assertSame(transformer, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedUpdateClause(null, transformer);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedUpdateClause(null, null);
        });
        final WrappedUpdateClause nullTransformerClause = new WrappedUpdateClause(delegate, null);
        assertSame(delegate, nullTransformerClause.getDelegate());
        assertSame(ExpressionTransformer.NOP, nullTransformerClause.getTransformer());
    }

    @Override
    protected @NotNull WrappedUpdateClause getClause() {
        return new WrappedUpdateClause(delegate, transformer);
    }

    /**
     * Unit test for {@link WrappedUpdateClause#set(List, List)}.
     */
    @Test
    void testSet() {
        final int count = RandomUtils.nextInt(1, 10);
        final List<Path<?>> paths = new ArrayList<>(count);
        final List<Object> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            paths.add(TestTypes.randomPath());
            if (RandomUtils.nextBoolean()) {
                values.add(TestTypes.randomValue(paths.get(i).getType()));
            } else {
                values.add(TestTypes.expressionOf(paths.get(i).getType()));
            }
        }
        final WrappedUpdateClause clause = getClause();
        final WrappedUpdateClause result = clause.set(paths, values);
        assertSame(clause, result);
        assertEquals(count, clause.getAssignments().size());
        for (int i = 0; i < count; i++) {
            final Path<?> path = paths.get(i);
            final Object value = values.get(i);
            assertTrue(clause.getAssignments().contains(path));
            if (value instanceof Expression) {
                assertSame(value, clause.getAssignments().get(path));
            } else {
                assertEquals(Expressions.constant(value), clause.getAssignments().get(path));
            }
        }
    }

    /**
     * Unit test for {@link WrappedUpdateClause#set(List, List)}.
     */
    @Test
    void testSetNulls() {
        final int count = RandomUtils.nextInt(1, 10);
        final List<Path<?>> paths = new ArrayList<>(count);
        final List<Object> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            paths.add(TestTypes.randomPath());
            if (RandomUtils.nextBoolean()) {
                values.add(TestTypes.randomValue(paths.get(i).getType()));
            } else {
                values.add(TestTypes.expressionOf(paths.get(i).getType()));
            }
        }
        final WrappedUpdateClause clause = getClause();
        assertThrows(NullPointerException.class, () -> {
            clause.set((List<Path<?>>)null, values);
        });
        assertThrows(NullPointerException.class, () -> {
            clause.set(paths, (List<?>)null);
        });
        assertThrows(NullPointerException.class, () -> {
            clause.set((List<Path<?>>)null, (List<?>)null);
        });
        final List<Path<?>> nullPaths = new ArrayList<>(paths);
        nullPaths.set(RandomUtils.nextInt(0, count), null);
        assertThrows(IllegalArgumentException.class, () -> {
            clause.set(nullPaths, values);
        });
    }

    /**
     * Unit test for {@link WrappedUpdateClause#set(List, List)}.
     */
    @Test
    void testSetWrongSize() {
        final int pathCount = RandomUtils.nextInt(2, 5);
        final int valuesCount = RandomUtils.nextInt(1, pathCount);
        final List<Path<?>> paths = new ArrayList<>(pathCount);
        final List<Object> values = new ArrayList<>(valuesCount);
        for (int i = 0; i < pathCount; i++) {
            paths.add(TestTypes.randomPath());
        }
        for (int i = 0; i < valuesCount; i++) {
            if (RandomUtils.nextBoolean()) {
                values.add(TestTypes.randomValue(paths.get(i).getType()));
            } else {
                values.add(TestTypes.expressionOf(paths.get(i).getType()));
            }
        }
        final WrappedUpdateClause clause = getClause();
        assertThrows(IllegalArgumentException.class, () -> {
            clause.set(paths, values);
        });
    }

    /**
     * Unit test for {@link WrappedUpdateClause#set(List, List)}.
     */
    @Test
    void testSetWrongType() {
        final int count = RandomUtils.nextInt(1, 10);
        final List<Path<?>> paths = new ArrayList<>(count);
        final List<Object> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            paths.add(TestTypes.randomPath());
            if (RandomUtils.nextBoolean()) {
                values.add(mock(TestTypes.UnrelatedType.class));
            } else {
                values.add(TestTypes.expressionOf(TestTypes.UnrelatedType.class));
            }
        }
        final WrappedUpdateClause clause = getClause();
        assertThrows(IllegalArgumentException.class, () -> {
            clause.set(paths, values);
        });
    }

    /**
     * Unit test for {@link WrappedUpdateClause#where(Predicate...)}.
     */
    @Test
    void testWhere() {
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        final Predicate[] texprs = new Predicate[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
            texprs[i] = mock(Predicate.class);
            willReturn(exprs[i]).given(exprs[i]).accept(PreFetchedSubQueryExtractor.INSTANCE, null);
        }
        given(transformer.translatePredicates(exprs)).willReturn(texprs);
        final WrappedUpdateClause clause = getClause();
        final WrappedUpdateClause result = clause.where(exprs);
        assertSame(clause, result);
        then(transformer).should().translatePredicates(exprs);
        then(delegate).should().where(texprs);
    }
}
