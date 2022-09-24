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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import dev.orne.qdsl.NopReplaceVisitor;
import dev.orne.qdsl.wrap.EntityPathNotSupportedException;
import dev.orne.qdsl.wrap.ExtendedInsertClause;
import dev.orne.qdsl.wrap.ExtendedUpdateClause;
import dev.orne.qdsl.wrap.ProjectionType;
import dev.orne.qdsl.wrap.impl.AbstractWrappedClauseProvider.ProjectionKey;
import dev.orne.qdsl.wrap.impl.transform.ChainedExpressionTransformer;

/**
 * Unit tests for {@code AbstractWrappedClauseProvider}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-08
 * @since 0.1
 * @see AbstractWrappedClauseProvider
 */
@Tag("ut")
class AbstractWrappedClauseProviderTest {

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#AbstractWrappedClauseProvider(EntityPath, EntityPath...)}.
     */
    @Test
    void testVarargsConstructor() {
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(null, QEntity2.base);
        });
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(QTargetEntity.base, (EntityPath<?>[]) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base, QEntity.base, null, QEntity2.base);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base);
        });
        AbstractWrappedClauseProvider result = new TestProvider(QTargetEntity.base, QEntity.base);
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertNotNull(result.getBaseEntities());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(1, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
        result = new TestProvider(QTargetEntity.base, QEntity.base, QEntity2.base);
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(2, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertTrue(result.getBaseEntities().containsKey(QEntity2.class));
        assertSame(QEntity2.base, result.getBaseEntities().get(QEntity2.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#AbstractWrappedClauseProvider(EntityPath, Collection)}.
     */
    @Test
    void testCollectionConstructor() {
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(null, Arrays.asList(QEntity.base));
        });
        assertThrows(NullPointerException.class, () -> {
            new TestProvider(QTargetEntity.base, (Collection<EntityPath<?>>) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base, Arrays.asList(QEntity.base, null, QEntity2.base));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TestProvider(QTargetEntity.base, Collections.emptyList());
        });
        AbstractWrappedClauseProvider result = new TestProvider(
                QTargetEntity.base,
                Arrays.asList(QEntity.base));
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertNotNull(result.getBaseEntities());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(1, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
        result = new TestProvider(
                QTargetEntity.base,
                Arrays.asList(QEntity.base, QEntity2.base));
        assertSame(QTargetEntity.base, result.getTargetEntity());
        assertFalse(result.getBaseEntities().isEmpty());
        assertEquals(2, result.getBaseEntities().size());
        assertTrue(result.getBaseEntities().containsKey(QEntity.class));
        assertSame(QEntity.base, result.getBaseEntities().get(QEntity.class));
        assertTrue(result.getBaseEntities().containsKey(QEntity2.class));
        assertSame(QEntity2.base, result.getBaseEntities().get(QEntity2.class));
        assertNotNull(result.getProjections());
        assertTrue(result.getProjections().isEmpty());
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#supports(Class)}.
     */
    @Test
    void testSupports() {
        AbstractWrappedClauseProvider result = new TestProvider(QTargetEntity.base, QEntity.base);
        assertTrue(result.supports(QEntity.class));
        assertFalse(result.supports(QEntity2.class));
        result = new TestProvider(QTargetEntity.base, QEntity.base, QEntity2.base);
        assertTrue(result.supports(QEntity.class));
        assertTrue(result.supports(QEntity2.class));
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#setProjection(Class, ProjectionType, Expression)}.
     */
    @Test
    void testSetProyection() {
        final AbstractWrappedClauseProvider provider = new TestProvider(QTargetEntity.base, QEntity.base);
        final ProjectionType customType = mock(ProjectionType.class);
        given(customType.getCode()).willReturn("mock type");
        @SuppressWarnings("unchecked")
        final Expression<BeanType> projection = mock(Expression.class);
        willReturn(BeanType.class).given(projection).getType();
        assertThrows(NullPointerException.class, () -> {
            provider.setProjection(null, null, null);
        });
        assertThrows(NullPointerException.class, () -> {
            provider.setProjection(null, customType, projection);
        });
        assertThrows(NullPointerException.class, () -> {
            provider.setProjection(QEntity.class, null, projection);
        });
        assertThrows(NullPointerException.class, () -> {
            provider.setProjection(QEntity.class, customType, null);
        });
        provider.setProjection(QEntity.class, customType, projection);
        final ProjectionKey key = new ProjectionKey(QEntity.class, customType);
        assertFalse(provider.getProjections().isEmpty());
        assertEquals(1, provider.getProjections().size());
        assertTrue(provider.getProjections().containsKey(key));
        assertSame(projection, provider.getProjections().get(key));
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#getProjection(EntityPath, ProjectionType)}
     * and {@link AbstractWrappedClauseProvider#setProjection(Class, ProjectionType, Expression)}.
     */
    @Test
    void testGetProyection() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        willReturn(QEntity.base).given(provider).getBaseEntity(QEntity.class);
        willReturn(QEntity2.base).given(provider).getBaseEntity(QEntity2.class);
        final ProjectionType customType = mock(ProjectionType.class);
        QEntity customEntity = new QEntity("alias");
        given(customType.getCode()).willReturn("mock type");
        final Expression<BeanType> projection = Projections.bean(BeanType.class, QEntity.base.propA);
        final Expression<BeanType> otherProjection = Projections.bean(BeanType.class, QEntity.base.propB);
        assertThrows(NullPointerException.class, () -> {
            provider.getProjection(null, null);
        });
        assertThrows(NullPointerException.class, () -> {
            provider.getProjection(QEntity.base, null);
        });
        assertThrows(NullPointerException.class, () -> {
            provider.getProjection(null, ProjectionType.Basic.DEFAULT);
        });
        assertThrows(IllegalStateException.class, () -> {
            provider.getProjection(QEntity.base, ProjectionType.Basic.DEFAULT);
        });
        assertThrows(IllegalStateException.class, () -> {
            provider.getProjection(QEntity.base, ProjectionType.Basic.REFERENCE);
        });
        assertThrows(IllegalStateException.class, () -> {
            provider.getProjection(QEntity.base, ProjectionType.Basic.NAMED_REFERENCE);
        });
        assertThrows(IllegalStateException.class, () -> {
            provider.getProjection(QEntity.base, customType);
        });
        provider.setProjection(QEntity.class, customType, projection);
        Expression<? extends BeanType> result = provider.getProjection(QEntity.base, customType);
        assertProjectionEqual(projection, result);
        assertThrows(IllegalStateException.class, () -> {
            provider.getProjection(QEntity.base, ProjectionType.Basic.DEFAULT);
        });
        assertThrows(IllegalStateException.class, () -> {
            provider.getProjection(QEntity.base, ProjectionType.Basic.REFERENCE);
        });
        assertThrows(IllegalStateException.class, () -> {
            provider.getProjection(QEntity.base, ProjectionType.Basic.NAMED_REFERENCE);
        });
        provider.setProjection(QEntity.class, ProjectionType.Basic.DEFAULT, otherProjection);
        result = provider.getProjection(QEntity.base, customType);
        assertProjectionEqual(projection, result);
        result = provider.getProjection(QEntity.base, ProjectionType.Basic.DEFAULT);
        assertProjectionEqual(otherProjection, result);
        result = provider.getProjection(QEntity.base, ProjectionType.Basic.REFERENCE);
        assertProjectionEqual(otherProjection, result);
        result = provider.getProjection(QEntity.base, ProjectionType.Basic.NAMED_REFERENCE);
        assertProjectionEqual(otherProjection, result);
        result = provider.getProjection(customEntity, customType);
        assertNotSame(projection, result);
        assertProjectionEqual(Projections.bean(BeanType.class, customEntity.propA), result);
        result = provider.getProjection(customEntity, ProjectionType.Basic.DEFAULT);
        assertNotSame(otherProjection, result);
        assertProjectionEqual(Projections.bean(BeanType.class, customEntity.propB), result);
        result = provider.getProjection(customEntity, ProjectionType.Basic.REFERENCE);
        assertNotSame(otherProjection, result);
        assertProjectionEqual(Projections.bean(BeanType.class, customEntity.propB), result);
        result = provider.getProjection(customEntity, ProjectionType.Basic.NAMED_REFERENCE);
        assertNotSame(otherProjection, result);
        assertProjectionEqual(Projections.bean(BeanType.class, customEntity.propB), result);
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
     * Unit test for {@link ProjectionKey#ProjectionKey(Class, ProjectionType)}.
     */
    @Test
    void testProjectionKeyConstructor() {
        final String typeCode = "mock type";
        final ProjectionType type = mock(ProjectionType.class);
        given(type.getCode()).willReturn(typeCode);
        final ProjectionKey key = new ProjectionKey(QEntity.class, type);
        assertSame(QEntity.class, key.getEntityType());
        assertEquals(typeCode, key.getProjectionType());
    }

    /**
     * Unit test for {@link ProjectionKey#ProjectionKey(Class, ProjectionType)}.
     */
    @Test
    void testProjectionKeyEqualsHashToString() {
        final String typeCode = "mock type";
        final ProjectionType type = mock(ProjectionType.class);
        given(type.getCode()).willReturn(typeCode);
        final String otherTypeCode = "other type";
        final ProjectionType otherType = mock(ProjectionType.class);
        given(otherType.getCode()).willReturn(otherTypeCode);
        final ProjectionKey key = new ProjectionKey(QEntity.class, type);
        assertFalse(key.equals(null));
        assertTrue(key.equals(key));
        assertFalse(key.equals(new Object()));
        ProjectionKey other = new ProjectionKey(QEntity.class, type);
        assertEquals(key, other);
        assertEquals(key.hashCode(), other.hashCode());
        assertNotNull(key.toString());
        assertEquals(key.toString(), other.toString());
        other = new ProjectionKey(QEntity.class, otherType);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new ProjectionKey(QEntity2.class, type);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
        other = new ProjectionKey(QEntity.class, otherType);
        assertNotEquals(key, other);
        assertNotEquals(key.toString(), other.toString());
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#getTargetEntity(EntityPath)}.
     */
    @Test
    void testGetTargetEntity_Entity() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final EntityPath<?> result = provider.getTargetEntity(new QEntity("alias"));
        assertEquals(QTargetEntity.class, result.getClass());
        assertEquals(new QTargetEntity("alias"), result);
        assertThrows(NullPointerException.class, () -> {
            provider.getTargetEntity((EntityPath<?>) null);
        });
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#getTargetEntity(String)}.
     */
    @Test
    void testGetTargetEntity_String() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final EntityPath<?> result = provider.getTargetEntity("alias");
        assertEquals(QTargetEntity.class, result.getClass());
        assertEquals(new QTargetEntity("alias"), result);
        assertThrows(NullPointerException.class, () -> {
            provider.getTargetEntity((String) null);
        });
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#getBaseEntity(Class)}.
     */
    @Test
    void testGetBaseEntity() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        assertSame(QEntity.base, provider.getBaseEntity(QEntity.class));
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.getBaseEntity(QEntity2.class);
        });
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#getEntityAlias(EntityPath)}.
     */
    @Test
    void testGetEntityAlias() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final QEntity alias = new QEntity("alias");
        assertEquals("alias", provider.getEntityAlias(alias));
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#createEntity(Class, String)}.
     */
    @Test
    void testCreateEntity() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        assertEquals(new QTargetEntity("alias"), provider.createEntity(QTargetEntity.class, "alias"));
        assertThrows(ExpressionTransformationException.class, () -> {
            provider.createEntity(QEntity.class, "alias");
        });
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#createAliasRemover(EntityPath)}.
     */
    @Test
    void testCreateAliasRemover() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base, QEntity2.base));
        final QEntity alias = new QEntity("alias");
        final QEntity2 alias2 = new QEntity2("alias2");
        Visitor<Expression<?>, ?> result = provider.createAliasRemover(QEntity.base);
        assertNotNull(result);
        assertSame(NopReplaceVisitor.INSTANCE, result);
        result = provider.createAliasRemover(QEntity2.base);
        assertNotNull(result);
        assertSame(NopReplaceVisitor.INSTANCE, result);
        result = provider.createAliasRemover(alias);
        assertNotNull(result);
        EntityAliasReplacer tresult = assertInstanceOf(EntityAliasReplacer.class, result);
        assertEquals(alias, tresult.getSource());
        assertEquals(QEntity.base, tresult.getTarget());
        result = provider.createAliasRemover(alias2);
        assertNotNull(result);
        tresult = assertInstanceOf(EntityAliasReplacer.class, result);
        assertEquals(alias2, tresult.getSource());
        assertEquals(QEntity2.base, tresult.getTarget());
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#createAliasRestorer(EntityPath)}.
     */
    @Test
    void testCreateAliasRestorer() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base, QEntity2.base));
        final QEntity alias = new QEntity("alias");
        final QEntity2 alias2 = new QEntity2("alias2");
        Visitor<Expression<?>, ?> result = provider.createAliasRestorer(QEntity.base);
        assertNotNull(result);
        assertSame(NopReplaceVisitor.INSTANCE, result);
        result = provider.createAliasRestorer(QEntity2.base);
        assertNotNull(result);
        assertSame(NopReplaceVisitor.INSTANCE, result);
        result = provider.createAliasRestorer(alias);
        assertNotNull(result);
        EntityAliasReplacer tresult = assertInstanceOf(EntityAliasReplacer.class, result);
        assertEquals(QTargetEntity.base, tresult.getSource());
        assertEquals(new QTargetEntity("alias"), tresult.getTarget());
        result = provider.createAliasRestorer(alias2);
        assertNotNull(result);
        tresult = assertInstanceOf(EntityAliasReplacer.class, result);
        assertEquals(QTargetEntity.base, tresult.getSource());
        assertEquals(new QTargetEntity("alias2"), tresult.getTarget());
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#createTransformerForAlias(EntityPath)}.
     */
    @Test
    void testCreateTransformerForAlias() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final ExpressionTransformer base = mock(ExpressionTransformer.class);
        final ReplaceVisitor<?> remover = mock(ReplaceVisitor.class);
        final ReplaceVisitor<?> restorer = mock(ReplaceVisitor.class);
        willReturn(base).given(provider).getTransformer(any(QEntity.class));
        willReturn(remover).given(provider).createAliasRemover(any(QEntity.class));
        willReturn(restorer).given(provider).createAliasRestorer(any(QEntity.class));
        ExpressionTransformer result = provider.createTransformerForAlias(QEntity.base);
        assertNotNull(result);
        assertSame(base, result);
        then(provider).should(times(1)).getTransformer(QEntity.base);
        then(provider).should(never()).createAliasRemover(any(QEntity.class));
        then(provider).should(never()).createAliasRestorer(any(QEntity.class));
        final QEntity alias = new QEntity("alias");
        result = provider.createTransformerForAlias(alias);
        assertNotNull(result);
        final ChainedExpressionTransformer cresult = assertInstanceOf(ChainedExpressionTransformer.class, result);
        assertEquals(3, cresult.getVisitors().size());
        assertSame(remover, cresult.getVisitors().get(0));
        assertSame(base, cresult.getVisitors().get(1));
        assertSame(restorer, cresult.getVisitors().get(2));
        then(provider).should(times(1)).getTransformer(alias);
        then(provider).should(times(1)).createAliasRemover(alias);
        then(provider).should(times(1)).createAliasRestorer(alias);
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#createTransformerForAlias(EntityPath)}.
     */
    @Test
    void testCreateTransformerForAlias_Real() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final ExpressionTransformer base = mock(ExpressionTransformer.class);
        willReturn(base).given(provider).getTransformer(any(QEntity.class));
        willReturn(QTargetEntity.base).given(base).visit(eq(QEntity.base), any());
        final QEntity alias = new QEntity("alias");
        final ExpressionTransformer transformer = provider.createTransformerForAlias(alias);
        assertNotNull(transformer);
        final QTargetEntity expected = new QTargetEntity("alias");
        final Expression<?> result = transformer.visit(alias, null);
        assertEquals(expected.getClass(), result.getClass());
        assertEquals(expected, result);
        then(provider).should(times(1)).getTransformer(alias);
        then(base).should(times(1)).visit(eq(QTargetEntity.base), any());
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#applyAlias(EntityPath, Expression)}.
     */
    @Test
    void testApplyAlias() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        final EntityPath<?> entity = mock(EntityPath.class);
        final ReplaceVisitor<?> setter = mock(ReplaceVisitor.class);
        willReturn(setter).given(provider).createAliasRestorer(entity);
        final Expression<?> expr = mock(Expression.class);
        final Expression<?> expected = mock(Expression.class);
        willReturn(expected).given(expr).accept(setter, null);
        assertThrows(NullPointerException.class, () -> {
            provider.applyAlias(null, null);
        });
        assertThrows(NullPointerException.class, () -> {
            provider.applyAlias(null, expr);
        });
        assertNull(provider.applyAlias(entity, null));
        then(provider).should(never()).createAliasRestorer(entity);
        assertSame(expected, provider.applyAlias(entity, expr));
        then(provider).should().createAliasRestorer(entity);
        then(expr).should().accept(setter, null);
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#insert(EntityPath)}.
     */
    @Test
    void testInsert() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        assertThrows(NullPointerException.class, () -> {
            provider.insert(null);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.insert(new QEntity2("unsupported"));
        });
        final InsertClause<?> delegated = mock(InsertClause.class);
        final ExpressionTransformer transformer = mock(ExpressionTransformer.class);
        final QEntity alias = new QEntity("alias");
        willReturn(delegated).given(provider).createDelegatedInsertClause(alias);
        willReturn(transformer).given(provider).createTransformerForAlias(alias);
        final ExtendedInsertClause<?> result = provider.insert(alias);
        final WrappedInsertClause wresult = assertInstanceOf(WrappedInsertClause.class, result);
        assertSame(delegated, wresult.getDelegate());
        assertSame(transformer, wresult.getTransformer());
        then(provider).should().createDelegatedInsertClause(alias);
        then(provider).should().createTransformerForAlias(alias);
        then(delegated).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#update(EntityPath)}.
     */
    @Test
    void testUpdate() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        assertThrows(NullPointerException.class, () -> {
            provider.update(null);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.update(new QEntity2("unsupported"));
        });
        final UpdateClause<?> delegated = mock(UpdateClause.class);
        final ExpressionTransformer transformer = mock(ExpressionTransformer.class);
        final QEntity alias = new QEntity("alias");
        willReturn(delegated).given(provider).createDelegatedUpdateClause(alias);
        willReturn(transformer).given(provider).createTransformerForAlias(alias);
        final ExtendedUpdateClause<?> result = provider.update(alias);
        final WrappedUpdateClause wresult = assertInstanceOf(WrappedUpdateClause.class, result);
        assertSame(delegated, wresult.getDelegate());
        assertSame(transformer, wresult.getTransformer());
        then(provider).should().createDelegatedUpdateClause(alias);
        then(provider).should().createTransformerForAlias(alias);
        then(delegated).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    /**
     * Unit test for {@link AbstractWrappedClauseProvider#delete(EntityPath)}.
     */
    @Test
    void testDelete() {
        final AbstractWrappedClauseProvider provider = spy(new TestProvider(QTargetEntity.base, QEntity.base));
        assertThrows(NullPointerException.class, () -> {
            provider.delete(null);
        });
        assertThrows(EntityPathNotSupportedException.class, () -> {
            provider.delete(new QEntity2("unsupported"));
        });
        final DeleteClause<?> delegated = mock(DeleteClause.class);
        final ExpressionTransformer transformer = mock(ExpressionTransformer.class);
        final QEntity alias = new QEntity("alias");
        willReturn(delegated).given(provider).createDelegatedDeleteClause(alias);
        willReturn(transformer).given(provider).createTransformerForAlias(alias);
        final DeleteClause<?> result = provider.delete(alias);
        final WrappedDeleteClause wresult = assertInstanceOf(WrappedDeleteClause.class, result);
        assertSame(delegated, wresult.getDelegate());
        assertSame(transformer, wresult.getTransformer());
        then(provider).should().createDelegatedDeleteClause(alias);
        then(provider).should().createTransformerForAlias(alias);
        then(delegated).shouldHaveNoInteractions();
        then(transformer).shouldHaveNoInteractions();
    }

    private static class TestProvider
    extends AbstractWrappedClauseProvider {
        public TestProvider(@NotNull EntityPath<?> targetEntity, @NotNull EntityPath<?>... supportedEntities) {
            super(targetEntity, supportedEntities);
        }
        public TestProvider(@NotNull EntityPath<?> targetEntity, @NotNull Collection<EntityPath<?>> supportedEntities) {
            super(targetEntity, supportedEntities);
        }
        @Override
        protected ExpressionTransformer getTransformer(@NotNull EntityPath<?> entity) {
            throw new AssertionError("Method call not mocked");
        }
        @Override
        protected <T> @NotNull InsertClause<?> createDelegatedInsertClause(@NotNull EntityPath<T> entity) {
            throw new AssertionError("Method call not mocked");
        }
        @Override
        protected <T> @NotNull UpdateClause<?> createDelegatedUpdateClause(@NotNull EntityPath<T> entity) {
            throw new AssertionError("Method call not mocked");
        }
        @Override
        protected <T> @NotNull DeleteClause<?> createDelegatedDeleteClause(@NotNull EntityPath<T> entity) {
            throw new AssertionError("Method call not mocked");
        }
    }
    private static interface BeanType {}
    private static class QEntity extends EntityPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public static final QEntity base = new QEntity("base");
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
        public static final QEntity2 base = new QEntity2("base");
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
    public static class QTargetEntity extends EntityPathBase<BeanType> {
        private static final long serialVersionUID = 1L;
        public static final QTargetEntity base = new QTargetEntity("base");
        public QTargetEntity(String variable) {
            super(BeanType.class, PathMetadataFactory.forVariable(variable));
        }
        public QTargetEntity(Path<? extends BeanType> path) {
            super(path.getType(), path.getMetadata());
        }
        public QTargetEntity(PathMetadata metadata) {
            super(BeanType.class, metadata);
        }
    }
}
