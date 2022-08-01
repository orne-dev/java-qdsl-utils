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

import javax.cache.Cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.types.EntityPath;

import dev.orne.qdsl.wrap.WrappedClauseProvider;
import dev.orne.qdsl.wrap.WrappedClauseProviderRegistry;
import dev.orne.qdsl.wrap.WrappedDeleteClauseProvider;
import dev.orne.qdsl.wrap.WrappedGroupableQueryClauseProvider;
import dev.orne.qdsl.wrap.WrappedInsertClauseProvider;
import dev.orne.qdsl.wrap.WrappedQueryClauseProvider;
import dev.orne.qdsl.wrap.WrappedUpdateClauseProvider;
import dev.orne.qdsl.wrap.impl.CacheableWrappedClauseProviderRegistry.CacheKey;

/**
 * Unit tests for {@code CacheableWrappedClauseProviderRegistry}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 * @see CacheableWrappedClauseProviderRegistry
 */
@Tag("ut")
class CacheableWrappedClauseProviderRegistryTest {

    private @Mock WrappedClauseProviderRegistry delegated;
    private @Mock Cache<CacheKey, WrappedClauseProvider> cache;
    private @Mock WrappedClauseProvider provider;
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
     * Unit test for {@link CacheableWrappedClauseProviderRegistry#CacheableWrappedClauseProviderRegistry(WrappedClauseProviderRegistry)}.
     */
    @Test
    void testConstructor() {
        final CacheableWrappedClauseProviderRegistry registry =
                new CacheableWrappedClauseProviderRegistry(delegated);
        assertSame(delegated, registry.getDelegated());
        assertNull(registry.getCache());
        then(delegated).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link CacheableWrappedClauseProviderRegistry#setCache(Cache)}.
     */
    @Test
    void testSetCache() {
        final CacheableWrappedClauseProviderRegistry registry =
                new CacheableWrappedClauseProviderRegistry(delegated);
        registry.setCache(cache);
        assertSame(cache, registry.getCache());
        registry.setCache(null);
        assertNull(registry.getCache());
        then(delegated).shouldHaveNoInteractions();
        then(cache).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link CacheableWrappedClauseProviderRegistry#reset()}.
     */
    @Test
    void testReset_NoCache() {
        final CacheableWrappedClauseProviderRegistry registry =
                new CacheableWrappedClauseProviderRegistry(delegated);
        registry.reset();
        then(delegated).shouldHaveNoInteractions();
        then(cache).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link CacheableWrappedClauseProviderRegistry#reset()}.
     */
    @Test
    void testReset() {
        final CacheableWrappedClauseProviderRegistry registry =
                new CacheableWrappedClauseProviderRegistry(delegated);
        registry.setCache(cache);
        registry.reset();
        then(delegated).shouldHaveNoInteractions();
        then(cache).should().clear();
        then(cache).shouldHaveNoMoreInteractions();
    }

    /**
     * Unit test for {@link CacheableWrappedClauseProviderRegistry#find(Class, Class)}.
     */
    @Test
    void testFind_NoCache() {
        final CacheableWrappedClauseProviderRegistry registry =
                new CacheableWrappedClauseProviderRegistry(delegated);
        given(delegated.find(WrappedClauseProvider.class, Type.class)).willReturn(provider);
        final WrappedClauseProvider result = registry.find(WrappedClauseProvider.class, Type.class);
        assertSame(provider, result);
        then(delegated).should().find(WrappedClauseProvider.class, Type.class);
        then(delegated).shouldHaveNoMoreInteractions();
        then(cache).shouldHaveNoInteractions();
        then(provider).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link CacheableWrappedClauseProviderRegistry#find(Class, Class)}.
     */
    @Test
    void testFind_Miss() {
        final CacheableWrappedClauseProviderRegistry registry =
                new CacheableWrappedClauseProviderRegistry(delegated);
        registry.setCache(cache);
        final CacheKey key = new CacheKey(WrappedClauseProvider.class, Type.class);
        given(cache.containsKey(key)).willReturn(false);
        given(delegated.find(WrappedClauseProvider.class, Type.class)).willReturn(provider);
        final WrappedClauseProvider result = registry.find(WrappedClauseProvider.class, Type.class);
        assertSame(provider, result);
        then(delegated).should().find(WrappedClauseProvider.class, Type.class);
        then(delegated).shouldHaveNoMoreInteractions();
        then(cache).should().containsKey(key);
        then(cache).should().put(key, provider);
        then(cache).shouldHaveNoMoreInteractions();
        then(provider).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link CacheableWrappedClauseProviderRegistry#find(Class, Class)}.
     */
    @Test
    void testFind_Hit() {
        final CacheableWrappedClauseProviderRegistry registry =
                new CacheableWrappedClauseProviderRegistry(delegated);
        registry.setCache(cache);
        final CacheKey key = new CacheKey(WrappedClauseProvider.class, Type.class);
        given(cache.containsKey(key)).willReturn(true);
        given(cache.get(key)).willReturn(provider);
        final WrappedClauseProvider result = registry.find(WrappedClauseProvider.class, Type.class);
        assertSame(provider, result);
        then(delegated).shouldHaveNoInteractions();
        then(cache).should().containsKey(key);
        then(cache).should().get(key);
        then(cache).shouldHaveNoMoreInteractions();
        then(provider).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link CacheKey#CacheKey(Class, Class)}.
     */
    @Test
    void testCacheKeyConstructor() {
        final CacheKey key = new CacheKey(WrappedClauseProvider.class, Type.class);
        assertSame(WrappedClauseProvider.class, key.getProviderType());
        assertSame(Type.class, key.getEntityType());
    }

    /**
     * Unit test for {@link CacheKey#CacheKey(Class, Class)}.
     */
    @Test
    void testCacheKeyEqualsHashToString() {
        final CacheKey key = new CacheKey(WrappedClauseProvider.class, Type.class);
        assertFalse(key.equals(null));
        assertTrue(key.equals(key));
        assertFalse(key.equals(new Object()));
        CacheKey other = new CacheKey(WrappedClauseProvider.class, Type.class);
        assertEquals(key, other);
        assertEquals(key.hashCode(), other.hashCode());
        assertNotNull(key.toString());
        assertEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedClauseProvider.class, AnotherType.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedQueryClauseProvider.class, Type.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedQueryClauseProvider.class, AnotherType.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedGroupableQueryClauseProvider.class, Type.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedGroupableQueryClauseProvider.class, AnotherType.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedInsertClauseProvider.class, Type.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedInsertClauseProvider.class, AnotherType.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedUpdateClauseProvider.class, Type.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedUpdateClauseProvider.class, AnotherType.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedDeleteClauseProvider.class, Type.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new CacheKey(WrappedDeleteClauseProvider.class, AnotherType.class);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
    }

    private interface Type
    extends EntityPath<Object> {}
    private interface AnotherType
    extends EntityPath<Object> {}
}
