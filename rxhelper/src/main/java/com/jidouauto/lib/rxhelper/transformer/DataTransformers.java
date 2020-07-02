package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.DataConverter;
import com.jidouauto.lib.rxhelper.NullableData;
import com.jidouauto.lib.rxhelper.Validator;

/**
 * @author eddie
 * <p>
 * 配合{@link DataConverter}对数据进行剥离
 */
public class DataTransformers {

    /**
     * 将data从实现DataConverter接口的数据中提取出来
     *
     * @param <T> the type parameter
     * @param <R> the type parameter
     * @return observable transformer
     */
    public static <T extends DataConverter<R>, R> DataConvertTransformer<T, R> convertToData() {
        return new DataConvertTransformer<>();
    }

    /**
     * 将data从实现DataConverter接口的数据中提取出来，如果这个数据可能为空，那么可以提供一个默认值
     *
     * @param defaultValue data为空的情况返回该默认值
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T extends DataConverter<NullableData<R>>, R> NullableDataConvertTransformer<T, R> convertToData(R defaultValue) {
        return new NullableDataConvertTransformer<>(defaultValue);
    }

}
