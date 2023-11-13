package cn.mvp.mlibs.utils;

import java.util.Random;

public class RandomUtils {

    /**
     * 生成范围内随机数(包含最大值,最小值)
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
