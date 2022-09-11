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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.collections.CollDeleteClause;
import com.querydsl.collections.CollQuery;
import com.querydsl.collections.CollUpdateClause;
import com.querydsl.collections.DefaultQueryEngine;
import com.querydsl.collections.QueryEngine;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;

import dev.orne.qdsl.wrap.EntityPathNotSupportedException;
import dev.orne.qdsl.wrap.ExtendedQueryClause;

/**
 * Unit tests for {@code AbstractWrappedCollectionClauseProvider}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 * @see AbstractWrappedCollClauseProvider
 */
@Tag("ut")
class AbstractWrappedCollClauseProviderTest {

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#AbstractWrappedCollectionClauseProvider(Class...)}.
     */
    @Test
    void testVarargsConstructor() {
        assertThrows(NullPointerException.class, () -> {
            new TestProvider((Class<? extends EntityPath<?>>[]) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QEntity.class, null, QEntity2.class);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider();
        });
        AbstractWrappedCollClauseProvider result = new TestProvider(QEntity.class);
        assertNotNull(result.getSupportedEntityTypes());
        assertFalse(result.getSupportedEntityTypes().isEmpty());
        assertEquals(1, result.getSupportedEntityTypes().size());
        assertTrue(result.getSupportedEntityTypes().contains(QEntity.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
        result = new TestProvider(QEntity.class, QEntity2.class);
        assertFalse(result.getSupportedEntityTypes().isEmpty());
        assertEquals(2, result.getSupportedEntityTypes().size());
        assertTrue(result.getSupportedEntityTypes().contains(QEntity.class));
        assertTrue(result.getSupportedEntityTypes().contains(QEntity2.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#AbstractWrappedCollectionClauseProvider(Set)}.
     */
    @Test
    void testSetConstructor() {
        assertThrows(NullPointerException.class, () -> {
            new TestProvider((Set<Class<? extends EntityPath<?>>>) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(new HashSet<>(Arrays.asList(QEntity.class, null, QEntity2.class)));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(Collections.emptySet());
        });
        AbstractWrappedCollClauseProvider result = new TestProvider(
                new HashSet<>(Arrays.asList(QEntity.class)));
        assertNotNull(result.getSupportedEntityTypes());
        assertFalse(result.getSupportedEntityTypes().isEmpty());
        assertEquals(1, result.getSupportedEntityTypes().size());
        assertTrue(result.getSupportedEntityTypes().contains(QEntity.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
        result = new TestProvider(
                new HashSet<>(Arrays.asList(QEntity.class, QEntity2.class)));
        assertFalse(result.getSupportedEntityTypes().isEmpty());
        assertEquals(2, result.getSupportedEntityTypes().size());
        assertTrue(result.getSupportedEntityTypes().contains(QEntity.class));
        assertTrue(result.getSupportedEntityTypes().contains(QEntity2.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#getQueryEngine()}.
     */
    @Test
    void testGetQueryEngine() {
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QueryEngine result = provider.getQueryEngine();
        assertSame(DefaultQueryEngine.getDefault(), result);
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#query(EntityPath)}.
     */
    @Test
    void testQuery() {
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QEntity.class));
        assertThrows(NullPointerException.class, () -> {
            provider.query(null);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.query(new QEntity2("unsupported"));
        });
        final ExpressionTransformer transformer = mock(ExpressionTransformer.class);
        final QEntity alias = new QEntity("alias");
        final QueryEngine engine = mock(QueryEngine.class);
        final Collection<BeanType> values = spy(new ArrayList<>());
        willReturn(engine).given(provider).getQueryEngine();
        willReturn(values).given(provider).getValues(alias);
        willReturn(transformer).given(provider).createTransformerForAlias(alias);
        final ExtendedQueryClause<?, ?> result = provider.query(alias);
        final WrappedCollQueryClause<?> wresult = assertInstanceOf(WrappedCollQueryClause.class, result);
        final CollQuery<?> delegated = assertInstanceOf(CollQuery.class, wresult.getDelegate());
        assertSame(transformer, wresult.getTransformer());
        verifyQuery(delegated, engine, alias, values);
        then(provider).should().getQueryEngine();
        then(provider).should().getValues(alias);
        then(provider).should().createTransformerForAlias(alias);
        then(engine).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
        then(values).should().iterator();
        then(values).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#createDelegatedInsertClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedInsertClause() {
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        assertThrows(UnsupportedOperationException.class, () -> {
            provider.createDelegatedInsertClause(alias);
        });
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#createDelegatedUpdateClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedUpdateClause() throws IllegalAccessException {
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QueryEngine engine = mock(QueryEngine.class);
        @SuppressWarnings("unchecked")
        final Collection<BeanType> values = mock(Collection.class);
        willReturn(engine).given(provider).getQueryEngine();
        willReturn(values).given(provider).getValues(alias);
        final UpdateClause<?> result = provider.createDelegatedUpdateClause(alias);
        final CollUpdateClause<?> clause = assertInstanceOf(CollUpdateClause.class, result);
        assertTrue(readField(clause, "paths", Map.class).isEmpty());
        final CollQuery<?> query = readField(clause, "query", CollQuery.class);
        verifyQuery(query, engine, alias, values);
        then(provider).should().getQueryEngine();
        then(provider).should().getValues(alias);
        then(engine).shouldHaveNoInteractions();
        then(values).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#createDelegatedDeleteClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedDeleteClause() {
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QueryEngine engine = mock(QueryEngine.class);
        @SuppressWarnings("unchecked")
        final Collection<BeanType> values = mock(Collection.class);
        willReturn(engine).given(provider).getQueryEngine();
        willReturn(values).given(provider).getValues(alias);
        final DeleteClause<?> result = provider.createDelegatedDeleteClause(alias);
        final CollDeleteClause<?> clause = assertInstanceOf(CollDeleteClause.class, result);
        assertSame(values, readField(clause, "col"));
        final CollQuery<?> query = readField(clause, "query", CollQuery.class);
        verifyQuery(query, engine, alias, values);
        then(provider).should().getQueryEngine();
        then(provider).should().getValues(alias);
        then(engine).shouldHaveNoInteractions();
        then(values).shouldHaveNoInteractions();
    }

    private Object readField(
            @NotNull Object obj,
            @NotNull String field) {
        return readField(obj, field, Object.class);
    }

    private <T> T readField(
            @NotNull Object obj,
            @NotNull String field,
            @NotNull Class<T> type) {
        try {
            return type.cast(FieldUtils.readField(obj, field, true));
        } catch (IllegalAccessException e) {
            final AssertionError err = new AssertionError("Error accesing field");
            err.initCause(e);
            throw err;
        }
    }

    private void verifyQuery(
            final @NotNull CollQuery<?> query,
            final @NotNull QueryEngine engine,
            final @NotNull EntityPath<?> entity,
            final @NotNull Collection<?> values) {
        assertSame(engine, readField(query, "queryEngine"));
        final Map<?, ?> iterables = readField(query, "iterables", Map.class);
        assertEquals(1, iterables.size());
        assertTrue(iterables.containsKey(entity));
        assertSame(values, iterables.get(entity));
        final QueryMetadata metadata;
        try {
            metadata = (QueryMetadata) MethodUtils.invokeMethod(query, true, "getMetadata");
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            final AssertionError err = new AssertionError("Error getting query metadata");
            err.initCause(e);
            throw err;
        }
        assertNotNull(metadata);
        assertSame(entity, metadata.getProjection());
        assertEquals(1, metadata.getJoins().size());
        assertEquals(entity, metadata.getJoins().get(0).getTarget());
        assertEquals(JoinType.DEFAULT, metadata.getJoins().get(0).getType());
        assertNull(metadata.getJoins().get(0).getCondition());
        assertTrue(metadata.getJoins().get(0).getFlags().isEmpty());
        assertNull(metadata.getWhere());
        assertTrue(metadata.getGroupBy().isEmpty());
        assertNull(metadata.getHaving());
        assertTrue(metadata.getOrderBy().isEmpty());
        assertNull(metadata.getModifiers().getLimit());
        assertNull(metadata.getModifiers().getOffset());
        assertTrue(metadata.getFlags().isEmpty());
        assertTrue(metadata.getParams().isEmpty());
    }

    private static class TestProvider
    extends AbstractWrappedCollClauseProvider {
        @SafeVarargs
        public TestProvider(@NotNull Class<? extends EntityPath<?>>... supportedEntityTypes) {
            super(supportedEntityTypes);
        }
        public TestProvider(@NotNull Set<Class<? extends EntityPath<?>>> supportedEntityTypes) {
            super(supportedEntityTypes);
        }
        @Override
        protected <T extends EntityPath<?>> T getBaseEntity(@NotNull Class<T> entityType) {
            throw new AssertionError("Method call not mocked");
        }
        @Override
        protected <T> Collection<T> getValues(@NotNull EntityPath<T> entity) {
            throw new AssertionError("Method call not mocked");
        }
        @Override
        protected ExpressionTransformer getTransformer(@NotNull EntityPath<?> entity) {
            throw new AssertionError("Method call not mocked");
        }
    }
    private static interface BeanType {}
    private static class QEntity extends EntityPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public QEntity(String variable) {
            super(BeanType.class, PathMetadataFactory.forVariable(variable));
        }
        public QEntity(Path<? extends BeanType> path) {
            super(path.getType(), path.getMetadata());
        }
        public QEntity(PathMetadata metadata) {
            super(BeanType.class, metadata);
        }
    }
    private static class QEntity2 extends EntityPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public QEntity2(String variable) {
            super(BeanType.class, PathMetadataFactory.forVariable(variable));
        }
        public QEntity2(Path<? extends BeanType> path) {
            super(path.getType(), path.getMetadata());
        }
        public QEntity2(PathMetadata metadata) {
            super(BeanType.class, metadata);
        }
    }
}
