package cn.mvp.mlibs.utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimerUtils {

    private static TimerUtils instance;
    private Timer timer;
    private final ArrayList<MyTask> mMyTasks;
    private boolean mTimerIsCancelled;

    private TimerUtils() {
        mMyTasks = new ArrayList<>();
    }

    public static TimerUtils getInstance() {
        if (instance == null) {
            synchronized (TimerUtils.class) {
                if (instance == null) {
                    instance = new TimerUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 计划一个任务在指定的延迟后运行。
     * ＊
     *
     * @param task  要运行的任务。
     * @param delay 任务运行前的延迟时间，以毫秒为单位。
     */
    public void schedule(MyTask task, long delay) {
        checkTimerCancelled();
        timer.schedule(task, delay);
    }

    private void checkTimerCancelled() {
        if (mTimerIsCancelled) {
            timer = new Timer();
            mTimerIsCancelled = false;
        }
    }

    /**
     * 安排任务在指定的延迟和周期后重复运行。
     *
     * @param task   要运行的任务。
     * @param delay  任务运行前的延迟时间，以毫秒为单位。
     * @param period 后续任务执行之间的毫秒间隔。
     */
    public void schedule(MyTask task, long delay, long period) {
        checkTimerCancelled();
        timer.schedule(task, delay, period);
    }

    /**
     * 取消所有计划任务并停止计时器。
     */
    public void cancelTimer() {
        mTimerIsCancelled = true;
        timer.cancel();
    }

    /**
     * 取消所有计划任务并停止计时器。
     */
    public void cancelTaskByName(String taskName) {
        for (MyTask myTask : mMyTasks) {
            if (myTask.taskName.equals(taskName)) {
                myTask.cancel();
                break;
            }
        }
    }

    public void cancelAllTasks() {
        for (MyTask myTask : mMyTasks) {
            myTask.cancel();
        }
    }

    public abstract static class MyTask extends TimerTask {
        private String taskName;

        public MyTask(String taskName) {
            this.taskName = taskName;
        }

        public String getTaskName() {
            return taskName;
        }
    }


}