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

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

/**
 * Base implementation for {@code Translator} based tests.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @since 0.1
 */
public abstract class AbstractTranslatorTest {

    /**
     * Returns the translator to test.
     * 
     * @return The translator to test
     */
    protected abstract @NotNull Translator getTranslator();

    /**
     * Asserts that translating the specified projection expression results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param expr The expression to translate
     */
    protected void assertProjectionTranslation(
            final Expression<?> expected,
            final @NotNull Expression<?> expr) {
        assertEquals(expected, getTranslator().translateProjection(expr));
    }

    /**
     * Asserts that translating the specified projection expressions results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param exprs The expressions to translate
     */
    protected void assertProjectionsTranslation(
            final Expression<?>[] expected,
            final @NotNull Expression<?>... exprs) {
        assertArrayEquals(expected, getTranslator().translateProjections(exprs));
    }

    /**
     * Asserts that translating the specified predicate expression results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param expr The expression to translate
     */
    protected void assertPredicateTranslation(
            final Predicate expected,
            final @NotNull Predicate expr) {
        assertEquals(expected, getTranslator().translatePredicate(expr));
    }

    /**
     * Asserts that translating the specified predicate expressions results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param exprs The expressions to translate
     */
    protected void assertPredicatesTranslation(
            final Predicate[] expected,
            final @NotNull Predicate... exprs) {
        assertArrayEquals(expected, getTranslator().translatePredicates(exprs));
    }

    /**
     * Asserts that translating the specified query ordering specifier results
     * in the expected result.
     * 
     * @param expected The expected result
     * @param expr The expression to translate
     */
    protected void assertOrderingTranslation(
            final OrderSpecifier<?>[] expected,
            final @NotNull OrderSpecifier<?> expr) {
        assertArrayEquals(expected, getTranslator().translateOrderSpecifier(expr));
    }

    /**
     * Asserts that translating the specified query ordering results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param expr The expressions to translate
     */
    protected void assertOrderingTranslation(
            final OrderSpecifier<?>[] expected,
            final @NotNull OrderSpecifier<?>... exprs) {
        assertArrayEquals(expected, getTranslator().translateOrderSpecifiers(exprs));
    }

    /**
     * Asserts that translating the specified value assignment results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param expr The value assignment to translate
     */
    protected void assertAssigmentTranslation(
            final ValueAssignments expected,
            final @NotNull ValueAssignment<?> expr) {
        assertEquals(expected, getTranslator().translateAssigment(expr));
    }

    /**
     * Asserts that translating the specified value assignments results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param expr The value assignments to translate
     */
    protected void assertAssigmentTranslation(
            final ValueAssignments expected,
            final @NotNull ValueAssignment<?>... exprs) {
        assertEquals(expected, getTranslator().translateAssigments(exprs));
    }
}
