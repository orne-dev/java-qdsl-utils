package dev.orne.qdsl;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021 Orne Developments
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;

/**
 * QueryDSL expression visitor that allows translate both expressions and value
 * assignments.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @since 0.1
 */
public class TranslateVisitor
extends ReplaceVisitor<TranslateVisitor.Context> {

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull Constant<?> expr,
            final Context context) {
        return super.visit(expr, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull FactoryExpression<?> expr,
            final Context context) {
        return super.visit(expr, context);
    }

    @Override
    public @NotNull Expression<?> visit(
            final @NotNull Operation<?> expr,
            final Context context) {
        return super.visit(expr, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull ParamExpression<?> expr,
            final Context context) {
        return super.visit(expr, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull Path<?> expr,
            final Context context) {
        return super.visit(expr, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull SubQueryExpression<?> expr,
            final Context context) {
        return super.visit(expr, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull TemplateExpression<?> expr,
            final Context context) {
        return super.visit(expr, context);
    }

    /**
     * Visits the specified value assignments, replacing if required.
     * 
     * @param vexprs The value assignments
     * @param context The visit context
     * @return The resulting value assignments
     */
    public @NotNull ValueAssigment<?>[] visit(
            final @NotNull ValueAssigment<?>[] vexprs,
            final Context context) {
        Validate.notNull(vexprs);
        Validate.noNullElements(vexprs);
        final List<ValueAssigment<?>> result = new ArrayList<>(vexprs.length);
        for (final ValueAssigment<?> vexpr : vexprs) {
            result.addAll(Arrays.asList(vexpr.accept(this, context)));
        }
        return ValueAssigment.array(result);
    }

    /**
     * Visits the specified value assignment, replacing if required.
     * 
     * @param vexpr The value assignment
     * @param context The visit context
     * @return The resulting value assignments
     */
    public @NotNull ValueAssigment<?>[] visit(
            final @NotNull ValueAssigment<?> vexpr,
            final Context context) {
        return ValueAssigment.array(Validate.notNull(vexpr));
    }

    /**
     * The translation enumeration contexts.
     * 
     * @since 0.1
     */
    public static enum Context {
        /** Expression is part of a query projection. */
        PROJECTION,
        /** Expression is part of a query from directive. */
        FROM,
        /** Expression is part of a predicate. */
        PREDICATE,
        /** Expression is part of a operation argument. */
        OPERATION_ARG,
        /** Expression is part of a query ordering directive. */
        ORDER,
        /** Expression is part of a query group by directive. */
        GROUP,
        /** Expression is part of a query having directive. */
        HAVING,
        /** Expression is part of a store operation (insert, update). */
        STORE,
        ;
    }
}
