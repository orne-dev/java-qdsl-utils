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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import dev.orne.qdsl.wrap.WrappedClauseProvider;

/**
 * Default implementation of {@code WrappedClauseProviderRegistry}.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 */
public class DefaultWrappedClauseProviderRegistry
extends AbstractWrappedClauseProviderRegistry {

    /** The registered wrapped clause providers. */
    private final Set<WrappedClauseProvider> providers = new HashSet<>();

    /**
     * Returns the registered wrapped clause providers.
     * 
     * @return The registered wrapped clause providers
     */
    protected @NotNull Set<WrappedClauseProvider> getProviders() {
        return Collections.unmodifiableSet(this.providers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull Iterator<WrappedClauseProvider> getProvidersIterator() {
        return this.providers.iterator();
    }

    /**
     * Adds the specified provider to the registered wrapped clause providers.
     * 
     * @param provider The wrapped clause provider to register
     */
    public void addProvider(
            final @NotNull WrappedClauseProvider provider) {
        this.providers.add(Validate.notNull(provider));
    }

    /**
     * Adds the specified provider to the registered wrapped clause providers.
     * 
     * @param provider The wrapped clause provider to register
     * @return This instance, for method chaining
     */
    public @NotNull DefaultWrappedClauseProviderRegistry withProvider(
            final @NotNull WrappedClauseProvider provider) {
        addProvider(provider);
        return this;
    }

    /**
     * Adds the specified providers to the registered wrapped clause providers.
     * 
     * @param providers The wrapped clause providers to register
     */
    public void addProviders(
            final @NotNull WrappedClauseProvider... providers) {
        Validate.notNull(providers);
        addProviders(Arrays.asList(providers));
    }

    /**
     * Adds the specified providers to the registered wrapped clause providers.
     * 
     * @param providers The wrapped clause providers to register
     */
    public void addProviders(
            final @NotNull Collection<WrappedClauseProvider> providers) {
        Validate.notNull(providers);
        Validate.noNullElements(providers);
        this.providers.addAll(providers);
    }

    /**
     * Adds the specified providers to the registered wrapped clause providers.
     * 
     * @param providers The wrapped clause providers to register
     * @return This instance, for method chaining
     */
    public @NotNull DefaultWrappedClauseProviderRegistry withProviders(
            final @NotNull WrappedClauseProvider... providers) {
        addProviders(providers);
        return this;
    }

    /**
     * Adds the specified providers to the registered wrapped clause providers.
     * 
     * @param providers The wrapped clause providers to register
     * @return This instance, for method chaining
     */
    public @NotNull DefaultWrappedClauseProviderRegistry withProviders(
            final @NotNull Collection<WrappedClauseProvider> providers) {
        addProviders(providers);
        return this;
    }
}
