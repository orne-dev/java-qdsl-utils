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

import java.util.Iterator;

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.EntityPath;

import dev.orne.qdsl.wrap.WrappedClauseProvider;
import dev.orne.qdsl.wrap.WrappedClauseProviderRegistry;

/**
 * Base abstract implementation of {@code WrappedClauseProviderRegistry}.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 */
public abstract class AbstractWrappedClauseProviderRegistry
implements WrappedClauseProviderRegistry {

    /**
     * Return the registered wrapped clause providers iterator.
     * 
     * @return The registered wrapped clause providers iterator
     */
    protected abstract @NotNull Iterator<WrappedClauseProvider> getProvidersIterator();

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends WrappedClauseProvider> T find(
            final @NotNull Class<T> type,
            final @NotNull Class<? extends EntityPath<?>> entityType) {
        final Iterator<WrappedClauseProvider> iterator = getProvidersIterator();
        while (iterator.hasNext()) {
            final WrappedClauseProvider provider = iterator.next();
            if (type.isInstance(provider) && provider.supports(entityType)) {
                return type.cast(provider);
            }
        }
        return null;
    }
}
