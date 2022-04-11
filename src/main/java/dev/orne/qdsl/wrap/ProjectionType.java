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

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apiguardian.api.API;

/**
 * Interface for entity projection types.
 * Enumeration {@code ProjectionType.Basic} provides basic projection types.
 * <p>
 * Entities or DAOs can provide enumerations that implement this interface
 * to provide custom projection types.
 * It's recommended that those custom projection types include enumeration
 * constants with codes of the enumeration constants of
 * {@code ProjectionType.Basic}.
 * <p>
 * Note that when comparing between values of distinct enumeration types the
 * code of the projection type must be used to match supported projection
 * types. Method {@link #codeEquals(Object)} is provided to perform such match.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see ProjectionType.Basic
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public interface ProjectionType
extends Serializable {

    /** The type unique code. */
    @NotNull String getCode();

    /**
     * Compares this projection type with the passed object.
     * <p>
     * If the object is of type {@code ProjectionType} equality of code is
     * enough for a {@code true} result.
     * 
     * @param obj The object to compare
     * @return If this projection type is equal or equivalent to passed object
     */
    default boolean codeEquals(final Object obj) {
        if (!(obj instanceof ProjectionType)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return getCode().equals(((ProjectionType) obj).getCode());
    }

    /**
     * Enumeration with the default projection types.
     * <p>
     * Entities or DAOs can provide enumerations that implement the interface
     * {@code ProjectionType} to provide custom projection types. It's
     * recommended that those custom projection types include enumeration constants
     * with codes of the enumeration constants of this class.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2022-04
     * @since 0.1
     */
    @API(status=API.Status.EXPERIMENTAL, since="0.1")
    public enum Basic
    implements ProjectionType {
        /** The default projection of the entity. */
        DEFAULT("default"),
        /** The projection of the entity with at least a valid reference (PK). */
        REFERENCE("reference"),
        /** The projection of the entity with at least a valid reference (PK) and a display name. */
        NAMED_REFERENCE("namedReference"),
        ;

        /** The type unique code. */
        public final String code;

        /**
         * Creates a new instance.
         * 
         * @param code The type unique code
         */
        private Basic(
                final @NotNull String code) {
            this.code = code;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull String getCode() {
            return this.code;
        }

        /**
         * Returns the enumeration constant with the code of the specified type.
         * 
         * @param type The projection type which code use
         * @return The enumeration constant with the specified code
         * or {@code null} if no constant has the same code
         */
        public static Basic fromCode(
                final @NotNull ProjectionType type) {
            if (type == null) {
                return null;
            }
            return fromCode(type.getCode());
        }

        /**
         * Returns the enumeration constant with the specified code.
         * 
         * @param code The type unique code
         * @return The enumeration constant with the specified code
         * or {@code null} if no constant has the same code
         */
        public static Basic fromCode(
                final @NotNull String code) {
            for (final Basic value : Basic.values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            return null;
        }
    }
}
