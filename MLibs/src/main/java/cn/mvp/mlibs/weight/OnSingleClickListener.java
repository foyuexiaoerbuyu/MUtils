package cn.mvp.mlibs.weight;

import android.view.View;

/**
 * 防止连点
 */
public abstract class OnSingleClickListener implements View.OnClickListener {

    private final long MIN_CLICK_INTERVAL = 1000; // 1秒
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < MIN_CLICK_INTERVAL) {
            return;
        }
        lastClickTime = currentTime;
        onSingleClick(v);
    }

    public abstract void onSingleClick(View v);
}