package com.jidouauto.lib.rxhelper.transformer;

import com.jidouauto.lib.rxhelper.DataConverter;
import com.jidouauto.lib.rxhelper.DataValidator;
import com.jidouauto.lib.rxhelper.ErrorConverter;
import com.jidouauto.lib.rxhelper.NullableData;
import com.jidouauto.lib.rxhelper.RetryOnError;
import com.jidouauto.lib.rxhelper.Validator;
import com.jidouauto.lib.rxhelper.transformer.CustomValidateTransformer;
import com.jidouauto.lib.rxhelper.transformer.NullableDataValidateTransformer;
import com.jidouauto.lib.rxhelper.transformer.ValidateTransformer;

import org.reactivestreams.Publisher;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleSource;
import io.reactivex.internal.operators.single.SingleToObservable;

/**
 * @author eddie
 * <p>
 * 利用Rxjava2 Transformer接口，配合{@link DataConverter},{@link Validator}
 * 来规范数据处理，进行数据校验，身份校验（Token），错误处理以及失败重试等操作
 */
public class ValidateTransformers {

    /**
     * 校验数据错误信息
     *
     * @param <T> 支持数据校验的数据模型必须实现DataValidator接口
     * @return observable transformer
     */
    public static <T extends Validator<?>> ValidateTransformer<T> validate() {
        return new ValidateTransformer<>();
    }

    /**
     * 校验数据错误信息
     *
     * @param <T> 支持数据校验的数据模型必须实现DataValidator接口
     * @return observable transformer
     */
    public static <T> CustomValidateTransformer<T> validate(DataValidator<T, ? extends Exception> dataValidator) {
        return new CustomValidateTransformer<>(dataValidator);
    }

    /**
     * 如果传递下来的数据是NullableData类型，并且NullableData中的value类型为Validator的子类，并且该value不为null，那么则调用该value的validate方法。
     *
     * @param <T>
     * @return
     */
    public static <T extends NullableData<? extends Validator>> ObservableTransformer<T, T> validateNullable() {
        return new NullableDataValidateTransformer<>();
    }
}
