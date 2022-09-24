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
import java.util.Collection;
import java.util.Collections;

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
     * Unit test for {@link AbstractWrappedJpaClauseProvider#AbstractWrappedJpaClauseProvider(EntityPath, EntityPath...)}.
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
     * Unit test for {@link AbstractWrappedJpaClauseProvider#AbstractWrappedJpaClauseProvider(EntityPath, Collection)}.
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
     * Unit test for {@link AbstractWrappedJpaClauseProvider#query(EntityPath)}.
     */
    @Test
    void testQuery() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
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
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final QEntity alias = new QEntity("alias");
        final QTargetEntity source = new QTargetEntity("alias");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final JPQLQuery<?> expected = mock(JPQLQuery.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(expected).given(factory).from(source);
        final JPQLQuery<?> result = provider.createDelegatedQueryClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(factory).should().from(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedJpaClauseProvider#createDelegatedInsertClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedInsertClause() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final QEntity alias = new QEntity("alias");
        final QTargetEntity source = new QTargetEntity("alias");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final InsertClause<?> expected = mock(InsertClause.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(expected).given(factory).insert(source);
        final InsertClause<?> result = provider.createDelegatedInsertClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(factory).should().insert(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedJpaClauseProvider#createDelegatedUpdateClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedUpdateClause() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final QEntity alias = new QEntity("alias");
        final QTargetEntity source = new QTargetEntity("alias");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final UpdateClause<?> expected = mock(UpdateClause.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(expected).given(factory).update(source);
        final UpdateClause<?> result = provider.createDelegatedUpdateClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(factory).should().update(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedJpaClauseProvider#createDelegatedDeleteClause(EntityPath)}.
     */
    @Test
    void testCreateDelegatedDeleteClause() {
        final AbstractWrappedJpaClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final QEntity alias = new QEntity("alias");
        final QTargetEntity source = new QTargetEntity("alias");
        final JPQLQueryFactory factory = mock(JPQLQueryFactory.class);
        final DeleteClause<?> expected = mock(DeleteClause.class);
        willReturn(factory).given(provider).getQueryFactory();
        willReturn(expected).given(factory).delete(source);
        final DeleteClause<?> result = provider.createDelegatedDeleteClause(alias);
        assertSame(expected, result);
        then(provider).should().getQueryFactory();
        then(factory).should().delete(source);
        then(factory).shouldHaveNoMoreInteractions();
        then(expected).shouldHaveNoInteractions();
    }

    private static class TestProvider
    extends AbstractWrappedJpaClauseProvider {
        public TestProvider(@NotNull EntityPath<?> targetEntity, @NotNull EntityPath<?>... supportedEntities) {
            super(targetEntity, supportedEntities);
        }
        public TestProvider(@NotNull EntityPath<?> targetEntity, @NotNull Collection<EntityPath<?>> supportedEntities) {
            super(targetEntity, supportedEntities);
        }
        @Override
        protected @NotNull JPQLQueryFactory getQueryFactory() {
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
