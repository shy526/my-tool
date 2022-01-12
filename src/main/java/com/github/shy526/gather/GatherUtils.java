package com.github.shy526.gather;

import java.util.Collection;
import java.util.Map;

/**
 * 集合的一些常用方法
 *
 * @author Administrator
 */
public class GatherUtils {
    /**
     * 判断是否胃口
     *
     * @param collection collection
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * @param map map
     * @return boolean
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * array
     *
     * @param array array
     * @return boolean
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }
}
