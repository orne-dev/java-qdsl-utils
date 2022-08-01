package dev.orne.qdsl.wrap.impl.transform;

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
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code ExpressionTransformers}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see ExpressionTransformers
 */
@Tag("ut")
class ExpressionTransformersTest {

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
    private static final StoredValuesTransformer ASSIGN_TR =
            mock(StoredValuesTransformer.class);
    @SuppressWarnings("unchecked")
    private static final SimpleExpressionTransformer<String, String> EXPR_TR =
            mock(SimpleExpressionTransformer.class);

    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_A =
            mock(Visitor.class);
    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_B =
            mock(Visitor.class);

    /**
     * Test for {@link ExpressionTransformers#with(Visitor...)}.
     */
    @Test
    void testWith_Empty() {
        final ChainedExpressionTransformer result = ExpressionTransformers.with();
        assertNotNull(result.getVisitors());
        assertEquals(0, result.getVisitors().size());
    }

    /**
     * Test for {@link ExpressionTransformers#with(Visitor...)}.
     */
    @Test
    void testWith() {
        assertThrows(IllegalArgumentException.class, ()-> {
            ExpressionTransformers.with(VISITOR_A, null);
        });
        final ChainedExpressionTransformer result = ExpressionTransformers.with(
                VISITOR_A, VISITOR_B);
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
    }

    /**
     * Test for {@link ExpressionTransformers#renamePath(Path, Path)}.
     */
    @Test
    void testRenamePath() {
        final SimplePathTransformer<String> result = ExpressionTransformers.renamePath(
                PROPERTY_A_PATH,
                PROPERTY_B_PATH);
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(PROPERTY_B_PATH, result.getTarget());
        final StoredValuesTransformer svTransformer = result.getAssignmentTranslator();
        assertNotNull(svTransformer);
        final StoredValues values = StoredValues.with(
                StoredValue.of(PROPERTY_A_PATH, EXPR));
        svTransformer.visit(values, null);
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals(EXPR, values.get(PROPERTY_B_PATH));
    }

    /**
     * Test for {@link ExpressionTransformers#fromPath(Path)}.
     */
    @Test
    void testFromPathToPath() {
        final SimplePathTransformer<String> result = ExpressionTransformers
                .fromPath(PROPERTY_A_PATH)
                .toPath(PROPERTY_B_PATH)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(PROPERTY_B_PATH, result.getTarget());
        final StoredValuesTransformer svTransformer = result.getAssignmentTranslator();
        assertNotNull(svTransformer);
        final StoredValues values = StoredValues.with(
                StoredValue.of(PROPERTY_A_PATH, EXPR));
        svTransformer.visit(values, null);
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals(EXPR, values.get(PROPERTY_B_PATH));
    }

    /**
     * Test for {@link ExpressionTransformers#fromPath(Path)}.
     */
    @Test
    void testFromPathToExpressionWithCustomExpressionTranslator() {
        final SimplePathTransformer<String> result = ExpressionTransformers
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingTo(PROPERTY_B_PATH, EXPR_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        final StoredValuesTransformer svTransformer = result.getAssignmentTranslator();
        assertNotNull(svTransformer);
        final Expression<String> mockRes = Expressions.asSimple("mockExpr");
        willReturn(mockRes).given(EXPR_TR).apply(EXPR);
        final StoredValues values = StoredValues.with(
                StoredValue.of(PROPERTY_A_PATH, EXPR));
        svTransformer.visit(values, null);
        assertNotNull(values);
        assertEquals(1, values.size());
        assertSame(mockRes, values.get(PROPERTY_B_PATH));
        then(EXPR_TR).should().apply(EXPR);
    }

    /**
     * Test for {@link ExpressionTransformers#fromPath(Path)}.
     */
    @Test
    void testFromPathToExpressionWithCustomAssignmentTranslator() {
        final SimplePathTransformer<String> result = ExpressionTransformers
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        assertSame(ASSIGN_TR, result.getAssignmentTranslator());
    }
}
