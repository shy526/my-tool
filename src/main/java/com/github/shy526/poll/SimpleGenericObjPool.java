package com.github.shy526.poll;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 自定义GenericObjectPool
 *
 * @author shy26
 */
@Slf4j
public class SimpleGenericObjPool<T> extends GenericObjectPool<T> {


    public SimpleGenericObjPool(PooledObjectFactory<T> factory) {
        super(factory);
    }

    public SimpleGenericObjPool(Supplier<T> supplier) {
        super(new SimplePolledObjFactory<>(supplier));
    }

    public SimpleGenericObjPool(Supplier<T> supplier, GenericObjectPoolConfig<T> config) {
        super(new SimplePolledObjFactory<>(supplier), config);
    }


    public SimpleGenericObjPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config) {
        super(factory, config);
    }

    public SimpleGenericObjPool(Supplier<T> supplier, GenericObjectPoolConfig<T> config, AbandonedConfig abandonedConfig) {
        super(new SimplePolledObjFactory<>(supplier), config, abandonedConfig);
    }

    public SimpleGenericObjPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }

    /**
     * 租借对象
     *
     * @param processor 执行器
     */
    public void leaseObject(Consumer<T> processor) {
        T pooledObject = null;
        try {
            pooledObject = this.borrowObject();
            processor.accept(pooledObject);
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
     *
     * @param processor 执行器
     * @param <R>       <R>
     * @return R
     */
    public <R> R leaseObject(Function<T, R> processor) {
        T pooledObject = null;
        R result = null;
        try {
            pooledObject = this.borrowObject();
            result = processor.apply(pooledObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (pooledObject != null) {
                this.returnObject(pooledObject);
            }
        }
        return result;
    }

    /**
     * 租借对象
     *
     * @param processor 执行器
     * @param maxWait   最大等待时间
     */
    public void leaseObject(Consumer<T> processor, Long maxWait) {
        T pooledObject = null;
        try {
            pooledObject = this.borrowObject(maxWait);
            processor.accept(pooledObject);
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
     *
     * @param processor 执行器
     * @param maxWait   最大等待时间
     * @param <R>       <R>
     * @return R
     */

    public <R> R leaseObject(Function<T, R> processor, Long maxWait) {
        T pooledObject = null;
        R result = null;
        try {
            pooledObject = this.borrowObject(maxWait);
            result = processor.apply(pooledObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (pooledObject != null) {
                this.returnObject(pooledObject);
            }
        }
        return result;
    }
}
