package cn.mvp.mlibs.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListUtils {
    public static <T> void removeItems(List<T> list, Condition<T> condition) {
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (condition.matches(item)) {
                iterator.remove();
            }
        }
    }

    public interface Condition<T> {
        boolean matches(T item);
    }

    /**
     * @param array 传一个空数组就行
     */
    public static <T> T[] toArray(List<T> list, T[] array) {
        if (list == null) return null;
        return list.toArray(array);
    }

    public static <T> T[] toArray(List<T> list) {
        if (list == null) return null;
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        return list.toArray(array);
    }

    public static <T> List<T> addAll(List<T> list, List<T> defaultValue) {
        if (list != null) {
            list.addAll(defaultValue);
            return list;
        }
        return defaultValue == null ? new ArrayList<>() : defaultValue;
    }

    public static <T> List<T> getList(List<T> list, T defaultValue) {
        if (list == null || list.isEmpty()) {
            List<T> newList = new ArrayList<>();
            newList.add(defaultValue);
            return newList;
        } else {
            return list;
        }
    }

    public static <T> List<T> getList(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }
}
