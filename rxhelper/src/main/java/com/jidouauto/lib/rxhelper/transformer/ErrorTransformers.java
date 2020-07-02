package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.ErrorConverter;

/**
 * @author eddie
 * <p>
 * 错误转换器，将错误进行转换后配合{@link RetryTransformers}进行重试，也可单独用来统一转换错误
 */
public class ErrorTransformers {

    /**
     * 异常转换
     *
     * @param <T>            the type parameter
     * @param errorConverter the error converter
     * @return observable transformer
     */
    public static <T> ConvertErrorTransformer<T> convertError(ErrorConverter errorConverter) {
        return new ConvertErrorTransformer<>(errorConverter);
    }


}
