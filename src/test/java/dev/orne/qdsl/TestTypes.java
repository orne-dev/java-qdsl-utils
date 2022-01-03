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

import static org.mockito.BDDMockito.*;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;

public class TestTypes {

    static interface SimpleType {}

    static interface UnrelatedType {}

    static interface ComparableType
    extends Comparable<ComparableType> {}

    static interface OrderSpecifierReplacer
    extends Visitor<Expression<?>, Void>,
            OrderSpecifierReplaceVisitor<Void> {
        /**
         * {@inheritDoc}
         * To help Mockito resolve generic types.
         */
        @Override
        List<OrderSpecifier<?>> visit(
                @NotNull OrderSpecifier<?> expr,
                Void context);
    }

    static interface ValueAssignmentReplacer
    extends Visitor<Expression<?>, Void>,
            ValueAssignmentReplaceVisitor<Void> {
        /**
         * {@inheritDoc}
         * To help Mockito resolve generic types.
         */
        @Override
        ValueAssignments visit(
                @NotNull ValueAssignment<?> expr,
                Void context);
    }

    static <T> Path<T> pathOf(
            final Class<T> type) {
        @SuppressWarnings("unchecked")
        final Path<T> expr = mock(Path.class);
        willReturn(type).given(expr).getType();
        return expr;
    }

    static <T> Expression<T> expressionOf(
            final Class<T> type) {
        @SuppressWarnings("unchecked")
        final Expression<T> expr = mock(Expression.class);
        willReturn(type).given(expr).getType();
        return expr;
    }

    static OrderSpecifier<ComparableType> randomOrderSpecifier() {
        return randomOrderSpecifier(expressionOf(ComparableType.class));
    }

    static <T extends Comparable<?>> OrderSpecifier<T> randomOrderSpecifier(
            final Expression<T> expr) {
        @SuppressWarnings("unchecked")
        final OrderSpecifier<T> result = mock(OrderSpecifier.class);
        final Order order = randomEnum(Order.class);
        final OrderSpecifier.NullHandling nullHandling =
                randomEnum(OrderSpecifier.NullHandling.class);
        willReturn(expr).given(result).getTarget();
        willReturn(order).given(result).getOrder();
        willReturn(nullHandling).given(result).getNullHandling();
        return result;
    }

    static <T extends Enum<T>> T randomEnum(
            final Class<T> type) {
        final T[] constants = type.getEnumConstants();
        final int index = RandomUtils.nextInt(0, constants.length);
        return constants[index];
    }

    static ValueAssignment<SimpleType> randomValueAssignment() {
        return randomValueAssignment(pathOf(SimpleType.class));
    }

    static <T> ValueAssignment<T> randomValueAssignment(
            final Path<T> path) {
        return new ValueAssignment<>(
                path,
                expressionOf(path.getType()));
    }
}
