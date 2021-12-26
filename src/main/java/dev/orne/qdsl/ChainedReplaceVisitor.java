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
import com.querydsl.core.types.Visitor;

/**
 * QueryDSL expression replace visitor that allows translate vanilla
 * expressions, order specifiers and value assignments.
 * <p>
 * Delegates in nested visitors, in order.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-12
 * @since 0.1
 */
public class ChainedReplaceVisitor
implements Visitor<Expression<?>, Void>,
        OrderSpecifierVisitor<OrderSpecifier<?>[], Void>,
        ValueAssignmentVisitor<ValueAssignments, Void> {

    /** The delegated visitors. */
    private final List<Visitor<Expression<?>, ?>> visitors;

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated visitors
     */
    @SafeVarargs
    public ChainedReplaceVisitor(
            final Visitor<Expression<?>, ?>... visitors) {
        this(Arrays.asList(Validate.notNull(visitors)));
    }

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated visitors
     */
    public ChainedReplaceVisitor(
            final Collection<Visitor<Expression<?>, ?>> visitors) {
        super();
        this.visitors = new ArrayList<>(Validate.notNull(visitors));
        Validate.noNullElements(visitors);
    }

    /**
     * Returns the delegated visitors.
     * 
     * @return The delegated visitors
     */
    protected List<Visitor<Expression<?>, ?>> getVisitors() {
        return Collections.unmodifiableList(this.visitors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Constant<?> expr,
            final Void context) {
        Expression<?> result = expr;
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final FactoryExpression<?> expr,
            final Void context) {
        Expression<?> result = expr;
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Operation<?> expr,
            final Void context) {
        Expression<?> result = expr;
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final ParamExpression<?> expr,
            final Void context) {
        Expression<?> result = expr;
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Path<?> expr,
            final Void context) {
        Expression<?> result = expr;
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final SubQueryExpression<?> expr,
            final Void context) {
        Expression<?> result = expr;
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final TemplateExpression<?> expr,
            final Void context) {
        Expression<?> result = expr;
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            result = result.accept(visitor, null);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderSpecifier<?>[] visit(
            final @NotNull OrderSpecifier<?> order,
            final Void context) {
        OrderSpecifier<?>[] result = new OrderSpecifier<?>[] { order };
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            if (visitor instanceof OrderSpecifierReplaceVisitor) {
                final OrderSpecifierReplaceVisitor<?> ovisitor =
                        (OrderSpecifierReplaceVisitor<?>) visitor;
                result = Arrays.asList(result)
                        .parallelStream()
                        .map(e -> ovisitor.visit(e, null))
                        .flatMap(r -> Arrays.asList(r).stream())
                        .toArray(OrderSpecifier<?>[]::new);
            } else {
                result = Arrays.asList(result)
                        .parallelStream()
                        .map(p -> OrderSpecifierReplaceVisitor.fromComponents(p, visitor))
                        .flatMap(r -> Arrays.asList(r).stream())
                        .toArray(OrderSpecifier<?>[]::new);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssignments visit(
            final @NotNull ValueAssignment<?> vexpr,
            final Void context) {
        ValueAssignments result = ValueAssignments.of(vexpr);
        for (final Visitor<Expression<?>, ?> visitor : this.visitors) {
            if (visitor instanceof ValueAssignmentReplaceVisitor) {
                final ValueAssignmentReplaceVisitor<?> vvisitor =
                        (ValueAssignmentReplaceVisitor<?>) visitor;
                result = result.parallelStream()
                        .map(p -> vvisitor.visit(p, null))
                        .collect(
                                ValueAssignments::new,
                                ValueAssignments::addAll,
                                ValueAssignments::addAll);
            } else {
                result = result.parallelStream()
                        .map(p -> AssignmentTranslator.translateFromComponents(p, visitor))
                        .collect(
                                ValueAssignments::new,
                                ValueAssignments::addAll,
                                ValueAssignments::addAll);
            }
        }
        return result;
    }
}
