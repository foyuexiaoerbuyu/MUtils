package cn.mvp.mlibs.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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


    /** 过滤元素 */
    public static <T> List<T> filterElements(List<T> list, Condition<T> iRemove) {
        if (list == null || list.size() == 0) return null;
        Iterator<T> iterator = list.iterator();
        ArrayList<T> newlist = new ArrayList<>();
        while (iterator.hasNext()) {
            T current = iterator.next();
            if (iRemove.matches(current)) {
                // 使用迭代器的remove方法安全地删除元素
                newlist.add(current);
            }
        }
        return newlist;
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
        return defaultValue == null ? new ArrayList<T>() : defaultValue;
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

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNotEmpty(List<T> list) {
        return !isEmpty(list);
    }


    /** 分组 */
    public static <T> List<List<T>> partitionList(List<T> list, int groupSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += groupSize) {
            int end = Math.min(list.size(), i + groupSize);
            partitions.add(new ArrayList<>(list.subList(i, end)));
        }
        return partitions;
    }

    /** 随机获取一个元素 */
    public static <T> T pickRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }


}
