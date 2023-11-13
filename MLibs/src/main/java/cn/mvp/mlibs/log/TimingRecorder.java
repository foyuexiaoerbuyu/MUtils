package cn.mvp.mlibs.log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <a href="https://www.jianshu.com/p/bbaa526c8193">...</a>
 * 一个实用程序类，用来帮助记录在方法调用中的耗时。
 * 用法如下:
 *
 * <pre>
 *     TimingRecorder.addRecord(label, "work");
 *     // ... do some work A ...
 *     TimingRecorder.addRecord(label, "work A");
 *     // ... do some work B ...
 *     TimingRecorder.addRecord(label, "work B");
 *     // ... do some work C ...
 *     TimingRecorder.addRecord(label, "work C");
 *     TimingRecorder.logTime(label);
 * </pre>
 *
 * <p>logTime调用会后输出示例如下:</p>
 *
 * <pre>
 *     标签: label <开始>
 *        0 ms  message: 开始
 *     1501 ms  message: 执行A
 *      302 ms  message: 执行B
 *     2002 ms  message: 执行完成~
 *     <结束>   总耗时: 3805 ms   记录次数: 4 次
 * </pre>
 *
 * @author LiuFeng
 * @date 2020-10-21
 */
public class TimingRecorder {
    private static String TAG = "TimingRecorder";

    // 是否调用打印
    private static boolean isToLog = true;

    // 单个标签可以添加记录的最大数量
    private static final int RECORD_MAX_NUM = 1000;

    // 记录储存容器
    private static final ConcurrentHashMap<String, List<RecordTime>> recordMap = new ConcurrentHashMap<>();

    /**
     * 添加记录
     *
     * @param label   记录的标签
     * @param message 记录的日志内容。
     */
    public static void addRecord(String label, String message) {
        // 时间记录
        RecordTime recordTime = new RecordTime();
        // Android系统可以使用SystemClock.elapsedRealtime()
        // 代替System.nanoTime() / 1000_000，作用相同，这里是考虑到Java里使用
        recordTime.time = System.nanoTime() / 1000_000;
        recordTime.message = message;

        // 处理存入记录
        handleRecordTimes(label, recordTime);
    }

    /**
     * 处理记录数据集合（数据过多时将释放）
     *
     * @param label
     * @param recordTime
     */
    private static synchronized void handleRecordTimes(String label, RecordTime recordTime) {
        // 取出记录
        List<RecordTime> recordTimes = getRecordTimes(label);

        // 超过记录容量时，调用打印释放，目的避免有未打印记录占用过多内存
        if (recordTimes.size() >= RECORD_MAX_NUM) {
            String desc = label + ": Add too many records! will invoke logTime.";
            println(desc);

            // 保留第一条，是为了释放打印后，新的记录中能计算到从第一条开始的耗时
            RecordTime firstRecordTime = recordTimes.get(0);

            // 执行打印记录，并清除标签
            logTime(label, desc);

            // 存入第一条数据到新容器
            recordTimes = getRecordTimes(label);
            recordTimes.add(firstRecordTime);
        }

        // 存入新记录
        recordTimes.add(recordTime);
    }

    /**
     * 获取标签对应容器
     *
     * @param label
     * @return
     */
    private static List<RecordTime> getRecordTimes(String label) {
        List<RecordTime> recordTimes = recordMap.get(label);

        // 创建容器
        if (recordTimes == null) {
            recordTimes = new ArrayList<>();
            List<RecordTime> tempRecords = recordMap.putIfAbsent(label, recordTimes);
            if (tempRecords != null) {
                recordTimes = tempRecords;
            }
        }
        return recordTimes;
    }

    /**
     * 打印记录时间
     *
     * @param label 记录的标签
     * @return
     */
    public static String logTime(String label) {
        return logTime(label, null);
    }

    /**
     * 打印记录时间
     *
     * @param label 记录的标签
     * @param msg   追加的日志内容。
     * @return
     */
    public static String logTime(String label, String msg) {
        List<RecordTime> recordTimes;

        // 加锁保证添加数据和移除数据操作安全
        synchronized (TimingRecorder.class) {
            // 取出记录，清除标记
            recordTimes = recordMap.remove(label);
        }

        // 在记录容量内，追加一条表示结束的记录
        if (recordTimes != null && recordTimes.size() < RECORD_MAX_NUM) {
            RecordTime recordTime = new RecordTime();
            recordTime.time = System.nanoTime() / 1000_000;
            recordTime.message = msg != null ? msg : "log time end.";
            recordTimes.add(recordTime);
        }

        // 解析记录
        String record = parseRecord(label, recordTimes);

        // 打印
        if (isToLog) {
            println(record);
        }

        return record;
    }

    /**
     * 解析记录数据
     *
     * @param label
     * @param recordTimes
     * @return
     */
    private static String parseRecord(String label, List<RecordTime> recordTimes) {
        // 组装数据
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n标签: ").append(label);

        // 无记录处理
        if (recordTimes == null || recordTimes.isEmpty()) {
            buffer.append(" <没有记录>");
            return buffer.toString();
        }

        buffer.append(" <开始>\n");

        int size = recordTimes.size();

        // 第一次记录时间
        long firstTime = recordTimes.get(0).time;

        // 最后一次记录时间
        long lastTime = recordTimes.get(size - 1).time;

        // 总耗时
        long totalTime = lastTime - firstTime;

        // 计算耗时的前一个时间
        long prevTime = firstTime;

        // 取总耗时位数长度，记作最大的位长，用于空格缩进
        int maxDigitLength = String.valueOf(totalTime).length();

        // 取出数据计算差值并拼接
        for (int i = 0; i < size; i++) {
            RecordTime record = recordTimes.get(i);
            long spaceTime = record.time - prevTime;
            prevTime = record.time;

            // 当前耗时位长
            int currentDigitLength = String.valueOf(spaceTime).length();

            // 缩进空格字符（目的是对齐易看）
            String indentSpaceStr = getSpaceStr(maxDigitLength - currentDigitLength);

            // 拼接
            buffer.append(indentSpaceStr);
            buffer.append(spaceTime).append(" ms");
            buffer.append("  message: ").append(record.message);
            buffer.append("\n");
        }

        buffer.append("<结束>");
        buffer.append("   总耗时: ").append(totalTime).append(" ms");
        buffer.append("   记录次数: ").append(size).append(" 次");


        return buffer.toString();
    }

    /**
     * 记录时间的model
     */
    private static class RecordTime {
        long time;
        String message;
    }

    /**
     * 生成指定数量空格字符串
     *
     * @param spaces
     * @return
     */
    private static String getSpaceStr(int spaces) {
        String number = String.valueOf(spaces <= 0 ? "" : spaces);
        return String.format("%" + number + "s", "");
    }

    /**
     * 调用日志打印
     *
     * @param msg
     */
    private static void println(String msg) {
        // Java里用System.out，Android最好是用Log
        System.out.println(msg);
        //Log.println(Log.INFO, TAG, msg);
    }
}