package com.github.shy526.poll;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 自定义GenericObjectPool
 *
 * @author shy26
 */
@Slf4j
public class MyGenericObjectPool<T> extends GenericObjectPool<T> {
    public MyGenericObjectPool(PooledObjectFactory<T> factory) {
        super(factory);
    }

    public MyGenericObjectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config) {
        super(factory, config);
    }

    public MyGenericObjectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }

    /**
     * 租借对象
     * @param processor 执行器
     */
    public void leaseObject(Processor<T> processor) {
        T pooledObject = null;
        try {
            pooledObject = this.borrowObject();
            processor.handle(pooledObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (pooledObject != null) {
                this.returnObject(pooledObject);
            }
        }
    }

    /**
     * 租借对象
     * @param processor 执行器
     * @param maxWait 最大等待时间
     */
    public void leaseObject(Processor<T> processor,Long maxWait) {
        T pooledObject = null;
        try {
            pooledObject = this.borrowObject(maxWait);
            processor.handle(pooledObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (pooledObject != null) {
                this.returnObject(pooledObject);
            }
        }
    }
}
