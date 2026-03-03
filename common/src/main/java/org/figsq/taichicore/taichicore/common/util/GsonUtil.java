package org.figsq.taichicore.taichicore.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class GsonUtil {
    private static final GsonBuilder BUILDER = new GsonBuilder();
    private static Gson GSON;

    public void registerAdapter(Type type, Object obj) {
        BUILDER.registerTypeAdapter(type, obj);
        GSON = BUILDER.create();
    }

    public static Gson getGson() {
        if (GSON == null) GSON = BUILDER.create();
        return GSON;
    }
}
