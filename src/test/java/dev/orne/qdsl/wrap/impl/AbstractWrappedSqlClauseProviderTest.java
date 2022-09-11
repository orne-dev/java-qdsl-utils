package dev.orne.qdsl.wrap.impl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLCommonQueryFactory;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;

import dev.orne.qdsl.wrap.EntityPathNotSupportedException;
import dev.orne.qdsl.wrap.ExtendedGroupableQueryClause;

/**
 * Unit tests for {@code AbstractWrappedSqlClauseProvider}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 * @see AbstractWrappedSqlClauseProvider
 */
@Tag("ut")
class AbstractWrappedSqlClauseProviderTest {

    /**
     * Unit test for {@link AbstractWrappedSqlClauseProvider#AbstractWrappedSqlClauseProvider(Class...)}.
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
        AbstractWrappedSqlClauseProvider result = new TestProvider(QEntity.class);
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
     * Unit test for {@link AbstractWrappedSqlClauseProvider#AbstractWrappedSqlClauseProvider(Set)}.
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
        AbstractWrappedSqlClauseProvider result = new TestProvider(
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
     * Unit test for {@link AbstractWrappedSqlClauseProvider#query(EntityPath)}.
     */
    @Test
    void testQuery() {
        final AbstractWrappedSqlClauseProvider provider = spy(new TestProvider(QEntity.class));
        assertThrows(NullPointerException.class, () -> {
            provider.query(null);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.query(new QEntity2("unsupported"));
        });
        final ProjectableSQLQuery<?, ?> delegated = mock(ProjectableSQLQuery.class);
        final ExpressionTransformer transformer = mock(ExpressionTransformer.class);
        final QEntity alias = new QEntity("alias");
        willReturn(delegated).given(provider).createDelegatedQueryClause(alias);
        willReturn(transformer).given(provider).createTransformerForAlias(alias);
        final ExtendedGroupableQueryClause<?, ?> result = provider.query(alias);
        final WrappedSqlQueryClause<?> wresult = assertInstanceOf(WrappedSqlQueryClause.class, result);
        assertSame(delegated, wresult.getDelegate());
        assertSame(transformer, wresult.getTransformer());
        then(provider).should().createDelegatedQueryClause(alias);
        then(provider).should().createTransformerForAlias(alias);
        then(delegated).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedSqlClauseProvider#createDelegatedQueryClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedQueryClause() {
        final AbstractWrappedSqlClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QRelationalEntity source = new QRelationalEntity("source");
        final SQLCommonQueryFactory<?, ?, ?, ?, ?> factory = mock(SQLCommonQueryFactory.class);
        final SQLQuery<?> expected = mock(SQLQuery.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(source).given(provider).getSource(alias);
        willReturn(expected).given(factory).from(source);
        final ProjectableSQLQuery<?, ?> result = provider.createDelegatedQueryClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(provider).should().getSource(alias);
        then(factory).should().from(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedSqlClauseProvider#createDelegatedInsertClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedInsertClause() {
        final AbstractWrappedSqlClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QRelationalEntity source = new QRelationalEntity("source");
        final SQLCommonQueryFactory<?, ?, ?, ?, ?> factory = mock(SQLCommonQueryFactory.class);
        final SQLInsertClause expected = mock(SQLInsertClause.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(source).given(provider).getSource(alias);
        willReturn(expected).given(factory).insert(source);
        final InsertClause<?> result = provider.createDelegatedInsertClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(provider).should().getSource(alias);
        then(factory).should().insert(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedSqlClauseProvider#createDelegatedUpdateClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedUpdateClause() {
        final AbstractWrappedSqlClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QRelationalEntity source = new QRelationalEntity("source");
        final SQLCommonQueryFactory<?, ?, ?, ?, ?> factory = mock(SQLCommonQueryFactory.class);
        final SQLUpdateClause expected = mock(SQLUpdateClause.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(source).given(provider).getSource(alias);
        willReturn(expected).given(factory).update(source);
        final UpdateClause<?> result = provider.createDelegatedUpdateClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(provider).should().getSource(alias);
        then(factory).should().update(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedSqlClauseProvider#createDelegatedDeleteClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedDeleteClause() {
        final AbstractWrappedSqlClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QRelationalEntity source = new QRelationalEntity("source");
        final SQLCommonQueryFactory<?, ?, ?, ?, ?> factory = mock(SQLCommonQueryFactory.class);
        final SQLDeleteClause expected = mock(SQLDeleteClause.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(source).given(provider).getSource(alias);
        willReturn(expected).given(factory).delete(source);
        final DeleteClause<?> result = provider.createDelegatedDeleteClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(provider).should().getSource(alias);
        then(factory).should().delete(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    private static class TestProvider
    extends AbstractWrappedSqlClauseProvider {
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
        protected @NotNull SQLCommonQueryFactory<? extends ProjectableSQLQuery<?, ?>, ?, ?, ?, ?> getQueryFactory() {
            throw new AssertionError("Method call not mocked");
        }
        @Override
        protected @NotNull RelationalPath<?> getSource(@NotNull EntityPath<?> entity) {
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
    private static class QRelationalEntity extends RelationalPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public QRelationalEntity(String variable) {
            super(BeanType.class, forVariable(variable), "null", "TABLE");
        }
        public QRelationalEntity(String variable, String schema, String table) {
            super(BeanType.class, forVariable(variable), schema, table);
        }
        public QRelationalEntity(String variable, String schema) {
            super(BeanType.class, forVariable(variable), schema, "TABLE");
        }
        public QRelationalEntity(Path<? extends BeanType> path) {
            super(path.getType(), path.getMetadata(), "null", "TABLE");
        }
        public QRelationalEntity(PathMetadata metadata) {
            super(BeanType.class, metadata, "null", "TABLE");
        }
    }
}
