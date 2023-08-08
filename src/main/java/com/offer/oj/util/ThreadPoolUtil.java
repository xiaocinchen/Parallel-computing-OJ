package com.offer.oj.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
    public static ExecutorService sendMQThreadPool =
            new ThreadPoolExecutor(2, 4,
                    1L, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(120),new ThreadFactoryBuilder().setNameFormat("SendMQ-pool-%d").build(),new ThreadPoolExecutor.AbortPolicy());
    public static ExecutorService compileThreadPool =
            new ThreadPoolExecutor(2, 4,
                    10L, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(120),new ThreadFactoryBuilder().setNameFormat("Monitor-pool-%d").build(),new ThreadPoolExecutor.AbortPolicy());
    public static ExecutorService execThreadPool =
            new ThreadPoolExecutor(2, 4,
                    10L, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(120),new ThreadFactoryBuilder().setNameFormat("Monitor-pool-%d").build(),new ThreadPoolExecutor.AbortPolicy());

}
