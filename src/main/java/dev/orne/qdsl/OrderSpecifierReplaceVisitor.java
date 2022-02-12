package dev.orne.qdsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;

/**
 * {@code OrderSpecifierReplaceVisitor} defines a visitor signature for
 * {@link OrderSpecifier} instances that can replace the visited specifier
 * with the returned specifiers.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-12
 * @param <C> Context type
 * @since 0.1
 */
public interface OrderSpecifierReplaceVisitor<C>
extends OrderSpecifierVisitor<List<OrderSpecifier<?>>, C> {

    /**
     * Tries to translate the order specifier translating its target expression
     * with the specified replace visitor.
     * 
     * @param from The source order specifier
     * @param visitor The replace visitor to use
     * @return The resulting order specifier
     */
    static List<OrderSpecifier<?>> fromComponents(
            final OrderSpecifier<?> from,
            final Visitor<Expression<?>, ?> visitor) {
        OrderSpecifier<?> result = from;
        final Expression<? extends Comparable<?>> newTarget =
                OrderSpecifierReplaceVisitor.asComparable(
                        from.getTarget().accept(visitor, null));
        if (newTarget == null) {
            return Collections.emptyList();
        }
        if (!from.getTarget().equals(newTarget)) {
            result = OrderSpecifierReplaceVisitor.resultFrom(
                    from,
                    newTarget);
        }
        return Collections.singletonList(result);
    }

    /**
     * Casts the untyped expression to an expression of a comparable type.
     * If the source expression is not of a comparable type {@code null} is
     * returned.
     * 
     * @param expr The source expression
     * @return The expression of comparable type, or {@code null} if the source
     * expression is not of a comparable type
     */
    @SuppressWarnings("unchecked")
    static Expression<? extends Comparable<?>> asComparable(
            final Expression<?> expr) {
        if (expr != null && Comparable.class.isAssignableFrom(expr.getType())) {
            return (Expression<? extends Comparable<?>>) expr;
        } else {
            return null;
        }
    }

    /**
     * Casts the untyped expression to an expression of a comparable type.
     * If the source expression is not of a comparable type {@code null} is
     * returned.
     * 
     * @param expr The source expression
     * @return The expression of comparable type, or {@code null} if the source
     * expression is not of a comparable type
     */
    @SuppressWarnings("unchecked")
    static @NotNull Expression<? extends Comparable<?>> asNonNullComparable(
            final @NotNull Expression<?> expr) {
        Validate.notNull(expr);
        Validate.isAssignableFrom(Comparable.class, expr.getType());
        return (Expression<? extends Comparable<?>>) expr;
    }

    /**
     * Creates a new typed order specifier with the specified target based on
     * the specified base instance.
     * 
     * @param <T> The target expression type
     * @param template The base order specifier
     * @param target The target expression
     * @return The created order specifier
     */
    static <T extends Comparable<?>> @NotNull OrderSpecifier<T> resultFrom(
            final @NotNull OrderSpecifier<?> template,
            final @NotNull Expression<T> target) {
        return createResult(
                target,
                template.getOrder(),
                template.getNullHandling());
    }

    /**
     * Creates a new typed order specifier with the specified target and order
     * based on the specified base instance.
     * 
     * @param <T> The target expression type
     * @param template The base order specifier
     * @param target The target expression
     * @param order The ordering direction
     * @return The created order specifier
     */
    static <T extends Comparable<?>> @NotNull OrderSpecifier<T> resultFrom(
            final @NotNull OrderSpecifier<?> template,
            final @NotNull Expression<T> target,
            final @NotNull Order order) {
        return createResult(
                target,
                order,
                template.getNullHandling());
    }

    /**
     * Creates a list of order specifier for the comparable elements of the
     * specified tuple.
     * 
     * @param template The base order specifier
     * @param target The target tuple
     * @return The created order specifier list
     */
    static @NotNull List<OrderSpecifier<?>> resultFrom(
            final @NotNull OrderSpecifier<?> template,
            final @NotNull Tuple target) {
        final List<OrderSpecifier<?>> result = new ArrayList<>(target.size());
        for (final Object elem : target.toArray()) {
            if (elem instanceof Expression &&
                    Comparable.class.isAssignableFrom(((Expression<?>) elem).getType())) {
                result.add(resultFrom(template, asNonNullComparable((Expression<?>) elem)));
            } else if (elem instanceof Comparable) {
                result.add(resultFrom(template, asNonNullComparable(Expressions.constant(elem))));
            }
        }
        return result;
    }

    /**
     * Creates a list of order specifiers for the comparable elements of the
     * specified tuple with the specified order.
     * 
     * @param template The base order specifier
     * @param target The target tuple
     * @param order The ordering direction
     * @return The created order specifier list
     */
    static @NotNull List<OrderSpecifier<?>> resultFrom(
            final @NotNull OrderSpecifier<?> template,
            final @NotNull Tuple target,
            final @NotNull Order order) {
        final List<OrderSpecifier<?>> result = new ArrayList<>(target.size());
        for (final Object elem : target.toArray()) {
            if (elem instanceof Expression &&
                    Comparable.class.isAssignableFrom(((Expression<?>) elem).getType())) {
                result.add(resultFrom(template, asNonNullComparable((Expression<?>) elem), order));
            } else if (elem instanceof Comparable) {
                result.add(resultFrom(template, asNonNullComparable(Expressions.constant(elem)), order));
            }
        }
        return result;
    }

    /**
     * Creates a new typed order specifier.
     * 
     * @param <T> The target expression type
     * @param target The target expression
     * @param order The ordering direction
     * @param nullHandling The behavior for order of null values
     * @return The created order specifier
     */
    static <T extends Comparable<?>> OrderSpecifier<T> createResult(
            final @NotNull Expression<T> target,
            final @NotNull Order order,
            final OrderSpecifier.NullHandling nullHandling) {
        return new OrderSpecifier<>(
                order,
                target,
                ObjectUtils.defaultIfNull(nullHandling, OrderSpecifier.NullHandling.Default));
    }
}
