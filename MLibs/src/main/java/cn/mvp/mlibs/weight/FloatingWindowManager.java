package cn.mvp.mlibs.weight;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * https://blog.csdn.net/xiaoerbuyu1233/article/details/143595475
 * <p>
 * 跨页面悬浮窗 Application#onCreate 初始化
 * <p>
 * FloatingWindowManager instance = FloatingWindowManager.getInstance(MyApplication.this);
 * instance.setImg(R.drawable.ic_bank_cards_footer_view_add).setWidthHeight(150, 150)
 * .setOnFloatingClickListener((context, floatingView) -> {
 * List<String> items = List.of("随机8位数字", "随机8位字母");
 * instance.showListDialogCopy(context, "", items, (dialog, which) -> {
 * String text = "";
 * if (which == 0) {
 * text = instance.getRandomNumber(8);
 * } else if (which == 1) {
 * text = instance.getRandomStr(8);
 * }
 * instance.copyText(context, text);
 * // 遍历所有子视图
 * instance.setFocusedEditTextContent(text);
 * Toast.makeText(context, "生成完毕", Toast.LENGTH_SHORT).show();
 * });
 * });
 */
public class FloatingWindowManager {
    private static FloatingWindowManager instance;
    private final Context context;
    private FrameLayout floatingView;
    private ViewGroup rootView;
    private OnFloatingClickListener listener;
    private int width = 150, height = 150;
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

    public void copyText(Context context, String text) {
        // 复制文本到剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }

    public void setFocusedEditTextContent(String content) {
        // 遍历所有子视图
        setEditTextContentIfFocused(rootView, content);
    }

    private void setEditTextContentIfFocused(View view, String content) {
        if (view instanceof ViewGroup) {  // 如果当前视图是一个容器
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {  // 遍历其子视图
                View child = viewGroup.getChildAt(i);
                setEditTextContentIfFocused(child, content);  // 递归调用
            }
        } else if (view instanceof EditText && view.isFocused()) {  // 如果当前视图是EditText且有焦点
            ((EditText) view).setText(content);  // 设置EditText的内容
        }
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
        layoutParams.gravity = Gravity.TOP | Gravity.END;
        floatingView.setLayoutParams(layoutParams);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
//        float density = dm.density;//屏幕密度
//        int densityDpi = dm.densityDpi;//像素密度(Android常用的dp/dip单位)
        floatingView.setY(dm.heightPixels - 200);//设置起始位置Y坐标
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
                    floatingView.getHandler().post(() -> listener.onClick(floatingView.getRootView().getContext(), floatingView));
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

    /**
     * @param edtId 编辑框id
     * @param text  设置内容
     * @return 设置内容的view 有可能为空
     */
    public View setEditTextContent(int edtId, String text) {
        // 尝试找到具有给定ID的视图
        View view = rootView.findViewById(edtId);

        // 检查找到的视图是否为 null
        if (view == null) {
            // 如果找不到视图，则打印错误信息
            Log.e("SetTextError", "No view found with id: " + edtId);
            return view;
        }

        // 检查找到的视图是否是 EditText
        if (view instanceof EditText) {
            // 设置文本
            ((EditText) view).setText(text);
        } else {
            // 如果视图不是 EditText，则打印警告信息
            Log.w("SetTextWarning", "View with id: " + edtId + " is not an EditText.");
            return view;
        }
        return view;
    }

    public interface OnFloatingClickListener {
        void onClick(Context context, FrameLayout floatingView);
    }

    /**
     * 显示一个列表对话框，用户点击列表项后会将该项内容复制到剪贴板
     *
     * @param context 上下文
     * @param items   列表项数据
     */
    public void showListDialogCopy(Context context, String title, List<String> items, DialogInterface.OnClickListener onClickListener) {
        // 创建一个AlertDialog.Builder对象
//准备一个String数组
        String[] strs = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            strs[i] = items.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 设置对话框标题
        builder.setTitle(title);
        // 设置列表项
        builder.setItems(strs, (dialog, which) -> {
            // 关闭对话框
            dialog.dismiss();
            if (onClickListener != null) {
                onClickListener.onClick(dialog, which);
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 生成指定长度的随机数字字符串
     *
     * @param length 随机数字字符串的长度
     * @return 指定长度的随机数字字符串
     */
    public String getRandomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer");
        }
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 生成0到9之间的随机整数
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字母字符串
     *
     * @param length 字符串的长度
     * @return 随机字母字符串
     */
    public String getRandomStr(int length) {
//        if (length < 5 || length > 64) {
//            throw new IllegalArgumentException("Length must be between 7 and 99 characters.");
//        }
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

}