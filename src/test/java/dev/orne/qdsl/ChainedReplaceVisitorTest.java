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

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;

/**
 * Unit tests for {@code ChainedReplaceVisitor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-01
 * @since 0.1
 * @see ChainedReplaceVisitor
 */
@Tag("ut")
class ChainedReplaceVisitorTest {

    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_A =
            mock(Visitor.class);
    @SuppressWarnings("unchecked")
    private static final Visitor<Expression<?>, ?> VISITOR_B =
            mock(Visitor.class);

    @Test
    void testEmptyVarargsConstructor() {
        final ChainedReplaceVisitor result = new ChainedReplaceVisitor();
        assertNotNull(result.getVisitors());
        assertEquals(0, result.getVisitors().size());
        assertNotNull(result.toString());
    }

    @Test
    void testVarargsConstructor() {
        assertThrows(IllegalArgumentException.class, ()-> {
            new ChainedReplaceVisitor(VISITOR_A, null);
        });
        final ChainedReplaceVisitor result = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
        assertNotNull(result.toString());
    }

    @Test
    void testCollectionConstructor() {
        assertThrows(NullPointerException.class, ()-> {
            new ChainedReplaceVisitor((Collection<Visitor<Expression<?>, ?>>) null);
        });
        final ChainedReplaceVisitor result = new ChainedReplaceVisitor(
                Arrays.asList(VISITOR_A, VISITOR_B));
        assertNotNull(result.getVisitors());
        assertEquals(2, result.getVisitors().size());
        assertSame(VISITOR_A, result.getVisitors().get(0));
        assertSame(VISITOR_B, result.getVisitors().get(1));
        assertNotNull(result.toString());
    }

    @Test
    void visitConstant() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final Constant<?> initial = mock(Constant.class);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(partialResultA).given(initial).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final Expression<?> result = chained.visit(initial, null);
        assertSame(partialResultB, result);
        then(initial).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitFactoryExpression() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final FactoryExpression<?> initial = mock(FactoryExpression.class);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(partialResultA).given(initial).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final Expression<?> result = chained.visit(initial, null);
        assertSame(partialResultB, result);
        then(initial).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitOperation() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final Operation<?> initial = mock(Operation.class);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(partialResultA).given(initial).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final Expression<?> result = chained.visit(initial, null);
        assertSame(partialResultB, result);
        then(initial).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitParamExpression() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final ParamExpression<?> initial = mock(ParamExpression.class);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(partialResultA).given(initial).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final Expression<?> result = chained.visit(initial, null);
        assertSame(partialResultB, result);
        then(initial).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitPath() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final Path<?> initial = mock(Path.class);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(partialResultA).given(initial).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final Expression<?> result = chained.visit(initial, null);
        assertSame(partialResultB, result);
        then(initial).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitSubQueryExpression() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final SubQueryExpression<?> initial = mock(SubQueryExpression.class);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(partialResultA).given(initial).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final Expression<?> result = chained.visit(initial, null);
        assertSame(partialResultB, result);
        then(initial).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitTemplateExpression() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final TemplateExpression<?> initial = mock(TemplateExpression.class);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(partialResultA).given(initial).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final Expression<?> result = chained.visit(initial, null);
        assertSame(partialResultB, result);
        then(initial).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }
}
