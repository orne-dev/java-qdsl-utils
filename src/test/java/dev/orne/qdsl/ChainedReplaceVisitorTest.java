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
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Unit tests for {@code ChainedReplaceVisitor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-01
 * @since 0.1
 * @see ChainedReplaceVisitor
 */
@Tag("ut")
public class ChainedReplaceVisitorTest {

    private static final PathBuilder<Object> BUILDER =
            new PathBuilder<Object>(Object.class, "builder");

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

    @Test
    void visitOrderSpecifier_Simple() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final Expression<TestTypes.ComparableType> initialExpr =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> initial =
                TestTypes.randomOrderSpecifier(initialExpr);
        final Expression<?> partialResultA = mock(Expression.class);
        final Expression<?> partialResultB = mock(Expression.class);
        willReturn(TestTypes.ComparableType.class).given(partialResultA).getType();
        willReturn(TestTypes.ComparableType.class).given(partialResultB).getType();
        willReturn(partialResultA).given(initialExpr).accept(VISITOR_A, null);
        willReturn(partialResultB).given(partialResultA).accept(VISITOR_B, null);
        final List<OrderSpecifier<?>> result = chained.visit(initial, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(partialResultB, result.get(0).getTarget());
        assertSame(initial.getOrder(), result.get(0).getOrder());
        assertSame(initial.getNullHandling(), result.get(0).getNullHandling());
        then(initialExpr).should().accept(VISITOR_A, null);
        then(partialResultA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitOrderSpecifier_OrderSpecifierReplaceVisitor() {
        final TestTypes.OrderSpecifierReplacer visitorA =
                mock(TestTypes.OrderSpecifierReplacer.class);
        final TestTypes.OrderSpecifierReplacer visitorB = mock(
                TestTypes.OrderSpecifierReplacer.class);
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                visitorA, visitorB);
        final OrderSpecifier<?> initial =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultA1 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultA2 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB11 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB12 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB21 =
                TestTypes.randomOrderSpecifier();
        final OrderSpecifier<?> partialResultB22 =
                TestTypes.randomOrderSpecifier();
        willReturn(Arrays.asList(
            partialResultA1, partialResultA2
        )).given(visitorA).visit(initial, null);
        willReturn(Arrays.asList(
            partialResultB11, partialResultB12
        )).given(visitorB).visit(partialResultA1, null);
        willReturn(Arrays.asList(
            partialResultB21, partialResultB22
        )).given(visitorB).visit(partialResultA2, null);
        final List<OrderSpecifier<?>> result = chained.visit(initial, null);
        assertNotNull(result);
        assertEquals(4, result.size());
        assertSame(partialResultB11, result.get(0));
        assertSame(partialResultB12, result.get(1));
        assertSame(partialResultB21, result.get(2));
        assertSame(partialResultB22, result.get(3));
        then(visitorA).should().visit(initial, null);
        then(visitorB).should().visit(partialResultA1, null);
        then(visitorB).should().visit(partialResultA2, null);
    }

    @Test
    void visitOrderSpecifier_Mixed() {
        final TestTypes.OrderSpecifierReplacer visitorA =
                mock(TestTypes.OrderSpecifierReplacer.class);
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                visitorA, VISITOR_B);
        final OrderSpecifier<?> initial =
                TestTypes.randomOrderSpecifier();
        final Expression<TestTypes.ComparableType> partialExprA1 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Expression<TestTypes.ComparableType> partialExprA2 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final OrderSpecifier<?> partialResultA1 =
                TestTypes.randomOrderSpecifier(partialExprA1);
        final OrderSpecifier<?> partialResultA2 =
                TestTypes.randomOrderSpecifier(partialExprA2);
        final Expression<TestTypes.ComparableType> partialExprB1 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        final Expression<TestTypes.ComparableType> partialExprB2 =
                TestTypes.expressionOf(TestTypes.ComparableType.class);
        willReturn(Arrays.asList(
            partialResultA1, partialResultA2
        )).given(visitorA).visit(initial, null);
        willReturn(partialExprB1).given(partialExprA1).accept(VISITOR_B, null);
        willReturn(partialExprB2).given(partialExprA2).accept(VISITOR_B, null);
        final List<OrderSpecifier<?>> result = chained.visit(initial, null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(partialExprB1, result.get(0).getTarget());
        assertSame(partialResultA1.getOrder(), result.get(0).getOrder());
        assertSame(partialResultA1.getNullHandling(), result.get(0).getNullHandling());
        assertSame(partialExprB2, result.get(1).getTarget());
        assertSame(partialResultA2.getOrder(), result.get(1).getOrder());
        assertSame(partialResultA2.getNullHandling(), result.get(1).getNullHandling());
        then(visitorA).should().visit(initial, null);
        then(partialResultA1.getTarget()).should().accept(VISITOR_B, null);
        then(partialResultA2.getTarget()).should().accept(VISITOR_B, null);
    }

    @Test
    void visitValueAssignment_Simple() {
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                VISITOR_A, VISITOR_B);
        final Path<TestTypes.SimpleType> initialPath =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> initialValue =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final ValueAssignment<?> initial = ValueAssignment.of(initialPath, initialValue);
        final Path<TestTypes.SimpleType> partialPathA =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> partialValueA =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final Path<TestTypes.SimpleType> partialPathB =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> partialValueB =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(partialPathA).given(initialPath).accept(VISITOR_A, null);
        willReturn(partialValueA).given(initialValue).accept(VISITOR_A, null);
        willReturn(partialPathB).given(partialPathA).accept(VISITOR_B, null);
        willReturn(partialValueB).given(partialValueA).accept(VISITOR_B, null);
        final ValueAssignments result = chained.visit(initial, null);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(partialValueB, result.get(partialPathB));
        then(initialPath).should().accept(VISITOR_A, null);
        then(initialValue).should().accept(VISITOR_A, null);
        then(partialPathA).should().accept(VISITOR_B, null);
        then(partialValueA).should().accept(VISITOR_B, null);
    }

    @Test
    void visitValueAssignment_AssigmentTranslator() {
        final TestTypes.ValueAssignmentReplacer visitorA =
                mock(TestTypes.ValueAssignmentReplacer.class);
        final TestTypes.ValueAssignmentReplacer visitorB = mock(
                TestTypes.ValueAssignmentReplacer.class);
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                visitorA, visitorB);
        final ValueAssignment<?> initial = ValueAssignment.of(
                BUILDER.get("initial", TestTypes.SimpleType.class),
                BUILDER.get("initialValue", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultA1 = ValueAssignment.of(
                BUILDER.get("partialA1", TestTypes.SimpleType.class),
                BUILDER.get("initialValueA1", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultA2 = ValueAssignment.of(
                BUILDER.get("partialA2", TestTypes.SimpleType.class),
                BUILDER.get("initialValueA2", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultB11 = ValueAssignment.of(
                BUILDER.get("partialB11", TestTypes.SimpleType.class),
                BUILDER.get("initialValueB11", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultB12 = ValueAssignment.of(
                BUILDER.get("partialB12", TestTypes.SimpleType.class),
                BUILDER.get("initialValueB12", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultB21 = ValueAssignment.of(
                BUILDER.get("partialB21", TestTypes.SimpleType.class),
                BUILDER.get("initialValueB21", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultB22 = ValueAssignment.of(
                BUILDER.get("partialB22", TestTypes.SimpleType.class),
                BUILDER.get("initialValueB22", TestTypes.SimpleType.class));
        willReturn(ValueAssignments.of(
                partialResultA1,
                partialResultA2
        )).given(visitorA).visit(initial, null);
        willReturn(ValueAssignments.of(
                partialResultB11,
                partialResultB12
        )).given(visitorB).visit(partialResultA1, null);
        willReturn(ValueAssignments.of(
                partialResultB21,
                partialResultB22
        )).given(visitorB).visit(partialResultA2, null);
        final ValueAssignments result = chained.visit(initial, null);
        assertNotNull(result);
        assertEquals(4, result.size());
        assertSame(partialResultB11.getValue(), result.get(partialResultB11.getPath()));
        assertSame(partialResultB12.getValue(), result.get(partialResultB12.getPath()));
        assertSame(partialResultB21.getValue(), result.get(partialResultB21.getPath()));
        assertSame(partialResultB22.getValue(), result.get(partialResultB22.getPath()));
        then(visitorA).should().visit(initial, null);
        then(visitorB).should().visit(partialResultA1, null);
        then(visitorB).should().visit(partialResultA2, null);
    }

    @Test
    void visitValueAssignment_Mixed() {
        final TestTypes.ValueAssignmentReplacer visitorA =
                mock(TestTypes.ValueAssignmentReplacer.class);
        final ChainedReplaceVisitor chained = new ChainedReplaceVisitor(
                visitorA, VISITOR_B);
        final ValueAssignment<?> initial = ValueAssignment.of(
                BUILDER.get("initial", TestTypes.SimpleType.class),
                BUILDER.get("initialValue", TestTypes.SimpleType.class));
        final Path<TestTypes.SimpleType> partialPathA1 =
                spy(BUILDER.get("partialA1", TestTypes.SimpleType.class));
        final Path<TestTypes.SimpleType> partialValueA1 =
                spy(BUILDER.get("initialValueA1", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultA1 = ValueAssignment.of(
                partialPathA1,
                partialValueA1);
        final Path<TestTypes.SimpleType> partialPathA2 =
                spy(BUILDER.get("partialA2", TestTypes.SimpleType.class));
        final Path<TestTypes.SimpleType> partialValueA2 =
                spy(BUILDER.get("initialValueA2", TestTypes.SimpleType.class));
        final ValueAssignment<?> partialResultA2 = ValueAssignment.of(
                partialPathA2,
                partialValueA2);
        final ValueAssignments partialA = ValueAssignments.of(
                partialResultA1,
                partialResultA2
        );
        final Path<TestTypes.SimpleType> partialPathB1 =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> partialValueB1 =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final Path<TestTypes.SimpleType> partialPathB2 =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> partialValueB2 =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        willReturn(partialA).given(visitorA).visit(initial, null);
        willReturn(partialPathB1).given(VISITOR_B).visit(partialPathA1, null);
        willReturn(partialValueB1).given(VISITOR_B).visit(partialValueA1, null);
        willReturn(partialPathB2).given(VISITOR_B).visit(partialPathA2, null);
        willReturn(partialValueB2).given(VISITOR_B).visit(partialValueA2, null);
        final ValueAssignments result = chained.visit(initial, null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(partialValueB1, result.get(partialPathB1));
        assertSame(partialValueB2, result.get(partialPathB2));
        then(visitorA).should().visit(initial, null);
        then(VISITOR_B).should().visit(partialPathA1, null);
        then(VISITOR_B).should().visit(partialValueA1, null);
        then(VISITOR_B).should().visit(partialPathA2, null);
        then(VISITOR_B).should().visit(partialValueA2, null);
    }
}
