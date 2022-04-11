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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.EntityPath;

/**
 * Unit tests for {@code WrappedClauseProviderRegistry}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedClauseProviderRegistry
 */
@Tag("ut")
class WrappedClauseProviderRegistryTest {

    @AfterAll
    static void resetProviders() {
        WrappedClauseFactory.reset();
    }

    /**
     * Unit test for {@link WrappedClauseProviderRegistry#addToWrappedClauseFactory()}.
     */
    @Test
    void testAddToWrappedClauseFactory() {
        final WrappedClauseProviderRegistry registry = spy(WrappedClauseProviderRegistry.class);
        assertTrue(WrappedClauseFactory.getRegistries().isEmpty());
        registry.addToWrappedClauseFactory();
        assertFalse(WrappedClauseFactory.getRegistries().isEmpty());
        assertEquals(1, WrappedClauseFactory.getRegistries().size());
        assertSame(registry, WrappedClauseFactory.getRegistries().get(0));
    }

    public static void expectProviderNotFound(
            final @NotNull WrappedClauseProviderRegistry registry,
            final @NotNull Class<? extends WrappedClauseProvider> providerType,
            final @NotNull Class<? extends EntityPath<?>> entityType) {
        given(registry.find(providerType, entityType)).willReturn(null);
    }

    public static <T extends WrappedClauseProvider> void expectProviderFound(
            final @NotNull WrappedClauseProviderRegistry registry,
            final @NotNull Class<T> providerType,
            final @NotNull Class<? extends EntityPath<?>> entityType,
            final T provider) {
        given(registry.find(providerType, entityType)).willReturn(provider);
    }

    public static void assertProviderFound(
            final @NotNull WrappedClauseProviderRegistry registry,
            final @NotNull Class<? extends WrappedClauseProvider> providerType,
            final @NotNull Class<? extends EntityPath<?>> entityType) {
        then(registry).should().find(providerType, entityType);
    }
}
