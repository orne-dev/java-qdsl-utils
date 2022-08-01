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

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.orne.qdsl.wrap.ClauseProviderNotFoundException;
import dev.orne.qdsl.wrap.EntityPathNotSupportedException;
import dev.orne.qdsl.wrap.impl.ExpressionTransformationException;

/**
 * Unit tests for library exceptions.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-03
 * @since 0.1
 * @see ExpressionTransformationException
 * @see EntityPathNotSupportedException
 * @see ClauseProviderNotFoundException
 */
@Tag("ut")
class ExceptionsTest {

    /** Message for exception testing. */
    private static final String TEST_MESSAGE = "Test message";
    /** Cause for exception testing. */
    private static final Throwable TEST_CAUSE = new Exception();

    /**
     * Test for {@link ExpressionTransformationException}.
     */
    @Test
    void testQueryTranslationException() {
        assertMessageException(new ExpressionTransformationException(TEST_MESSAGE));
        assertCauseException(new ExpressionTransformationException(TEST_CAUSE));
        assertFullException(new ExpressionTransformationException(TEST_MESSAGE, TEST_CAUSE));
    }

    /**
     * Test for {@link EntityPathNotSupportedException}.
     */
    @Test
    void testEntityPathNotSupportedException() {
        assertMessageException(new EntityPathNotSupportedException(TEST_MESSAGE));
        assertCauseException(new EntityPathNotSupportedException(TEST_CAUSE));
        assertFullException(new EntityPathNotSupportedException(TEST_MESSAGE, TEST_CAUSE));
        final EntityPathNotSupportedException classException =
                new EntityPathNotSupportedException(TestClass.class);
        assertNotNull(classException);
        assertNotNull(classException.getMessage());
        assertNull(classException.getCause());
    }

    /**
     * Test for {@link ClauseProviderNotFoundException}.
     */
    @Test
    void testClauseProviderNotFoundException() {
        assertMessageException(new ClauseProviderNotFoundException(TEST_MESSAGE));
        assertCauseException(new ClauseProviderNotFoundException(TEST_CAUSE));
        assertFullException(new ClauseProviderNotFoundException(TEST_MESSAGE, TEST_CAUSE));
    }

    /**
     * Asserts that exception has message but no cause.
     * 
     * @param exception The exception to test
     */
    private void assertMessageException(
            final @NotNull Exception exception) {
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Asserts that exception has cause but no message.
     * 
     * @param exception The exception to test
     */
    private void assertCauseException(
            final @NotNull Exception exception) {
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertEquals(TEST_CAUSE.toString(), exception.getMessage());
        assertNotNull(exception.getCause());
        assertSame(TEST_CAUSE, exception.getCause());
    }

    /**
     * Asserts that exception has message and cause.
     * 
     * @param exception The exception to test
     */
    private void assertFullException(
            final @NotNull Exception exception) {
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNotNull(exception.getCause());
        assertSame(TEST_CAUSE, exception.getCause());
    }

    private static class TestClass {}
}
