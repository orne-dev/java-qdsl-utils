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

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@code ProjectionType}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see ProjectionType
 */
@Tag("ut")
class ProjectionTypeTest {

    /**
     * Unit test for {@link ProjectionType#codeEquals(Object)}.
     */
    @Test
    void testCodeEquals() {
        assertFalse(ProjectionType.Basic.DEFAULT.codeEquals(null));
        assertTrue(ProjectionType.Basic.DEFAULT.codeEquals(TestType.DEFAULT));
        assertFalse(ProjectionType.Basic.DEFAULT.codeEquals(TestType.REFERENCE));
        assertFalse(ProjectionType.Basic.DEFAULT.codeEquals(TestType.NAMED_REFERENCE));
        assertFalse(ProjectionType.Basic.DEFAULT.codeEquals(TestType.CUSTOM));
        assertTrue(ProjectionType.Basic.DEFAULT.codeEquals(ProjectionType.Basic.DEFAULT));
        assertFalse(ProjectionType.Basic.DEFAULT.codeEquals(ProjectionType.Basic.REFERENCE));
        assertFalse(ProjectionType.Basic.DEFAULT.codeEquals(ProjectionType.Basic.NAMED_REFERENCE));
        assertFalse(ProjectionType.Basic.REFERENCE.codeEquals(null));
        assertFalse(ProjectionType.Basic.REFERENCE.codeEquals(TestType.DEFAULT));
        assertTrue(ProjectionType.Basic.REFERENCE.codeEquals(TestType.REFERENCE));
        assertFalse(ProjectionType.Basic.REFERENCE.codeEquals(TestType.NAMED_REFERENCE));
        assertFalse(ProjectionType.Basic.REFERENCE.codeEquals(TestType.CUSTOM));
        assertFalse(ProjectionType.Basic.REFERENCE.codeEquals(ProjectionType.Basic.DEFAULT));
        assertTrue(ProjectionType.Basic.REFERENCE.codeEquals(ProjectionType.Basic.REFERENCE));
        assertFalse(ProjectionType.Basic.REFERENCE.codeEquals(ProjectionType.Basic.NAMED_REFERENCE));
        assertFalse(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(null));
        assertFalse(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(TestType.DEFAULT));
        assertFalse(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(TestType.REFERENCE));
        assertTrue(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(TestType.NAMED_REFERENCE));
        assertFalse(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(TestType.CUSTOM));
        assertFalse(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(ProjectionType.Basic.DEFAULT));
        assertFalse(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(ProjectionType.Basic.REFERENCE));
        assertTrue(ProjectionType.Basic.NAMED_REFERENCE.codeEquals(ProjectionType.Basic.NAMED_REFERENCE));
    }

    /**
     * Unit test for {@link ProjectionType#codeEquals(Object)}.
     */
    @Test
    void testCodeEqualsCustom() {
        assertFalse(TestType.DEFAULT.codeEquals(null));
        assertTrue(TestType.DEFAULT.codeEquals(TestType.DEFAULT));
        assertFalse(TestType.DEFAULT.codeEquals(TestType.REFERENCE));
        assertFalse(TestType.DEFAULT.codeEquals(TestType.NAMED_REFERENCE));
        assertFalse(TestType.DEFAULT.codeEquals(TestType.CUSTOM));
        assertTrue(TestType.DEFAULT.codeEquals(ProjectionType.Basic.DEFAULT));
        assertFalse(TestType.DEFAULT.codeEquals(ProjectionType.Basic.REFERENCE));
        assertFalse(TestType.DEFAULT.codeEquals(ProjectionType.Basic.NAMED_REFERENCE));
        assertFalse(TestType.REFERENCE.codeEquals(null));
        assertFalse(TestType.REFERENCE.codeEquals(TestType.DEFAULT));
        assertTrue(TestType.REFERENCE.codeEquals(TestType.REFERENCE));
        assertFalse(TestType.REFERENCE.codeEquals(TestType.NAMED_REFERENCE));
        assertFalse(TestType.REFERENCE.codeEquals(TestType.CUSTOM));
        assertFalse(TestType.REFERENCE.codeEquals(ProjectionType.Basic.DEFAULT));
        assertTrue(TestType.REFERENCE.codeEquals(ProjectionType.Basic.REFERENCE));
        assertFalse(TestType.REFERENCE.codeEquals(ProjectionType.Basic.NAMED_REFERENCE));
        assertFalse(TestType.NAMED_REFERENCE.codeEquals(null));
        assertFalse(TestType.NAMED_REFERENCE.codeEquals(TestType.DEFAULT));
        assertFalse(TestType.NAMED_REFERENCE.codeEquals(TestType.REFERENCE));
        assertTrue(TestType.NAMED_REFERENCE.codeEquals(TestType.NAMED_REFERENCE));
        assertFalse(TestType.NAMED_REFERENCE.codeEquals(TestType.CUSTOM));
        assertFalse(TestType.NAMED_REFERENCE.codeEquals(ProjectionType.Basic.DEFAULT));
        assertFalse(TestType.NAMED_REFERENCE.codeEquals(ProjectionType.Basic.REFERENCE));
        assertTrue(TestType.NAMED_REFERENCE.codeEquals(ProjectionType.Basic.NAMED_REFERENCE));
        assertFalse(TestType.CUSTOM.codeEquals(null));
        assertFalse(TestType.CUSTOM.codeEquals(TestType.DEFAULT));
        assertFalse(TestType.CUSTOM.codeEquals(TestType.REFERENCE));
        assertFalse(TestType.CUSTOM.codeEquals(TestType.NAMED_REFERENCE));
        assertTrue(TestType.CUSTOM.codeEquals(TestType.CUSTOM));
        assertFalse(TestType.CUSTOM.codeEquals(ProjectionType.Basic.DEFAULT));
        assertFalse(TestType.CUSTOM.codeEquals(ProjectionType.Basic.REFERENCE));
        assertFalse(TestType.CUSTOM.codeEquals(ProjectionType.Basic.NAMED_REFERENCE));
    }

    /**
     * Unit test for {@link ProjectionType.Basic#fromCode(String)}.
     */
    @Test
    void testFromCodeString() {
        assertNull(ProjectionType.Basic.fromCode((String) null));
        assertSame(
                ProjectionType.Basic.DEFAULT,
                ProjectionType.Basic.fromCode(TestType.DEFAULT.code));
        assertSame(
                ProjectionType.Basic.REFERENCE,
                ProjectionType.Basic.fromCode(TestType.REFERENCE.code));
        assertSame(
                ProjectionType.Basic.NAMED_REFERENCE,
                ProjectionType.Basic.fromCode(TestType.NAMED_REFERENCE.code));
        assertNull(ProjectionType.Basic.fromCode(TestType.CUSTOM.code));
    }

    /**
     * Unit test for {@link ProjectionType.Basic#fromCode(ProjectionType)}.
     */
    @Test
    void testFromCodeType() {
        assertNull(ProjectionType.Basic.fromCode((ProjectionType) null));
        assertSame(
                ProjectionType.Basic.DEFAULT,
                ProjectionType.Basic.fromCode(TestType.DEFAULT));
        assertSame(
                ProjectionType.Basic.REFERENCE,
                ProjectionType.Basic.fromCode(TestType.REFERENCE));
        assertSame(
                ProjectionType.Basic.NAMED_REFERENCE,
                ProjectionType.Basic.fromCode(TestType.NAMED_REFERENCE));
        assertNull(ProjectionType.Basic.fromCode(TestType.CUSTOM));
    }

    static enum TestType
    implements ProjectionType {
        DEFAULT(ProjectionType.Basic.DEFAULT.code),
        REFERENCE(ProjectionType.Basic.REFERENCE.code),
        NAMED_REFERENCE(ProjectionType.Basic.NAMED_REFERENCE.code),
        CUSTOM("custom"),
        ;
        public final String code;
        private TestType(
                final @NotNull String code) {
            this.code = code;
        }
        @Override
        public @NotNull String getCode() {
            return this.code;
        }
    }
}
