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

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;

/**
 * QueryDSL expression translator.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-07
 * @since 0.1
 */
public class Translator
extends DelegatedTranslateVisitor {

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     */
    @SafeVarargs
    public Translator(
            final TranslateVisitor... visitors) {
        super(visitors);
    }

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     */
    public Translator(
            final List<TranslateVisitor> visitors) {
        super(visitors);
    }

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     */
    public static Translator of(
            final TranslateVisitor... visitors) {
        return new Translator(visitors);
    }

    /**
     * Creates a property path translator that translates source path
     * references to the target path of the same type.
     * 
     * @param <V> The properties type
     * @param source The source property path
     * @param target The target property path
     * @return The property path translator
     */
    public static <V> PropertyPathTranslator<V, V> forProperty(
            final @NotNull Path<V> source,
            final @NotNull Path<V> target) {
        return new PropertyPathTranslator<>(
                source,
                target,
                target,
                ValueTranslator.identity(),
                ExpressionTranslator.identity(),
                AssigmentTranslator.identity(target));
    }

    /**
     * Creates a property path translator that translates source path
     * references to the target path, converting the type of the relevant
     * expressions.
     * <p>
     * Uses Apache BeanUtils converter to convert the values
     * <p>
     * Value assignment translation is created using
     * {@link AssigmentTranslator#forPath(Path, ValueTranslator, ExpressionTranslator)}.
     * 
     * @param <S> The source property type
     * @param <T> The target property type
     * @param source The source property path
     * @param target The target property path
     * @param factory The query projections expression
     * @param expressionTranslator The expression translator
     * @return The property path translator
     */
    public static <S, T> PropertyPathTranslator<S, T> forProperty(
            final @NotNull Path<S> source,
            final @NotNull Path<T> target,
            final @NotNull Expression<S> factory,
            final @NotNull ExpressionTranslator<S, T> expressionTranslator) {
        return new PropertyPathTranslator<>(
                source,
                target,
                factory,
                ValueTranslator.beanUtilsBased(target.getType()),
                expressionTranslator);
    }

    /**
     * Creates a property path translator that translates source path
     * references to the target path, converting the type of the relevant
     * expressions.
     * <p>
     * Uses Apache BeanUtils converter to convert the values
     * 
     * @param <S> The source property type
     * @param <T> The target expression type
     * @param source The source property path
     * @param target The target expression path
     * @param factory The query projections expression
     * @param expressionTranslator The expression translator
     * @param assigmentTranslator The value assignment translator
     * @return The property path translator
     */
    public static <S, T> PropertyPathTranslator<S, T> forProperty(
            final @NotNull Path<S> source,
            final @NotNull Expression<T> target,
            final @NotNull Expression<S> factory,
            final @NotNull ExpressionTranslator<S, T> expressionTranslator,
            final @NotNull AssigmentTranslator<S> assigmentTranslator) {
        return new PropertyPathTranslator<>(
                source,
                target,
                factory,
                ValueTranslator.beanUtilsBased(target.getType()),
                expressionTranslator,
                assigmentTranslator);
    }

    /**
     * Creates a property path translator that translates source path
     * references to the target path, converting the type of the relevant
     * expressions.
     * <p>
     * Value assignment translation is created using
     * {@link AssigmentTranslator#forPath(Path, ValueTranslator, ExpressionTranslator)}.
     * 
     * @param <S> The source property type
     * @param <T> The target expression type
     * @param source The source property path
     * @param target The target expression path
     * @param factory The query projections expression
     * @param valueTranslator The value translator
     * @param expressionTranslator The expression translator
     * @return The property path translator
     */
    public static <S, T> PropertyPathTranslator<S, T> forProperty(
            final @NotNull Path<S> source,
            final @NotNull Path<T> target,
            final @NotNull Expression<S> factory,
            final @NotNull ValueTranslator<S, T> valueTranslator,
            final @NotNull ExpressionTranslator<S, T> expressionTranslator) {
        return new PropertyPathTranslator<>(
                source,
                target,
                factory,
                valueTranslator,
                expressionTranslator);
    }

    /**
     * Creates a property path translator that translates source path
     * references to the target path, converting the type of the relevant
     * expressions.
     * 
     * @param <S> The source property type
     * @param <T> The target expression type
     * @param source The source property path
     * @param target The target expression path
     * @param factory The query projections expression
     * @param valueTranslator The value translator
     * @param expressionTranslator The expression translator
     * @param assigmentTranslator The value assignment translator
     * @return The property path translator
     */
    public static <S, T> PropertyPathTranslator<S, T> forProperty(
            final @NotNull Path<S> source,
            final @NotNull Expression<T> target,
            final @NotNull Expression<S> factory,
            final @NotNull ValueTranslator<S, T> valueTranslator,
            final @NotNull ExpressionTranslator<S, T> expressionTranslator,
            final @NotNull AssigmentTranslator<S> assigmentTranslator) {
        return new PropertyPathTranslator<>(
                source,
                target,
                factory,
                valueTranslator,
                expressionTranslator,
                assigmentTranslator);
    }

    /**
     * Translates the specified query projection expression if required.
     * 
     * @param <U> The type of the expression
     * @param expr The expression to translate
     * @return The resulting expression, translated if required
     */
    @SuppressWarnings("unchecked")
    public <U> Expression<U> translateProjection(
            final Expression<U> expr) {
        final Expression<?> result = expr.accept(this, Context.PROJECTION);
        Validate.isTrue(expr.getType().isAssignableFrom(result.getType()));
        return (Expression<U>) result;
    }

    /**
     * Translates the specified predicate expressions if required.
     * 
     * @param expr The expressions to translate
     * @return The resulting expressions, translated if required
     */
    public Predicate[] translatePredicates(
            final @NotNull Predicate... o) {
        return Arrays.asList(o)
                .parallelStream()
                .map(this::translatePredicate)
                .toArray(Predicate[]::new);
    }

    /**
     * Translates the specified predicate expression if required.
     * 
     * @param expr The expression to translate
     * @return The resulting expression, translated if required
     */
    public Predicate translatePredicate(
            final @NotNull Predicate expr) {
        return (Predicate) expr.accept(this, Context.PREDICATE);
    }

    /**
     * Translates the specified order specifier if required.
     * 
     * @param order The order specifier to translate
     * @return The resulting order specifier, translated if required
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public OrderSpecifier<?> translateOrderSpecifier(
            final @NotNull OrderSpecifier<?> order) {
        return new OrderSpecifier<>(
                order.getOrder(),
                (Expression<? extends Comparable>) order.getTarget().accept(this, null),
                order.getNullHandling());
    }

    /**
     * Translates the specified value assignments if required.
     * 
     * @param assigments The value assignments to translate
     * @return The resulting value assignments, translated if required
     */
    public ValueAssigment<?>[] translateAssigments(
            final @NotNull ValueAssigment<?>... assigments) {
        return Arrays.asList(assigments)
                .parallelStream()
                .map(this::translateAssigment)
                .flatMap(r -> Arrays.asList(r).stream())
                .toArray(ValueAssigment<?>[]::new);
    }

    /**
     * Translates the specified value assignment if required.
     * 
     * @param assigment The value assignment to translate
     * @return The resulting value assignments, translated if required
     */
    public ValueAssigment<?>[] translateAssigment(
            final @NotNull ValueAssigment<?> assigment) {
        return assigment.accept(this, Context.STORE);
    }
}
