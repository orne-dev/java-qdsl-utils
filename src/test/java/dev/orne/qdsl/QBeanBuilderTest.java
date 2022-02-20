package dev.orne.qdsl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.QBean;

/**
 * Unit tests for {@code SimplePathTranslator}.
 *
 * @author <a href="mailto:wamphiry@orne.dev">(w) Iker Hernaez</a>
 * @version 1.0, 2021-12
 * @since 0.1
 * @see SimplePathTranslator
 */
@Tag("ut")
class QBeanBuilderTest {

    @Test
    void testConstructor() {
        final QBeanBuilder<TestBean> builder = new QBeanBuilder<>(TestBean.class);
        assertEquals(TestBean.class, builder.getType());
        assertFalse(builder.isFieldAccess());
        assertNotNull(builder.getBindings());
        assertTrue(builder.getBindings().isEmpty());
    }

    @Test
    void testConstructor_Null() {
        assertThrows(NullPointerException.class, () -> {
            new QBeanBuilder<TestBean>(null);
        });
    }

    @Test
    void testWithFieldAccess() {
        final QBeanBuilder<TestBean> builder = new QBeanBuilder<>(TestBean.class)
                .withFieldAccess(true);
        assertEquals(TestBean.class, builder.getType());
        assertTrue(builder.isFieldAccess());
        assertNotNull(builder.getBindings());
        assertTrue(builder.getBindings().isEmpty());
    }

    @Test
    void testWithFieldAccess_Override() {
        final QBeanBuilder<TestBean> builder = new QBeanBuilder<>(TestBean.class)
                .withFieldAccess(true)
                .withFieldAccess(false);
        assertEquals(TestBean.class, builder.getType());
        assertFalse(builder.isFieldAccess());
        assertNotNull(builder.getBindings());
        assertTrue(builder.getBindings().isEmpty());
    }

    @Test
    void testBind() {
        @SuppressWarnings("unchecked")
        final Expression<Integer> intExpr = mock(Expression.class);
        final QBeanBuilder<TestBean> builder = new QBeanBuilder<>(TestBean.class)
                .bind(TestBean.INT_VALUE_PROP, intExpr);
        assertEquals(TestBean.class, builder.getType());
        assertFalse(builder.isFieldAccess());
        assertNotNull(builder.getBindings());
        assertEquals(1, builder.getBindings().size());
        assertEquals(intExpr, builder.getBindings().get(TestBean.INT_VALUE_PROP));
    }

    @Test
    void testBind_Override() {
        @SuppressWarnings("unchecked")
        final Expression<Integer> intExpr = mock(Expression.class);
        @SuppressWarnings("unchecked")
        final Expression<Integer> intExpr2 = mock(Expression.class);
        @SuppressWarnings("unchecked")
        final Expression<String> strExpr = mock(Expression.class);
        final QBeanBuilder<TestBean> builder = new QBeanBuilder<>(TestBean.class)
                .bind(TestBean.INT_VALUE_PROP, intExpr)
                .bind(TestBean.STR_VALUE_PROP, strExpr)
                .bind(TestBean.INT_VALUE_PROP, intExpr2);
        assertEquals(TestBean.class, builder.getType());
        assertFalse(builder.isFieldAccess());
        assertNotNull(builder.getBindings());
        assertEquals(2, builder.getBindings().size());
        assertEquals(intExpr2, builder.getBindings().get(TestBean.INT_VALUE_PROP));
        assertEquals(strExpr, builder.getBindings().get(TestBean.STR_VALUE_PROP));
    }

    @Test
    void testBuild() {
        @SuppressWarnings("unchecked")
        final Expression<Integer> intExpr = mock(Expression.class);
        willReturn(Integer.class).given(intExpr).getType();
        @SuppressWarnings("unchecked")
        final Expression<String> strExpr = mock(Expression.class);
        willReturn(String.class).given(strExpr).getType();
        final QBeanBuilder<TestBean> builder = new QBeanBuilder<>(TestBean.class)
                .bind(TestBean.INT_VALUE_PROP, intExpr)
                .bind(TestBean.STR_VALUE_PROP, strExpr);
        assertEquals(TestBean.class, builder.getType());
        assertFalse(builder.isFieldAccess());
        assertNotNull(builder.getBindings());
        assertEquals(2, builder.getBindings().size());
        assertEquals(intExpr, builder.getBindings().get(TestBean.INT_VALUE_PROP));
        assertEquals(strExpr, builder.getBindings().get(TestBean.STR_VALUE_PROP));
        final QBean<TestBean> result = builder.build();
        assertEquals(TestBean.class, result.getType());
        assertNotNull(result.getArgs());
        assertEquals(2, result.getArgs().size());
        assertTrue(result.getArgs().contains(intExpr));
        assertTrue(result.getArgs().contains(strExpr));
        final TestBean bean = result.newInstance(10, "test");
        assertNotNull(bean);
        assertEquals(10, bean.getIntValue());
        assertEquals("test", bean.getStrValue());
        assertEquals(1, bean.getIntValueSets());
        assertEquals(1, bean.getStrValueSets());
    }

    @Test
    void testBuild_Fields() {
        @SuppressWarnings("unchecked")
        final Expression<Integer> intExpr = mock(Expression.class);
        willReturn(Integer.class).given(intExpr).getType();
        @SuppressWarnings("unchecked")
        final Expression<String> strExpr = mock(Expression.class);
        willReturn(String.class).given(strExpr).getType();
        final QBeanBuilder<TestBean> builder = new QBeanBuilder<>(TestBean.class)
                .withFieldAccess(true)
                .bind(TestBean.INT_VALUE_PROP, intExpr)
                .bind(TestBean.STR_VALUE_PROP, strExpr);
        assertEquals(TestBean.class, builder.getType());
        assertTrue(builder.isFieldAccess());
        assertNotNull(builder.getBindings());
        assertEquals(2, builder.getBindings().size());
        assertEquals(intExpr, builder.getBindings().get(TestBean.INT_VALUE_PROP));
        assertEquals(strExpr, builder.getBindings().get(TestBean.STR_VALUE_PROP));
        final QBean<TestBean> result = builder.build();
        assertEquals(TestBean.class, result.getType());
        assertNotNull(result.getArgs());
        assertEquals(2, result.getArgs().size());
        assertTrue(result.getArgs().contains(intExpr));
        assertTrue(result.getArgs().contains(strExpr));
        final TestBean bean = result.newInstance(10, "test");
        assertNotNull(bean);
        assertEquals(10, bean.getIntValue());
        assertEquals("test", bean.getStrValue());
        assertEquals(0, bean.getIntValueSets());
        assertEquals(0, bean.getStrValueSets());
    }

    public static class TestBean {
        public static final String INT_VALUE_PROP = "intValue";
        public static final String STR_VALUE_PROP = "strValue";
        private int intValue;
        private int intValueSets;
        private String strValue;
        private int strValueSets;
        public int getIntValue() {
            return intValue;
        }
        @SuppressWarnings("unused")
        public void setIntValue(int intValue) {
            this.intValue = intValue;
            intValueSets++;
        }
        public int getIntValueSets() {
            return intValueSets;
        }
        public String getStrValue() {
            return strValue;
        }
        @SuppressWarnings("unused")
        public void setStrValue(String strValue) {
            this.strValue = strValue;
            strValueSets++;
        }
        public int getStrValueSets() {
            return strValueSets;
        }
    }
}
