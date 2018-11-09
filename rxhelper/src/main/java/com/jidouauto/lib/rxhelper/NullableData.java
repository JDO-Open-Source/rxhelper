package com.jidouauto.lib.rxhelper;

import com.jidouauto.lib.rxhelper.transformer.Transformers;

/**
 * @param <T>
 * @author eddie
 * <p>
 * 可空对象包装类,解决Rxjava不能传递空对象的问题
 * @@see {@link Transformers#validateNullable()}
 * @see {@link Transformers#convertToData(Object)}
 */
public final class NullableData<T> {

    private final T value;

    private NullableData(T value) {
        this.value = value;
    }

    /**
     * 包装一个可空的对象
     * @param value 可空对象
     * @param <T>   被包装的对象类型
     * @return 包装类
     */
    public static <T> NullableData<T> of(T value) {
        return new NullableData<>(value);
    }

    /**
     * 调用此方法前需先判空，可以使用
     * {@link NullableData#isNull()}
     * 或者
     * {@link NullableData#isNotNull()}
     * 如果value为空则抛出一个{@link NullPointerException}
     *
     * @return
     */
    public T get() {
        if (value == null) {
            throw new NullPointerException("Null data");
        }
        return value;
    }

    /**
     * 判断被包装的value是否为空
     *
     * @return 返回被包装的对象是否为空
     * @see {@link NullableData#isNotNull()}
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * 判断被包装的value是否不为空
     *
     * @return 返回被包装的对象是否不为空
     * @see {@link NullableData#isNull()}
     */
    public boolean isNotNull() {
        return value != null;
    }

    @Override
    public String toString() {
        return value != null
                ? String.format("NullableData[%s]", value)
                : "NullableData.empty";
    }
}
