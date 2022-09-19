package dev.orne.qdsl.wrap.impl.transform;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code SimplePathTransformer}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-12
 * @since 0.1
 * @see SimplePathTransformer
 */
@Tag("ut")
class SimplePathTransformerTest {

    private static final String ENTITY = "entity";
    private static final String PROPERTY_A = "propertyA";
    private static final String PROPERTY_B = "propertyB";

    private static final PathBuilder<Object> ENTITY_PATH =
            new PathBuilder<Object>(Object.class, ENTITY);
    private static final StringPath PROPERTY_A_PATH =
            ENTITY_PATH.getString(PROPERTY_A);
    private static final StringPath PROPERTY_B_PATH =
            ENTITY_PATH.getString(PROPERTY_B);
    private @Mock Expression<String> EXPR;
    private @Mock StoredValuesTransformer ASSIGN_TR;
    private @Mock SimpleExpressionTransformer<String, String> EXPR_TR;

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    @Test
    void testConstructor() {
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTransformer<>(null, null, null);
        });
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTransformer<>((Path<String>) null, EXPR, ASSIGN_TR);
        });
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTransformer<>(PROPERTY_A_PATH, null, ASSIGN_TR);
        });
        assertThrows(NullPointerException.class, ()-> {
            new SimplePathTransformer<>(PROPERTY_A_PATH, EXPR, null);
        });
        final SimplePathTransformer<String> result =
                new SimplePathTransformer<>(
                        PROPERTY_A_PATH,
                        EXPR,
                        ASSIGN_TR);
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        assertSame(ASSIGN_TR, result.getAssignmentTranslator());
        assertNotNull(result.toString());
    }

    @Test
    void testBuilderToPath() {
        final SimplePathTransformer<String> result = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toPath(PROPERTY_B_PATH)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(PROPERTY_B_PATH, result.getTarget());
        final StoredValuesTransformer strsf = result.getAssignmentTranslator();
        assertNotNull(strsf);
        final StoredValues values = StoredValues.with(
                StoredValue.of(PROPERTY_A_PATH, EXPR));
        strsf.visit(values, null);
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals(EXPR, values.get(PROPERTY_B_PATH));
    }

    @Test
    void testBuilderToPathWithDisallowedStorage() {
        final SimplePathTransformer<String> result = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toPath(PROPERTY_B_PATH)
                .withDisallowedStorage()
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(PROPERTY_B_PATH, result.getTarget());
        assertNull(result.getAssignmentTranslator());
    }

    @Test
    void testBuilderToExpressionWithCustomExpressionTranslator() {
        final SimplePathTransformer<String> result = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingTo(PROPERTY_B_PATH, EXPR_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        final StoredValuesTransformer strsf = result.getAssignmentTranslator();
        assertNotNull(strsf);
        final Expression<String> mockRes = Expressions.asSimple("mockExpr");
        willReturn(mockRes).given(EXPR_TR).apply(EXPR);
        final StoredValues values = StoredValues.with(
                StoredValue.of(PROPERTY_A_PATH, EXPR));
        strsf.visit(values, null);
        assertNotNull(values);
        assertEquals(1, values.size());
        assertSame(mockRes, values.get(PROPERTY_B_PATH));
        then(EXPR_TR).should().apply(EXPR);
    }

    @Test
    void testBuilderToExpressionWithCustomAssignmentTranslator() {
        final SimplePathTransformer<String> result =
                SimplePathTransformer.fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        assertSame(ASSIGN_TR, result.getAssignmentTranslator());
    }

    @Test
    void testBuilderToExpressionWithDisallowedStorage() {
        final SimplePathTransformer<String> result =
                SimplePathTransformer.fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .withDisallowedStorage()
                .build();
        assertSame(PROPERTY_A_PATH, result.getSource());
        assertSame(EXPR, result.getTarget());
        assertNull(result.getAssignmentTranslator());
    }

    @Test
    void testVisitSourcePath() {
        final SimplePathTransformer<String> translator = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final Expression<?> result = translator.visit(PROPERTY_A_PATH, null);
        assertSame(EXPR, result);
    }

    @Test
    void testVisitOtherPath() {
        final SimplePathTransformer<String> translator = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final Expression<?> result = translator.visit(PROPERTY_B_PATH, null);
        assertSame(PROPERTY_B_PATH, result);
    }

    @Test
    void testVisitSourceAssignment() {
        final SimplePathTransformer<String> translator = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final StoredValues values = spy(StoredValues.with(
                StoredValue.of(PROPERTY_A_PATH, EXPR)));
        final StoredValues result = translator.visit(values, null);
        assertSame(values, result);
        then(ASSIGN_TR).should().visit(values, null);
    }

    @Test
    void testVisitSourceAssignmentWithDisallowedStorage() {
        final SimplePathTransformer<String> translator = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .withDisallowedStorage()
                .build();
        final StoredValues values = spy(StoredValues.with(
                StoredValue.of(PROPERTY_A_PATH, EXPR)));
        assertThrows(UnsupportedOperationException.class, () -> {
            translator.visit(values, null);
        });
    }

    @Test
    void testVisitOtherAssignment() {
        final SimplePathTransformer<String> translator = SimplePathTransformer
                .fromPath(PROPERTY_A_PATH)
                .toExpression(EXPR)
                .storingWith(ASSIGN_TR)
                .build();
        final StoredValue<?> assign = StoredValue.of(PROPERTY_B_PATH, EXPR);
        final StoredValues values = StoredValues.with(assign);
        final StoredValues result = translator.visit(values, null);
        assertSame(values, result);
        assertEquals(1, result.size());
        assertEquals(EXPR, result.get(PROPERTY_B_PATH));
        then(ASSIGN_TR).shouldHaveNoInteractions();
    }
}
