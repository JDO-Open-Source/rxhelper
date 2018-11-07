package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.exception.IdentityException;
import com.jidouauto.lib.middleware.transformer.StreamTransformer;

/**
 * The interface Identity validator.
 *
 * @author eddie
 * <p>
 * DataValidator接口用于身份校验 该接口配合{@link StreamTransformer#validateIdentity()} 实现数据流的安全校验，并将错误信息反馈到调用者
 * @see {@link StreamTransformer}
 */
public interface IdentityValidator {

    /**
     * 检验返回数据是否包含身份校验失败的信息
     * 校验失败的时候返回IdentityException
     *
     * @throws IdentityException the identity exception
     */
    void validateIdentity() throws IdentityException;
}
