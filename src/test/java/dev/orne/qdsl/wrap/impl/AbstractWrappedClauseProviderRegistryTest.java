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

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.EntityPath;

import dev.orne.qdsl.wrap.WrappedClauseProvider;
import dev.orne.qdsl.wrap.WrappedQueryClauseProvider;

/**
 * Unit tests for {@code AbstractWrappedClauseProviderRegistry}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 * @see AbstractWrappedClauseProviderRegistry
 */
@Tag("ut")
class AbstractWrappedClauseProviderRegistryTest {

    /**
     * Unit test for {@link AbstractWrappedClauseProviderRegistry#find(Class, Class)}.
     */
    @Test
    void testFind() {
        final AbstractWrappedClauseProviderRegistry registry =
                spy(AbstractWrappedClauseProviderRegistry.class);
        final int count = RandomUtils.nextInt(2, 10);
        final List<WrappedClauseProvider> providers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
            providers.add(provider);
            given(provider.supports(Type.class)).willReturn(false);
        }
        final WrappedClauseProvider expected = mock(WrappedClauseProvider.class);
        given(expected.supports(Type.class)).willReturn(true);
        providers.add(RandomUtils.nextInt(0, count + 1), expected);
        given(registry.getProvidersIterator()).willReturn(providers.iterator());
        final WrappedClauseProvider result = registry.find(
                WrappedClauseProvider.class,
                Type.class);
        assertSame(expected, result);
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProviderRegistry#find(Class, Class)}.
     */
    @Test
    void testFind_NotFound() {
        final AbstractWrappedClauseProviderRegistry registry =
                spy(AbstractWrappedClauseProviderRegistry.class);
        final int count = RandomUtils.nextInt(2, 10);
        final List<WrappedClauseProvider> providers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
            providers.add(provider);
            given(provider.supports(Type.class)).willReturn(false);
        }
        given(registry.getProvidersIterator()).willReturn(providers.iterator());
        final WrappedClauseProvider result = registry.find(
                WrappedClauseProvider.class,
                Type.class);
        assertNull(result);
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProviderRegistry#find(Class, Class)}.
     */
    @Test
    void testFind_SubClass() {
        final AbstractWrappedClauseProviderRegistry registry =
                spy(AbstractWrappedClauseProviderRegistry.class);
        final int count = RandomUtils.nextInt(2, 10);
        final List<WrappedClauseProvider> providers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
            providers.add(provider);
            given(provider.supports(Type.class)).willReturn(false);
        }
        final WrappedClauseProvider supported = mock(WrappedClauseProvider.class);
        given(supported.supports(Type.class)).willReturn(true);
        providers.add(RandomUtils.nextInt(0, count), supported);
        final WrappedQueryClauseProvider expected = mock(WrappedQueryClauseProvider.class);
        given(expected.supports(Type.class)).willReturn(true);
        providers.add(expected);
        given(registry.getProvidersIterator()).willReturn(providers.iterator());
        final WrappedClauseProvider result = registry.find(
                WrappedQueryClauseProvider.class,
                Type.class);
        assertSame(expected, result);
    }

    private interface Type
    extends EntityPath<Object> {}
}
