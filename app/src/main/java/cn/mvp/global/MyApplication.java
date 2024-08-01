package cn.mvp.global;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import cn.mvp.db.DbHelp;
import cn.mvp.mlibs.MLibs;
import cn.mvp.mlibs.log.XLogUtil;
import cn.mvp.mlibs.other.ClipperReceiver;
import cn.mvp.mlibs.other.ICallBack;
import cn.mvp.mlibs.utils.CrashHandlerUtil;

/**
 * Created by efan on 2017/4/13.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MLibs.init(this);
        MMKV.initialize(this);
        LogUtils.getConfig().setGlobalTag("调试信息");
        context = getApplicationContext();
        ToastUtils.init(this);
        CrashHandlerUtil.getInstance().init(this);
        DbHelp.init(this);
//        MLibs.init();
        XLogUtil.showActivity(this, true);
        XLogUtil.lockLog(new ICallBack() {
            @Override
            public void back() {

            }
        });

        ClipperReceiver.getInstance().registerReceiver(this, null);
//        ClipperReceiver clipperReceiver = new ClipperReceiver();
//        clipperReceiver.registerReceiver(this, null);
        /*registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i("调试信息", "姐姐家基金基金基金 "+activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });*/
    }

    public static Context getContext() {
        return context;
    }
}
