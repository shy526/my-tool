package com.github.shy526.poll;

/**
 * 处理器接口
 * @author Administrator
 */
public interface Processor<T> {
    /**
     * 处理
     * @param t
     */
    void handle(T t);
}
