package dev.orne.qdsl;

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

import javax.validation.constraints.NotNull;

import org.apiguardian.api.API;

import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;

/**
 * No operation substitute for {@code ReplaceVisitor}.
 * Returns the provided expression, without deep visiting.
 * Usable when a {@code ReplaceVisitor<?>} or
 * {@code Visitor<Expression<?>, ?>} is expected but no replacement should be
 * done.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 */
@API(status=API.Status.EXPERIMENTAL, since="0.1")
public final class NopReplaceVisitor
extends ReplaceVisitor<Void> {

    /** The shared instance. */
    public static final NopReplaceVisitor INSTANCE = new NopReplaceVisitor();

    /**
     * Private constructor. Only one shared instance.
     */
    private NopReplaceVisitor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Constant<?> visit(
            final @NotNull Constant<?> expr,
            final Void context) {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull FactoryExpression<?> visit(
            final @NotNull FactoryExpression<?> expr,
            final Void context) {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Operation<?> visit(
            final @NotNull Operation<?> expr,
            final Void context) {
        return expr;
    }

    @Override
    public @NotNull ParamExpression<?> visit(
            final @NotNull ParamExpression<?> expr,
            final Void context) {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Path<?> visit(
            final @NotNull Path<?> expr,
            final Void context) {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull SubQueryExpression<?> visit(
            final @NotNull SubQueryExpression<?> expr,
            final Void context) {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull TemplateExpression<?> visit(
            final @NotNull TemplateExpression<?> expr,
            final Void context) {
        return expr;
    }
}
