package com.github.shy526.poll;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.script.ScriptEngine;

/**
 * 自定义GenericObjectPool
 *
 * @author Administrator
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
}
