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

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.types.Predicate;

/**
 * Unit tests for {@code WrappedDeleteClause}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see WrappedDeleteClause
 */
@Tag("ut")
class WrappedDeleteClauseTest
extends WrappedClauseTest {

    protected @Mock DeleteClause<?> delegate;

    /**
     * Unit test for {@link WrappedDeleteClause#WrappedDeleteClause(DeleteClause)}.
     */
    @Test
    void testConstructor() {
        final WrappedDeleteClause clause = new WrappedDeleteClause(delegate);
        assertSame(delegate, clause.getDelegate());
        assertSame(ExpressionTransformer.NOP, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedDeleteClause(null);
        });
    }

    /**
     * Unit test for {@link WrappedDeleteClause#WrappedDeleteClause(DeleteClause, ExpressionTransformer)}.
     */
    @Test
    void testTransformerConstructor() {
        final WrappedDeleteClause clause = new WrappedDeleteClause(delegate, transformer);
        assertSame(delegate, clause.getDelegate());
        assertSame(transformer, clause.getTransformer());
        assertThrows(NullPointerException.class, () -> {
            new WrappedDeleteClause(null, transformer);
        });
        assertThrows(NullPointerException.class, () -> {
            new WrappedDeleteClause(null, null);
        });
        final WrappedDeleteClause nullTransformerClause = new WrappedDeleteClause(delegate, null);
        assertSame(delegate, nullTransformerClause.getDelegate());
        assertSame(ExpressionTransformer.NOP, nullTransformerClause.getTransformer());
    }

    @Override
    protected @NotNull WrappedDeleteClause getClause() {
        return new WrappedDeleteClause(delegate, transformer);
    }

    /**
     * Unit test for {@link WrappedDeleteClause#where(Predicate...)}.
     */
    @Test
    void testWhere() {
        final WrappedDeleteClause clause = spy(getClause());
        final int count = RandomUtils.nextInt(2, 10);
        final Predicate[] exprs = new Predicate[count];
        final Predicate[] eexprs = new Predicate[count];
        final Predicate[] texprs = new Predicate[count];
        for (int i = 0; i < count; i++) {
            exprs[i] = mock(Predicate.class);
            eexprs[i] = mock(Predicate.class);
            texprs[i] = mock(Predicate.class);
        }
        willReturn(eexprs).given(clause).extractPreFetched(exprs);
        given(transformer.translatePredicates(eexprs)).willReturn(texprs);
        final WrappedDeleteClause result = clause.where(exprs);
        assertSame(clause, result);
        then(clause).should().extractPreFetched(exprs);
        then(transformer).should().translatePredicates(eexprs);
        then(delegate).should().where(texprs);
    }

    /**
     * Unit test for {@link WrappedDeleteClause#execute()}.
     */
    @Test
    void testExecute() {
        final WrappedDeleteClause clause = getClause();
        final long expected = RandomUtils.nextLong();
        given(delegate.execute()).willReturn(expected);
        final long result = clause.execute();
        assertEquals(expected, result);
        then(delegate).should().execute();
    }
}
