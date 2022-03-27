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

import java.util.List;

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

/**
 * QueryDSL expression translator.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-03
 * @since 0.1
 */
public interface Translator {

    /** Shared instance of no operation translator. */
    public static final Translator NOP = new NopTranslator();

    /**
     * Creates a new instance.
     * 
     * @param visitors The delegated translators
     * @return The created instance
     */
    @SafeVarargs
    public static ChainedTranslator with(
            final Visitor<Expression<?>, ?>... visitors) {
        return new ChainedTranslator(visitors);
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
        return SimplePathTranslator.fromPath(source)
                .toPath(target)
                .build();
    }

    /**
     * Translates the specified query projection expressions if required.
     * 
     * @param exprs The expressions to translate
     * @return The resulting expressions, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull Expression<?>[] translateProjections(
            @NotNull Expression<?>... exprs);

    /**
     * Translates the specified query projection expression if required.
     * 
     * @param <U> The type of the expression
     * @param expr The expression to translate
     * @return The resulting expression, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull <U> Expression<U> translateProjection(
            @NotNull Expression<U> expr);

    /**
     * Translates the specified predicate expressions if required.
     * 
     * @param exprs The expressions to translate
     * @return The resulting expressions, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull Predicate[] translatePredicates(
            @NotNull Predicate... exprs);

    /**
     * Translates the specified predicate expression if required.
     * 
     * @param expr The expression to translate
     * @return The resulting expression, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull Predicate translatePredicate(
            @NotNull Predicate expr);

    /**
     * Translates the specified order specifiers if required.
     * 
     * @param orders The order specifiers to translate
     * @return The resulting order specifiers, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull OrderSpecifier<?>[] translateOrderSpecifiers(
            @NotNull OrderSpecifier<?>... orders);

    /**
     * Translates the specified order specifier if required.
     * 
     * @param order The order specifier to translate
     * @return The resulting order specifier, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull List<OrderSpecifier<?>> translateOrderSpecifier(
            @NotNull OrderSpecifier<?> order);

    /**
     * Translates the specified value assignments if required.
     * 
     * @param assigments The value assignments to translate
     * @return The resulting value assignments, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull ValueAssignments translateAssigments(
            @NotNull ValueAssignment<?>... assigments);

    /**
     * Translates the specified value assignments if required.
     * 
     * @param assigments The value assignments to translate
     * @return The resulting value assignments, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull ValueAssignments translateAssigments(
            @NotNull ValueAssignments assigments);

    /**
     * Translates the specified value assignment if required.
     * 
     * @param assigment The value assignment to translate
     * @return The resulting value assignments, translated if required
     * @throws QueryTranslationException If an exception occurs
     */
    @NotNull ValueAssignments translateAssigment(
            @NotNull ValueAssignment<?> assigment);
}
