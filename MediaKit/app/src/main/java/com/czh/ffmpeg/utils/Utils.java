package com.czh.ffmpeg.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
    private static class ExecutorHolder {
        private static final ExecutorService INSTANCE = Executors.newCachedThreadPool();

    }
    public static ExecutorService getExecutor(){
        return ExecutorHolder.INSTANCE;
    }
}
