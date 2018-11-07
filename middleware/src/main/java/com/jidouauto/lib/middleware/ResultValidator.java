package com.jidouauto.lib.middleware;


import com.jidouauto.lib.middleware.exception.BaseException;
import com.jidouauto.lib.middleware.transformer.StreamTransformer;

/**
 * The interface Result validator.
 *
 * @author eddie
 * <p>
 * ResultValidator接口用于数据结果的校验 该接口配合{@link StreamTransformer#validateResult()} ()} 实现数据结果校验，并将错误信息反馈到调用者
 * @see {@link StreamTransformer} eg. 请求返回的错误code 数据异常 {@link com.jidouauto.lib.middleware.exception.DataException}
 */
public interface ResultValidator {

    /**
     * 数据校验失败的情况抛出异常
     *
     * @throws BaseException the base exception
     */
    void validateResult() throws BaseException;
}
