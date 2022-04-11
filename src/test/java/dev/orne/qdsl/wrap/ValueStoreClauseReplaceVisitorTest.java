package dev.orne.qdsl.wrap;

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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Unit tests for {@code ValueStoreClauseReplaceVisitor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see ValueStoreClauseReplaceVisitor
 */
@Tag("ut")
class ValueStoreClauseReplaceVisitorTest {

    private static final String ENTITY = "entity";
    private static final String PROPERTY_A = "propertyA";
    private static final String PROPERTY_B = "propertyB";
    private static final String PROPERTY_C = "propertyC";

    private static final PathBuilder<Object> ENTITY_PATH =
            new PathBuilder<Object>(Object.class, ENTITY);
    private static final StringPath PROPERTY_A_PATH =
            ENTITY_PATH.getString(PROPERTY_A);
    private static final StringPath PROPERTY_B_PATH =
            ENTITY_PATH.getString(PROPERTY_B);
    private static final StringPath PROPERTY_C_PATH =
            ENTITY_PATH.getString(PROPERTY_C);
    private static final String STR_A =
            "SomeValue";
    private static final String STR_B =
            "OtherValue";
    private static final Expression<String> VALUE_A =
            Expressions.constant(STR_A);
    private static final Expression<String> VALUE_B =
            Expressions.constant(STR_B);

    private static final ValueStoreClause<String> NULL_ASSIGNMENT_A =
            ValueStoreClause.of(PROPERTY_A_PATH, (String) null);
    private static final ValueStoreClause<String> VALUE_A_ASSIGNMENT_A =
            ValueStoreClause.of(PROPERTY_A_PATH, VALUE_A);
    private static final ValueStoreClause<String> VALUE_A_ASSIGNMENT_B =
            ValueStoreClause.of(PROPERTY_B_PATH, VALUE_A);
    private static final ValueStoreClause<String> VALUE_B_ASSIGNMENT_B =
            ValueStoreClause.of(PROPERTY_B_PATH, VALUE_B);
    private static final ValueStoreClause<String> VALUE_B_ASSIGNMENT_C =
            ValueStoreClause.of(PROPERTY_C_PATH, VALUE_B);

    /**
     * Test for {@link ValueStoreClauseReplaceVisitor#visit(ValuesStoreClause, Object)}
     */
    @Test
    void testVisisClausesEqual() {
        final ValuesStoreClause clauses = ValuesStoreClause.of(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        final ValuesStoreClause backup = clauses.clone();
        final ValueStoreClauseReplaceVisitor<?> visitor = spy(ValueStoreClauseReplaceVisitor.class);
        given(visitor.visit(NULL_ASSIGNMENT_A, null)).willReturn(ValuesStoreClause.of(NULL_ASSIGNMENT_A));
        given(visitor.visit(VALUE_A_ASSIGNMENT_B, null)).willReturn(ValuesStoreClause.of(VALUE_A_ASSIGNMENT_B));
        final ValuesStoreClause result = visitor.visit(clauses, null);
        assertSame(clauses, result);
        assertEquals(backup, result);
        then(visitor).should().visit(NULL_ASSIGNMENT_A, null);
        then(visitor).should().visit(VALUE_A_ASSIGNMENT_B, null);
    }

    /**
     * Test for {@link ValueStoreClauseReplaceVisitor#visit(ValuesStoreClause, Object)}
     */
    @Test
    void testVisisClausesModified() {
        final ValuesStoreClause clauses = ValuesStoreClause.of(
                NULL_ASSIGNMENT_A,
                VALUE_A_ASSIGNMENT_B);
        final ValueStoreClauseReplaceVisitor<?> visitor = spy(ValueStoreClauseReplaceVisitor.class);
        given(visitor.visit(NULL_ASSIGNMENT_A, null)).willReturn(ValuesStoreClause.of(VALUE_A_ASSIGNMENT_A));
        given(visitor.visit(VALUE_A_ASSIGNMENT_B, null)).willReturn(ValuesStoreClause.of(VALUE_B_ASSIGNMENT_B, VALUE_B_ASSIGNMENT_C));
        final ValuesStoreClause expected = ValuesStoreClause.of(
                VALUE_A_ASSIGNMENT_A,
                VALUE_B_ASSIGNMENT_B,
                VALUE_B_ASSIGNMENT_C);
        final ValuesStoreClause result = visitor.visit(clauses, null);
        assertNotSame(clauses, result);
        assertEquals(expected, result);
        then(visitor).should().visit(NULL_ASSIGNMENT_A, null);
        then(visitor).should().visit(VALUE_A_ASSIGNMENT_B, null);
    }
}
