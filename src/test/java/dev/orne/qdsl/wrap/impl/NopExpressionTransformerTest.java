package dev.orne.qdsl.wrap.impl;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code NopExpressionTransformer}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see NopExpressionTransformer
 */
@Tag("ut")
class NopExpressionTransformerTest {

    /**
     * Unit test for {@link ExpressionTransformer#NOP}.
     */
    @Test
    void testSharedInstance() {
        final ExpressionTransformer result = ExpressionTransformer.NOP;
        assertNotNull(result);
        assertInstanceOf(NopExpressionTransformer.class, result);
    }

    /**
     * Unit test for {@link NopExpressionTransformer#NopExpressionTransformer()}.
     */
    @Test
    void testConstructor() {
        assertDoesNotThrow(NopExpressionTransformer::new);
    }

    /**
     * Unit test for {@link NopExpressionTransformer#translateProjection(Expression)}.
     */
    @Test
    void testTranslateProjection() {
        final Expression<?> expr = mock(Expression.class);
        assertSame(expr, ExpressionTransformer.NOP.translateProjection(expr));
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopExpressionTransformer#translateProjections(Expression...)}.
     */
    @Test
    void testTranslateProjections() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
        }
        final Expression<?>[] result = ExpressionTransformer.NOP.translateProjections(exprs);
        assertNotNull(result);
        assertEquals(count, result.length);
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i], result[i]);
            then(exprs[i]).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link NopExpressionTransformer#translatePredicates(Predicate...)}.
     */
    @Test
    void testTranslatePredicates() {
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
        }
        final Predicate[] result = ExpressionTransformer.NOP.translatePredicates(exprs);
        assertNotNull(result);
        assertEquals(count, result.length);
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i], result[i]);
            then(exprs[i]).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link NopExpressionTransformer#translateOrderSpecifiers(OrderSpecifier...)}.
     */
    @Test
    void testTranslateOrderSpecifiers() {
        final int count = RandomUtils.nextInt(2, 10);
        final OrderSpecifier<?>[] exprs = new OrderSpecifier<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(OrderSpecifier.class);
        }
        final OrderSpecifier<?>[] result = ExpressionTransformer.NOP.translateOrderSpecifiers(exprs);
        assertNotNull(result);
        assertEquals(count, result.length);
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i], result[i]);
            then(exprs[i]).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link NopExpressionTransformer#translateGroupByExpressions(Expression...)}.
     */
    @Test
    void testTranslateGroupByExpressions() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
        }
        final Expression<?>[] result = ExpressionTransformer.NOP.translateGroupByExpressions(exprs);
        assertNotNull(result);
        assertEquals(count, result.length);
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i], result[i]);
            then(exprs[i]).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link NopExpressionTransformer#translateStoredValues(StoredValues)}.
     */
    @Test
    void testTranslateStoredValues() {
        final StoredValues assignments = mock(StoredValues.class);
        final StoredValues result = ExpressionTransformer.NOP.translateStoredValues(assignments);
        assertSame(assignments, result);
        then(assignments).shouldHaveNoInteractions();
    }
}
