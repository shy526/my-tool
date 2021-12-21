package com.github.shy526.poll;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.function.Supplier;

/**
 * 简化PolledObjFactory
 *
 * @author Administrator
 */
public class SimplePolledObjFactory<T> extends BasePooledObjectFactory<T> {
    private final Supplier<T> supplier;

    public SimplePolledObjFactory(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T create() throws Exception {
        return supplier.get();
    }

    @Override
    public PooledObject<T> wrap(T obj) {
        return new DefaultPooledObject<>(obj);
    }
}
