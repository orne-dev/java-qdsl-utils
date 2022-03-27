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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Unit tests for {@code NopTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-03
 * @since 0.1
 * @see NopTranslator
 */
@Tag("ut")
class NopTranslatorTest {

    private static final PathBuilder<Object> ENTITY_PATH =
            new PathBuilder<Object>(Object.class, "entity");

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    /**
     * Unit test for {@link NopTranslator#NopTranslator()}.
     */
    @Test
    void testConstructor() {
        assertDoesNotThrow(NopTranslator::new);
    }

    /**
     * Unit test for {@link NopTranslator#translateProjection(Expression)}.
     */
    @Test
    void testTranslateProjection() {
        final Expression<?> expr = mock(Expression.class);
        assertSame(expr, Translator.NOP.translateProjection(expr));
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopTranslator#translateProjections(Expression...)}.
     */
    @Test
    void testTranslateProjections() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
        }
        final Expression<?>[] result = Translator.NOP.translateProjections(exprs);
        assertNotNull(result);
        assertEquals(count, result.length);
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i], result[i]);
            then(exprs[i]).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link NopTranslator#translatePredicate(Predicate)}.
     */
    @Test
    void testTranslatePredicate() {
        final Predicate expr = mock(Predicate.class);
        assertSame(expr, Translator.NOP.translatePredicate(expr));
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopTranslator#translatePredicates(Predicate...)}.
     */
    @Test
    void testTranslatePredicates() {
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
        }
        final Predicate[] result = Translator.NOP.translatePredicates(exprs);
        assertNotNull(result);
        assertEquals(count, result.length);
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i], result[i]);
            then(exprs[i]).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link NopTranslator#translateOrderSpecifier(OrderSpecifier)}.
     */
    @Test
    void testTranslateOrderSpecifier() {
        final OrderSpecifier<?> expr = mock(OrderSpecifier.class);
        final List<OrderSpecifier<?>> result = Translator.NOP.translateOrderSpecifier(expr);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(expr, result.get(0));
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopTranslator#translateOrderSpecifiers(OrderSpecifier...)}.
     */
    @Test
    void testTranslateOrderSpecifiers() {
        final int count = RandomUtils.nextInt(2, 10);
        final OrderSpecifier<?>[] exprs = new OrderSpecifier<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(OrderSpecifier.class);
        }
        final OrderSpecifier<?>[] result = Translator.NOP.translateOrderSpecifiers(exprs);
        assertNotNull(result);
        assertEquals(count, result.length);
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i], result[i]);
            then(exprs[i]).shouldHaveNoInteractions();
        }
    }

    /**
     * Unit test for {@link NopTranslator#translateAssigment(ValueAssignment)}.
     */
    @Test
    void testTranslateAssigment() {
        final Path<String> path = ENTITY_PATH.getString("path");
        final Expression<String> value = Expressions.asString("value");
        final ValueAssignment<?> expr = ValueAssignment.of(path, value);
        final ValueAssignments result = Translator.NOP.translateAssigment(expr);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(value, result.get(path));
    }

    /**
     * Unit test for {@link NopTranslator#translateAssigments(ValueAssignment...)}.
     */
    @Test
    void testTranslateAssigmentArray() {
        final int count = RandomUtils.nextInt(2, 10);
        final ValueAssignment<?>[] exprs = new ValueAssignment<?>[count];
        for (int i = 0; i < count; i++) {
            final Path<String> path = ENTITY_PATH.getString("path" + i);
            final Expression<String> value = Expressions.asString("value" + i);
            final ValueAssignment<?> expr = ValueAssignment.of(path, value);
            exprs[i] = expr;
        }
        final ValueAssignments result = Translator.NOP.translateAssigments(exprs);
        assertNotNull(result);
        assertEquals(count, result.size());
        for (int i = 0; i < count; i++) {
            assertSame(exprs[i].getValue(), result.get(exprs[i].getPath()));
        }
    }

    /**
     * Unit test for {@link NopTranslator#translateAssigments(ValueAssignments)}.
     */
    @Test
    void testTranslateAssigments() {
        final ValueAssignments assignments = mock(ValueAssignments.class);
        final ValueAssignments result = Translator.NOP.translateAssigments(assignments);
        assertSame(assignments, result);
        then(assignments).shouldHaveNoInteractions();
    }
}
