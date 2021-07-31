package com.github.shy526;

import com.github.shy526.thread.ThreadPoolUtils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolUtilsTest extends TestCase {

    @Test
    public void testGetThreadPool() {
        ThreadPoolExecutor threadPool = ThreadPoolUtils.getThreadPool();
        for (int i = 0; i < 100; i++) {
            threadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
    }
}