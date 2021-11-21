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

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;

/**
 * QueryDSL expression translator.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
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
    public static Translator with(
            final TranslateVisitor... visitors) {
        return new Translator(visitors);
    }

    /**
     * Starts the creation of a expression translator for the specified source
     * path.
     * 
     * @param <S> The source path type
     * @param path The source path
     * @return A builder, to chain configuration calls
     */
    public static <S> SimplePathTranslator.TargetBuilder<S> fromPath(
            final @NotNull Path<S> path) {
        return SimplePathTranslator.fromPath(path);
    }

    /**
     * Starts the creation of a expression translator for the specified source
     * entity path.
     * 
     * @param <S> The source entity path type
     * @param path The source entity path
     * @return A builder, to chain configuration calls
     */
    public static <S> EntityPathTranslator.TargetBuilder<S> fromEntity(
            final @NotNull EntityPath<S> path) {
        return EntityPathTranslator.fromPath(path);
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
    public static <V> SimplePathTranslator<V> renamePath(
            final @NotNull Path<V> source,
            final @NotNull Path<V> target) {
        return SimplePathTranslator.fromPath(source).toPath(target).build();
    }

    /**
     * Translates the specified query projection expressions if required.
     * 
     * @param expr The expressions to translate
     * @return The resulting expressions, translated if required
     */
    public @NotNull Expression<?>[] translateProjections(
            final @NotNull Expression<?>... o) {
        return Arrays.asList(o)
                .parallelStream()
                .map(this::translateProjection)
                .toArray(Expression<?>[]::new);
    }

    /**
     * Translates the specified query projection expression if required.
     * 
     * @param <U> The type of the expression
     * @param expr The expression to translate
     * @return The resulting expression, translated if required
     */
    @SuppressWarnings("unchecked")
    public @NotNull <U> Expression<U> translateProjection(
            final Expression<U> expr) {
        final Expression<?> result = expr.accept(this, Context.PROJECTION);
        Validate.notNull(result);
        Validate.isTrue(expr.getType().isAssignableFrom(result.getType()));
        return (Expression<U>) result;
    }

    /**
     * Translates the specified predicate expressions if required.
     * 
     * @param expr The expressions to translate
     * @return The resulting expressions, translated if required
     */
    public @NotNull Predicate[] translatePredicates(
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
    public @NotNull Predicate translatePredicate(
            final @NotNull Predicate expr) {
        return (Predicate) expr.accept(this, Context.PREDICATE);
    }

    /**
     * Translates the specified order specifiers if required.
     * 
     * @param orders The order specifiers to translate
     * @return The resulting order specifiers, translated if required
     */
    public @NotNull OrderSpecifier<?>[] translateOrderSpecifiers(
            final @NotNull OrderSpecifier<?>... orders) {
        return Arrays.asList(orders)
                .parallelStream()
                .map(this::translateOrderSpecifier)
                .flatMap(r -> Arrays.asList(r).stream())
                .toArray(OrderSpecifier<?>[]::new);
    }

    /**
     * Translates the specified order specifier if required.
     * 
     * @param order The order specifier to translate
     * @return The resulting order specifier, translated if required
     */
    public @NotNull OrderSpecifier<?>[] translateOrderSpecifier(
            final @NotNull OrderSpecifier<?> order) {
        return this.visit(order, Context.ORDER);
    }

    /**
     * Translates the specified value assignments if required.
     * 
     * @param assigments The value assignments to translate
     * @return The resulting value assignments, translated if required
     */
    public @NotNull ValueAssignments translateAssigments(
            final @NotNull ValueAssignment<?>... assigments) {
        return translateAssigments(ValueAssignments.of(assigments));
    }

    /**
     * Translates the specified value assignments if required.
     * 
     * @param assigments The value assignments to translate
     * @return The resulting value assignments, translated if required
     */
    public @NotNull ValueAssignments translateAssigments(
            final @NotNull ValueAssignments assigments) {
        return assigments.parallelStream()
                .map(this::translateAssigment)
                .collect(
                    ValueAssignments::new,
                    ValueAssignments::addAll,
                    ValueAssignments::addAll);
    }

    /**
     * Translates the specified value assignment if required.
     * 
     * @param assigment The value assignment to translate
     * @return The resulting value assignments, translated if required
     */
    public @NotNull ValueAssignments translateAssigment(
            final @NotNull ValueAssignment<?> assigment) {
        return assigment.accept(this, Context.STORE);
    }
}
