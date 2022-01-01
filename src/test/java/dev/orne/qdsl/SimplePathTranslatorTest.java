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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;


/**
 * Unit tests for {@code SimplePathTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-12
 * @since 0.1
 * @see SimplePathTranslator
 */
@Tag("ut")
class SimplePathTranslatorTest {

    private static final String ENTITY = "entity";
    private static final String PROPERTY_A = "propertyA";
    private static final String PROPERTY_B = "propertyB";

    private static final PathBuilder<Object> ENTITY_PATH =
            new PathBuilder<Object>(Object.class, ENTITY);
    private static final StringPath PROPERTY_A_PATH =
            ENTITY_PATH.getString(PROPERTY_A);
    private static final StringPath PROPERTY_B_PATH =
            ENTITY_PATH.getString(PROPERTY_B);
    @SuppressWarnings("unchecked")
    private static final Expression<String> EXPR =
            mock(Expression.class);
    @SuppressWarnings("unchecked")
    private static final AssignmentTranslator<String> ASSIGN_TR =
            mock(AssignmentTranslator.class);
    @SuppressWarnings("unchecked")
    private static final ExpressionTranslator<String, String> EXPR_TR =
            mock(ExpressionTranslator.class);

    @Test
    void testConstructor() {
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTranslator<>(null, null, null);
        });
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTranslator<>((Path<String>) null, EXPR, ASSIGN_TR);
        });
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTranslator<>(PROPERTY_A_PATH, null, ASSIGN_TR);
        });
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTranslator<>(PROPERTY_A_PATH, EXPR, null);
        });
        final SimplePathTranslator<String> result =
                new SimplePathTranslator<>(
                        PROPERTY_A_PATH,
                        EXPR,
                        ASSIGN_TR);
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        assertSame(ASSIGN_TR, result.getAssignmentTranslator());
    }

    @Test
    void testBuilderToPath() {
        final SimplePathTranslator<String> result = SimplePathTranslator
                .fromPath(PROPERTY_A_PATH)
                .toPath(PROPERTY_B_PATH)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(PROPERTY_B_PATH, result.getTarget());
        final AssignmentTranslator<String> atr = result.getAssignmentTranslator();
        assertNotNull(atr);
        final ValueAssignments atrResult = atr.apply(EXPR);
        assertNotNull(atrResult);
        assertEquals(1, atrResult.size());
        assertEquals(EXPR, atrResult.get(PROPERTY_B_PATH));
    }

    @Test
    void testBuilderToExpressionWithCustomExpressionTranslator() {
        final SimplePathTranslator<String> result = SimplePathTranslator
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingTo(PROPERTY_B_PATH, EXPR_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        final AssignmentTranslator<String> atr = result.getAssignmentTranslator();
        assertNotNull(atr);
        final Expression<String> mockRes = Expressions.asSimple("mockExpr");
        willReturn(mockRes).given(EXPR_TR).apply(EXPR);
        final ValueAssignments atrResult = atr.apply(EXPR);
        assertNotNull(atrResult);
        assertEquals(1, atrResult.size());
        assertSame(mockRes, atrResult.get(PROPERTY_B_PATH));
        then(EXPR_TR).should().apply(EXPR);
    }

    @Test
    void testBuilderToExpressionWithCustomAssignmentTranslator() {
        final SimplePathTranslator<String> result =
                SimplePathTranslator.fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        assertSame(ASSIGN_TR, result.getAssignmentTranslator());
    }

    @Test
    void testVisitSourcePath() {
        final SimplePathTranslator<String> translator = SimplePathTranslator
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final Expression<?> result = translator.visit(PROPERTY_A_PATH, null);
        assertSame(EXPR, result);
    }

    @Test
    void testVisitOtherPath() {
        final SimplePathTranslator<String> translator = SimplePathTranslator
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final Expression<?> result = translator.visit(PROPERTY_B_PATH, null);
        assertSame(PROPERTY_B_PATH, result);
    }

    @Test
    void testVisitSourceAssignment() {
        final SimplePathTranslator<String> translator = SimplePathTranslator
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final ValueAssignments mockResult = mock(ValueAssignments.class);
        willReturn(mockResult).given(ASSIGN_TR).apply(EXPR);
        final ValueAssignment<?> assign = ValueAssignment.of(PROPERTY_A_PATH, EXPR);
        final ValueAssignments result = translator.visit(assign, null);
        assertSame(mockResult, result);
        then(ASSIGN_TR).should().apply(EXPR);
    }

    @Test
    void testVisitOtherAssignment() {
        final SimplePathTranslator<String> translator = SimplePathTranslator
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final ValueAssignment<?> assign = ValueAssignment.of(PROPERTY_B_PATH, EXPR);
        final ValueAssignments result = translator.visit(assign, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EXPR, result.get(PROPERTY_B_PATH));
    }
}
