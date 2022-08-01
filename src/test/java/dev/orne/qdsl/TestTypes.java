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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;

import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValueReplaceVisitor;
import dev.orne.qdsl.wrap.StoredValues;
import dev.orne.qdsl.wrap.StoredValuesReplaceVisitor;
import dev.orne.qdsl.wrap.impl.transform.StoredValuesTransformer;

public class TestTypes {

    private static final Class<?>[] TYPES = new Class<?>[] {
        Boolean.class,
        String.class,
        Integer.class,
        Long.class,
        BigInteger.class,
        BigDecimal.class,
        SimpleType.class,
        ComparableType.class
    };

    public static interface SimpleType {}

    public static interface UnrelatedType {}

    public static interface ComparableType
    extends Comparable<ComparableType> {}

    public static interface OrderSpecifierReplacer
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

    public static interface StoredValuesTransformerVisitor
    extends Visitor<Expression<?>, Void>,
            StoredValuesTransformer {
        /**
         * {@inheritDoc}
         * To help Mockito resolve generic types.
         */
        @Override
        Void visit(
                @NotNull StoredValues expr,
                Void context);
    }

    public static interface StoredValuesReplacer
    extends Visitor<Expression<?>, Void>,
            StoredValuesReplaceVisitor<Void> {
        /**
         * {@inheritDoc}
         * To help Mockito resolve generic types.
         */
        @Override
        StoredValues visit(
                @NotNull StoredValues expr,
                Void context);
    }

    public static interface StoredValueReplacer
    extends Visitor<Expression<?>, Void>,
            StoredValueReplaceVisitor<Void> {
        /**
         * {@inheritDoc}
         * To help Mockito resolve generic types.
         */
        @Override
        StoredValues visit(
                @NotNull StoredValue<?> expr,
                Void context);
    }

    public static Class<?> randomPathType() {
        return TYPES[RandomUtils.nextInt(0, TYPES.length)];
    }

    public static Path<?> randomPath() {
        return pathOf(randomPathType());
    }

    public static Expression<?> randomExpression() {
        return expressionOf(randomPathType());
    }

    public static <T> T randomValue(
            final Class<T> type) {
        final Object result;
        if (Boolean.class.equals(type)) {
            result = RandomUtils.nextBoolean();
        } else if (String.class.equals(type)) {
            result = RandomStringUtils.random(10);
        } else if (Integer.class.equals(type)) {
            result = RandomUtils.nextInt();
        } else if (Long.class.equals(type)) {
            result = RandomUtils.nextLong();
        } else if (BigInteger.class.equals(type)) {
            result = BigInteger.valueOf(RandomUtils.nextLong());
        } else if (BigDecimal.class.equals(type)) {
            result = BigDecimal.valueOf(RandomUtils.nextLong(), RandomUtils.nextInt());
        } else if (SimpleType.class.equals(type)) {
            result = mock(SimpleType.class);
        } else if (UnrelatedType.class.equals(type)) {
            result = mock(UnrelatedType.class);
        } else if (ComparableType.class.equals(type)) {
            result = mock(ComparableType.class);
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
        return type.cast(result);
    }

    public static <T> Path<T> pathOf(
            final Class<T> type) {
        @SuppressWarnings("unchecked")
        final Path<T> expr = mock(Path.class);
        willReturn(type).given(expr).getType();
        return expr;
    }

    public static <T> Expression<T> expressionOf(
            final Class<T> type) {
        @SuppressWarnings("unchecked")
        final Expression<T> expr = mock(Expression.class);
        willReturn(type).given(expr).getType();
        return expr;
    }

    public static OrderSpecifier<ComparableType> randomOrderSpecifier() {
        return randomOrderSpecifier(expressionOf(ComparableType.class));
    }

    public static <T extends Comparable<?>> OrderSpecifier<T> randomOrderSpecifier(
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

    public static <T extends Enum<T>> T randomEnum(
            final Class<T> type) {
        final T[] constants = type.getEnumConstants();
        final int index = RandomUtils.nextInt(0, constants.length);
        return constants[index];
    }

    public static StoredValue<?> randomStoredValue() {
        return randomStoredValue(randomPath());
    }

    public static <T> StoredValue<T> randomStoredValue(
            final Path<T> path) {
        return StoredValue.of(
                path,
                expressionOf(path.getType()));
    }

    public static StoredValues randomStoredValues() {
        final int count = RandomUtils.nextInt(1, 10);
        final StoredValues result = new StoredValues(count);
        for (int i = 0; i < count; i++) {
            result.add(randomStoredValue());
        }
        return result;
    }
}
