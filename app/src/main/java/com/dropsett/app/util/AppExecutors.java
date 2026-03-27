package com.dropsett.app.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final ExecutorService diskIO = Executors.newSingleThreadExecutor();

    public static ExecutorService diskIO() {
        return diskIO;
    }
}