package cn.mvp.mlibs.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtils {

    private Timer timer;

    public TimerUtils() {
        timer = new Timer();
    }

    /**
     * 计划一个任务在指定的延迟后运行。
     * ＊
     *
     * @param task  要运行的任务。
     * @param delay 任务运行前的延迟时间，以毫秒为单位。
     */
    public void schedule(TimerTask task, long delay) {
        timer.schedule(task, delay);
    }

    /**
     * 安排任务在指定的延迟和周期后重复运行。
     *
     * @param task   要运行的任务。
     * @param delay  任务运行前的延迟时间，以毫秒为单位。
     * @param period 后续任务执行之间的毫秒间隔。
     */
    public void schedule(TimerTask task, long delay, long period) {
        timer.schedule(task, delay, period);
    }

    /**
     * 取消所有计划任务并停止计时器。
     */
    public void cancel() {
        timer.cancel();
    }

}