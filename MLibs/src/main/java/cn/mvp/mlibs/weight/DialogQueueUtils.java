package cn.mvp.mlibs.weight;

import android.app.Dialog;
import android.content.DialogInterface;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * https://www.jianshu.com/p/6ee3b054965a
 * 队列弹框
 */
public class DialogQueueUtils {

    private static final String TAG = "DialogQueueUtils";
    private MyQueue mMyQueue;

    private Dialog mCurrentDialog = null;//当前显示的Dialog

    private DialogQueueUtils() {
        mMyQueue = new MyQueue();
    }

    public static DialogQueueUtils getInstance() {
        return DialogQueueHolder.singleton;
    }

    /**
     * 单例模式->静态内部类<br/>
     * 多线程情况下，使用合理一些,推荐
     */
    static class DialogQueueHolder {
        private static DialogQueueUtils singleton = new DialogQueueUtils();
    }

    public void addDialog(List<Dialog> dialogs) {
        for (Dialog dialog : dialogs) {
            if (dialog != null) {
                mMyQueue.offer(dialog);
            }
        }
    }

    public void addDialog(Dialog dialog) {
        if (dialog != null) {
            mMyQueue.offer(dialog);
        }
    }

    public void show() {
        if (mCurrentDialog == null) {
            //从队列中拿出一个Dialog实例,并从列表中移除
            mCurrentDialog = mMyQueue.poll();
            //当队列为空的时候拿出来的会是null
            if (mCurrentDialog != null) {
                mCurrentDialog.show();
                mCurrentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //这边设置了dismiss监听,在监听回调中再次调用show方法,可以获取下一个弹窗
                        mCurrentDialog = null;
                        show();
                    }
                });
            }
        }
    }

    public class MyQueue {

        private Queue<Dialog> mDialogQueue = new LinkedList<>();

        /**
         * 进队
         *
         * @param dialog
         */
        public void offer(Dialog dialog) {
            mDialogQueue.offer(dialog);
        }

        /**
         * 出队
         *
         * @return
         */
        public Dialog poll() {
            return mDialogQueue.poll();
        }

    }
}