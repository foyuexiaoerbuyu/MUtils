package cn.mvp.mlibs.utils;

import android.app.Activity;
import android.os.SystemClock;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Mi<p>
 * Description: IOnClickListener<p>
 * Date: 2023/5/25 15:44<p>
 * Updater：<p>
 * 防止快速点击
 */
public abstract class IOnClickListener implements View.OnClickListener {

    private final Map<Integer, Long> mMap = new HashMap<>();

    public static void click(IOnClickListener iOnClickListener, View... views) {
        for (View view : views) {
            view.setOnClickListener(iOnClickListener);
        }
    }

    public static void click(Activity activity, IOnClickListener iOnClickListener, int... ids) {
        for (int id : ids) {
            activity.findViewById(id).setOnClickListener(iOnClickListener);
        }
    }

    public void onClick(View v, IOnClickListener iOnClickListener) {
        Long lastClickTime = mMap.get(v.getId());
        if (lastClickTime != null && (SystemClock.elapsedRealtime() - lastClickTime < 1000)) {
            return;
        }
        mMap.put(v.getId(), SystemClock.elapsedRealtime());
        iOnClickListener.OnClick(v);
    }

    @Override
    public void onClick(View v) {
        Long lastClickTime = mMap.get(v.getId());
        if (lastClickTime != null && (SystemClock.elapsedRealtime() - lastClickTime < 1000)) {
            return;
        }
        mMap.put(v.getId(), SystemClock.elapsedRealtime());
        OnClick(v);
    }

    public abstract void OnClick(View v);

}
