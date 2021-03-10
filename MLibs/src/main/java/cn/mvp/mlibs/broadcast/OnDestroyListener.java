package cn.mvp.mlibs.broadcast;

import android.content.BroadcastReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import cn.mvp.mlibs.log.LogUtils;

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
            LogUtils.i(TAG, "已自动注销 " + receiver.toString());
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
