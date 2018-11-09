package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.transformer.Transformers;

/**
 * @param <T> 业务数据模型
 * @author eddie
 * <p>
 * DataSource接口用于将业务数据模型与传输数据模型分离 该接口配合{@link Transformers#convertToData()}将传输模型与业务模型分离
 * @see {@link Transformers}
 */
public interface DataConverter<T> {
    /**
     * Gets data.
     *
     * @return 业务数据模型 data
     */
    T convert();
}
