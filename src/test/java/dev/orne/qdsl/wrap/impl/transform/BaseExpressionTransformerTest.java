package dev.orne.qdsl.wrap.impl.transform;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils.FactoryExpressionAdapter;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.TemplateFactory;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.PathBuilder;

import dev.orne.qdsl.wrap.StoredValue;
import dev.orne.qdsl.wrap.StoredValues;

/**
 * Unit tests for {@code TranslateVisitor}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-11
 * @since 0.1
 * @see BaseExpressionTransformer
 */
@Tag("ut")
class BaseExpressionTransformerTest {

    private static final PathBuilder<Object> ROOT =
            new PathBuilder<Object>(Object.class, "root");
    private static final PathBuilder<Object> ALT_ROOT =
            new PathBuilder<Object>(Object.class, "altRoot");

    /**
     * Tests for {@link BaseExpressionTransformer#visit(Constant, Context)}.
     */
    @Nested
    @DisplayName("when visiting a Constant")
    class ForConstant {

        /**
         * Test for {@link BaseExpressionTransformer#visit(Constant, Context)}.
         */
        @Test
        @DisplayName("returns same Constant")
        void returnsSame() {
            final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
            final Constant<?> value = mock(Constant.class);
            final Expression<?> result = visitor.visit(value, null);
            assertNotNull(result);
            assertSame(value, result);
            then(visitor).should().visit(value, null);
            then(visitor).shouldHaveNoMoreInteractions();
            then(value).shouldHaveNoInteractions();
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(FactoryExpression, Context)}.
     */
    @Nested
    @DisplayName("when visiting a FactoryExpression")
    class ForFactoryExpression {

        /**
         * Tests for {@link BaseExpressionTransformer#visit(FactoryExpression, Context)}
         * for same arguments.
         */
        @Nested
        @DisplayName("and arguments translation returns same arguments")
        class WhenArgumentsRemain {

            /**
             * Test for {@link BaseExpressionTransformer#visit(FactoryExpression, Context)}.
             */
            @Test
            @DisplayName("returns same FactoryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final FactoryExpression<?> value = mock(FactoryExpression.class);
                final List<Expression<?>> args = generateArgs();
                for (int i = 0; i < args.size(); i++) {
                    willReturn(args.get(i)).given(args.get(i)).accept(visitor, null);
                }
                willReturn(args).given(value).getArgs();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertSame(value, result);
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(value).should(atLeastOnce()).getArgs();
                then(value).shouldHaveNoMoreInteractions();
                for (final Expression<?> arg : args) {
                    then(arg).should().accept(visitor, null);
                    then(arg).shouldHaveNoMoreInteractions();
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(FactoryExpression, Context)}
         * for translated arguments.
         */
        @Nested
        @DisplayName("and arguments translation returns modified arguments")
        class WhenArgumentsChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(FactoryExpression, Context)}.
             */
            @Test
            @DisplayName("returns a wrapped FactoryExpression with translated arguments")
            void returnsAdapter() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final FactoryExpression<?> value = mock(FactoryExpression.class);
                final List<Expression<?>> args = generateArgs();
                final List<Expression<?>> transformedArgs = generateArgs(args.size());
                for (int i = 0; i < args.size(); i++) {
                    willReturn(transformedArgs.get(i)).given(args.get(i)).accept(visitor, null);
                }
                willReturn(args).given(value).getArgs();
                willReturn(MyType.class).given(value).getType();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertInstanceOf(FactoryExpression.class, result);
                assertNotSame(value, result);
                assertInstanceOf(FactoryExpressionAdapter.class, result);
                assertEquals(transformedArgs, ((FactoryExpression<?>) result).getArgs());
                assertEquals(MyType.class, result.getType());
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(value).should(atLeastOnce()).getArgs();
                then(value).should().getType();
                then(value).shouldHaveNoMoreInteractions();
                for (final Expression<?> arg : args) {
                    then(arg).should().accept(visitor, null);
                    then(arg).shouldHaveNoMoreInteractions();
                }
            }
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}.
     */
    @Nested
    @DisplayName("when visiting an Operation")
    class ForOperation {

        /**
         * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
         * for same arguments.
         */
        @Nested
        @DisplayName("and arguments translation returns same arguments")
        class WhenArgumentsRemain {

            /**
             * Test for {@link BaseExpressionTransformer#visit(Operation, Context)}.
             */
            @Test
            @DisplayName("returns same Operation")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final Operation<?> value = mock(Operation.class);
                final List<Expression<?>> args = generateArgs();
                for (int i = 0; i < args.size(); i++) {
                    willReturn(args.get(i)).given(args.get(i)).accept(visitor, null);
                }
                willReturn(args).given(value).getArgs();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertSame(value, result);
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(value).should(atLeastOnce()).getArgs();
                then(value).shouldHaveNoMoreInteractions();
                for (final Expression<?> arg : args) {
                    then(arg).should().accept(visitor, null);
                    then(arg).shouldHaveNoMoreInteractions();
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
         * for translated arguments.
         */
        @Nested
        @DisplayName("and arguments translation returns modified arguments")
        class WhenArgumentsChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(Operation, Context)}.
             */
            @Test
            @DisplayName("returns a new Operation with translated arguments")
            void returnsNewInstance() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final Operation<?> value = mock(Operation.class);
                final List<Expression<?>> args = generateArgs();
                final Operator operator = mock(Operator.class);
                final List<Expression<?>> transformedArgs = generateArgs(args.size());
                for (int i = 0; i < args.size(); i++) {
                    willReturn(transformedArgs.get(i)).given(args.get(i)).accept(visitor, null);
                }
                willReturn(args).given(value).getArgs();
                willReturn(operator).given(value).getOperator();
                willReturn(MyType.class).given(operator).getType();
                willReturn(MyType.class).given(value).getType();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertInstanceOf(Operation.class, result);
                assertNotSame(value, result);
                assertEquals(transformedArgs, ((Operation<?>) result).getArgs());
                assertEquals(operator, ((Operation<?>) result).getOperator());
                assertEquals(MyType.class, result.getType());
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(value).should(atLeastOnce()).getArgs();
                then(value).should(atLeastOnce()).getOperator();
                then(value).should().getType();
                then(value).shouldHaveNoMoreInteractions();
                for (final Expression<?> arg : args) {
                    then(arg).should().accept(visitor, null);
                    then(arg).shouldHaveNoMoreInteractions();
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
         * for Predicate operation.
         */
        @Nested
        @DisplayName("that implements Predicate")
        class ForPredicate {

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
             * for same arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns same arguments")
            class WhenArgumentsRemain {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Operation, Context)}.
                 */
                @Test
                @DisplayName("returns same Predicate")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final PredicateOperation value = mock(PredicateOperation.class);
                    final List<Expression<?>> args = generateArgs();
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(args.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
             * for translated arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns modified arguments")
            class WhenArgumentsChange {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Operation, Context)}.
                 */
                @Test
                @DisplayName("returns a new Predicate with translated arguments")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final PredicateOperation value = mock(PredicateOperation.class);
                    final List<Expression<?>> args = generateArgs();
                    final Operator operator = mock(Operator.class);
                    final List<Expression<?>> transformedArgs = generateArgs(args.size());
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(transformedArgs.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    willReturn(operator).given(value).getOperator();
                    willReturn(Boolean.class).given(operator).getType();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(Operation.class, result);
                    assertNotSame(value, result);
                    assertInstanceOf(Predicate.class, result);
                    assertEquals(transformedArgs, ((Operation<?>) result).getArgs());
                    assertEquals(operator, ((Operation<?>) result).getOperator());
                    assertEquals(Boolean.class, result.getType());
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).should(atLeastOnce()).getOperator();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
         * of type Boolean.
         */
        @Nested
        @DisplayName("of type Boolean")
        class OfTypeBoolean {

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
             * for same arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns same arguments")
            class WhenArgumentsRemain {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Operation, Context)}.
                 */
                @Test
                @DisplayName("returns same Operation")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final Operation<?> value = mock(Operation.class);
                    final List<Expression<?>> args = generateArgs();
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(args.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Operation, Context)}
             * for translated arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns modified arguments")
            class WhenArgumentsChange {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Operation, Context)}.
                 */
                @Test
                @DisplayName("returns a new Predicate with translated arguments")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final Operation<?> value = mock(Operation.class);
                    final List<Expression<?>> args = generateArgs();
                    final Operator operator = mock(Operator.class);
                    final List<Expression<?>> transformedArgs = generateArgs(args.size());
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(transformedArgs.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    willReturn(operator).given(value).getOperator();
                    willReturn(Boolean.class).given(operator).getType();
                    willReturn(Boolean.class).given(value).getType();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(Operation.class, result);
                    assertNotSame(value, result);
                    assertInstanceOf(Predicate.class, result);
                    assertEquals(transformedArgs, ((Operation<?>) result).getArgs());
                    assertEquals(operator, ((Operation<?>) result).getOperator());
                    assertEquals(Boolean.class, result.getType());
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).should(atLeastOnce()).getType();
                    then(value).should(atLeastOnce()).getOperator();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(ParamExpression, Context)}.
     */
    @Nested
    @DisplayName("when visiting a ParamExpression")
    class ForParamExpression {

        /**
         * Test for {@link BaseExpressionTransformer#visit(ParamExpression, Context)}.
         */
        @Test
        @DisplayName("returns same ParamExpression")
        void returnsSame() {
            final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
            final ParamExpression<?> value = mock(ParamExpression.class);
            final Expression<?> result = visitor.visit(value, null);
            assertNotNull(result);
            assertSame(value, result);
            then(visitor).should().visit(value, null);
            then(visitor).shouldHaveNoMoreInteractions();
            then(value).shouldHaveNoInteractions();
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}.
     */
    @Nested
    @DisplayName("when visiting a Path")
    class ForPath {

        /**
         * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
         * for same arguments.
         */
        @Nested
        @DisplayName("that is root")
        class WithNoParent {

            /**
             * Test for {@link BaseExpressionTransformer#visit(Path, Context)}.
             */
            @Test
            @DisplayName("returns same Path")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final Path<?> value = mock(Path.class);
                final PathMetadata metadata = PathMetadataFactory.forVariable("root");
                willReturn(metadata).given(value).getMetadata();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertSame(value, result);
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(value).should().getMetadata();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
         * for path with simple element.
         */
        @Nested
        @DisplayName("whose element is an String")
        class WithSimpleElement{

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
             * for same parent.
             */
            @Nested
            @DisplayName("and parent translation returns the same parent")
            class WhenParentRemains {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Path, Context)}.
                 */
                @Test
                @DisplayName("returns same Path")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final Path<?> root = mock(Path.class);
                    final Path<?> parent = mock(Path.class);
                    willReturn(root).given(parent).getRoot();
                    final Path<?> value = mock(Path.class);
                    final PathMetadata metadata = PathMetadataFactory.forProperty(parent, "prop");
                    willReturn(metadata).given(value).getMetadata();
                    willReturn(parent).given(parent).accept(visitor, null);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getMetadata();
                    then(value).shouldHaveNoMoreInteractions();
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
             * for translated parent.
             */
            @Nested
            @DisplayName("and parent translation returns a modified parent")
            class WhenParentChanges {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Path, Context)}.
                 */
                @Test
                @DisplayName("returns a new Path with translated parent")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final PathBuilder<?> parent = ROOT.get("parent");
                    final Path<?> value = parent.get("value", MyType.class);
                    final PathBuilder<?> newParent = ALT_ROOT.get("altparent");
                    final Path<?> expectedResult = newParent.get("value", MyType.class);
                    willReturn(newParent).given(visitor).visit(parent, null);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(Path.class, result);
                    final Path<?> presult = (Path<?>) result;
                    assertNotSame(value, presult);
                    assertEquals(MyType.class, presult.getType());
                    assertEquals(newParent.getRoot(), presult.getRoot());
                    assertEquals(value.getMetadata().getElement(), presult.getMetadata().getElement());
                    assertEquals(value.getMetadata().getName(), presult.getMetadata().getName());
                    assertEquals(value.getMetadata().getPathType(), presult.getMetadata().getPathType());
                    assertEquals(newParent, presult.getMetadata().getParent());
                    assertEquals(expectedResult, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).should().visit(parent, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
         * for path with Expression element.
         */
        @Nested
        @DisplayName("whose element is an Expression")
        class WithExpressionElement{

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
             * for same parent amd element.
             */
            @Nested
            @DisplayName("and both parent and element translations return same values")
            class WhenBothRemain {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Path, Context)}.
                 */
                @Test
                @DisplayName("returns same Path")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final ListPath<MyType, PathBuilder<MyType>> parent = ROOT.getList("parent", MyType.class);
                    final Path<Integer> index = ROOT.getNumber("index", Integer.class);
                    final Path<MyType> value = parent.get(index);
                    willReturn(parent).given(visitor).visit(parent, null);
                    willReturn(index).given(visitor).visit(index, null);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).should().visit(parent, null);
                    then(visitor).should().visit(index, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
             * for translated parent.
             */
            @Nested
            @DisplayName("and parent translation returns a modified parent")
            class WhenParentChanges {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Path, Context)}.
                 */
                @Test
                @DisplayName("returns a new Path with translated parent")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final ListPath<MyType, PathBuilder<MyType>> parent = ROOT.getList("parent", MyType.class);
                    final Path<Integer> index = ROOT.getNumber("index", Integer.class);
                    final Path<MyType> value = parent.get(index);
                    final ListPath<MyType, PathBuilder<MyType>> newParent = ALT_ROOT.getList("altparent", MyType.class);
                    final Path<MyType> expectedResult = newParent.get(index);
                    willReturn(newParent).given(visitor).visit(parent, null);
                    willReturn(index).given(visitor).visit(index, null);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(Path.class, result);
                    final Path<?> presult = (Path<?>) result;
                    assertNotSame(value, presult);
                    assertEquals(MyType.class, presult.getType());
                    assertEquals(newParent.getRoot(), presult.getRoot());
                    assertEquals(index, presult.getMetadata().getElement());
                    assertEquals(value.getMetadata().getPathType(), presult.getMetadata().getPathType());
                    assertEquals(newParent, presult.getMetadata().getParent());
                    assertEquals(expectedResult, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).should().visit(parent, null);
                    then(visitor).should().visit(index, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
             * for translated element.
             */
            @Nested
            @DisplayName("and element translation returns a modified element")
            class WhenElementChanges {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Path, Context)}.
                 */
                @Test
                @DisplayName("returns a new Path with translated element")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final ListPath<MyType, PathBuilder<MyType>> parent = ROOT.getList("parent", MyType.class);
                    final Path<Integer> index = ROOT.getNumber("index", Integer.class);
                    final Path<MyType> value = parent.get(index);
                    final Path<Integer> newIndex = ALT_ROOT.getNumber("altindex", Integer.class);
                    final Path<MyType> expectedResult = parent.get(newIndex);
                    willReturn(parent).given(visitor).visit(parent, null);
                    willReturn(newIndex).given(visitor).visit(index, null);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(Path.class, result);
                    final Path<?> presult = (Path<?>) result;
                    assertNotSame(value, presult);
                    assertEquals(MyType.class, presult.getType());
                    assertEquals(parent.getRoot(), presult.getRoot());
                    assertEquals(newIndex, presult.getMetadata().getElement());
                    assertEquals(value.getMetadata().getPathType(), presult.getMetadata().getPathType());
                    assertEquals(parent, presult.getMetadata().getParent());
                    assertEquals(expectedResult, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).should().visit(parent, null);
                    then(visitor).should().visit(index, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(Path, Context)}
             * for translated element and parent.
             */
            @Nested
            @DisplayName("and both parent and element translations return modified values")
            class WhenBothChange {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(Path, Context)}.
                 */
                @Test
                @DisplayName("returns a new Path with translated element and parent")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final ListPath<MyType, PathBuilder<MyType>> parent = ROOT.getList("parent", MyType.class);
                    final Path<Integer> index = ROOT.getNumber("index", Integer.class);
                    final Path<MyType> value = parent.get(index);
                    final Path<Integer> newIndex = ALT_ROOT.getNumber("altindex", Integer.class);
                    final ListPath<MyType, PathBuilder<MyType>> newParent = ALT_ROOT.getList("altparent", MyType.class);
                    final Path<MyType> expectedResult = newParent.get(newIndex);
                    willReturn(newParent).given(visitor).visit(parent, null);
                    willReturn(newIndex).given(visitor).visit(index, null);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(Path.class, result);
                    final Path<?> presult = (Path<?>) result;
                    assertNotSame(value, presult);
                    assertEquals(MyType.class, presult.getType());
                    assertEquals(newParent.getRoot(), presult.getRoot());
                    assertEquals(newIndex, presult.getMetadata().getElement());
                    assertEquals(value.getMetadata().getPathType(), presult.getMetadata().getPathType());
                    assertEquals(newParent, presult.getMetadata().getParent());
                    assertEquals(expectedResult, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).should().visit(parent, null);
                    then(visitor).should().visit(index, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                }
            }
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}.
     */
    @Nested
    @DisplayName("when visiting a TemplateExpression")
    class ForTemplateExpression {

        /**
         * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
         * for same arguments.
         */
        @Nested
        @DisplayName("and arguments translation returns same arguments")
        class WhenArgumentsRemain {

            /**
             * Test for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}.
             */
            @Test
            @DisplayName("returns same TemplateExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final TemplateExpression<?> value = mock(TemplateExpression.class);
                final List<Expression<?>> args = generateArgs();
                for (int i = 0; i < args.size(); i++) {
                    willReturn(args.get(i)).given(args.get(i)).accept(visitor, null);
                }
                willReturn(args).given(value).getArgs();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertSame(value, result);
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(value).should(atLeastOnce()).getArgs();
                then(value).shouldHaveNoMoreInteractions();
                for (final Expression<?> arg : args) {
                    then(arg).should().accept(visitor, null);
                    then(arg).shouldHaveNoMoreInteractions();
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
         * for translated arguments.
         */
        @Nested
        @DisplayName("and arguments translation returns modified arguments")
        class WhenArgumentsChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}.
             */
            @Test
            @DisplayName("returns a new TemplateExpression with translated arguments")
            void returnsNewInstance() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final TemplateExpression<?> value = mock(TemplateExpression.class);
                final List<Expression<?>> args = generateArgs();
                final Template template = TemplateFactory.DEFAULT.create("Some {0} template for {1}");
                final List<Expression<?>> transformedArgs = generateArgs(args.size());
                for (int i = 0; i < args.size(); i++) {
                    willReturn(transformedArgs.get(i)).given(args.get(i)).accept(visitor, null);
                }
                willReturn(args).given(value).getArgs();
                willReturn(template).given(value).getTemplate();
                willReturn(MyType.class).given(value).getType();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertInstanceOf(TemplateExpression.class, result);
                assertNotSame(value, result);
                assertEquals(transformedArgs, ((TemplateExpression<?>) result).getArgs());
                assertEquals(template, ((TemplateExpression<?>) result).getTemplate());
                assertEquals(MyType.class, result.getType());
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(value).should(atLeastOnce()).getArgs();
                then(value).should(atLeastOnce()).getTemplate();
                then(value).should().getType();
                then(value).shouldHaveNoMoreInteractions();
                for (final Expression<?> arg : args) {
                    then(arg).should().accept(visitor, null);
                    then(arg).shouldHaveNoMoreInteractions();
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
         * for Predicate operation.
         */
        @Nested
        @DisplayName("that implements Predicate")
        class ForPredicate {

            /**
             * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
             * for same arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns same arguments")
            class WhenArgumentsRemain {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}.
                 */
                @Test
                @DisplayName("returns same TemplateExpression")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final PredicateTemplateExpression value = mock(PredicateTemplateExpression.class);
                    final List<Expression<?>> args = generateArgs();
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(args.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
             * for translated arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns modified arguments")
            class WhenArgumentsChange {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}.
                 */
                @Test
                @DisplayName("returns a new TemplateExpression implementing Predicate with translated arguments")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final PredicateTemplateExpression value = mock(PredicateTemplateExpression.class);
                    final List<Expression<?>> args = generateArgs();
                    final Template template = TemplateFactory.DEFAULT.create("Some {0} template for {1}");
                    final List<Expression<?>> transformedArgs = generateArgs(args.size());
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(transformedArgs.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    willReturn(template).given(value).getTemplate();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(TemplateExpression.class, result);
                    assertNotSame(value, result);
                    assertInstanceOf(Predicate.class, result);
                    assertEquals(transformedArgs, ((TemplateExpression<?>) result).getArgs());
                    assertEquals(template, ((TemplateExpression<?>) result).getTemplate());
                    assertEquals(Boolean.class, result.getType());
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).should(atLeastOnce()).getTemplate();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
         * of type Boolean.
         */
        @Nested
        @DisplayName("of type Boolean")
        class OfTypeBoolean {

            /**
             * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
             * for same arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns same arguments")
            class WhenArgumentsRemain {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}.
                 */
                @Test
                @DisplayName("returns same TemplateExpression")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final TemplateExpression<?> value = mock(TemplateExpression.class);
                    final List<Expression<?>> args = generateArgs();
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(args.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}
             * for translated arguments.
             */
            @Nested
            @DisplayName("and arguments translation returns modified arguments")
            class WhenArgumentsChange {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(TemplateExpression, Context)}.
                 */
                @Test
                @DisplayName("returns a new TemplateExpression implementing Predicate with translated arguments")
                void returnsNewInstance() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final TemplateExpression<?> value = mock(TemplateExpression.class);
                    final List<Expression<?>> args = generateArgs();
                    final Template template = TemplateFactory.DEFAULT.create("Some {0} template for {1}");
                    final List<Expression<?>> transformedArgs = generateArgs(args.size());
                    for (int i = 0; i < args.size(); i++) {
                        willReturn(transformedArgs.get(i)).given(args.get(i)).accept(visitor, null);
                    }
                    willReturn(args).given(value).getArgs();
                    willReturn(template).given(value).getTemplate();
                    willReturn(Boolean.class).given(value).getType();
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertInstanceOf(TemplateExpression.class, result);
                    assertNotSame(value, result);
                    assertInstanceOf(Predicate.class, result);
                    assertEquals(transformedArgs, ((TemplateExpression<?>) result).getArgs());
                    assertEquals(template, ((TemplateExpression<?>) result).getTemplate());
                    assertEquals(Boolean.class, result.getType());
                    then(visitor).should().visit(value, null);
                    then(visitor).shouldHaveNoMoreInteractions();
                    then(value).should(atLeastOnce()).getArgs();
                    then(value).should(atLeastOnce()).getType();
                    then(value).should(atLeastOnce()).getTemplate();
                    then(value).shouldHaveNoMoreInteractions();
                    for (final Expression<?> arg : args) {
                        then(arg).should().accept(visitor, null);
                        then(arg).shouldHaveNoMoreInteractions();
                    }
                }
            }
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
     */
    @Nested
    @DisplayName("when visiting a SubQueryExpression")
    class ForSubQueryExpression {

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for same components.
         */
        @Nested
        @DisplayName("and component translation returns same components")
        class WhenComponentsRemain {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns same SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(value).getType();
                willPreserveAllComponents(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertSame(value, result);
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(value).shouldHaveNoMoreInteractions();
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
             * for same components when Having predicate is null.
             */
            @Nested
            @DisplayName("and Having predicate is null")
            class AndHavingIsNull {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
                 */
                @Test
                @DisplayName("returns same SubQueryExpression")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                    final QueryMetadata metadata = generateMetadataWithoutOptionals();
                    metadata.setProjection(generateProjection());
                    metadata.addWhere(generateWhere());
                    willReturn(metadata).given(value).getMetadata();
                    willReturn(MyType.class).given(value).getType();
                    willPreserveQueryFlags(metadata, visitor);
                    willPreserveGroupByExpressions(metadata, visitor);
                    willPreserveJoinExpressions(metadata, visitor);
                    willPreserveOrderSpecifiers(metadata, visitor);
                    willPreserveParams(metadata, visitor);
                    willPreserveProjection(metadata, visitor);
                    willPreserveWhere(metadata, visitor);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    thenShouldVisitAllComponents(metadata, visitor);
                    then(value).should(atLeastOnce()).getMetadata();
                    then(value).shouldHaveNoMoreInteractions();
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
             * for same components when Projection expression is null.
             */
            @Nested
            @DisplayName("and Projection expression is null")
            class AndProjectionIsNull {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
                 */
                @Test
                @DisplayName("returns same SubQueryExpression")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                    final QueryMetadata metadata = generateMetadataWithoutOptionals();
                    metadata.addHaving(generateHaving());
                    metadata.addWhere(generateWhere());
                    willReturn(metadata).given(value).getMetadata();
                    willReturn(MyType.class).given(value).getType();
                    willPreserveQueryFlags(metadata, visitor);
                    willPreserveGroupByExpressions(metadata, visitor);
                    willPreserveHaving(metadata, visitor);
                    willPreserveJoinExpressions(metadata, visitor);
                    willPreserveOrderSpecifiers(metadata, visitor);
                    willPreserveParams(metadata, visitor);
                    willPreserveWhere(metadata, visitor);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    thenShouldVisitAllComponents(metadata, visitor);
                    then(value).should(atLeastOnce()).getMetadata();
                    then(value).shouldHaveNoMoreInteractions();
                }
            }

            /**
             * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
             * for same components when Where predicate is null.
             */
            @Nested
            @DisplayName("and Where predicate is null")
            class AndWhereIsNull {

                /**
                 * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
                 */
                @Test
                @DisplayName("returns same SubQueryExpression")
                void returnsSame() {
                    final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                    final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                    final QueryMetadata metadata = generateMetadataWithoutOptionals();
                    metadata.addHaving(generateHaving());
                    metadata.setProjection(generateProjection());
                    willReturn(metadata).given(value).getMetadata();
                    willReturn(MyType.class).given(value).getType();
                    willPreserveQueryFlags(metadata, visitor);
                    willPreserveGroupByExpressions(metadata, visitor);
                    willPreserveHaving(metadata, visitor);
                    willPreserveJoinExpressions(metadata, visitor);
                    willPreserveOrderSpecifiers(metadata, visitor);
                    willPreserveParams(metadata, visitor);
                    willPreserveProjection(metadata, visitor);
                    final Expression<?> result = visitor.visit(value, null);
                    assertNotNull(result);
                    assertSame(value, result);
                    then(visitor).should().visit(value, null);
                    thenShouldVisitAllComponents(metadata, visitor);
                    then(value).should(atLeastOnce()).getMetadata();
                    then(value).shouldHaveNoMoreInteractions();
                }
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated query flags.
         */
        @Nested
        @DisplayName("and component translation changes query flags")
        class WhenQueryFlagsChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                final Set<QueryFlag> newFlags = willTranslateQueryFlags(metadata, visitor);
                willPreserveGroupByExpressions(metadata, visitor);
                willPreserveHaving(metadata, visitor);
                willPreserveJoinExpressions(metadata, visitor);
                willPreserveOrderSpecifiers(metadata, visitor);
                willPreserveParams(metadata, visitor);
                willPreserveProjection(metadata, visitor);
                willPreserveWhere(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(newFlags, resultSQ.getMetadata().getFlags());
                assertEquals(value.getMetadata().getGroupBy(), resultSQ.getMetadata().getGroupBy());
                assertEquals(value.getMetadata().getHaving(), resultSQ.getMetadata().getHaving());
                assertEquals(value.getMetadata().getJoins(), resultSQ.getMetadata().getJoins());
                assertEquals(value.getMetadata().getOrderBy(), resultSQ.getMetadata().getOrderBy());
                assertEquals(value.getMetadata().getParams(), resultSQ.getMetadata().getParams());
                assertEquals(value.getMetadata().getProjection(), resultSQ.getMetadata().getProjection());
                assertEquals(value.getMetadata().getWhere(), resultSQ.getMetadata().getWhere());
                assertEquals(MyType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(metadata.getProjection()).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated GroupBy expressions.
         */
        @Nested
        @DisplayName("and component translation changes GroupBy expressions")
        class WhenGroupByChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                willPreserveQueryFlags(metadata, visitor);
                final List<Expression<?>> newGroupBy = willTranslateGroupByExpressions(metadata, visitor);
                willPreserveHaving(metadata, visitor);
                willPreserveJoinExpressions(metadata, visitor);
                willPreserveOrderSpecifiers(metadata, visitor);
                willPreserveParams(metadata, visitor);
                willPreserveProjection(metadata, visitor);
                willPreserveWhere(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(value.getMetadata().getFlags(), resultSQ.getMetadata().getFlags());
                assertEquals(newGroupBy, resultSQ.getMetadata().getGroupBy());
                assertEquals(value.getMetadata().getHaving(), resultSQ.getMetadata().getHaving());
                assertEquals(value.getMetadata().getJoins(), resultSQ.getMetadata().getJoins());
                assertEquals(value.getMetadata().getOrderBy(), resultSQ.getMetadata().getOrderBy());
                assertEquals(value.getMetadata().getParams(), resultSQ.getMetadata().getParams());
                assertEquals(value.getMetadata().getProjection(), resultSQ.getMetadata().getProjection());
                assertEquals(value.getMetadata().getWhere(), resultSQ.getMetadata().getWhere());
                assertEquals(MyType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(metadata.getProjection()).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated Having predicate.
         */
        @Nested
        @DisplayName("and component translation changes Having predicate")
        class WhenHavingChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression for null Context")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                willPreserveQueryFlags(metadata, visitor);
                willPreserveGroupByExpressions(metadata, visitor);
                final Predicate newHaving = willTranslateHaving(metadata, visitor);
                willPreserveJoinExpressions(metadata, visitor);
                willPreserveOrderSpecifiers(metadata, visitor);
                willPreserveParams(metadata, visitor);
                willPreserveProjection(metadata, visitor);
                willPreserveWhere(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(value.getMetadata().getFlags(), resultSQ.getMetadata().getFlags());
                assertEquals(value.getMetadata().getGroupBy(), resultSQ.getMetadata().getGroupBy());
                assertEquals(newHaving, resultSQ.getMetadata().getHaving());
                assertEquals(value.getMetadata().getJoins(), resultSQ.getMetadata().getJoins());
                assertEquals(value.getMetadata().getOrderBy(), resultSQ.getMetadata().getOrderBy());
                assertEquals(value.getMetadata().getParams(), resultSQ.getMetadata().getParams());
                assertEquals(value.getMetadata().getProjection(), resultSQ.getMetadata().getProjection());
                assertEquals(value.getMetadata().getWhere(), resultSQ.getMetadata().getWhere());
                assertEquals(MyType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(metadata.getProjection()).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated Join expressions.
         */
        @Nested
        @DisplayName("and component translation changes Join expressions")
        class WhenJoinsChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                willPreserveQueryFlags(metadata, visitor);
                willPreserveGroupByExpressions(metadata, visitor);
                willPreserveHaving(metadata, visitor);
                final List<JoinExpression> newJoins = willTranslateJoinExpressions(metadata, visitor);
                willPreserveOrderSpecifiers(metadata, visitor);
                willPreserveParams(metadata, visitor);
                willPreserveProjection(metadata, visitor);
                willPreserveWhere(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(value.getMetadata().getFlags(), resultSQ.getMetadata().getFlags());
                assertEquals(value.getMetadata().getGroupBy(), resultSQ.getMetadata().getGroupBy());
                assertEquals(value.getMetadata().getHaving(), resultSQ.getMetadata().getHaving());
                assertEquals(newJoins, resultSQ.getMetadata().getJoins());
                assertEquals(value.getMetadata().getOrderBy(), resultSQ.getMetadata().getOrderBy());
                assertEquals(value.getMetadata().getParams(), resultSQ.getMetadata().getParams());
                assertEquals(value.getMetadata().getProjection(), resultSQ.getMetadata().getProjection());
                assertEquals(value.getMetadata().getWhere(), resultSQ.getMetadata().getWhere());
                assertEquals(MyType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(metadata.getProjection()).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated OrderBy expressions.
         */
        @Nested
        @DisplayName("and component translation changes OrderBy expressions")
        class WhenOrderByChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                willPreserveQueryFlags(metadata, visitor);
                willPreserveGroupByExpressions(metadata, visitor);
                willPreserveHaving(metadata, visitor);
                willPreserveJoinExpressions(metadata, visitor);
                final List<OrderSpecifier<?>> newOrderBy = willTranslateOrderSpecifiers(metadata, visitor);
                willPreserveParams(metadata, visitor);
                willPreserveProjection(metadata, visitor);
                willPreserveWhere(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(value.getMetadata().getFlags(), resultSQ.getMetadata().getFlags());
                assertEquals(value.getMetadata().getGroupBy(), resultSQ.getMetadata().getGroupBy());
                assertEquals(value.getMetadata().getHaving(), resultSQ.getMetadata().getHaving());
                assertEquals(value.getMetadata().getJoins(), resultSQ.getMetadata().getJoins());
                assertEquals(newOrderBy, resultSQ.getMetadata().getOrderBy());
                assertEquals(value.getMetadata().getParams(), resultSQ.getMetadata().getParams());
                assertEquals(value.getMetadata().getProjection(), resultSQ.getMetadata().getProjection());
                assertEquals(value.getMetadata().getWhere(), resultSQ.getMetadata().getWhere());
                assertEquals(MyType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(metadata.getProjection()).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated parameter expressions.
         */
        @Nested
        @DisplayName("and component translation changes parameter expressions")
        class WhenParamsChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                willPreserveQueryFlags(metadata, visitor);
                willPreserveGroupByExpressions(metadata, visitor);
                willPreserveHaving(metadata, visitor);
                willPreserveJoinExpressions(metadata, visitor);
                willPreserveOrderSpecifiers(metadata, visitor);
                final Map<ParamExpression<?>, Object> newParams = willTranslateParams(metadata, visitor);
                willPreserveProjection(metadata, visitor);
                willPreserveWhere(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(value.getMetadata().getFlags(), resultSQ.getMetadata().getFlags());
                assertEquals(value.getMetadata().getGroupBy(), resultSQ.getMetadata().getGroupBy());
                assertEquals(value.getMetadata().getHaving(), resultSQ.getMetadata().getHaving());
                assertEquals(value.getMetadata().getJoins(), resultSQ.getMetadata().getJoins());
                assertEquals(value.getMetadata().getOrderBy(), resultSQ.getMetadata().getOrderBy());
                assertEquals(newParams, resultSQ.getMetadata().getParams());
                assertEquals(value.getMetadata().getProjection(), resultSQ.getMetadata().getProjection());
                assertEquals(value.getMetadata().getWhere(), resultSQ.getMetadata().getWhere());
                assertEquals(MyType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(metadata.getProjection()).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated Projection expressions.
         */
        @Nested
        @DisplayName("and component translation changes Projection expressions")
        class WhenProjectionChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                willPreserveQueryFlags(metadata, visitor);
                willPreserveGroupByExpressions(metadata, visitor);
                willPreserveHaving(metadata, visitor);
                willPreserveJoinExpressions(metadata, visitor);
                willPreserveOrderSpecifiers(metadata, visitor);
                willPreserveParams(metadata, visitor);
                final Expression<?> newProjection = willTranslateProjection(metadata, visitor);
                willPreserveWhere(metadata, visitor);
                willReturn(MyOtherType.class).given(newProjection).getType();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(value.getMetadata().getFlags(), resultSQ.getMetadata().getFlags());
                assertEquals(value.getMetadata().getGroupBy(), resultSQ.getMetadata().getGroupBy());
                assertEquals(value.getMetadata().getHaving(), resultSQ.getMetadata().getHaving());
                assertEquals(value.getMetadata().getJoins(), resultSQ.getMetadata().getJoins());
                assertEquals(value.getMetadata().getOrderBy(), resultSQ.getMetadata().getOrderBy());
                assertEquals(value.getMetadata().getParams(), resultSQ.getMetadata().getParams());
                assertEquals(newProjection, resultSQ.getMetadata().getProjection());
                assertEquals(value.getMetadata().getWhere(), resultSQ.getMetadata().getWhere());
                assertEquals(MyOtherType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(newProjection).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated Where predicate.
         */
        @Nested
        @DisplayName("and component translation changes Where predicate")
        class WhenWhereChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                willPreserveQueryFlags(metadata, visitor);
                willPreserveGroupByExpressions(metadata, visitor);
                willPreserveHaving(metadata, visitor);
                willPreserveJoinExpressions(metadata, visitor);
                willPreserveOrderSpecifiers(metadata, visitor);
                willPreserveParams(metadata, visitor);
                willPreserveProjection(metadata, visitor);
                final Predicate newWhere = willTranslateWhere(metadata, visitor);
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(value.getMetadata().getFlags(), resultSQ.getMetadata().getFlags());
                assertEquals(value.getMetadata().getGroupBy(), resultSQ.getMetadata().getGroupBy());
                assertEquals(value.getMetadata().getHaving(), resultSQ.getMetadata().getHaving());
                assertEquals(value.getMetadata().getJoins(), resultSQ.getMetadata().getJoins());
                assertEquals(value.getMetadata().getOrderBy(), resultSQ.getMetadata().getOrderBy());
                assertEquals(value.getMetadata().getParams(), resultSQ.getMetadata().getParams());
                assertEquals(value.getMetadata().getProjection(), resultSQ.getMetadata().getProjection());
                assertEquals(newWhere, resultSQ.getMetadata().getWhere());
                assertEquals(MyType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(metadata.getProjection()).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}
         * for translated Where predicate.
         */
        @Nested
        @DisplayName("and component translation changes every component")
        class WhenComponentsChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(SubQueryExpression, Context)}.
             */
            @Test
            @DisplayName("returns new SubQueryExpression")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final SubQueryExpression<?> value = mock(SubQueryExpression.class);
                final QueryMetadata metadata = generateMetadata();
                willReturn(metadata).given(value).getMetadata();
                willReturn(MyType.class).given(metadata.getProjection()).getType();
                willReturn(MyType.class).given(value).getType();
                final Set<QueryFlag> newFlags = willTranslateQueryFlags(metadata, visitor);
                final List<Expression<?>> newGroupBy = willTranslateGroupByExpressions(metadata, visitor);
                final Predicate newHaving = willTranslateHaving(metadata, visitor);
                final List<JoinExpression> newJoins = willTranslateJoinExpressions(metadata, visitor);
                final List<OrderSpecifier<?>> newOrderBy = willTranslateOrderSpecifiers(metadata, visitor);
                final Map<ParamExpression<?>, Object> newParams = willTranslateParams(metadata, visitor);
                final Expression<?> newProjection = willTranslateProjection(metadata, visitor);
                final Predicate newWhere = willTranslateWhere(metadata, visitor);
                willReturn(MyOtherType.class).given(newProjection).getType();
                final Expression<?> result = visitor.visit(value, null);
                assertNotNull(result);
                assertNotSame(value, result);
                final SubQueryExpression<?> resultSQ = assertInstanceOf(SubQueryExpression.class, result);
                assertNotEquals(value, resultSQ);
                assertEquals(value.getMetadata().isDistinct(), resultSQ.getMetadata().isDistinct());
                assertEquals(value.getMetadata().getModifiers(), resultSQ.getMetadata().getModifiers());
                assertEquals(value.getMetadata().isUnique(), resultSQ.getMetadata().isUnique());
                assertEquals(newFlags, resultSQ.getMetadata().getFlags());
                assertEquals(newGroupBy, resultSQ.getMetadata().getGroupBy());
                assertEquals(newHaving, resultSQ.getMetadata().getHaving());
                assertEquals(newJoins, resultSQ.getMetadata().getJoins());
                assertEquals(newOrderBy, resultSQ.getMetadata().getOrderBy());
                assertEquals(newParams, resultSQ.getMetadata().getParams());
                assertEquals(newProjection, resultSQ.getMetadata().getProjection());
                assertEquals(newWhere, resultSQ.getMetadata().getWhere());
                assertEquals(MyOtherType.class, resultSQ.getType());
                then(visitor).should().visit(value, null);
                thenShouldVisitAllComponents(metadata, visitor);
                then(value).should(atLeastOnce()).getMetadata();
                then(newProjection).should(atLeastOnce()).getType();
                then(value).shouldHaveNoMoreInteractions();
            }
        }

        @SuppressWarnings("unchecked")
        QueryMetadata generateMetadata() {
            DefaultQueryMetadata metadata = new DefaultQueryMetadata();
            metadata.setDistinct(RandomUtils.nextBoolean());
            metadata.setModifiers(generateQueryModifiers());
            metadata.setUnique(RandomUtils.nextBoolean());
            for (QueryFlag flag: generateQueryFlags()) {
                metadata.addFlag(flag);
            }
            for (Expression<?> expr : generateGroupBy()) {
                metadata.addGroupBy(expr);
            }
            metadata.addHaving(generateHaving());
            for (JoinExpression expr : generateJoinExpressions()) {
                metadata.addJoin(expr.getType(), expr.getTarget());
                if (expr.getCondition() != null) {
                    metadata.addJoinCondition(expr.getCondition());
                }
                for (JoinFlag flag : expr.getFlags()) {
                    metadata.addJoinFlag(flag);
                }
            }
            for (OrderSpecifier<?> expr : generateOrderSpecifiers()) {
                metadata.addOrderBy(expr);
            }
            for (Map.Entry<ParamExpression<?>, Object> entry : generateParams().entrySet()) {
                metadata.setParam((ParamExpression<Object>) entry.getKey(), entry.getValue());
            }
            metadata.setProjection(generateProjection());
            metadata.addWhere(generateWhere());
            return metadata;
        }

        @SuppressWarnings("unchecked")
        QueryMetadata generateMetadataWithoutOptionals() {
            DefaultQueryMetadata metadata = new DefaultQueryMetadata();
            metadata.setDistinct(RandomUtils.nextBoolean());
            metadata.setModifiers(generateQueryModifiers());
            metadata.setUnique(RandomUtils.nextBoolean());
            for (QueryFlag flag: generateQueryFlags()) {
                metadata.addFlag(flag);
            }
            for (Expression<?> expr : generateGroupBy()) {
                metadata.addGroupBy(expr);
            }
            for (JoinExpression expr : generateJoinExpressions()) {
                metadata.addJoin(expr.getType(), expr.getTarget());
                if (expr.getCondition() != null) {
                    metadata.addJoinCondition(expr.getCondition());
                }
                for (JoinFlag flag : expr.getFlags()) {
                    metadata.addJoinFlag(flag);
                }
            }
            for (OrderSpecifier<?> expr : generateOrderSpecifiers()) {
                metadata.addOrderBy(expr);
            }
            for (Map.Entry<ParamExpression<?>, Object> entry : generateParams().entrySet()) {
                metadata.setParam((ParamExpression<Object>) entry.getKey(), entry.getValue());
            }
            return metadata;
        }

        QueryModifiers generateQueryModifiers() {
            final QueryModifiers result;
            if (RandomUtils.nextBoolean()) {
                result = QueryModifiers.EMPTY;
            } else {
                result = new QueryModifiers(RandomUtils.nextLong(), RandomUtils.nextLong());
            }
            return result;
        }

        Set<QueryFlag> generateQueryFlags() {
            final Set<QueryFlag> flags = new HashSet<>();
            final int count = RandomUtils.nextInt(1, 2);
            for (int i = 0; i < count; i++) {
                flags.add(generateQueryFlag());
            }
            return flags;
        }

        QueryFlag generateQueryFlag() {
            final int positionIndex = RandomUtils.nextInt(0, QueryFlag.Position.values().length);
            final QueryFlag.Position position = QueryFlag.Position.values()[positionIndex];
            final Expression<?> expr = mock(Expression.class);
            return new QueryFlag(position, expr);
        }

        List<Expression<?>> generateGroupBy() {
            final List<Expression<?>> result = new ArrayList<>();
            final int count = RandomUtils.nextInt(1, 5);
            for (int i = 0; i < count; i++) {
                result.add(mock(Expression.class));
            }
            return result;
        }

        Predicate generateHaving() {
            final Predicate result = mock(Predicate.class);
            withPredicateExtraction(result);
            return result;
        }

        List<JoinExpression> generateJoinExpressions() {
            final List<JoinExpression> result = new ArrayList<>();
            final int count = RandomUtils.nextInt(1, 3);
            final Path<?> target = mock(Path.class);
            final PathMetadata metadata = new PathMetadata(null, target.toString(), PathType.VARIABLE);
            willReturn(metadata).given(target).getMetadata();
            result.add(new JoinExpression(JoinType.DEFAULT, target));
            for (int i = 1; i < count; i++) {
                result.add(generateJoinExpression());
            }
            return result;
        }

        JoinExpression generateJoinExpression() {
            final int typeIndex = RandomUtils.nextInt(0, JoinType.values().length);
            final JoinType type = JoinType.values()[typeIndex];
            final Path<?> target = mock(Path.class);
            final PathMetadata metadata = new PathMetadata(null, target.toString(), PathType.VARIABLE);
            willReturn(metadata).given(target).getMetadata();
            final Predicate condition = mock(Predicate.class);
            withPredicateExtraction(condition);
            final Set<JoinFlag> flags = generateJoinFlags();
            return new JoinExpression(type, target, condition, flags);
        }

        Set<JoinFlag> generateJoinFlags() {
            final Set<JoinFlag> flags = new HashSet<>();
            final int count = RandomUtils.nextInt(1, 2);
            for (int i = 0; i < count; i++) {
                flags.add(generateJoinFlag());
            }
            return flags;
        }

        JoinFlag generateJoinFlag() {
            final int positionIndex = RandomUtils.nextInt(0, JoinFlag.Position.values().length);
            final JoinFlag.Position position = JoinFlag.Position.values()[positionIndex];
            final Expression<?> expr = mock(Expression.class);
            return new JoinFlag(expr, position);
        }

        List<OrderSpecifier<?>> generateOrderSpecifiers() {
            final List<OrderSpecifier<?>> result = new ArrayList<>();
            final int count = RandomUtils.nextInt(1, 2);
            for (int i = 0; i < count; i++) {
                result.add(generateOrderSpecifier());
            }
            return result;
        }

        Map<ParamExpression<?>, Object> generateParams() {
            final Map<ParamExpression<?>, Object> result = new HashMap<>();
            final int count = RandomUtils.nextInt(1, 2);
            for (int i = 0; i < count; i++) {
                result.put(mock(ParamExpression.class), new Object());
            }
            return result;
        }

        Expression<?> generateProjection() {
            return mock(Expression.class);
        }

        Predicate generateWhere() {
            final Predicate result = mock(Predicate.class);
            withPredicateExtraction(result);
            return result;
        }

        void willPreserveAllComponents(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            willPreserveQueryFlags(metadata, visitor);
            willPreserveGroupByExpressions(metadata, visitor);
            willPreserveHaving(metadata, visitor);
            willPreserveJoinExpressions(metadata, visitor);
            willPreserveOrderSpecifiers(metadata, visitor);
            willPreserveParams(metadata, visitor);
            willPreserveProjection(metadata, visitor);
            willPreserveWhere(metadata, visitor);
        }

        void willPreserveQueryFlags(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            for (QueryFlag flag : metadata.getFlags()) {
                final Expression<?> expr = flag.getFlag();
                willReturn(expr).given(expr).accept(visitor, null);
            }
        }

        Set<QueryFlag> willTranslateQueryFlags(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Set<QueryFlag> result = new HashSet<>();
            for (QueryFlag flag : metadata.getFlags()) {
                final Expression<?> expr = flag.getFlag();
                final Expression<?> translated = mock(Expression.class);
                willReturn(translated).given(expr).accept(visitor, null);
                result.add(new QueryFlag(flag.getPosition(), translated));
            }
            return result;
        }

        void willPreserveGroupByExpressions(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            for (Expression<?> expr : metadata.getGroupBy()) {
                willReturn(expr).given(expr).accept(visitor, null);
            }
        }

        List<Expression<?>> willTranslateGroupByExpressions(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final List<Expression<?>> result = new ArrayList<>();
            for (Expression<?> expr : metadata.getGroupBy()) {
                final Expression<?> translated = mock(Expression.class);
                willReturn(translated).given(expr).accept(visitor, null);
                result.add(translated);
            }
            return result;
        }

        void willPreserveHaving(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Predicate expr = metadata.getHaving();
            willReturn(expr).given(expr).accept(visitor, null);
        }

        Predicate willTranslateHaving(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Predicate expr = metadata.getHaving();
            final Predicate translated = mock(Predicate.class);
            willReturn(translated).given(expr).accept(visitor, null);
            withPredicateExtraction(translated);
            return translated;
        }

        void willPreserveJoinExpressions(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            for (JoinExpression je : metadata.getJoins()) {
                willPreserveJoinExpression(je, visitor);
            }
        }

        void willPreserveJoinExpression(
                final JoinExpression expr,
                final BaseExpressionTransformer<?> visitor) {
            final Expression<?> target = expr.getTarget();
            willReturn(target).given(target).accept(visitor, null);
            final Predicate condition = expr.getCondition();
            if (condition != null) {
                willReturn(condition).given(condition).accept(visitor, null);
            }
            for (JoinFlag flag : expr.getFlags()) {
                final Expression<?> flagExpr = flag.getFlag();
                willReturn(flagExpr).given(flagExpr).accept(visitor, null);
            }
        }

        List<JoinExpression> willTranslateJoinExpressions(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final List<JoinExpression> result = new ArrayList<>();
            for (JoinExpression je : metadata.getJoins()) {
                result.add(willTranslateJoinExpression(je, visitor));
            }
            return result;
        }

        JoinExpression willTranslateJoinExpression(
                final JoinExpression expr,
                final BaseExpressionTransformer<?> visitor) {
            final Expression<?> target = expr.getTarget();
            final Path<?> translatedTarget = mock(Path.class);
            final PathMetadata metadata = new PathMetadata(null, target.toString(), PathType.VARIABLE);
            willReturn(metadata).given(translatedTarget).getMetadata();
            willReturn(translatedTarget).given(target).accept(visitor, null);
            final Predicate condition = expr.getCondition();
            final Predicate translatedCondition;
            if (condition == null) {
                translatedCondition = null;
            } else {
                translatedCondition = mock(Predicate.class);
                willReturn(translatedCondition).given(condition).accept(visitor, null);
            }
            final Set<JoinFlag> translatedFlags = willTranslateJoinFlags(expr.getFlags(), visitor);
            return new JoinExpression(expr.getType(), translatedTarget, translatedCondition, translatedFlags);
        }

        Set<JoinFlag> willTranslateJoinFlags(
                final Set<JoinFlag> flags,
                final BaseExpressionTransformer<?> visitor) {
            final Set<JoinFlag> result = new HashSet<>();
            for (JoinFlag flag : flags) {
                final Expression<?> flagExpr = flag.getFlag();
                final Expression<?> translated = mock(Expression.class);
                willReturn(translated).given(flagExpr).accept(visitor, null);
                result.add(new JoinFlag(translated, flag.getPosition()));
            }
            return result;
        }

        void willPreserveOrderSpecifiers(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            for (OrderSpecifier<?> os : metadata.getOrderBy()) {
                willPreserveOrderSpecifier(os, visitor);
            }
        }

        void willPreserveOrderSpecifier(
                final OrderSpecifier<?> os,
                final BaseExpressionTransformer<?> visitor) {
            willReturn(Arrays.asList(os)).given(visitor).visit(os, null);
        }

        List<OrderSpecifier<?>> willTranslateOrderSpecifiers(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final List<OrderSpecifier<?>> result = new ArrayList<>();
            for (OrderSpecifier<?> os : metadata.getOrderBy()) {
                result.addAll(willTranslateOrderSpecifier(os, visitor));
            }
            return result;
        }

        List<OrderSpecifier<?>> willTranslateOrderSpecifier(
                final OrderSpecifier<?> os,
                final BaseExpressionTransformer<?> visitor) {
            final List<OrderSpecifier<?>> result = generateOrderSpecifiers();
            willReturn(result).given(visitor).visit(os, null);
            return result;
        }

        void willPreserveParams(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            for (ParamExpression<?> expr : metadata.getParams().keySet()) {
                willReturn(expr).given(expr).accept(visitor, null);
            }
        }

        Map<ParamExpression<?>, Object> willTranslateParams(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Map<ParamExpression<?>, Object> result = new HashMap<>();
            for (Map.Entry<ParamExpression<?>, Object> entry : metadata.getParams().entrySet()) {
                final ParamExpression<?> translated = mock(ParamExpression.class);
                willReturn(translated).given(entry.getKey()).accept(visitor, null);
                result.put(translated, entry.getValue());
            }
            return result;
        }

        void willPreserveProjection(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Expression<?> expr = metadata.getProjection();
            willReturn(expr).given(expr).accept(visitor, null);
        }

        Expression<?> willTranslateProjection(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Expression<?> expr = metadata.getProjection();
            final Expression<?> translated = mock(Expression.class);
            willReturn(translated).given(expr).accept(visitor, null);
            return translated;
        }

        void willPreserveWhere(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Predicate expr = metadata.getWhere();
            willReturn(expr).given(expr).accept(visitor, null);
        }

        Predicate willTranslateWhere(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            final Predicate expr = metadata.getWhere();
            final Predicate translated = mock(Predicate.class);
            willReturn(translated).given(expr).accept(visitor, null);
            withPredicateExtraction(translated);
            return translated;
        }

        void thenShouldVisitAllComponents(
                final QueryMetadata metadata,
                final BaseExpressionTransformer<?> visitor) {
            for (QueryFlag flag : metadata.getFlags()) {
                then(flag.getFlag()).should().accept(visitor, null);
            }
            for (Expression<?> expr : metadata.getGroupBy()) {
                then(expr).should().accept(visitor, null);
            }
            if (metadata.getHaving() != null) {
                then(metadata.getHaving()).should().accept(visitor, null);
            }
            for (JoinExpression je : metadata.getJoins()) {
                then(je.getTarget()).should().accept(visitor, null);
                if (je.getCondition() != null) {
                    then(je.getCondition()).should().accept(visitor, null);
                }
                for (JoinFlag flag : je.getFlags()) {
                    then(flag.getFlag()).should().accept(visitor, null);
                }
            }
            for (OrderSpecifier<?> os : metadata.getOrderBy()) {
                then(visitor).should().visit(os, null);
            }
            for (ParamExpression<?> param : metadata.getParams().keySet()) {
                then(param).should().accept(visitor, null);
            }
            if (metadata.getProjection() != null) {
                then(metadata.getProjection()).should().accept(visitor, null);
            }
            if (metadata.getWhere() != null) {
                then(metadata.getWhere()).should().accept(visitor, null);
            }
        }

        void withPredicateExtraction(final Predicate expr) {
            willReturn(expr).given(expr).accept(
                    argThat(v -> {
                        return v.getClass().getSimpleName().equals("ExtractorVisitor");
                    }),
                    isNull());
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(OrderSpecifier, Context)}.
     */
    @Nested
    @DisplayName("when visiting an OrderSpecifier")
    class ForOrderSpecifier {

        /**
         * Tests for {@link BaseExpressionTransformer#visit(OrderSpecifier, Context)}
         * for same target expression.
         */
        @Nested
        @DisplayName("and target expression translation returns same expression")
        class WhenTargetRemains {

            /**
             * Test for {@link BaseExpressionTransformer#visit(OrderSpecifier, Context)}.
             */
            @Test
            @DisplayName("returns singleton list with same OrderSpecifier")
            void returnsSame() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final OrderSpecifier<?> value = generateOrderSpecifier();
                final Expression<?> target = value.getTarget();
                willReturn(target).given(target).accept(visitor, null);
                willReturn(Comparable.class).given(target).getType();
                final List<OrderSpecifier<?>> result = visitor.visit(value, null);
                assertNotNull(result);
                assertEquals(1, result.size());
                assertSame(value, result.get(0));
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(target).should().accept(visitor, null);
                then(target).should(atLeast(0)).getType();
                then(target).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(OrderSpecifier, Context)}
         * for translated target expression.
         */
        @Nested
        @DisplayName("and target expression translation returns modified expression")
        class WhenTargetChanges {

            /**
             * Test for {@link BaseExpressionTransformer#visit(OrderSpecifier, Context)}.
             */
            @Test
            @DisplayName("returns singleton list with new OrderSpecifier")
            void returnsAdapter() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final OrderSpecifier<?> value = generateOrderSpecifier();
                final Expression<?> target = value.getTarget();
                final ComparableExpression<?> newTarget = mock(ComparableExpression.class);
                willReturn(newTarget).given(target).accept(visitor, null);
                willReturn(Comparable.class).given(newTarget).getType();
                final List<OrderSpecifier<?>> result = visitor.visit(value, null);
                assertNotNull(result);
                assertEquals(1, result.size());
                OrderSpecifier<?> resultOrder = (OrderSpecifier<?>) result.get(0);
                assertNotSame(value, resultOrder);
                assertSame(newTarget, resultOrder.getTarget());
                assertSame(value.getOrder(), resultOrder.getOrder());
                assertSame(value.getNullHandling(), resultOrder.getNullHandling());
                then(visitor).should().visit(value, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(target).should().accept(visitor, null);
                then(target).shouldHaveNoMoreInteractions();
           }
        }
    }

    /**
     * Tests for {@link BaseExpressionTransformer#visit(StoredValues, Context)}.
     */
    @Nested
    @DisplayName("when visiting a ValuesStoreClause")
    class ForValuesStoreClause {

        /**
         * Tests for {@link BaseExpressionTransformer#visit(StoredValue, Context)}
         * for same path and value.
         */
        @Nested
        @DisplayName("and path and value translations return same values")
        class WhenPathAndValueRemain {

            /**
             * Test for {@link BaseExpressionTransformer#visit(StoredValue, Context)}.
             */
            @Test
            @DisplayName("returns singleton ValueAssignments with same ValueAssignment")
            void returnsSameFor() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                @SuppressWarnings("unchecked")
                final Path<MyType> path = mock(Path.class);
                @SuppressWarnings("unchecked")
                final Expression<MyType> valueExpr = mock(Expression.class);
                final StoredValue<?> value = StoredValue.of(path, valueExpr);
                final StoredValues values = StoredValues.with(value);
                willReturn(path).given(path).accept(visitor, null);
                willReturn(valueExpr).given(valueExpr).accept(visitor, null);
                final StoredValues result = visitor.visit(values, null);
                assertNotNull(result);
                assertEquals(1, result.size());
                assertSame(valueExpr, result.get(path));
                then(visitor).should().visit(values, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(path).should().accept(visitor, null);
                then(path).shouldHaveNoMoreInteractions();
                then(valueExpr).should().accept(visitor, null);
                then(valueExpr).shouldHaveNoMoreInteractions();
            }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(StoredValue, Context)}
         * for translated path.
         */
        @Nested
        @DisplayName("and path translation returns modified values")
        class WhenPathChanges {

            /**
             * Test for {@link BaseExpressionTransformer#visit(StoredValue, Context)}.
             */
            @Test
            @DisplayName("returns singleton ValueAssignments with new ValueAssignment")
            void returnsAdapter() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final StoredValue<?> value = generateValueAssignment();
                final StoredValues values = StoredValues.with(value);
                final Path<?> path = value.getPath();
                final Expression<?> valueExpr = value.getValue();
                final Path<?> newPath = mock(Path.class);
                willReturn(newPath).given(path).accept(visitor, null);
                willReturn(valueExpr).given(valueExpr).accept(visitor, null);
                willReturn(MyType.class).given(newPath).getType();
                willReturn(MyType.class).given(valueExpr).getType();
                final StoredValues result = visitor.visit(values, null);
                assertNotNull(result);
                assertEquals(1, result.size());
                assertSame(valueExpr, result.get(newPath));
                then(visitor).should().visit(values, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(path).should().accept(visitor, null);
                then(path).shouldHaveNoMoreInteractions();
                then(valueExpr).should().accept(visitor, null);
                then(valueExpr).should().getType();
                then(valueExpr).shouldHaveNoMoreInteractions();
                then(newPath).should().getType();
                then(newPath).shouldHaveNoMoreInteractions();
           }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(StoredValue, Context)}
         * for translated path the is not instance of Path.
         */
        @Nested
        @DisplayName("and path translation returns modified values")
        class WhenPathTranslationIsNotPath {

            /**
             * Test for {@link BaseExpressionTransformer#visit(StoredValue, Context)}.
             */
            @Test
            @DisplayName("throws IllegalArgumentException")
            void returnsAdapter() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final StoredValue<?> value = generateValueAssignment();
                final StoredValues values = StoredValues.with(value);
                final Path<?> path = value.getPath();
                final Expression<?> valueExpr = value.getValue();
                final Expression<?> newPath = mock(Expression.class);
                willReturn(newPath).given(path).accept(visitor, null);
                willReturn(valueExpr).given(valueExpr).accept(visitor, null);
                assertThrows(IllegalArgumentException.class, () -> visitor.visit(values, null));
                then(visitor).should().visit(values, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(path).should().accept(visitor, null);
                then(path).shouldHaveNoMoreInteractions();
                then(valueExpr).should(atMostOnce()).accept(visitor, null);
                then(valueExpr).shouldHaveNoMoreInteractions();
                then(newPath).shouldHaveNoMoreInteractions();
           }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(StoredValue, Context)}
         * for translated value.
         */
        @Nested
        @DisplayName("and value translation returns modified values")
        class WhenValueChanges {

            /**
             * Test for {@link BaseExpressionTransformer#visit(StoredValue, Context)}.
             */
            @Test
            @DisplayName("returns singleton ValueAssignments with new ValueAssignment")
            void returnsAdapter() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final StoredValue<?> value = generateValueAssignment();
                final StoredValues values = StoredValues.with(value);
                final Path<?> path = value.getPath();
                final Expression<?> valueExpr = value.getValue();
                final Expression<?> newValueExpr = mock(Expression.class);
                willReturn(path).given(path).accept(visitor, null);
                willReturn(newValueExpr).given(valueExpr).accept(visitor, null);
                willReturn(MyType.class).given(path).getType();
                willReturn(MyType.class).given(newValueExpr).getType();
                final StoredValues result = visitor.visit(values, null);
                assertNotNull(result);
                assertEquals(1, result.size());
                assertSame(newValueExpr, result.get(path));
                then(visitor).should().visit(values, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(path).should().accept(visitor, null);
                then(path).should().getType();
                then(path).shouldHaveNoMoreInteractions();
                then(valueExpr).should().accept(visitor, null);
                then(valueExpr).shouldHaveNoMoreInteractions();
                then(newValueExpr).should().getType();
                then(newValueExpr).shouldHaveNoMoreInteractions();
           }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(StoredValue, Context)}
         * for translated path and value.
         */
        @Nested
        @DisplayName("and path and value translations return modified values")
        class WhenPathAndValueChange {

            /**
             * Test for {@link BaseExpressionTransformer#visit(StoredValue, Context)}.
             */
            @Test
            @DisplayName("returns singleton ValueAssignments with new ValueAssignment")
            void returnsAdapter() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final StoredValue<?> value = generateValueAssignment();
                final StoredValues values = StoredValues.with(value);
                final Path<?> path = value.getPath();
                final Expression<?> valueExpr = value.getValue();
                final Path<?> newPath = mock(Path.class);
                final Expression<?> newValueExpr = mock(Expression.class);
                willReturn(newPath).given(path).accept(visitor, null);
                willReturn(newValueExpr).given(valueExpr).accept(visitor, null);
                willReturn(MyType.class).given(newPath).getType();
                willReturn(MyType.class).given(newValueExpr).getType();
                final StoredValues result = visitor.visit(values, null);
                assertNotNull(result);
                assertEquals(1, result.size());
                assertSame(newValueExpr, result.get(newPath));
                then(visitor).should().visit(values, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(path).should().accept(visitor, null);
                then(path).shouldHaveNoMoreInteractions();
                then(valueExpr).should().accept(visitor, null);
                then(valueExpr).shouldHaveNoMoreInteractions();
                then(newPath).should().getType();
                then(newPath).shouldHaveNoMoreInteractions();
                then(newValueExpr).should().getType();
                then(newValueExpr).shouldHaveNoMoreInteractions();
           }
        }

        /**
         * Tests for {@link BaseExpressionTransformer#visit(StoredValue, Context)}
         * for translated path and value.
         */
        @Nested
        @DisplayName("and path and value translations return modified values of incompatible types")
        class WhenTranslatedPathAndValueTypesDontMatch {

            /**
             * Test for {@link BaseExpressionTransformer#visit(StoredValue, Context)}.
             */
            @Test
            @DisplayName("throws IllegalArgumentException")
            void returnsAdapter() {
                final BaseExpressionTransformer<?> visitor = spy(BaseExpressionTransformer.class);
                final StoredValue<?> value = generateValueAssignment();
                final StoredValues values = StoredValues.with(value);
                final Path<?> path = value.getPath();
                final Expression<?> valueExpr = value.getValue();
                final Path<?> newPath = mock(Path.class);
                final Expression<?> newValueExpr = mock(Expression.class);
                willReturn(newPath).given(path).accept(visitor, null);
                willReturn(newValueExpr).given(valueExpr).accept(visitor, null);
                willReturn(MyType.class).given(newPath).getType();
                willReturn(MyOtherType.class).given(newValueExpr).getType();
                assertThrows(IllegalArgumentException.class, () -> visitor.visit(values, null));
                then(visitor).should().visit(values, null);
                then(visitor).shouldHaveNoMoreInteractions();
                then(path).should().accept(visitor, null);
                then(path).shouldHaveNoMoreInteractions();
                then(valueExpr).should().accept(visitor, null);
                then(valueExpr).shouldHaveNoMoreInteractions();
                then(newPath).should().getType();
                then(newPath).shouldHaveNoMoreInteractions();
                then(newValueExpr).should().getType();
                then(newValueExpr).shouldHaveNoMoreInteractions();
           }
        }
    }

    List<Expression<?>> generateArgs() {
        return generateArgs(RandomUtils.nextInt(1, 10));
    }

    List<Expression<?>> generateArgs(final int count) {
        final List<Expression<?>> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(mock(Expression.class));
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    OrderSpecifier<?> generateOrderSpecifier() {
        final int orderIndex = RandomUtils.nextInt(0, Order.values().length);
        final Order order = Order.values()[orderIndex];
        final ComparableExpression<?> expr = mock(ComparableExpression.class);
        final int nullHandlingIndex = RandomUtils.nextInt(0, OrderSpecifier.NullHandling.values().length);
        final OrderSpecifier.NullHandling nullHandling = OrderSpecifier.NullHandling.values()[nullHandlingIndex];
        return new OrderSpecifier(order, expr, nullHandling);
    }

    @SuppressWarnings("unchecked")
    StoredValue<?> generateValueAssignment() {
        final Path<MyType> path = mock(Path.class);
        final Expression<MyType> value = mock(Expression.class);
        return StoredValue.of(path, value);
    }

    StoredValues generateValueAssignments() {
        return generateValueAssignments(RandomUtils.nextInt(1, 10));
    }

    StoredValues generateValueAssignments(final int count) {
        final StoredValues result = new StoredValues();
        for (int i = 0; i < count; i++) {
            result.add(generateValueAssignment());
        }
        return result;
    }

    private static interface MyType {}
    private static interface MyOtherType {}
    private static interface PredicateOperation
    extends Operation<Boolean>, Predicate {}
    private static interface PredicateTemplateExpression
    extends TemplateExpression<Boolean>, Predicate {}
    private static interface ComparableExpression<T>
    extends Expression<T>, Comparable<ComparableExpression<T>> {}
}
