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
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;

import dev.orne.qdsl.wrap.EntityPathNotSupportedException;
import dev.orne.qdsl.wrap.ExtendedGroupableQueryClause;

/**
 * Unit tests for {@code AbstractWrappedJpaClauseProvider}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 * @see AbstractWrappedJpaClauseProvider
 */
@Tag("ut")
class AbstractWrappedJpaClauseProviderTest {

    /**
     * Unit test for {@link AbstractWrappedJpaClauseProvider#AbstractWrappedJpaClauseProvider(Class...)}.
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
        AbstractWrappedJpaClauseProvider result = new TestProvider(QEntity.class);
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
     * Unit test for {@link AbstractWrappedJpaClauseProvider#AbstractWrappedJpaClauseProvider(Set)}.
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
        AbstractWrappedJpaClauseProvider result = new TestProvider(
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
     * Unit test for {@link AbstractWrappedJpaClauseProvider#query(EntityPath)}.
     */
    @Test
    void testQuery() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QEntity.class));
        assertThrows(NullPointerException.class, () -> {
            provider.query(null);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.query(new QEntity2("unsupported"));
        });
        final JPQLQuery<?> delegated = mock(JPQLQuery.class);
        final ExpressionTransformer transformer = mock(ExpressionTransformer.class);
        final QEntity alias = new QEntity("alias");
        willReturn(delegated).given(provider).createDelegatedQueryClause(alias);
        willReturn(transformer).given(provider).createTransformerForAlias(alias);
        final ExtendedGroupableQueryClause<?, ?> result = provider.query(alias);
        final WrappedJpaQueryClause<?> wresult = assertInstanceOf(WrappedJpaQueryClause.class, result);
        assertSame(delegated, wresult.getDelegate());
        assertSame(transformer, wresult.getTransformer());
        then(provider).should().createDelegatedQueryClause(alias);
        then(provider).should().createTransformerForAlias(alias);
        then(delegated).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedJpaClauseProvider#createDelegatedQueryClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedQueryClause() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QEntity source = new QEntity("source");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final JPQLQuery<?> expected = mock(JPQLQuery.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(source).given(provider).getSource(alias);
        willReturn(expected).given(factory).from(source);
        final JPQLQuery<?> result = provider.createDelegatedQueryClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(provider).should().getSource(alias);
        then(factory).should().from(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedJpaClauseProvider#createDelegatedInsertClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedInsertClause() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QEntity source = new QEntity("source");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final InsertClause<?> expected = mock(InsertClause.class);
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
     * Unit test for {@link AbstractWrappedJpaClauseProvider#createDelegatedUpdateClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedUpdateClause() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QEntity source = new QEntity("source");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final UpdateClause<?> expected = mock(UpdateClause.class);
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
     * Unit test for {@link AbstractWrappedJpaClauseProvider#createDelegatedDeleteClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedDeleteClause() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QEntity.class));
        final QEntity alias = new QEntity("alias");
        final QEntity source = new QEntity("source");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final DeleteClause<?> expected = mock(DeleteClause.class);
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
    extends AbstractWrappedJpaClauseProvider {
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
        protected @NotNull JPQLQueryFactory getQueryFactory() {
            throw new AssertionError("Method call not mocked");
        }
        @Override
        protected @NotNull EntityPath<?> getSource(@NotNull EntityPath<?> entity) {
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
