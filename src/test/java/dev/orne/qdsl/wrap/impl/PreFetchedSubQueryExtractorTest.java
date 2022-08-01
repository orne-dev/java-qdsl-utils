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
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinType;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamExpressionImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.SubQueryExpressionImpl;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

import dev.orne.qdsl.TestTypes;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code PreFetchedSubQueryExtractor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see PreFetchedSubQueryExtractor
 */
@Tag("ut")
class PreFetchedSubQueryExtractorTest {

    private final static PathBuilder<Object> BUILDER =
            new PathBuilder<>(Object.class, "bean");
    private final static PathBuilder<ResultType> BEANS =
            new PathBuilder<>(ResultType.class, "bean");

    private @Mock Expression<ResultType> expr;
    private @Mock ResultType value1;
    private @Mock ResultType value2;
    private @Mock ResultType value3;
    private List<ResultType> values;
    private PreFetchedSubQueryExpression<ResultType> subquery;

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
        values = Arrays.asList(value1, value2, value3);
        final DefaultQueryMetadata metadata = new DefaultQueryMetadata();
        metadata.addJoin(JoinType.DEFAULT, BEANS);
        metadata.setProjection(BEANS);
        metadata.addWhere(BEANS.getBoolean("enabled").eq(true));
        subquery = new PreFetchedSubQueryExpression<>(metadata, ResultType.class, values);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#INSTANCE}.
     */
    @Test
    void SharedInstance() {
        assertNotNull(PreFetchedSubQueryExtractor.INSTANCE);
        assertTrue(PreFetchedSubQueryExtractor.INSTANCE instanceof PreFetchedSubQueryExtractor);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(Path, Void)}.
     */
    @Test
    void testPath() {
        final Path<?> original = BUILDER.getString("prop");
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertSame(original, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(Constant, Void)}.
     */
    @Test
    void testConstant() {
        final Constant<?> original = (Constant<?>) Expressions.constant("value");
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertSame(original, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(FactoryExpression, Void)}.
     */
    @Test
    void testFactoryExpression() {
        final FactoryExpression<?> original = Projections.tuple(
                BUILDER.getString("propA"),
                BUILDER.getNumber("propB", Long.class));
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertSame(original, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(FactoryExpression, Void)}.
     */
    @Test
    void testFactoryExpressionWithPreFetchedSubQuery() {
        final FactoryExpression<?> original = Projections.tuple(
                BUILDER.getString("propA"),
                subquery);
        final Expression<?> expected = Projections.tuple(
                BUILDER.getString("propA"),
                Expressions.constant(values));
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(Operation, Void)}.
     */
    @Test
    void testOperation() {
        final Operation<?> original = (Operation<?>) BUILDER.getString("propA").substring(4);
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertSame(original, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(Operation, Void)}.
     */
    @Test
    void testOperationWithPreFetchedSubQuery() {
        final Operation<?> original = (Operation<?>) BUILDER.getSimple("propA", ResultType.class)
                .in(subquery);
        final Expression<?> expected = BUILDER.getSimple("propA", ResultType.class)
                .in(values);
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(ParamExpression, Void)}.
     */
    @Test
    void testParamExpression() {
        final ParamExpression<?> original = new ParamExpressionImpl<>(ResultType.class, "param");
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertSame(original, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(TemplateExpression, Void)}.
     */
    @Test
    void testTemplateExpression() {
        final TemplateExpression<?> original = Expressions.booleanTemplate("SOME_TEMPLATE(?)", Expressions.constant(value1));
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertSame(original, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(TemplateExpression, Void)}.
     */
    @Test
    void testTemplateExpressionWithPreFetchedSubQuery() {
        final TemplateExpression<?> original = Expressions.booleanTemplate("SOME_TEMPLATE(?)", subquery);
        final TemplateExpression<?> expected = Expressions.booleanTemplate("SOME_TEMPLATE(?)", Expressions.constant(values));
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(SubQueryExpression, Void)}.
     */
    @Test
    void testSubQuery() {
        final DefaultQueryMetadata metadata = new DefaultQueryMetadata();
        metadata.addJoin(JoinType.DEFAULT, BUILDER);
        metadata.setProjection(BUILDER.getString("proj"));
        metadata.addWhere(BUILDER.getString("prop").in("val1", "val2"));
        final SubQueryExpressionImpl<?> original = new SubQueryExpressionImpl<>(ResultType.class, metadata);
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertSame(original, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(SubQueryExpression, Void)}.
     */
    @Test
    void testPreFetchedSubQuery() {
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(subquery, null);
        final Expression<?> expected = Expressions.constant(values);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExtractor#visit(SubQueryExpression, Void)}.
     */
    @Test
    void testNestedPreFetchedSubQuery() {
        final DefaultQueryMetadata metadata = new DefaultQueryMetadata();
        metadata.addJoin(JoinType.DEFAULT, BUILDER);
        metadata.setProjection(BUILDER.getString("proj"));
        metadata.addWhere(BUILDER.getSimple("propA", ResultType.class).in(subquery));
        final SubQueryExpressionImpl<?> original = new SubQueryExpressionImpl<>(String.class, metadata);
        final DefaultQueryMetadata expectedMetadata = new DefaultQueryMetadata();
        expectedMetadata.addJoin(JoinType.DEFAULT, BUILDER);
        expectedMetadata.setProjection(BUILDER.getString("proj"));
        expectedMetadata.addWhere(BUILDER.getSimple("propA", ResultType.class).in(values));
        final SubQueryExpressionImpl<?> expected = new SubQueryExpressionImpl<>(String.class, expectedMetadata);
        final Expression<?> result = PreFetchedSubQueryExtractor.INSTANCE.visit(original, null);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#extract(Expression)}.
     */
    @Test
    void testExtractExpression() {
        final PreFetchedSubQueryExtractor extractor = PreFetchedSubQueryExtractor.INSTANCE;
        final Expression<?> original = TestTypes.expressionOf(ResultType.class);
        final Expression<?> expected = TestTypes.expressionOf(ResultType.class);
        willReturn(expected).given(original).accept(extractor, null);
        final Expression<?> result = extractor.extract(original);
        assertSame(expected, result);
        then(original).should().accept(extractor, null);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#extract(Expression)}.
     */
    @Test
    void testExtractExpressionIncompatible() {
        final PreFetchedSubQueryExtractor extractor = PreFetchedSubQueryExtractor.INSTANCE;
        final Expression<?> original = TestTypes.expressionOf(ResultType.class);
        final Expression<?> incompatible = TestTypes.expressionOf(TestTypes.UnrelatedType.class);
        willReturn(incompatible).given(original).accept(extractor, null);
        assertThrows(ExpressionTransformationException.class, () -> {
            extractor.extract(original);
        });
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#extract(Expression...)}.
     */
    @Test
    void testExtractExpressions() {
        final PreFetchedSubQueryExtractor extractor = PreFetchedSubQueryExtractor.INSTANCE;
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] texprs = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
            texprs[i] = mock(Expression.class);
            willReturn(texprs[i]).given(exprs[i]).accept(extractor, null);
        }
        final Expression<?>[] result = extractor.extract(exprs);
        assertArrayEquals(texprs, result);
        for (int i = 0; i < count; i++) {
            then(exprs[i]).should().accept(extractor, null);
        }
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#extract(Predicate...)}.
     */
    @Test
    void testExtractPredicates() {
        final PreFetchedSubQueryExtractor extractor = PreFetchedSubQueryExtractor.INSTANCE;
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        final Predicate[] texprs = new Predicate[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
            texprs[i] = mock(Predicate.class);
            willReturn(texprs[i]).given(exprs[i]).accept(extractor, null);
        }
        final Predicate[] result = extractor.extract(exprs);
        assertArrayEquals(texprs, result);
        for (int i = 0; i < count; i++) {
            then(exprs[i]).should().accept(extractor, null);
        }
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#extract(StoredValue)}.
     */
    @Test
    void testExtractStoredValue() {
        final PreFetchedSubQueryExtractor extractor = PreFetchedSubQueryExtractor.INSTANCE;
        final Path<TestTypes.SimpleType> path =
                TestTypes.pathOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> oexpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final Expression<TestTypes.SimpleType> texpr =
                TestTypes.expressionOf(TestTypes.SimpleType.class);
        final StoredValue<?> value = StoredValue.of(path, oexpr);
        willReturn(texpr).given(oexpr).accept(extractor, null);
        final StoredValue<?> result = extractor.extract(value);
        assertNotNull(result);
        assertSame(path, result.getPath());
        assertSame(texpr, result.getValue());
        then(oexpr).should().accept(extractor, null);
    }

    /**
     * Unit test for {@link PreFetchedSubQueryExpression#extract(StoredValues)}.
     */
    @Test
    void testExtractStoredValues() {
        final PreFetchedSubQueryExtractor extractor = PreFetchedSubQueryExtractor.INSTANCE;
        final int count = RandomUtils.nextInt(2, 10);
        final Path<?>[] paths = new Path<?>[count];
        final Expression<?>[] oexprs = new Expression<?>[count];
        final Expression<?>[] texprs = new Expression<?>[count];
        final StoredValues values = new StoredValues(count);
        for (int i = 0; i < count; i++) {
            paths[i] = TestTypes.pathOf(TestTypes.SimpleType.class);
            oexprs[i] = TestTypes.expressionOf(TestTypes.SimpleType.class);
            texprs[i] = TestTypes.expressionOf(TestTypes.SimpleType.class);
            values.add(StoredValue.ofUntyped(paths[i], oexprs[i]));
            willReturn(texprs[i]).given(oexprs[i]).accept(extractor, null);
        }
        final StoredValues result = extractor.extract(values);
        assertNotNull(result);
        assertEquals(count, result.size());
        for (int i = 0; i < count; i++) {
            assertTrue(result.contains(paths[i]));
            assertEquals(texprs[i], result.get(paths[i]));
            then(oexprs[i]).should().accept(extractor, null);
        }
    }

    private static interface ResultType
    extends Serializable {}
}
