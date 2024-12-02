package cn.mvp.mlibs.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

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
//            ResourcesCompat.getDrawable(getResources(), id, null);
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

    //颜色 #2A82FF
    public static int getColor(String colorString) {
        if (!colorString.startsWith("#")) {
            colorString = "#" + colorString;
        }
        return Color.parseColor(colorString);
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
     * 单位:px
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
     * 获取屏幕尺寸  [0]:宽度 [1]:高度 单位:px
     */
    public static int[] getScreenSize() {
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;//屏幕密度
        int densityDpi = dm.densityDpi;//像素密度(Android常用的dp/dip单位)
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /**
     * 获取屏幕密度
     */
    public static float getScreenDensity() {
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }


    /**
     * @param view 获取控件相对于屏幕的控件中心位置坐标
     * @return view中心坐标(相对于屏幕)
     */
    public static int[] getCentralPosForScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewCenterX = location[0] + view.getWidth() / 2;
        int viewCenterY = location[1] + view.getHeight() / 2;
        //赋值
        location[0] = viewCenterX;
        location[1] = viewCenterY;
        return location;
    }


    /**
     * 获取屏幕尺寸  [0]:宽度 [1]:高度 单位:dp
     */
    public static int[] getScreenSizeInDp(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        return new int[]{(int) (dm.widthPixels / density), (int) (dm.heightPixels / density)};
    }


    /*----------------加载布局-----------------------*/
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    /*----------------判断是运行在主线程-----------------------*/
    public static Boolean isRunOnUIThread() {
        //获取当前线程id，如果当前线程和主线程id相同，那么当前就是主线程。此方法有可能会阻塞线程
//        return android.os.Process.myTid() == getMainThreadId();
        return Looper.getMainLooper() == Looper.myLooper() ||
                Looper.getMainLooper().getThread() == Thread.currentThread() ||
                Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId() ||
                android.os.Process.myTid() == getMainThreadId();
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

    /**
     * @param view 目标view
     * @param x    触摸位置x坐标
     * @param y    触摸位置y坐标
     * @return 触摸坐标是否在目标view内
     */
    public static boolean isTouchPointInView(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        // 判断触摸点是否在该View内
        return y >= top && y <= bottom && x >= left && x <= right;
    }

    /**
     * 设置透明度（0~255之间的值，0为完全透明，255为不透明）
     *
     * @param view 目标view
     */
    public static void setAlpha(View view, float alpha) {
        view.setAlpha(alpha);  // 0.5f: 设置透明度为50%
    }

    /**
     * 修改View的背景颜色和透明度
     *
     * @param view  要修改的View
     * @param color 背景颜色
     * @param alpha 透明度，取值范围0-255，0表示完全透明，255表示完全不透明
     */
    public static void changeViewBgColorAlpha(View view, int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        view.setBackgroundColor(Color.argb(alpha, red, green, blue));
    }

    /**
     * @param view 目标view
     * @return 目标view四个角屏幕坐标
     */
    public static List<Integer> getViewPos(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        return Arrays.asList(left, top, right, bottom);
    }

    /**
     * @param view view
     * @return view四个角屏幕坐标是否都在原点 (是否还未进行绘制测量)
     */
    public static boolean isOrigin(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        Log.i("调试信息", "getViewPos:  " + left + " " + top + " " + right + " " + bottom + " ");
        return (left == 0 && top == 0 && right == 0 && bottom == 0);
    }

    /**
     * @param activity 打开的Activity
     * @return 是否横屏
     */
    public static boolean isLandscape(Activity activity) {
        return activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    /*----------------提示消息start-----------------------*/
    //需引入jar包:implementation 'com.hjq:toast:8.0'
//    public static void tipToast(final String msg) {
//        ToastUtils.show(msg);
//    }

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

    /**
     * px转sp
     */
    public static int pxToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scaledDensity + 0.5f);
    }


    /**
     * 设置view显示隐藏
     */
    public static void setVisibility(View view, boolean isShow) {
        view.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

}
