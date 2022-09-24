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
import java.util.Map;

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
     * Unit test for {@link AbstractWrappedCollClauseProvider#AbstractWrappedCollClauseProvider(EntityPath, EntityPath...)}.
     */
    @Test
    void testVarargsConstructor() {
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(null, QEntity2.base);
        });
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(QTargetEntity.base, (EntityPath<?>[]) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base, QEntity.base, null, QEntity2.base);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base);
        });
        AbstractWrappedClauseProvider result = new TestProvider(QTargetEntity.base, QEntity.base);
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertNotNull(result.getBaseEntities());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(1, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
        result = new TestProvider(QTargetEntity.base, QEntity.base, QEntity2.base);
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(2, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertTrue(result.getBaseEntities().containsKey(QEntity2.class));
        assertSame(QEntity2.base, result.getBaseEntities().get(QEntity2.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#AbstractWrappedCollClauseProvider(EntityPath, Collection)}.
     */
    @Test
    void testCollectionConstructor() {
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(null, Arrays.asList(QEntity.base));
        });
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(QTargetEntity.base, (Collection<EntityPath<?>>) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base, Arrays.asList(QEntity.base, null, QEntity2.base));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base, Collections.emptyList());
        });
        AbstractWrappedClauseProvider result = new TestProvider(
                QTargetEntity.base,
                Arrays.asList(QEntity.base));
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertNotNull(result.getBaseEntities());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(1, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
        result = new TestProvider(
                QTargetEntity.base,
                Arrays.asList(QEntity.base, QEntity2.base));
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(2, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertTrue(result.getBaseEntities().containsKey(QEntity2.class));
        assertSame(QEntity2.base, result.getBaseEntities().get(QEntity2.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#getQueryEngine()}.
     */
    @Test
    void testGetQueryEngine() {
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final QueryEngine result = provider.getQueryEngine();
        assertSame(DefaultQueryEngine.getDefault(), result);
    }

    /**
     * Unit test for {@link AbstractWrappedCollClauseProvider#query(EntityPath)}.
     */
    @Test
    void testQuery() {
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        assertThrows(NullPointerException.class, () -> {
            provider.query(null);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.query(new QEntity2("unsupported"));
        });
        final ExpressionTransformer transformer = mock(ExpressionTransformer.class);
        final QEntity alias = new QEntity("alias");
        final QTargetEntity source = new QTargetEntity("alias");
        final QueryEngine engine = mock(QueryEngine.class);
        final Collection<BeanType> values = spy(new ArrayList<>());
        willReturn(engine).given(provider).getQueryEngine();
        willReturn(values).given(provider).getValues(alias);
        willReturn(transformer).given(provider).createTransformerForAlias(alias);
        final ExtendedQueryClause<?, ?> result = provider.query(alias);
        final WrappedCollQueryClause<?> wresult = assertInstanceOf(WrappedCollQueryClause.class, result);
        final CollQuery<?> delegated = assertInstanceOf(CollQuery.class, wresult.getDelegate());
        assertSame(transformer, wresult.getTransformer());
        verifyQuery(delegated, engine, source, values);
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
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
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
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
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
        final AbstractWrappedCollClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
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
        assertEquals(entity.getClass(), metadata.getProjection().getClass());
        assertEquals(entity, metadata.getProjection());
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
        public TestProvider(@NotNull EntityPath<?> targetEntity, @NotNull EntityPath<?>... supportedEntities) {
            super(targetEntity, supportedEntities);
        }
        public TestProvider(@NotNull EntityPath<?> targetEntity, @NotNull Collection<EntityPath<?>> supportedEntities) {
            super(targetEntity, supportedEntities);
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
        public static final QEntity base = new QEntity("base");
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
        public static final QEntity2 base = new QEntity2("base");
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
    public static class QTargetEntity extends EntityPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public static final QTargetEntity base = new QTargetEntity("base");
        public QTargetEntity(String variable) {
            super(BeanType.class, PathMetadataFactory.forVariable(variable));
        }
        public QTargetEntity(Path<? extends BeanType> path) {
            super(path.getType(), path.getMetadata());
        }
        public QTargetEntity(PathMetadata metadata) {
            super(BeanType.class, metadata);
        }
    }
}
