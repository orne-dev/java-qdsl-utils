package dev.orne.qdsl.wrap.impl.transform;

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

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.SubQueryExpressionImpl;

import dev.orne.qdsl.OrderSpecifierReplaceVisitor;
import dev.orne.qdsl.wrap.ReferenceProjectionReplaceVisitor;
import dev.orne.qdsl.wrap.StoredValues;
import dev.orne.qdsl.wrap.StoredValuesReplaceVisitor;

/**
 * QueryDSL expression visitor that can translate expressions,
 * order specifiers and value assignments.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-12
 * @since 0.1
 */
public class BaseExpressionTransformer<C>
extends ReplaceVisitor<C>
implements ReferenceProjectionReplaceVisitor<C>,
        OrderSpecifierReplaceVisitor<C>,
        StoredValuesReplaceVisitor<C> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public @NotNull Expression<?> visit(
            final @NotNull SubQueryExpression<?> expr,
            final C context) {
        final QueryMetadata exprMD = expr.getMetadata();
        final QueryMetadata md = new DefaultQueryMetadata();
        md.setValidate(false);
        md.setDistinct(exprMD.isDistinct());
        md.setModifiers(exprMD.getModifiers());
        md.setUnique(exprMD.isUnique());
        translateSubQueryFlags(exprMD, md, context);
        translateSubQueryGroupBy(exprMD, md, context);
        translateSubQueryHaving(exprMD, md, context);
        translateSubQueryJoins(exprMD, md, context);
        translateSubQueryOrderBy(exprMD, md, context);
        translateSubQueryParams(exprMD, md, context);
        translateSubQueryProjection(exprMD, md, context);
        translateSubQueryWhere(exprMD, md, context);
        if (exprMD.equals(md)) {
            return expr;
        } else {
            return new SubQueryExpressionImpl(md.getProjection().getType(), md);
        }
    }

    /**
     * Translates the {@code QueryFlag}s of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryFlags(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        for (final QueryFlag flag : source.getFlags()) {
            target.addFlag(new QueryFlag(
                    flag.getPosition(),
                    flag.getFlag().accept(this, context)));
        }
    }

    /**
     * Translates the {@code GroupBy} expressions of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryGroupBy(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        for (final Expression<?> e : source.getGroupBy()) {
            target.addGroupBy(e.accept(this, context));
        }
    }

    /**
     * Translates the {@code Having} predicate of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryHaving(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        final Predicate having = source.getHaving();
        if (having != null) {
            target.addHaving((Predicate) having.accept(this, context));
        }
    }

    /**
     * Translates the {@code Join} expressions of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryJoins(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        for (final JoinExpression je : source.getJoins()) {
            translateSubQueryJoin(je, target, context);
        }
    }

    /**
     * Translates the {@code Join} expression of a {@code SubQueryExpression}.
     * 
     * @param source The {@code Join} expression
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryJoin(
            final JoinExpression expression,
            final QueryMetadata target,
            final C context) {
        target.addJoin(
                expression.getType(),
                expression.getTarget().accept(this, context));
        if (expression.getCondition() != null) {
            target.addJoinCondition((Predicate) expression.getCondition().accept(this, context));
        }
        for (final JoinFlag jf : expression.getFlags()) {
            target.addJoinFlag(new JoinFlag(
                    jf.getFlag().accept(this, context),
                    jf.getPosition()));
        }
    }

    /**
     * Translates the {@code OrderBy} expressions of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryOrderBy(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        for (final OrderSpecifier<?> os : source.getOrderBy()) {
            final List<OrderSpecifier<?>> toss = this.visit(os, context);
            for (final OrderSpecifier<?> tos : toss) {
                target.addOrderBy(tos);
            }
        }
    }

    /**
     * Translates the {@code ParamExpression}s of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void translateSubQueryParams(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        for (final Map.Entry<ParamExpression<?>, Object> entry : source.getParams().entrySet()) {
            target.setParam(
                    (ParamExpression) entry.getKey().accept(this, context),
                    entry.getValue());
        }
    }

    /**
     * Translates the projection expression of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryProjection(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        final Expression<?> projection = source.getProjection();
        if (projection != null) {
            target.setProjection(projection.accept(this, context));
        }
    }

    /**
     * Translates the {@code Where} predicate of a {@code SubQueryExpression}.
     * 
     * @param source The source SubQueryExpression metadata
     * @param target The target SubQueryExpression metadata
     * @param context The context of the visit or null, if not used
     */
    protected void translateSubQueryWhere(
            final QueryMetadata source,
            final QueryMetadata target,
            final C context) {
        final Predicate where = source.getWhere();
        if (where != null) {
            target.addWhere((Predicate) where.accept(this, context));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderSpecifier<?>> visit(
            final @NotNull OrderSpecifier<?> order,
            final C context) {
        return OrderSpecifierReplaceVisitor.fromComponents(order, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull StoredValues visit(
            final @NotNull StoredValues vexpr,
            final C context) {
        final StoredValues result = new StoredValues();
        vexpr.parallelStream()
                .map(assignment -> StoredValuesTransformer.translateFromComponents(assignment, this))
                .forEach(result::addAll);
        return result;
    }
}
