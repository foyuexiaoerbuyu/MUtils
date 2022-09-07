package cn.mvp.mlibs.broadcast;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.mvp.mlibs.log.XLogUtil;

class OnDestroyListener implements LifecycleObserver {
    private final BroadcastReceiver receiver;
    private String TAG;

    OnDestroyListener(@NonNull BroadcastReceiver receiver, String logTag) {
        this.receiver = receiver;
        TAG = logTag;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void unregister(@NonNull LifecycleOwner lifecycleOwner) {
        LocalBroadcastUtil.unregisterReceiver(receiver);
        lifecycleOwner.getLifecycle().removeObserver(this);
        if (LocalBroadcastUtil.debugEnable) {
            XLogUtil.i(TAG, "已自动注销 " + receiver.toString());
        }
    }

    @Override
    public int hashCode() {
        return receiver.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof OnDestroyListener) {
            return ((OnDestroyListener) obj).receiver.equals(receiver);
        }
        return false;
    }
}
