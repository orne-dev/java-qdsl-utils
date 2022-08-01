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
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.orne.qdsl.wrap.WrappedClauseProvider;

/**
 * Unit tests for {@code DefaultWrappedClauseProviderRegistry}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 * @see DefaultWrappedClauseProviderRegistry
 */
@Tag("ut")
class DefaultWrappedClauseProviderRegistryTest {

    /**
     * Unit test for {@link DefaultWrappedClauseProviderRegistry#DefaultWrappedClauseProviderRegistry()}.
     */
    @Test
    void testConstructor() {
        final DefaultWrappedClauseProviderRegistry result = new DefaultWrappedClauseProviderRegistry();
        assertNotNull(result.getProviders());
        assertTrue(result.getProviders().isEmpty());
        assertFalse(result.getProvidersIterator().hasNext());
    }

    /**
     * Unit test for {@link DefaultWrappedClauseProviderRegistry#addProvider(WrappedClauseProvider)}.
     */
    @Test
    void testAddProvider() {
        final DefaultWrappedClauseProviderRegistry registry = new DefaultWrappedClauseProviderRegistry();
        final WrappedClauseProvider provider1 = mock(WrappedClauseProvider.class);
        final WrappedClauseProvider provider2 = mock(WrappedClauseProvider.class);
        assertThrows(NullPointerException.class, () -> {
            registry.addProvider(null);
        });
        registry.addProvider(provider1);
        assertEquals(1, registry.getProviders().size());
        assertTrue(registry.getProviders().contains(provider1));
        Iterator<WrappedClauseProvider> iterator = registry.getProvidersIterator();
        assertTrue(iterator.hasNext());
        assertSame(provider1, iterator.next());
        assertFalse(iterator.hasNext());
        registry.addProvider(provider1);
        assertEquals(1, registry.getProviders().size());
        assertTrue(registry.getProviders().contains(provider1));
        iterator = registry.getProvidersIterator();
        assertTrue(iterator.hasNext());
        assertSame(provider1, iterator.next());
        assertFalse(iterator.hasNext());
        registry.addProvider(provider2);
        assertEquals(2, registry.getProviders().size());
        assertTrue(registry.getProviders().contains(provider1));
        assertTrue(registry.getProviders().contains(provider2));
        iterator = registry.getProvidersIterator();
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
        then(provider1).shouldHaveNoInteractions();
        then(provider2).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link DefaultWrappedClauseProviderRegistry#withProvider(WrappedClauseProvider)}.
     */
    @Test
    void testWithProvider() {
        final DefaultWrappedClauseProviderRegistry registry = spy(DefaultWrappedClauseProviderRegistry.class);
        final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
        assertThrows(NullPointerException.class, () -> {
            registry.withProvider(null);
        });
        willDoNothing().given(registry).addProvider(provider);
        assertSame(registry, registry.withProvider(provider));
        then(registry).should().addProvider(provider);
    }

    /**
     * Unit test for {@link DefaultWrappedClauseProviderRegistry#addProviders(WrappedClauseProvider...)}.
     */
    @Test
    void testAddProviders_Varargs() {
        final DefaultWrappedClauseProviderRegistry registry = new DefaultWrappedClauseProviderRegistry();
        assertThrows(NullPointerException.class, () -> {
            registry.addProviders((WrappedClauseProvider[]) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            registry.addProviders(
                    mock(WrappedClauseProvider.class),
                    (WrappedClauseProvider) null,
                    mock(WrappedClauseProvider.class));
        });
        final int count = RandomUtils.nextInt(2, 10);
        final WrappedClauseProvider[] providers = new WrappedClauseProvider[count];
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
            providers[i] = provider;
        }
        registry.addProviders(providers);
        assertEquals(count, registry.getProviders().size());
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = providers[i];
            assertTrue(registry.getProviders().contains(provider));
            then(provider).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link DefaultWrappedClauseProviderRegistry#withProviders(WrappedClauseProvider...)}.
     */
    @Test
    void testWithProviders_Varargs() {
        final DefaultWrappedClauseProviderRegistry registry = spy(DefaultWrappedClauseProviderRegistry.class);
        assertThrows(NullPointerException.class, () -> {
            registry.withProviders((WrappedClauseProvider[]) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            registry.withProviders(
                    mock(WrappedClauseProvider.class),
                    (WrappedClauseProvider) null,
                    mock(WrappedClauseProvider.class));
        });
        final int count = RandomUtils.nextInt(2, 10);
        final WrappedClauseProvider[] providers = new WrappedClauseProvider[count];
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
            providers[i] = provider;
        }
        assertTrue(registry.getProviders().isEmpty());
        assertThrows(NullPointerException.class, () -> {
            registry.withProvider(null);
        });
        willDoNothing().given(registry).addProviders(providers);
        assertSame(registry, registry.withProviders(providers));
        then(registry).should().addProviders(providers);
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = providers[i];
            then(provider).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link DefaultWrappedClauseProviderRegistry#addProviders(Collection)}.
     */
    @Test
    void testAddProviders_Collection() {
        final DefaultWrappedClauseProviderRegistry registry = new DefaultWrappedClauseProviderRegistry();
        assertThrows(NullPointerException.class, () -> {
            registry.addProviders((WrappedClauseProvider[]) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            registry.addProviders(Arrays.asList(
                    mock(WrappedClauseProvider.class),
                    (WrappedClauseProvider) null,
                    mock(WrappedClauseProvider.class)));
        });
        final int count = RandomUtils.nextInt(2, 10);
        final List<WrappedClauseProvider> providers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
            providers.add(provider);
        }
        registry.addProviders(providers);
        assertEquals(count, registry.getProviders().size());
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = providers.get(i);
            assertTrue(registry.getProviders().contains(provider));
            then(provider).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link DefaultWrappedClauseProviderRegistry#withProviders(Collection)}.
     */
    @Test
    void testWithProviders_Collection() {
        final DefaultWrappedClauseProviderRegistry registry = spy(DefaultWrappedClauseProviderRegistry.class);
        assertThrows(NullPointerException.class, () -> {
            registry.addProviders((Collection<WrappedClauseProvider>) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            registry.addProviders(Arrays.asList(
                    mock(WrappedClauseProvider.class),
                    (WrappedClauseProvider) null,
                    mock(WrappedClauseProvider.class)));
        });
        final int count = RandomUtils.nextInt(2, 10);
        final List<WrappedClauseProvider> providers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = mock(WrappedClauseProvider.class);
            providers.add(provider);
        }
        assertTrue(registry.getProviders().isEmpty());
        willDoNothing().given(registry).addProviders(providers);
        assertSame(registry, registry.withProviders(providers));
        then(registry).should().addProviders(providers);
        for (int i = 0; i < count; i++) {
            final WrappedClauseProvider provider = providers.get(i);
            then(provider).shouldHaveNoInteractions();
        }
    }
}
