package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.transformer.StreamTransformer;

/**
 * @param <T> 业务数据模型
 * @author eddie
 * <p>
 * DataSource接口用于将业务数据模型与传输数据模型分离 该接口配合{@link StreamTransformer#convertToData()}将传输模型与业务模型分离
 * @see {@link StreamTransformer}
 */
public interface DataConverter<T> {
    /**
     * Gets data.
     *
     * @return 业务数据模型 data
     */
    T convert();
}
