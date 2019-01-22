package com.jeffrey.studio.jeffeystudio.common;

import okhttp3.OkHttpClient;

public class OkHttpUtils {
    private static class OkHttpHolder {
        private static final OkHttpClient INSTANCE = new OkHttpClient();
    }

    public static OkHttpClient getClient() {
        return OkHttpHolder.INSTANCE;
    }
}
