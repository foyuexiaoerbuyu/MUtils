package cn.mvp.mlibs.weight;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

/**
 * https://blog.csdn.net/xiaoerbuyu1233/article/details/143595475
 * <p>
 * 跨页面悬浮窗 Application#onCreate 初始化
 * <p>
 * FloatingWindowManager
 * .getInstance(App.this).setImg(R.drawable.ic_test).setWidthHeight(280,280)
 * .setOnFloatingClickListener(new FloatingWindowManager.OnFloatingClickListener() {
 *
 * @Override public void onClick(FrameLayout floatingView) {
 * Toast.makeText(floatingView.getContext(), "测试点击悬浮窗", Toast.LENGTH_SHORT).show();
 * }
 * });
 */
public class FloatingWindowManager {
    private static FloatingWindowManager instance;
    private final Context context;
    private FrameLayout floatingView;
    private ViewGroup rootView;
    private OnFloatingClickListener listener;
    private int width = 200, height = 200;
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    private FloatingWindowManager(Context context) {
        this.context = context;
        initFloatingView();
        registerActivityLifecycleCallbacks();
    }

    public static synchronized FloatingWindowManager getInstance(Context context) {
        if (instance == null) {
            instance = new FloatingWindowManager(context);
        }
        return instance;
    }

    private void registerActivityLifecycleCallbacks() {
        Application application = (Application) context.getApplicationContext();
        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d("FloatingWindowManager", "onActivityCreated: " + activity.getLocalClassName());
                // 可以在这里附加悬浮窗口到新创建的 Activity
                attachToActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d("FloatingWindowManager", "onActivityStarted: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d("FloatingWindowManager", "onActivityResumed: " + activity.getLocalClassName());
                attachToActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                detachFromActivity();
                Log.d("FloatingWindowManager", "onActivityPaused: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d("FloatingWindowManager", "onActivityStopped: " + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.d("FloatingWindowManager", "onActivitySaveInstanceState: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("FloatingWindowManager", "onActivityDestroyed: " + activity.getLocalClassName());
            }
        };

        // 注册 Activity 生命周期回调
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }


    public FloatingWindowManager setImg(int icon) {
        // 使用资源ID直接设置背景
        if (floatingView != null) {
            floatingView.setBackgroundResource(icon);
        }
//        // 获取Drawable对象
//        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_test);
//
//        // 设置背景
//        if (drawable != null) {
//            floatingView.setBackground(drawable);
//        }
        return this;
    }

    public FloatingWindowManager setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;

        // 设置布局参数
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height); // 宽度和高度
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        floatingView.setLayoutParams(layoutParams);
        return this;
    }

    private void initFloatingView() {
        // 初始化悬浮窗视图
        floatingView = new FrameLayout(context) {
            @Override
            public boolean performClick() {
                if (super.performClick()) {
                    return true;
                }
                if (listener != null) {
                    floatingView.getHandler().post(() -> listener.onClick(floatingView));
                }
                return true;
            }
        };

        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float startX, startY;
            private static final int CLICK_DRAG_TOLERANCE = 40; // 点击和拖动之间的容差距离

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = (int) (event.getRawX() - v.getX());
                        initialY = (int) (event.getRawY() - v.getY());
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) (event.getRawX() - initialX);
                        int newY = (int) (event.getRawY() - initialY);
                        if (rootView != null) {
                            v.setX(newX);
                            v.setY(newY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        float distanceX = Math.abs(endX - startX);
                        float distanceY = Math.abs(endY - startY);
                        if (distanceX < CLICK_DRAG_TOLERANCE && distanceY < CLICK_DRAG_TOLERANCE) {
                            // 如果移动的距离小于容差，那么我们认为这是一个点击
                            if (floatingView.performClick()) {
                                return true; // 点击已被处理
                            }
                        } else {
                            // 如果移动的距离大于容差，那么我们认为这是一个拖动
                            snapToEdge();
                        }
                        break;
                    default:
                        break;
                }
                return true; // 消耗事件
            }
        });

        // 设置布局参数
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height); // 宽度和高度
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        floatingView.setLayoutParams(layoutParams);
    }

    public void attachToActivity(Activity activity) {
        if (activity == null || floatingView == null) return;

        rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        if (rootView != null && floatingView.getParent() == null) {
            rootView.addView(floatingView);
        }
    }

    public void detachFromActivity() {
        if (rootView != null && floatingView != null && floatingView.getParent() != null) {
            rootView.removeView(floatingView);
        }
    }

    public void setOnFloatingClickListener(OnFloatingClickListener listener) {
        this.listener = listener;
    }

    private void snapToEdge() {
        // 实现贴边功能
        // 这里只是一个简单的例子，你可以根据自己的需求调整贴边的具体实现
        int[] location = new int[2];
        floatingView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        int screenWidth = rootView.getWidth();
        int screenHeight = rootView.getHeight();

        int halfScreenWidth = screenWidth / 4; // 假设屏幕宽度的3/4处作为贴边判断点
        if (x < halfScreenWidth) {
            floatingView.setX(0);
        } else {
            floatingView.setX(screenWidth - floatingView.getWidth());
        }
    }

    public interface OnFloatingClickListener {
        void onClick(FrameLayout floatingView);
    }

    /**
     * 显示一个列表对话框，用户点击列表项后会将该项内容复制到剪贴板
     *
     * @param context 上下文
     * @param items   列表项数据
     */
    public static void showListDialogCopy(Context context, String title, List<String> items) {
        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 设置对话框标题
        builder.setTitle(title);

        // 设置列表项
        builder.setItems(items.toArray(new CharSequence[0]), (dialog, which) -> {
            // 获取用户选择的项
            String selectedItem = items.get(which);

            // 复制文本到剪贴板
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", selectedItem);
            clipboard.setPrimaryClip(clip);

            // 可选: 提示用户已成功复制
            Toast.makeText(context, "已复制: " + selectedItem, Toast.LENGTH_SHORT).show();

            // 关闭对话框
            dialog.dismiss();
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}