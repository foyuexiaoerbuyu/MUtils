package cn.mvp.mlibs.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    /**
     * 使用Gson解析json字符串
     *
     * @param jsonStr json字符串
     * @param key     属性key
     * @return 返回key对应的值
     */
    public static String parseJson(String jsonStr, String key) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        // 判断字段是否存在
        if (jsonObject.has(key)) {
            // 判断字段是否为空
            if (!jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsString();
            }
        }
        return "";
    }


    public static JsonArray getJsonArray(JsonObject asJsonObject, String key) {
        if (!asJsonObject.get(key).isJsonNull()) {
            return asJsonObject.get(key).getAsJsonArray();
        }
        return new JsonArray();
    }

    public static JsonElement parse(String json) {
        return new JsonParser().parse(json);
    }

    public static String getString(JsonObject asJsonObject, String key) {
        if (!asJsonObject.get(key).isJsonNull()) {
            return asJsonObject.get(key).getAsString();
        }
        return "";
    }
}
