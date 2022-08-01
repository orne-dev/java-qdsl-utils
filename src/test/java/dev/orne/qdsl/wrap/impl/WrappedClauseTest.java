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
import static org.mockito.Mockito.mock;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;

import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code AbstractWrappedClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see AbstractWrappedClause
 */
@Tag("ut")
class WrappedClauseTest {

    protected @Mock ExpressionTransformer transformer;

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    protected @NotNull WrappedClause<?> getClause() {
        return new TestWrappedClause(transformer);
    }

    /**
     * Unit test for {@link WrappedClause#self()}.
     */
    @Test
    void testSelf() {
        final WrappedClause<?> clause = getClause();
        final WrappedClause<?> result = clause.self();
        assertSame(clause, result);
    }

    /**
     * Unit test for {@link WrappedClause#getTransformer()}.
     */
    @Test
    void testGetTransformer() {
        final WrappedClause<?> clause = getClause();
        final ExpressionTransformer result = clause.getTransformer();
        assertSame(transformer, result);
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClause#getPreFetchedExtractor()}.
     */
    @Test
    void testGetPreFetchedExtractor() {
        final WrappedClause<?> clause = getClause();
        final PreFetchedSubQueryExtractor result = clause.getPreFetchedExtractor();
        assertSame(PreFetchedSubQueryExtractor.INSTANCE, result);
    }

    /**
     * Unit test for {@link WrappedClause#extractPreFetched(Expression)}.
     */
    @Test
    void testGetExtractPreFetchedExpression() {
        final Expression<?> expr = mock(Expression.class);
        final Expression<?> expected = mock(Expression.class);
        final PreFetchedSubQueryExtractor extractor = mock(PreFetchedSubQueryExtractor.class);
        final WrappedClause<?> clause = spy(getClause());
        given(clause.getPreFetchedExtractor()).willReturn(extractor);
        willReturn(expected).given(extractor).extract(expr);
        final Expression<?> result = clause.extractPreFetched(expr);
        assertSame(expected, result);
        then(extractor).should().extract(expr);
        then(extractor).shouldHaveNoMoreInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClause#extractPreFetched(Expression...)}.
     */
    @Test
    void testGetExtractPreFetchedExpressions() {
        final int count = RandomUtils.nextInt(2, 10);
        final Expression<?>[] exprs = new Expression<?>[count];
        final Expression<?>[] expected = new Expression<?>[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Expression.class);
            expected[i] = mock(Expression.class);
        }
        final PreFetchedSubQueryExtractor extractor = mock(PreFetchedSubQueryExtractor.class);
        final WrappedClause<?> clause = spy(getClause());
        given(clause.getPreFetchedExtractor()).willReturn(extractor);
        given(extractor.extract(exprs)).willReturn(expected);
        final Expression<?>[] result = clause.extractPreFetched(exprs);
        assertSame(expected, result);
        then(extractor).should().extract(exprs);
        then(extractor).shouldHaveNoMoreInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClause#extractPreFetched(Predicate...)}.
     */
    @Test
    void testGetExtractPreFetchedPredicates() {
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        final Predicate[] expected = new Predicate[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
            expected[i] = mock(Predicate.class);
        }
        final PreFetchedSubQueryExtractor extractor = mock(PreFetchedSubQueryExtractor.class);
        final WrappedClause<?> clause = spy(getClause());
        given(clause.getPreFetchedExtractor()).willReturn(extractor);
        given(extractor.extract(exprs)).willReturn(expected);
        final Expression<?>[] result = clause.extractPreFetched(exprs);
        assertSame(expected, result);
        then(extractor).should().extract(exprs);
        then(extractor).shouldHaveNoMoreInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link WrappedClause#extractPreFetched(StoredValues)}.
     */
    @Test
    void testGetExtractPreFetchedStoredValues() {
        final StoredValues exprs = mock(StoredValues.class);
        final StoredValues expected = mock(StoredValues.class);
        final PreFetchedSubQueryExtractor extractor = mock(PreFetchedSubQueryExtractor.class);
        final WrappedClause<?> clause = spy(getClause());
        given(clause.getPreFetchedExtractor()).willReturn(extractor);
        given(extractor.extract(exprs)).willReturn(expected);
        final StoredValues result = clause.extractPreFetched(exprs);
        assertSame(expected, result);
        then(extractor).should().extract(exprs);
        then(extractor).shouldHaveNoMoreInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    private class TestWrappedClause
    extends WrappedClause<TestWrappedClause> {
        public TestWrappedClause(ExpressionTransformer transformer) {
            super(transformer);
        }
    }
}
