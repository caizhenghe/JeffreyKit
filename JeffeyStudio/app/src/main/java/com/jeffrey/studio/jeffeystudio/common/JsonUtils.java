package com.jeffrey.studio.jeffeystudio.common;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jeffrey.studio.jeffeystudio.bean.Base;

import java.util.List;

public class JsonUtils {
    private static class GsonHolder {
        private static final Gson INSTANCE = new GsonBuilder().create();
    }

    public static <T> T getObject(String json, Class<T> clazz) {
        return GsonHolder.INSTANCE.fromJson(json, clazz);
    }

    public static <T> List<T> getArray(String json) {
        return GsonHolder.INSTANCE.fromJson(json, new TypeToken<List<T>>() {}.getType());
    }

    public static <T> String toJson(T t) {
        return GsonHolder.INSTANCE.toJson(t);
    }

}
