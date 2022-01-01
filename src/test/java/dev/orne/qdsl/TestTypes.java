package dev.orne.qdsl;

import static org.mockito.BDDMockito.*;

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
        OrderSpecifier<?>[] visit(
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
