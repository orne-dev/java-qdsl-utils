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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Unit tests for {@code Translator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-01
 * @since 0.1
 * @see Translator
 */
@Tag("ut")
class TranslatorTest {

    private static final String PROPERTY_A = "propertyA";
    private static final String PROPERTY_B = "propertyB";

    private static final PathBuilder<Object> BUILDER =
            new PathBuilder<Object>(Object.class, "builder");

    private static final StringPath PROPERTY_A_PATH =
            BUILDER.getString(PROPERTY_A);
    private static final StringPath PROPERTY_B_PATH =
            BUILDER.getString(PROPERTY_B);
    @SuppressWarnings("unchecked")
    private static final Expression<String> EXPR =
            mock(Expression.class);
    @SuppressWarnings("unchecked")
    private static final ValueAssignmentTranslator<String> ASSIGN_TR =
            mock(ValueAssignmentTranslator.class);
    @SuppressWarnings("unchecked")
    private static final ExpressionTranslator<String, String> EXPR_TR =
            mock(ExpressionTranslator.class);

    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_A =
            mock(Visitor.class);
    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_B =
            mock(Visitor.class);
    @Test
    void testNop() {
        final Translator result = Translator.NOP;
        assertNotNull(result);
        assertInstanceOf(NopTranslator.class, result);
    }

    @Test
    void testWith_Empty() {
        final ChainedTranslator result = Translator.with();
        assertNotNull(result.getVisitors());
        assertEquals(0, result.getVisitors().size());
    }

    @Test
    void testWith() {
        assertThrows(IllegalArgumentException.class, ()-> {
            Translator.with(VISITOR_A, null);
        });
        final ChainedTranslator result = Translator.with(
                VISITOR_A, VISITOR_B);
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
    }

    @Test
    void testRenamePath() {
        final SimplePathTranslator<String> result = Translator.renamePath(
                PROPERTY_A_PATH,
                PROPERTY_B_PATH);
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(PROPERTY_B_PATH, result.getTarget());
        final ValueAssignmentTranslator<String> atr = result.getAssignmentTranslator();
        assertNotNull(atr);
        final ValueAssignments atrResult = atr.apply(EXPR);
        assertNotNull(atrResult);
        assertEquals(1, atrResult.size());
        assertEquals(EXPR, atrResult.get(PROPERTY_B_PATH));
    }

    @Test
    void testFromPathToPath() {
        final SimplePathTranslator<String> result = Translator
                .fromPath(PROPERTY_A_PATH)
                .toPath(PROPERTY_B_PATH)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(PROPERTY_B_PATH, result.getTarget());
        final ValueAssignmentTranslator<String> atr = result.getAssignmentTranslator();
        assertNotNull(atr);
        final ValueAssignments atrResult = atr.apply(EXPR);
        assertNotNull(atrResult);
        assertEquals(1, atrResult.size());
        assertEquals(EXPR, atrResult.get(PROPERTY_B_PATH));
    }

    @Test
    void testFromPathToExpressionWithCustomExpressionTranslator() {
        final SimplePathTranslator<String> result = Translator
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingTo(PROPERTY_B_PATH, EXPR_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        final ValueAssignmentTranslator<String> atr = result.getAssignmentTranslator();
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
    void testFromPathToExpressionWithCustomAssignmentTranslator() {
        final SimplePathTranslator<String> result = Translator
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        assertSame(ASSIGN_TR, result.getAssignmentTranslator());
    }
}
