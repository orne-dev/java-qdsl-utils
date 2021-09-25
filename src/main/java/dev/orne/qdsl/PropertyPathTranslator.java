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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Translator of QueryDSL values and expressions based in a property
 * path.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @param <S> The source path type
 * @param <T> The target path type
 * @since 0.1
 */
public class PropertyPathTranslator<S, T>
extends TranslateVisitor {

    /** The source path. */
    private final @NotNull Path<S> source;
    /** The target expression. */
    private final @NotNull Expression<T> target;
    /** The expression for query projections. */
    private final @NotNull Expression<S> projection;
    /** The value translator. */
    private final @NotNull ValueTranslator<S, ? extends T> valueTranslator;
    /** The expression translator. */
    private final @NotNull ExpressionTranslator<S, ? extends T> expressionTranslator;
    /** The value assignment translator. */
    private final @NotNull AssigmentTranslator<S> storeTranslator;

    /**
     * Creates a new instance.
     * <p>
     * Value assignment translation is created using
     * {@link AssigmentTranslator#forPath(Path, ValueTranslator, ExpressionTranslator)}.
     * 
     * @param source The source path
     * @param target The target expression
     * @param projection The expression for query projections
     * @param valueTranslator The value translator
     * @param expressionTranslator The value assignment translator
     */
    public PropertyPathTranslator(
            final @NotNull Path<S> source,
            final @NotNull Path<T> target,
            final @NotNull Expression<S> projection,
            final @NotNull ValueTranslator<S, ? extends T> valueTranslator,
            final @NotNull ExpressionTranslator<S, ? extends T> expressionTranslator) {
        this(
                source,
                target,
                projection,
                valueTranslator,
                expressionTranslator,
                AssigmentTranslator.forPath(target, valueTranslator, expressionTranslator));
    }

    /**
     * Creates a new instance.
     * 
     * @param source The source path
     * @param target The target expression
     * @param projection The expression for query projections
     * @param valueTranslator The value translator
     * @param expressionTranslator The value assignment translator
     * @param storeTranslator The value assignment translator
     */
    public PropertyPathTranslator(
            final @NotNull Path<S> source,
            final @NotNull Expression<T> target,
            final @NotNull Expression<S> projection,
            final @NotNull ValueTranslator<S, ? extends T> valueTranslator,
            final @NotNull ExpressionTranslator<S, ? extends T> expressionTranslator,
            final @NotNull AssigmentTranslator<S> storeTranslator) {
        super();
        this.source = source;
        this.target = target;
        this.projection = Expressions.operation(
                source.getType(),
                Ops.ALIAS,
                projection,
                source);
        this.valueTranslator = valueTranslator;
        this.expressionTranslator = expressionTranslator;
        this.storeTranslator = storeTranslator;
    }

    /**
     * Returns the source path.
     * 
     * @return The source path
     */
    public @NotNull Path<S> getSource() {
        return this.source;
    }

    /**
     * Returns the target expression.
     * 
     * @return The target expression
     */
    public @NotNull Expression<T> getTarget() {
        return this.target;
    }

    /**
     * Returns the expression for query projections.
     * 
     * @return The expression for query projections
     */
    public @NotNull Expression<S> getProjection() {
        return this.projection;
    }

    /**
     * Returns the value translator.
     * 
     * @return The value translator
     */
    public @NotNull ValueTranslator<S, ? extends T> getValueTranslator() {
        return this.valueTranslator;
    }

    /**
     * Returns the expression translator.
     * 
     * @return The expression translator
     */
    public @NotNull ExpressionTranslator<S, ? extends T> getExpressionTranslator() {
        return this.expressionTranslator;
    }

    /**
     * Returns the value assignment translator.
     * 
     * @return The value assignment translator
     */
    public @NotNull AssigmentTranslator<S> getStoreTranslator() {
        return this.storeTranslator;
    }

    /**
     * Converts the value of the source type to a value of the target type.
     * 
     * @param value The source value
     * @return The converted value
     */
    public @NotNull T convertValue(
            final @NotNull S value) {
        return this.valueTranslator.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Path<?> expr,
            final Context context) {
        if (this.source.equals(expr))  {
            switch (context) {
                case PROJECTION:
                    return getProjection();
                default:
                    return getTarget();
            }
        } else if (context == Context.PROJECTION
                && expr instanceof ProjectionRole) {
            return ((ProjectionRole<?>) expr).getProjection().accept(this, context);
        } else {
            return super.visit(expr, context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final FactoryExpression<?> expr,
            final Context context) {
        return super.visit(expr, Context.PROJECTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Constant<?> expr,
            final Context context) {
        switch (context) {
            case OPERATION_ARG:
                if (this.source.getType().isAssignableFrom(expr.getType())) {
                    return Expressions.constant(convertValue(this.source.getType().cast(expr.getConstant())));
                } else if (List.class.isAssignableFrom(expr.getType())) {
                    return translateList(List.class.cast(expr.getConstant()), context);
                } else {
                    return super.visit(expr, context);
                }
            default:
                return super.visit(expr, context);
        }
    }

    /**
     * Checks if the constant is of the type of the source property.
     * 
     * @param expr The constant to check
     * @return If the constant is of the type of the source property
     */
    protected boolean isSourceConstant(
            final @NotNull Constant<?> expr) {
        return this.source.getType().isAssignableFrom(expr.getType());
    }

    /**
     * Checks if the constant is of {@code List} type.
     * <p>
     * Used by Querydsl for {@code IN} predicates.
     * 
     * @param expr The constant to check
     * @return If the constant is of {@code List} type
     */
    protected boolean isListConstant(
            final @NotNull Constant<?> expr) {
        return List.class.isAssignableFrom(expr.getType());
    }

    /**
     * Translates the specified operation argument list, translating the values
     * of the type of the source property if required.
     * 
     * @param list The list to translate
     * @param context The translation context
     * @return The translated list
     */
    protected Expression<List<?>> translateList(
            final @NotNull List<?> list,
            final Context context) {
        final List<Object> components = new ArrayList<>();
        for (final Object component : list) {
            if (component instanceof Expression) {
                components.add(((Expression<?>) component).accept(this, context));
            } else if (this.source.getType().isAssignableFrom(component.getClass())) {
                components.add(convertValue(this.source.getType().cast(component)));
            } else {
                components.add(component);
            }
        }
        return Expressions.constant(components);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Operation<?> expr,
            final Context context) {
        if (isSourceOperation(expr)) {
            return translateOperation(expr);
        } else {
            return super.visit(expr, context);
        }
    }

    /**
     * Checks if the operation uses the source property path.
     * 
     * @param op The operation to check
     * @return If the operation uses the source property path
     */
    protected boolean isSourceOperation(
            final @NotNull Operation<?> op) {
        return op.getArgs().contains(this.source);
    }

    /**
     * Translates the specified operation that uses the source property path.
     * 
     * @param <OT> The operation type
     * @param op The operation to translate
     * @return The translated operation
     */
    protected <OT> @NotNull Operation<OT> translateOperation(
            final @NotNull Operation<OT> op) {
        final List<Expression<?>> args = op.getArgs().parallelStream()
                .map(v -> v.accept(this, Context.OPERATION_ARG))
                .collect(Collectors.toList());
        if (args.equals(op.getArgs())) {
            return op;
        } else {
            return ExpressionUtils.operation(op.getType(), op.getOperator(), args);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssigment<?>[] visit(
            final @NotNull ValueAssigment<?> vexpr,
            final Context context) {
        if (this.source.equals(vexpr.getPath())) {
            @SuppressWarnings("unchecked")
            final Expression<S> value = (Expression<S>) vexpr.getValue();
            return this.storeTranslator.apply(value);
        } else {
            return super.visit(vexpr, context);
        }
    }
}
