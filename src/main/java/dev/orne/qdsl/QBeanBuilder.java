package dev.orne.qdsl;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021 - 2022 Orne Developments
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;
import org.apiguardian.api.API;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

/**
 * Builder for {@code QBean} projections with support for fluent property
 * binding.
 * <p>
 * Should be part of {@code Projections} API.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @param <T> The bean type
 * @since 0.1
 * @see QBean
 * @see Projections
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public class QBeanBuilder<T>
implements Builder<QBean<T>> {

    /** The bean type. */
    private final @NotNull Class<T> type;
    /** The property bindings. */
    private final @NotNull LinkedHashMap<String, Expression<?>> bindings =
            new LinkedHashMap<>();
    /** If direct field access must be used. */
    private boolean fieldAccess;

    /**
     * Creates a new instance.
     * 
     * @param type The bean type
     */
    public QBeanBuilder(
            final @NotNull Class<T> type) {
        super();
        this.type = Validate.notNull(type, "The type must not be null");
    }

    /**
     * Utility factory method for clearer syntax.
     * <p>
     * Allows writing
     * <code>QBeanBuilder.of(Type.class).build()</code>
     * instead of
     * <code>new QBeanBuilder{@literal <}{@literal >}(Type.class).build()</code>
     * .
     * 
     * @param <T> The bean type
     * @param type The bean type
     * @return The created instance
     */
    public static <T> QBeanBuilder<T> of(
            final @NotNull Class<T> type) {
        return new QBeanBuilder<>(type);
    }

    /**
     * Returns {@code true} for field access and {@code false}, for property
     * access.
     * 
     * @return If direct field access must be used
     */
    public boolean isFieldAccess() {
        return this.fieldAccess;
    }

    /**
     * Returns the type of the projection.
     * 
     * @return The type of the projection
     */
    public @NotNull Class<T> getType() {
        return this.type;
    }

    /**
     * Returns the property bindings.
     * 
     * @return The property bindings
     */
    public @NotNull Map<String, Expression<?>> getBindings() {
        return Collections.unmodifiableMap(this.bindings);
    }

    /**
     * Sets if direct field access must be used.
     * 
     * @param fieldAccess {@code true} for field access and
     * {@code false} for property access
     * @return This instance for method chaining
     */
    public @NotNull QBeanBuilder<T> withFieldAccess(
            final boolean fieldAccess) {
        this.fieldAccess = fieldAccess;
        return this;
    }

    /**
     * Binds the specified property with the specified expression.
     * 
     * @param property The property to bind to
     * @param expr The binded expression
     * @return This instance for method chaining
     */
    public @NotNull QBeanBuilder<T> bind(
            final @NotNull String property,
            final @NotNull Expression<?> expr) {
        this.bindings.put(
                Validate.notNull(property, "The property must not be null"),
                Validate.notNull(expr, "The expression must not be null"));
        return this;
    }

    /**
     * Creates a new {@code QBean} instance with current bindings.
     * 
     * @return The {@code QBean} instance
     */
    @Override
    public @NotNull QBean<T> build() {
        if (this.fieldAccess) {
            return Projections.fields(this.type, this.bindings);
        } else {
            return Projections.bean(this.type, this.bindings);
        }
    }
}
