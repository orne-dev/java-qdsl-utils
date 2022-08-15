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

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import dev.orne.qdsl.OrderSpecifierReplaceVisitor;
import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.impl.transform.StoredValuesTransformer;

/**
 * Unit tests for {@code EntityAliasReplacer}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-04
 * @since 0.1
 * @see EntityAliasReplacer
 */
@Tag("ut")
class EntityAliasReplacerTest {

    /**
     * Unit test for {@link EntityAliasReplacer#EntityAliasReplacer(EntityPath, EntityPath)}.
     */
    @Test
    void testConstructor() {
        final QEntity source = new QEntity("source");
        final QEntity target = new QEntity("target");
        final QEntity2 wrongTarget = new QEntity2("target");
        assertThrows(NullPointerException.class, () -> {
            new EntityAliasReplacer(null, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new EntityAliasReplacer(null, target);
        });
        assertThrows(NullPointerException.class, () -> {
            new EntityAliasReplacer(source, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAliasReplacer(source, wrongTarget);
        });
        final EntityAliasReplacer result = new EntityAliasReplacer(source, target);
        assertSame(source, result.getSource());
        assertSame(target, result.getTarget());
    }

    /**
     * Unit test for {@link EntityAliasReplacer#apply(Expression)}.
     */
    @Test
    void testApply() {
        final QEntity source = new QEntity("source");
        final QEntity target = new QEntity("target");
        final EntityAliasReplacer replacer = new EntityAliasReplacer(source, target);
        assertNull(replacer.apply(null));
        final Expression<?> expr = mock(Expression.class);
        final Expression<?> expected = mock(expr.getClass());
        willReturn(expected).given(expr).accept(replacer, null);
        final Expression<?> result = replacer.apply(expr);
        assertSame(expected, result);
        then(expr).should().accept(replacer, null);
    }

    /**
     * Unit test for {@link EntityAliasReplacer#visit(Path, Void)}
     * for {@code FactoryExpression}.
     */
    @Test
    void testApplyProjection() {
        final QEntity source = new QEntity("source");
        final QEntity target = new QEntity("target");
        final EntityAliasReplacer replacer = new EntityAliasReplacer(source, target);
        FactoryExpression<BeanType> expr = Projections.bean(BeanType.class, source.propA.as("aliasA"), source.propB);
        FactoryExpression<BeanType> expected = Projections.bean(BeanType.class, target.propA.as("aliasA"), target.propB);
        FactoryExpression<BeanType> result = replacer.apply(expr);
        assertProjectionEqual(expected, result);
        final QEntity2 other = new QEntity2("source2");
        expr = Projections.bean(BeanType.class, source.propA, other.propA.toUpperCase().as("alias2"));
        expected = Projections.bean(BeanType.class, target.propA, other.propA.toUpperCase().as("alias2"));
        result = replacer.apply(expr);
        assertProjectionEqual(expected, result);
    }

    /**
     * Unit test for {@link EntityAliasReplacer#visit(Path, Void)}
     * for {@code FactoryExpression}.
     */
    @Test
    void testVisitProjection() {
        final QEntity source = new QEntity("source");
        final QEntity target = new QEntity("target");
        final EntityAliasReplacer replacer = new EntityAliasReplacer(source, target);
        QBean<BeanType> expr = Projections.bean(BeanType.class, source.propA.as("aliasA"), source.propB);
        QBean<BeanType> expected = Projections.bean(BeanType.class, target.propA.as("aliasA"), target.propB);
        Expression<?> result = expr.accept(replacer, null);
        assertProjectionEqual(expected, result);
        final QEntity2 other = new QEntity2("source2");
        expr = Projections.bean(BeanType.class, source.propA, other.propA.toUpperCase().as("alias2"));
        expected = Projections.bean(BeanType.class, target.propA, other.propA.toUpperCase().as("alias2"));
        result = expr.accept(replacer, null);
        assertProjectionEqual(expected, result);
    }

    private void assertProjectionEqual(
            final Expression<?> expected,
            final Expression<?> result) {
        if (result instanceof FactoryExpressionUtils.FactoryExpressionAdapter) {
            final FactoryExpression<?> aResult = (FactoryExpression<?>) result;
            final FactoryExpression<?> fExpected = FactoryExpressionUtils.wrap(
                    (FactoryExpression<?>) expected);
            assertEquals(fExpected.getType(), aResult.getType());
            assertEquals(fExpected.getArgs(), aResult.getArgs());
        } else {
            assertEquals(expected, result);
        }
    }

    /**
     * Unit test for {@link EntityAliasReplacer#visit(Path, Void)}
     * for {@code Predicate}.
     */
    @Test
    void testVisitPredicate() {
        final QEntity source = new QEntity("source");
        final QEntity target = new QEntity("target");
        final EntityAliasReplacer replacer = new EntityAliasReplacer(source, target);
        Predicate expr = source.propA.as("aliasA").eq(source.propB);
        Predicate expected = target.propA.as("aliasA").eq(target.propB);
        Expression<?> result = expr.accept(replacer, null);
        assertEquals(expected, result);
        final QEntity2 other = new QEntity2("source2");
        expr = source.propA.eq(other.propA.toUpperCase());
        expected = target.propA.eq(other.propA.toUpperCase());
        result = expr.accept(replacer, null);
        assertEquals(expected, result);
    }

    /**
     * Unit test for {@link EntityAliasReplacer#visit(Path, Void)}
     * for {@code OrderSpecifier}.
     */
    @Test
    void testVisitOrderEspecifier() {
        final QEntity source = new QEntity("source");
        final QEntity target = new QEntity("target");
        final EntityAliasReplacer replacer = new EntityAliasReplacer(source, target);
        OrderSpecifier<?> expr = source.propA.substring(0, 3).concat(source.propB).asc();
        OrderSpecifier<?> expected = target.propA.substring(0, 3).concat(target.propB).asc();
        List<OrderSpecifier<?>> result = OrderSpecifierReplaceVisitor.fromComponents(expr, replacer);
        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
        final QEntity2 other = new QEntity2("source2");
        expr = source.propA.substring(0, 3).concat(other.propA.toUpperCase()).asc();
        expected = target.propA.substring(0, 3).concat(other.propA.toUpperCase()).asc();
        result = OrderSpecifierReplaceVisitor.fromComponents(expr, replacer);
        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
    }

    /**
     * Unit test for {@link EntityAliasReplacer#visit(Path, Void)}
     * for {@code StoredValue}.
     */
    @Test
    void testVisitStoredValue() {
        final QEntity source = new QEntity("source");
        final QEntity target = new QEntity("target");
        final EntityAliasReplacer replacer = new EntityAliasReplacer(source, target);
        StoredValue<?> expr = StoredValue.of(source.propA, source.propB.substring(0, 3));
        StoredValue<?> expected = StoredValue.of(target.propA, target.propB.substring(0, 3));
        StoredValue<?> result = StoredValuesTransformer.translateFromComponents(expr, replacer);
        assertEquals(expected, result);
        final QEntity2 other = new QEntity2("source2");
        expr = StoredValue.of(source.propA, other.propA.toUpperCase());
        expected = StoredValue.of(target.propA, other.propA.toUpperCase());
        result = StoredValuesTransformer.translateFromComponents(expr, replacer);
        assertEquals(expected, result);
    }

    private static interface BeanType {}
    private static class QEntity extends EntityPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public final StringPath propA = createString("propA");
        public final StringPath propB = createString("propB");
        public QEntity(String variable) {
            super(BeanType.class, PathMetadataFactory.forVariable(variable));
        }
        public QEntity(Path<? extends BeanType> path) {
            super(path.getType(), path.getMetadata());
        }
        public QEntity(PathMetadata metadata) {
            super(BeanType.class, metadata);
        }
    }
    private static class QEntity2 extends EntityPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public final StringPath propA = createString("propA");
        public QEntity2(String variable) {
            super(BeanType.class, PathMetadataFactory.forVariable(variable));
        }
        public QEntity2(Path<? extends BeanType> path) {
            super(path.getType(), path.getMetadata());
        }
        public QEntity2(PathMetadata metadata) {
            super(BeanType.class, metadata);
        }
    }
}
