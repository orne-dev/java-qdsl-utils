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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;

/**
 * QueryDSL expression visitor that allows translate both expressions and value
 * assignments.
 * <p>
 * Delegates in nested visitors, in order.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @since 0.1
 */
public class DelegatedTranslateVisitor
extends TranslateVisitor {

    /** The delegated visitors. */
    private final List<TranslateVisitor> visitors;

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated visitors
     */
    @SafeVarargs
    public DelegatedTranslateVisitor(
            final TranslateVisitor... visitors) {
        this(Arrays.asList(Validate.notNull(visitors)));
    }

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated visitors
     */
    public DelegatedTranslateVisitor(
            final Collection<TranslateVisitor> visitors) {
        super();
        this.visitors = new ArrayList<>(Validate.notNull(visitors));
        Validate.noNullElements(visitors);
    }

    /**
     * Returns the delegated visitors.
     * 
     * @return The delegated visitors
     */
    protected List<TranslateVisitor> getVisitors() {
        return Collections.unmodifiableList(this.visitors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Constant<?> expr,
            final Context context) {
        Expression<?> result = expr;
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.accept(visitor, context);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final FactoryExpression<?> expr,
            final Context context) {
        Expression<?> result = expr;
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.accept(visitor, context);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Operation<?> expr,
            final Context context) {
        Expression<?> result = expr;
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.accept(visitor, context);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final ParamExpression<?> expr,
            final Context context) {
        Expression<?> result = expr;
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.accept(visitor, context);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Path<?> expr,
            final Context context) {
        Expression<?> result = expr;
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.accept(visitor, context);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final SubQueryExpression<?> expr,
            final Context context) {
        Expression<?> result = expr;
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.accept(visitor, context);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final TemplateExpression<?> expr,
            final Context context) {
        Expression<?> result = expr;
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.accept(visitor, context);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderSpecifier<?>[] visit(
            final @NotNull OrderSpecifier<?> order,
            final Context context) {
        OrderSpecifier<?>[] result = new OrderSpecifier<?>[] { order };
        for (final TranslateVisitor visitor : this.visitors) {
            result = Arrays.asList(result)
                    .parallelStream()
                    .map(e -> visitor.visit(e, context))
                    .flatMap(r -> Arrays.asList(r).stream())
                    .toArray(OrderSpecifier<?>[]::new);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssigment<?>[] visit(
            final @NotNull ValueAssigment<?> vexpr,
            final Context context) {
        List<ValueAssigment<?>> result = Arrays.asList(vexpr);
        for (final TranslateVisitor visitor : this.visitors) {
            result = result.parallelStream()
                .map((p) -> visitor.visit(p, context))
                .flatMap((p) -> Stream.of(p))
                .collect(Collectors.toList());
        }
        return ValueAssigment.array(result);
    }
}
