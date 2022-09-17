package dev.orne.qdsl.wrap;

import javax.validation.constraints.NotNull;

import com.querydsl.core.types.Expression;

/**
 * {@code ReferenceProjectionReplaceVisitor} defines a visitor signature for
 * {@link ReferenceProjection} instances that can replace the visited projection
 * with the returned values.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2022-09
 * @param <C> Context type
 * @since 0.1
 */
public interface ReferenceProjectionReplaceVisitor<C>
extends ReferenceProjectionVisitor<Expression<?>, C> {

    /**
     * {@inheritDoc}
     */
    @Override
    default Expression<?> visit(
            final @NotNull ReferenceProjection<?, ?> value,
            final C context) {
        final Expression<?> projection = value.getProjection().accept(this, context);
        final Expression<?> reference = value.getReference().accept(this, context);
        if (value.getProjection().equals(projection) &&
                value.getReference().equals(reference)) {
            return value;
        } else {
            return ReferenceProjection.of(projection, reference);
        }
    }
}
