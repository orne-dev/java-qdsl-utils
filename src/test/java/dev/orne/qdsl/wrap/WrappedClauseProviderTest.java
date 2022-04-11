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

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;

/**
 * Unit tests for {@code WrappedClauseProvider}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedClauseProvider
 */
@Tag("ut")
class WrappedClauseProviderTest {

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    /**
     * Unit test for {@link WrappedClauseProvider#getEntityPathType(EntityPath)}.
     */
    @Test
    void testGetEntityPathType() {
        final EntityPath<?> entity = mock(EntityPath.class);
        final Class<? extends EntityPath<?>> result = WrappedClauseProvider.getEntityPathType(entity);
        assertNotNull(result);
        assertSame(entity.getClass(), result);
    }

    /**
     * Unit test for {@link WrappedClauseProvider#getEntityPathType(EntityPath)}.
     */
    @Test
    void testGetEntityPathTypeWithNull() {
        assertThrows(NullPointerException.class, () -> {
            WrappedClauseProvider.getEntityPathType((EntityPath<?>) null);
        });
    }

    /**
     * Unit test for {@link WrappedClauseProvider#supports(EntityPath)}.
     */
    @Test
    void testSupportsPath() {
        final EntityPath<?> entity = mock(EntityPath.class);
        final Class<? extends EntityPath<?>> entityType = WrappedClauseProvider.getEntityPathType(entity);
        final WrappedClauseProvider provider = spy(WrappedClauseProvider.class);
        given(provider.supports(entityType)).willReturn(true, false);
        assertTrue(provider.supports(entity));
        assertFalse(provider.supports(entity));
    }

    /**
     * Unit test for {@link WrappedClauseProvider#supports(EntityPath)}.
     */
    @Test
    void testSupportsPathWithNull() {
        final WrappedClauseProvider provider = spy(WrappedClauseProvider.class);
        assertThrows(NullPointerException.class, () -> {
            provider.supports((EntityPath<?>) null);
        });
    }

    /**
     * Unit test for {@link WrappedClauseProvider#validateSupported(Class)}.
     */
    @Test
    void testValidateSupportedClass() {
        final EntityPath<?> entity = mock(EntityPath.class);
        final Class<? extends EntityPath<?>> entityType = WrappedClauseProvider.getEntityPathType(entity);
        final WrappedClauseProvider provider = spy(WrappedClauseProvider.class);
        given(provider.supports(entityType)).willReturn(true, false);
        assertDoesNotThrow(() -> {
            provider.validateSupported(entityType);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.validateSupported(entityType);
        });
    }

    /**
     * Unit test for {@link WrappedClauseProvider#validateSupported(EntityPath)}.
     */
    @Test
    void testValidateSupportedEntityPath() {
        final EntityPath<?> entity = mock(EntityPath.class);
        final Class<? extends EntityPath<?>> entityType = WrappedClauseProvider.getEntityPathType(entity);
        final WrappedClauseProvider provider = spy(WrappedClauseProvider.class);
        given(provider.supports(entityType)).willReturn(true, false);
        assertDoesNotThrow(() -> {
            provider.validateSupported(entity);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.validateSupported(entity);
        });
    }

    public static void expectGetProjection(
            final @NotNull WrappedQueryClauseProvider provider,
            final @NotNull EntityPath<?> entity,
            final @NotNull ProjectionType type,
            final @NotNull Expression<?> projecction) {
        willReturn(projecction).given(provider).getProjection(entity, type);
    }

    public static void assertGetProjection(
            final @NotNull WrappedQueryClauseProvider provider,
            final @NotNull EntityPath<?> entity,
            final @NotNull ProjectionType type) {
        then(provider).should().getProjection(entity, type);
    }

    public static void expectCreateClause(
            final @NotNull WrappedQueryClauseProvider provider,
            final @NotNull EntityPath<?> entity,
            final @NotNull ExtendedQueryClause<?, ?> queryClause) {
        willReturn(queryClause).given(provider).query(entity);
    }

    public static void assertCreateClause(
            final @NotNull WrappedQueryClauseProvider provider,
            final @NotNull EntityPath<?> entity) {
        then(provider).should().query(entity);
    }

    public static void expectCreateClause(
            final @NotNull WrappedInsertClauseProvider provider,
            final @NotNull EntityPath<?> entity,
            final @NotNull InsertClause<?> queryClause) {
        willReturn(queryClause).given(provider).insert(entity);
    }

    public static void assertCreateClause(
            final @NotNull WrappedInsertClauseProvider provider,
            final @NotNull EntityPath<?> entity) {
        then(provider).should().insert(entity);
    }

    public static void expectCreateClause(
            final @NotNull WrappedUpdateClauseProvider provider,
            final @NotNull EntityPath<?> entity,
            final @NotNull UpdateClause<?> queryClause) {
        willReturn(queryClause).given(provider).update(entity);
    }

    public static void assertCreateClause(
            final @NotNull WrappedUpdateClauseProvider provider,
            final @NotNull EntityPath<?> entity) {
        then(provider).should().update(entity);
    }

    public static void expectCreateClause(
            final @NotNull WrappedDeleteClauseProvider provider,
            final @NotNull EntityPath<?> entity,
            final @NotNull DeleteClause<?> queryClause) {
        willReturn(queryClause).given(provider).delete(entity);
    }

    public static void assertCreateClause(
            final @NotNull WrappedDeleteClauseProvider provider,
            final @NotNull EntityPath<?> entity) {
        then(provider).should().delete(entity);
    }
}
