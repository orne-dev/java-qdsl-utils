package dev.orne.qdsl.wrap;

/*-
 * #%L
 * Orne Querydsl Utils
 * %%
 * Copyright (C) 2021-2022 Orne Developments
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

import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Unit tests for {@code ValueStoreClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see ValueStoreClause
 */
@Tag("ut")
class ValueStoreClauseTest {

    private static final String ENTITY = "entity";
    private static final String PROPERTY_A = "propertyA";
    private static final String PROPERTY_B = "propertyB";

    private static final PathBuilder<Object> ENTITY_PATH =
            new PathBuilder<Object>(Object.class, ENTITY);
    private static final StringPath PROPERTY_A_PATH =
            ENTITY_PATH.getString(PROPERTY_A);
    private static final StringPath PROPERTY_B_PATH =
            ENTITY_PATH.getString(PROPERTY_B);

    @Test
    void testOfNullValue() {
        final ValueStoreClause<String> result =
                ValueStoreClause.of(PROPERTY_A_PATH, (String) null);
        assertNotNull(result);
        assertSame(PROPERTY_A_PATH, result.getPath());
        assertNull(result.getValue());
        assertNotNull(result.toString());
    }

    @Test
    void testOfValue() {
        final String value = "Test value";
        final ValueStoreClause<String> result =
                ValueStoreClause.of(PROPERTY_A_PATH, value);
        assertNotNull(result);
        assertSame(PROPERTY_A_PATH, result.getPath());
        assertNotNull(result.getValue());
        assertInstanceOf(Constant.class, result.getValue());
        assertSame(value, ((Constant<?>) result.getValue()).getConstant());
        assertNotNull(result.toString());
    }

    @Test
    void testOfNullExpression() {
        final Expression<String> value = null;
        final ValueStoreClause<String> result =
                ValueStoreClause.of(PROPERTY_A_PATH, value);
        assertNotNull(result);
        assertSame(PROPERTY_A_PATH, result.getPath());
        assertNull(result.getValue());
        assertNotNull(result.toString());
    }

    @Test
    void testOfExpression() {
        final Expression<String> value = Expressions.asString("some");
        final ValueStoreClause<String> result =
                ValueStoreClause.of(PROPERTY_A_PATH, value);
        assertNotNull(result);
        assertSame(PROPERTY_A_PATH, result.getPath());
        assertSame(value, result.getValue());
        assertNotNull(result.toString());
    }

    @Test
    void testOfUntypedNullExpression() {
        final Expression<?> value = null;
        final ValueStoreClause<String> result =
                ValueStoreClause.ofUntyped(PROPERTY_A_PATH, value);
        assertNotNull(result);
        assertSame(PROPERTY_A_PATH, result.getPath());
        assertNull(result.getValue());
        assertNotNull(result.toString());
    }

    @Test
    void testOfUntypedExpression() {
        final Expression<String> value = Expressions.asString("some");
        final Expression<?> uvalue = (Expression<?>) value;
        final ValueStoreClause<String> result =
                ValueStoreClause.ofUntyped(PROPERTY_A_PATH, uvalue);
        assertNotNull(result);
        assertSame(PROPERTY_A_PATH, result.getPath());
        assertSame(value, result.getValue());
        assertNotNull(result.toString());
    }

    @Test
    void testOfUntypedInvalidExpression() {
        final Expression<Integer> value = Expressions.asNumber(10);
        final Expression<?> uvalue = (Expression<?>) value;
        assertThrows(IllegalArgumentException.class, () -> {
            ValueStoreClause.ofUntyped(PROPERTY_A_PATH, uvalue);
        });
    }

    @Test
    void testAccept() {
        final ValueStoreClause<?> assignment = spy(ValueStoreClause.of(PROPERTY_A_PATH, "someValue"));
        final ValueStoreClauseVisitor<?, ?> visitor = mock(ValueStoreClauseVisitor.class);
        final Object expected = new Object();
        willReturn(expected).given(visitor).visit(assignment, null);
        final Object result = assignment.accept(visitor, null);
        then(visitor).should().visit(assignment, null);
        assertSame(expected, result);
    }

    @Test
    void testApply() {
        final Expression<String> value = Expressions.constant("Test value");
        final ValueStoreClause<String> assignment =
                ValueStoreClause.of(PROPERTY_A_PATH, value);
        final StoreClause<?> clause = mock(StoreClause.class);
        willReturn(clause).given(clause).set(PROPERTY_A_PATH, value);
        final StoreClause<?> result = assignment.apply(clause);
        then(clause).should().set(PROPERTY_A_PATH, value);
        assertSame(clause, result);
    }

    @Test
    void testApplyNull() {
        final ValueStoreClause<String> assignment =
                ValueStoreClause.of(PROPERTY_A_PATH, (String) null);
        final StoreClause<?> clause = mock(StoreClause.class);
        willReturn(clause).given(clause).setNull(PROPERTY_A_PATH);
        final StoreClause<?> result = assignment.apply(clause);
        then(clause).should().setNull(PROPERTY_A_PATH);
        assertSame(clause, result);
    }

    @Test
    void testEqualsAndHashCode() {
        final String value = "Test value";
        Path<String> altPropAPath = new PathBuilder<String>(String.class, ENTITY)
                .getSimple(PROPERTY_A, String.class);
        final ValueStoreClause<String> resultA =
                ValueStoreClause.of(PROPERTY_A_PATH, value);
        final ValueStoreClause<String> resultB =
                ValueStoreClause.of(altPropAPath, value);
        final ValueStoreClause<String> resultC =
                ValueStoreClause.of(PROPERTY_B_PATH, value);
        final ValueStoreClause<String> resultD =
                ValueStoreClause.of(altPropAPath, (String) null);
        final ValueStoreClause<String> resultE =
                ValueStoreClause.of(altPropAPath, Expressions.constant(value));
        final ValueStoreClause<String> resultF =
                ValueStoreClause.of(altPropAPath,
                        Expressions.asString(value).toLowerCase());
        assertFalse(resultA.equals(null));
        assertTrue(resultA.equals(resultA));
        assertFalse(resultA.equals(value));
        assertEquals(resultA, resultB);
        assertEquals(resultA.hashCode(), resultB.hashCode());
        assertNotEquals(resultA, resultC);
        assertNotEquals(resultA.hashCode(), resultC.hashCode());
        assertNotEquals(resultA, resultD);
        assertNotEquals(resultA.hashCode(), resultD.hashCode());
        assertEquals(resultA, resultE);
        assertEquals(resultA.hashCode(), resultE.hashCode());
        assertNotEquals(resultA, resultF);
        assertNotEquals(resultA.hashCode(), resultF.hashCode());
    }
}
