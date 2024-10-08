package cn.mvp.mlibs.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GsonUtil {

    public synchronized static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    /**
     * GsonUtil.fromJsonToList(json, Pos.class);
     *
     * @param json         列表字符串
     * @param elementClass 目标对象.class
     * @return List<目标对象.class>
     */
    public synchronized static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        Type listType = TypeToken.getParameterized(List.class, elementClass).getType();
        return new Gson().fromJson(json, listType);
    }

    public static <T> T[] fromJsonToArr(String json, Class<T> elementClass) {
        return new Gson().fromJson(json, TypeToken.getArray(elementClass).getType());
    }

    public static String[] fromJsonToStrArr(String json) {
        return new Gson().fromJson(json, TypeToken.getArray(String.class).getType());
    }

    public synchronized static <T> String toJson(T obj) {
        return new Gson().toJson(obj);
    }

    /**
     * 使用Gson解析json字符串
     *
     * @param jsonObjStr json字符串
     * @param key        属性key
     * @return 返回key对应的值
     */
    public static String parseJson(String jsonObjStr, String key) {
        //JsonParser.parseString(String.valueOf(obj)).
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonObjStr).getAsJsonObject();
        // 判断字段是否存在
        if (jsonObject.has(key)) {
            // 判断字段是否为空
            if (!jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsString();
            }
        }
        return "";
    }


    public static <T> Map<String, Object> toMap(Class<T> clazz) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(clazz), type);
    }


    /*
     *将 JSON 字符串转换为 Map
     * Map<String, Object> map = GsonUtils.jsonToMap(jsonString, String.class, Object.class);
     */
    public static <K, V> Map<K, V> jsonToMap(String jsonString, Class<K> keyClass, Class<V> valueClass) {
        Gson gson = new Gson();
        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();
        return gson.fromJson(jsonString, type);
    }


    /** 将 Map 转换为 JSON 字符串 */
    public static String mapToJson(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }


    /**
     * 获取JsonObject
     *
     * @param json
     * @return
     */
    public static JsonObject parseJson(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
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

    public static void putJoStrToFile(String filePath, String joStr) {
        if (FileUtils.isFileExist(filePath)) {
            FileUtils.createDir(filePath);
            FileUtils.createFile(filePath);
        }
        FileUtils.writeFile(filePath, joStr);
    }

    public static <T> T getJoStrForFile(String filePath, Class<T> clazz) {
        try {
            return fromJson(FileUtils.readFile(filePath, "utf-8"), clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonElement fromJson(String json) {
        return new Gson().fromJson(json, JsonElement.class);
    }

    public static JsonObject getJsonObject(String json, String key) {
        return fromJson(json).getAsJsonObject().get(key).getAsJsonObject();
    }

    public static JsonArray getJsonArray(String json, String key) {
        return fromJson(json).getAsJsonObject().get(key).getAsJsonArray();
    }


    public static <T> T fromJson(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    public static <T> Type getTypeToken(Class<T> clazz) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{clazz};
            }

            @Override
            public Type getRawType() {
                return TypeToken.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

}
