package dev.orne.qdsl.wrap;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;

/**
 * Unit tests for {@code WrappedClauseFactory}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedClauseFactory
 */
@Tag("ut")
class WrappedClauseFactoryTest {

    private @Mock WrappedClauseProviderRegistry registry1;
    private @Mock WrappedClauseProviderRegistry registry2;
    private @Mock EntityPath<?> entity;
    private @Mock ProjectionType projectionType;
    private @Mock WrappedQueryClauseProvider queryProvider;
    private @Mock Expression<?> projection;
    private @Mock ExtendedQueryClause<?, ?> queryClause;
    private @Mock WrappedInsertClauseProvider insertProvider;
    private @Mock ExtendedInsertClause<?> insertClause;
    private @Mock WrappedUpdateClauseProvider updateProvider;
    private @Mock ExtendedUpdateClause<?> updateClause;
    private @Mock WrappedDeleteClauseProvider deleteProvider;
    private @Mock DeleteClause<?> deleteClause;
    private @Mock Expression<?> expression1;
    private @Mock Expression<?> expression2;
    private @Mock Expression<?> expression3;
    private Class<? extends EntityPath<?>> entityType;
    private Expression<?>[] expressions;
    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
        entityType = WrappedClauseProvider.getEntityPathType(entity);
        expressions = new Expression<?>[] { expression1, expression2, expression3 };
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    @AfterEach
    void resetProviders() {
        WrappedClauseFactory.reset();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#addRegistry(WrappedClauseProviderRegistry)}.
     */
    @Test
    void testAddRegistry() {
        assertTrue(WrappedClauseFactory.getRegistries().isEmpty());
        WrappedClauseFactory.addRegistry(registry1);
        assertFalse(WrappedClauseFactory.getRegistries().isEmpty());
        assertEquals(1, WrappedClauseFactory.getRegistries().size());
        assertSame(registry1, WrappedClauseFactory.getRegistries().get(0));
        WrappedClauseFactory.addRegistry(registry2);
        assertFalse(WrappedClauseFactory.getRegistries().isEmpty());
        assertEquals(2, WrappedClauseFactory.getRegistries().size());
        assertSame(registry1, WrappedClauseFactory.getRegistries().get(0));
        assertSame(registry2, WrappedClauseFactory.getRegistries().get(1));
    }

    /**
     * Unit test for {@link WrappedClauseFactory#reset()}.
     */
    @Test
    void testReset() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseFactory.reset();
        assertTrue(WrappedClauseFactory.getRegistries().isEmpty());
    }

    /**
     * Unit test for {@link WrappedClauseFactory#query(EntityPath)}.
     */
    @Test
    void testQuery() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.query(entity);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(queryClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#query(EntityPath)}.
     */
    @Test
    void testQueryMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.query(entity);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(queryClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#query(EntityPath)}.
     */
    @Test
    void testQueryNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.query(entity);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#query(EntityPath)}.
     */
    @Test
    void testQueryUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.query(entity);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#getProjection(EntityPath, ProjectionType)}.
     */
    @Test
    void testGetProjection() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectGetProjection(queryProvider, entity, projectionType, projection);
        final Expression<?> result = WrappedClauseFactory.getProjection(entity, projectionType);
        assertSame(projection, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertGetProjection(queryProvider, entity, projectionType);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#getProjection(EntityPath, ProjectionType)}.
     */
    @Test
    void testGetProjectionMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectGetProjection(queryProvider, entity, projectionType, projection);
        final Expression<?> result = WrappedClauseFactory.getProjection(entity, projectionType);
        assertSame(projection, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertGetProjection(queryProvider, entity, projectionType);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#getProjection(EntityPath, ProjectionType)}.
     */
    @Test
    void testGetProjectionNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.getProjection(entity, projectionType);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#getProjection(EntityPath, ProjectionType)}.
     */
    @Test
    void testGetProjectionUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.getProjection(entity, projectionType);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath)}.
     */
    @Test
    void testSelect() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        WrappedClauseProviderTest.expectGetProjection(queryProvider, entity, ProjectionType.Basic.DEFAULT, projection);
        willReturn(queryClause).given(queryClause).select(projection);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        WrappedClauseProviderTest.assertGetProjection(queryProvider, entity, ProjectionType.Basic.DEFAULT);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(projection);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath)}.
     */
    @Test
    void testSelectMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        WrappedClauseProviderTest.expectGetProjection(queryProvider, entity, ProjectionType.Basic.DEFAULT, projection);
        willReturn(queryClause).given(queryClause).select(projection);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        WrappedClauseProviderTest.assertGetProjection(queryProvider, entity, ProjectionType.Basic.DEFAULT);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(projection);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath)}.
     */
    @Test
    void testSelectNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath)}.
     */
    @Test
    void testSelectUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, ProjectionType)}.
     */
    @Test
    void testSelectProjectionType() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        WrappedClauseProviderTest.expectGetProjection(queryProvider, entity, projectionType, projection);
        willReturn(queryClause).given(queryClause).select(projection);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity, projectionType);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        WrappedClauseProviderTest.assertGetProjection(queryProvider, entity, projectionType);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(projection);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, ProjectionType)}.
     */
    @Test
    void testSelectProjectionTypeMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        WrappedClauseProviderTest.expectGetProjection(queryProvider, entity, projectionType, projection);
        willReturn(queryClause).given(queryClause).select(projection);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity, projectionType);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        WrappedClauseProviderTest.assertGetProjection(queryProvider, entity, projectionType);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(projection);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, ProjectionType)}.
     */
    @Test
    void testSelectProjectionTypeNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity, projectionType);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, ProjectionType)}.
     */
    @Test
    void testSelectProjectionTypeUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity, projectionType);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression)}.
     */
    @Test
    void testSelectProjection() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        willReturn(queryClause).given(queryClause).select(projection);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity, projection);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(projection);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression)}.
     */
    @Test
    void testSelectProjectionMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        willReturn(queryClause).given(queryClause).select(projection);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity, projection);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(projection);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression)}.
     */
    @Test
    void testSelectProjectionNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity, projection);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression)}.
     */
    @Test
    void testSelectProjectionUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity, projection);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression...)}.
     */
    @Test
    void testSelectExpressions() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        willReturn(queryClause).given(queryClause).select(expressions);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity, expressions);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(expressions);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression...)}.
     */
    @Test
    void testSelectExpressionsMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType,
                queryProvider);
        WrappedClauseProviderTest.expectCreateClause(queryProvider, entity, queryClause);
        willReturn(queryClause).given(queryClause).select(expressions);
        final ExtendedQueryClause<?, ?> result = WrappedClauseFactory.select(entity, expressions);
        assertSame(queryClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(queryProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(projection).shouldHaveNoInteractions();
        then(queryClause).should().select(expressions);
        then(queryClause).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression...)}.
     */
    @Test
    void testSelectExpressionsNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity, expressions);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedQueryClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#select(EntityPath, Expression...)}.
     */
    @Test
    void testSelectExpressionsUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.select(entity, expressions);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#insert(EntityPath)}.
     */
    @Test
    void testInsert() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedInsertClauseProvider.class,
                entityType,
                insertProvider);
        WrappedClauseProviderTest.expectCreateClause(insertProvider, entity, insertClause);
        final ExtendedInsertClause<?> result = WrappedClauseFactory.insert(entity);
        assertSame(insertClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedInsertClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(insertProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(insertClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#insert(EntityPath)}.
     */
    @Test
    void testInsertMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedInsertClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedInsertClauseProvider.class,
                entityType,
                insertProvider);
        WrappedClauseProviderTest.expectCreateClause(insertProvider, entity, insertClause);
        final ExtendedInsertClause<?> result = WrappedClauseFactory.insert(entity);
        assertSame(insertClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedInsertClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedInsertClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(insertProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(insertClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#insert(EntityPath)}.
     */
    @Test
    void testInsertNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedInsertClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedInsertClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.insert(entity);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedInsertClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedInsertClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#insert(EntityPath)}.
     */
    @Test
    void testInsertUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.insert(entity);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#update(EntityPath)}.
     */
    @Test
    void testUpdate() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedUpdateClauseProvider.class,
                entityType,
                updateProvider);
        WrappedClauseProviderTest.expectCreateClause(updateProvider, entity, updateClause);
        final ExtendedUpdateClause<?> result = WrappedClauseFactory.update(entity);
        assertSame(updateClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedUpdateClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(updateProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(updateClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#update(EntityPath)}.
     */
    @Test
    void testUpdateMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedUpdateClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedUpdateClauseProvider.class,
                entityType,
                updateProvider);
        WrappedClauseProviderTest.expectCreateClause(updateProvider, entity, updateClause);
        final ExtendedUpdateClause<?> result = WrappedClauseFactory.update(entity);
        assertSame(updateClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedUpdateClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedUpdateClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(updateProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(updateClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#update(EntityPath)}.
     */
    @Test
    void testUpdateNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedUpdateClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedUpdateClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.update(entity);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedUpdateClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedUpdateClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#update(EntityPath)}.
     */
    @Test
    void testUpdateUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.update(entity);
        });
    }

    /**
     * Unit test for {@link WrappedClauseFactory#delete(EntityPath)}.
     */
    @Test
    void testDelete() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry1,
                WrappedDeleteClauseProvider.class,
                entityType,
                deleteProvider);
        WrappedClauseProviderTest.expectCreateClause(deleteProvider, entity, deleteClause);
        final DeleteClause<?> result = WrappedClauseFactory.delete(entity);
        assertSame(deleteClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedDeleteClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        then(registry2).shouldHaveNoInteractions();
        WrappedClauseProviderTest.assertCreateClause(deleteProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(deleteClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#delete(EntityPath)}.
     */
    @Test
    void testDeleteMultipleRegistries() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedDeleteClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderFound(
                registry2,
                WrappedDeleteClauseProvider.class,
                entityType,
                deleteProvider);
        WrappedClauseProviderTest.expectCreateClause(deleteProvider, entity, deleteClause);
        final DeleteClause<?> result = WrappedClauseFactory.delete(entity);
        assertSame(deleteClause, result);
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedDeleteClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedDeleteClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
        WrappedClauseProviderTest.assertCreateClause(deleteProvider, entity);
        then(queryProvider).shouldHaveNoMoreInteractions();
        then(deleteClause).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#delete(EntityPath)}.
     */
    @Test
    void testDeleteNotFound() {
        WrappedClauseFactory.addRegistry(registry1);
        WrappedClauseFactory.addRegistry(registry2);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry1,
                WrappedDeleteClauseProvider.class,
                entityType);
        WrappedClauseProviderRegistryTest.expectProviderNotFound(
                registry2,
                WrappedDeleteClauseProvider.class,
                entityType);
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.delete(entity);
        });
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry1,
                WrappedDeleteClauseProvider.class,
                entityType);
        then(registry1).shouldHaveNoMoreInteractions();
        WrappedClauseProviderRegistryTest.assertProviderFound(
                registry2,
                WrappedDeleteClauseProvider.class,
                entityType);
        then(registry2).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link WrappedClauseFactory#delete(EntityPath)}.
     */
    @Test
    void testDeleteUnconfigured() {
        assertThrows(ClauseProviderNotFoundException.class, () -> {
            WrappedClauseFactory.delete(entity);
        });
    }
}
