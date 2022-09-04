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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;

/**
 * Unit tests for {@code NopReplaceVisitor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @since 0.1
 * @see NopReplaceVisitor
 */
@Tag("ut")
class NopReplaceVisitorTest {

    /**
     * Unit test for {@link NopReplaceVisitor#visit(Constant, Void)}.
     */
    @Test
    void testVisitConstant() {
        final Constant<?> expr = mock(Constant.class);
        final Constant<?> result = NopReplaceVisitor.INSTANCE.visit(expr, null);
        assertSame(expr, result);
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopReplaceVisitor#visit(FactoryExpression, Void)}.
     */
    @Test
    void testVisitFactoryExpression() {
        final FactoryExpression<?> expr = mock(FactoryExpression.class);
        final FactoryExpression<?> result = NopReplaceVisitor.INSTANCE.visit(expr, null);
        assertSame(expr, result);
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopReplaceVisitor#visit(Operation, Void)}.
     */
    @Test
    void testVisitOperation() {
        final Operation<?> expr = mock(Operation.class);
        final Operation<?> result = NopReplaceVisitor.INSTANCE.visit(expr, null);
        assertSame(expr, result);
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopReplaceVisitor#visit(ParamExpression, Void)}.
     */
    @Test
    void testVisitParamExpression() {
        final ParamExpression<?> expr = mock(ParamExpression.class);
        final ParamExpression<?> result = NopReplaceVisitor.INSTANCE.visit(expr, null);
        assertSame(expr, result);
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopReplaceVisitor#visit(Path, Void)}.
     */
    @Test
    void testVisitPath() {
        final Path<?> expr = mock(Path.class);
        final Path<?> result = NopReplaceVisitor.INSTANCE.visit(expr, null);
        assertSame(expr, result);
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopReplaceVisitor#visit(SubQueryExpression, Void)}.
     */
    @Test
    void testVisitSubQueryExpression() {
        final SubQueryExpression<?> expr = mock(SubQueryExpression.class);
        final SubQueryExpression<?> result = NopReplaceVisitor.INSTANCE.visit(expr, null);
        assertSame(expr, result);
        then(expr).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link NopReplaceVisitor#visit(TemplateExpression, Void)}.
     */
    @Test
    void testVisitTemplateExpression() {
        final TemplateExpression<?> expr = mock(TemplateExpression.class);
        final TemplateExpression<?> result = NopReplaceVisitor.INSTANCE.visit(expr, null);
        assertSame(expr, result);
        then(expr).shouldHaveNoInteractions();
    }
}
