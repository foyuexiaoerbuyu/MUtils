package cn.mvp.mlibs.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import cn.mvp.mlibs.MLibs;

public class UIUtils {

    public static Context getContext() {
        return MLibs.getContext();
    }

    public static Handler getHandler() {
        return MLibs.getHandler();
    }

    public static int getMainThreadId() {
        return MLibs.getMainThreadId();
    }

    /*----------------获取资源-----------------------*/
    public static Resources getResources() {
        return getContext().getResources();
    }

    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    //数组资源
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    //图片资源
    public static Drawable getDrawable(int id) {
        try {
            return ContextCompat.getDrawable(getContext(), id);
        } catch (Exception e) {
            return getContext().getResources().getDrawable(id);
        }
    }

    //颜色
    public static int getColor(int id) {
        try {
            return ContextCompat.getColor(getContext(), id);
        } catch (NoSuchMethodError e) {
            return getContext().getResources().getColor(id);
        }
    }

    //颜色
    public static int getColor(Context context, int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    //根据id获取颜色的状态选择器
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    //尺寸
    public static int getDimen(int id) {// 获取尺寸 Dimension（待门神）尺寸
        return getContext().getResources().getDimensionPixelSize(id);
    }

    /**
     * 设置view边距
     *
     * @param v 视图
     * @param l 左
     * @param t 上
     * @param r 右
     * @param b 下
     */
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v == null) {
            return;
        }
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /*----------------dip和px转换-----------------------*/
    public static int dip2px(float dip) { // Metrics（满锤思）测量density（扽思提）密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    /**
     * 获取屏幕尺寸  [0]:宽度 [1]:高度
     */
    public static int[] getScreenSize() {
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
//        float density1 = dm.density;//密度
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /*----------------加载布局-----------------------*/
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    /*----------------判断是运行在主线程-----------------------*/
    public static Boolean isRunOnUIThread() {
        //获取当前线程id，如果当前线程和主线程id相同，那么当前就是主线程。
        return android.os.Process.myTid() == getMainThreadId();
    }

    //运行在主线程
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            //已经是主线程，直接运行
            r.run();
        } else {
            //如果是 子线程，借助handler让其运行在主线程
            getHandler().post(r);
        }
    }

    /*----------------提示消息start-----------------------*/
    //需引入jar包:implementation 'com.hjq:toast:8.0'
    public static void tipToast(final String msg) {
        ToastUtils.show(msg);
    }

    public static void tipToast(final Context context, final String msg) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                ToastUtils.show(msg);
            }
        });
    }

    public static void showSimpleDialog(Context context, String msg) {
        showDialog(context, true, msg, null);
    }

    public static void showDialog(Context context, boolean cancelable, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(msg).setCancelable(cancelable).setPositiveButton("确定", listener);
        if (cancelable) {
            builder.setNegativeButton("取消", null);
        }
        builder.show();
    }
    /*----------------提示消息end-----------------------*/

}
