package cn.mvp.mlibs.utils;

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
}
