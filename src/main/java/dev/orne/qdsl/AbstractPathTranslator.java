package dev.orne.qdsl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.core.types.dsl.Expressions;

/**
 * Abstract implementation of {@code TranslateVisitor} that translates
 * expressions referencing a single path.
 * 
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-09
 * @param <S> The source path type
 * @param <T> The target expression type
 * @since 0.1
 */
public abstract class AbstractPathTranslator<S, T>
extends TranslateVisitor {

    /** The source path. */
    private final @NotNull Path<S> source;
    /** The source path type. */
    private final @NotNull Class<? extends S> sourceType;
    /** The target expression. */
    private final @NotNull Expression<T> target;
    /** The value assignment translator. */
    private final @NotNull AssignmentTranslator<S> assignmentTranslator;

    /**
     * Creates a new instance.
     * 
     * @param source The source path
     * @param target The target expression
     * @param assignmentTranslator The value assignment translator
     */
    protected AbstractPathTranslator(
            final @NotNull Path<S> source,
            final @NotNull Expression<T> target,
            final @NotNull AssignmentTranslator<S> assignmentTranslator) {
        super();
        this.source = Validate.notNull(source);
        this.sourceType = source.getType();
        this.target = Validate.notNull(target);
        this.assignmentTranslator = Validate.notNull(assignmentTranslator);
    }

    /**
     * Builder constructor.
     * 
     * @param builder The builder
     */
    protected AbstractPathTranslator(
            final @NotNull Builder<S, T> builder) {
        super();
        Validate.notNull(builder);
        this.source = builder.source;
        this.sourceType = this.source.getType();
        this.target = builder.target;
        this.assignmentTranslator = builder.assignmentTranslator;
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
     * Returns the source path type.
     * 
     * @return The source path type
     */
    public @NotNull Class<? extends S> getSourceType() {
        return this.sourceType;
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
     * Checks if the expression is the source path.
     * 
     * @param expr The expression to check
     * @return If the expression is the source path
     */
    protected boolean isSource(
            final @NotNull Expression<?> expr) {
        return this.source.equals(expr);
    }

    /**
     * Checks if the value is of the type of the source path.
     * 
     * @param value The value to check
     * @return If the value is of the type of the source path
     */
    protected boolean isOfSourceType(
            final @NotNull Object value) {
        return this.sourceType.isInstance(value);
    }

    /**
     * Checks if the expression is of the type of the source path.
     * 
     * @param expr The expression to check
     * @return If the expression is of the type of the source path
     */
    protected boolean isOfSourceType(
            final @NotNull Expression<?> expr) {
        return this.sourceType.isAssignableFrom(expr.getType());
    }

    /**
     * Casts the specified value to the type of the source path.
     * 
     * @param value The value to cast
     * @return The value as the source path type
     * @throws IllegalArgumentException If the value is not of the expected
     * type
     */
    protected S asSourceType(
            final Object value) {
        Validate.isInstanceOf(this.sourceType, value);
        return this.sourceType.cast(value);
    }

    /**
     * Casts the specified expression to a expression the type of the source
     * path.
     * 
     * @param expr The expression to case
     * @return The expression as the source path type
     * @throws IllegalArgumentException If the expression is not of the
     * expected type
     */
    @SuppressWarnings("unchecked")
    protected @NotNull Expression<S> asSourceType(
            final @NotNull Expression<?> expr) {
        Validate.isAssignableFrom(this.sourceType, expr.getType());
        return (Expression<S>) expr;
    }

    /**
     * Checks if the constant is of {@code List} type.
     * <p>
     * Querydsl uses List for {@code IN} predicates.
     * 
     * @param expr The constant to check
     * @return If the constant is of {@code List} type
     */
    protected boolean isList(
            final @NotNull Constant<?> expr) {
        return List.class.isAssignableFrom(expr.getType());
    }

    /**
     * Extracts the List value of the specified constant.
     * <p>
     * Querydsl uses List for {@code IN} predicates.
     * 
     * @param expr The constant expression
     * @return The list instance
     * @throws IllegalArgumentException If the constant value is not of the
     * expected type
     */
    protected List<?> asList(
            final @NotNull Constant<?> expr) {
        Validate.isInstanceOf(List.class, expr.getConstant());
        return (List<?>) expr.getConstant();
    }

    /**
     * Translates the specified expression argument list, translating the values
     * of the type of the source property if required.
     * 
     * @param list The constant list expression to translate
     * @param valueTranslator The value translator
     * @param context The translation context
     * @return The translated list
     * @throws IllegalArgumentException If the constant type is not a list
     */
    protected <V> Expression<List<?>> translateList(
            final @NotNull Constant<?> expr,
            final @NotNull ValueTranslator<S, V> valueTranslator,
            final Context context) {
        Validate.isAssignableFrom(List.class, expr.getType());
        return translateList(asList(expr), valueTranslator, context);
    }

    /**
     * Translates the specified operation argument list, translating the values
     * of the type of the target type if required.
     * 
     * @param list The list to translate
     * @param valueTranslator The value translator
     * @param context The translation context
     * @return The translated list
     */
    protected Expression<List<?>> translateList(
            final @NotNull List<?> list,
            final @NotNull ValueTranslator<S, ?> valueTranslator,
            final Context context) {
        final List<Object> components = new ArrayList<>();
        for (final Object component : list) {
            if (component instanceof Expression) {
                components.add(((Expression<?>) component).accept(this, context));
            } else if (isOfSourceType(component)) {
                components.add(valueTranslator.apply(asSourceType(component)));
            } else {
                components.add(component);
            }
        }
        return Expressions.constant(components);
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
     * Returns the translation of the source path for the specified context.
     * 
     * @param context The visit context
     * @return The source path translation
     */
    protected @NotNull Expression<?> getTranslatedPath(
            final Context context) {
        return this.target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression<?> visit(
            final Path<?> expr,
            final Context context) {
        if (isSource(expr))  {
            return getTranslatedPath(context);
        } else if (context == Context.PROJECTION &&
                expr instanceof ProjectionRole) {
            return ((ProjectionRole<?>) expr).getProjection().accept(this, context);
        } else {
            return super.visit(expr, context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ValueAssignments visit(
            final @NotNull ValueAssignment<?> vexpr,
            final Context context) {
        if (isSource(vexpr.getPath())) {
            @SuppressWarnings("unchecked")
            final Expression<S> value = (Expression<S>) vexpr.getValue();
            return this.assignmentTranslator.apply(value);
        } else {
            return super.visit(vexpr, context);
        }
    }

    /**
     * Internal implementation of builder interfaces.
     * 
     * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
     * @version 1.0, 2021-09
     * @param <S> The source path type
     * @param <T> The target expression type
     * @since 0.1
     */
    protected abstract static class Builder<S, T> {

        /** The source path. */
        private final @NotNull Path<S> source;
        /** The target expression. */
        private Expression<T> target;
        /** The value assignment translator. */
        private AssignmentTranslator<S> assignmentTranslator;

        /**
         * Creates a new instance.
         * 
         * @param source The source path
         */
        protected Builder(
                final @NotNull Path<S> source) {
            super();
            this.source = Validate.notNull(source);
        }

        /**
         * Returns the source path.
         * 
         * @return The source path
         */
        protected Path<S> getSource() {
            return this.source;
        }

        /**
         * Returns the target expression.
         * 
         * @return The target expression
         */
        protected Expression<T> getTarget() {
            return this.target;
        }

        /**
         * Sets the target expression.
         * 
         * @param target The target expression
         */
        protected void setTarget(
                final @NotNull Expression<T> target) {
            this.target = target;
        }

        /**
         * Returns the value assignment translator.
         * 
         * @return The value assignment translator
         */
        protected AssignmentTranslator<S> getAssignmentTranslator() {
            return this.assignmentTranslator;
        }

        /**
         * Sets the value assignment translator.
         * 
         * @param translator The value assignment translator
         */
        protected void setAssignmentTranslator(
                final @NotNull AssignmentTranslator<S> translator) {
            this.assignmentTranslator = translator;
        }
    }
}
