package cn.mvp.mlibs.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GsonUtils {

    public synchronized static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public synchronized static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        Type listType = TypeToken.getParameterized(List.class, elementClass).getType();
        return new Gson().fromJson(json, listType);
    }

    public synchronized static <T> String toJson(T obj) {
        return new Gson().toJson(obj);
    }

}
