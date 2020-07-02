package com.jidouauto.lib.rxhelper;


import com.jidouauto.lib.rxhelper.transformer.RetryTransformers;
import com.jidouauto.lib.rxhelper.transformer.ValidateTransformers;

/**
 * The interface Result validator.
 *
 * @author eddie
 * <p>
 * ResultValidator接口用于数据结果的校验 该接口配合{@link ValidateTransformers#validate()} ()} ()} 实现数据结果校验，并将错误信息反馈到调用者
 * @see {@link RetryTransformers} eg. 请求返回的错误code 数据异常。
 */
public interface Validator<T extends Exception> {

    /**
     * 数据校验失败的情况抛出异常
     *
     * @throws T the base exception
     */
    void validate() throws T;
}
