package dev.orne.qdsl;

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
            final ValueAssigment<?>[] expected,
            final @NotNull ValueAssigment<?> expr) {
        assertArrayEquals(expected, getTranslator().translateAssigment(expr));
    }

    /**
     * Asserts that translating the specified value assignments results in
     * the expected result.
     * 
     * @param expected The expected result
     * @param expr The value assignments to translate
     */
    protected void assertAssigmentTranslation(
            final ValueAssigment<?>[] expected,
            final @NotNull ValueAssigment<?>... exprs) {
        assertArrayEquals(expected, getTranslator().translateAssigments(exprs));
    }
}
