package cn.mvp.global;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.hjq.toast.ToastUtils;

import cn.mvp.db.DbHelp;
import cn.mvp.mlibs.MLibs;
import cn.mvp.mlibs.log.XLogUtil;
import cn.mvp.mlibs.utils.CrashHandlerUtil;

/**
 * Created by efan on 2017/4/13.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        MLibs.init(this);
        ToastUtils.init(this);
        CrashHandlerUtil.getInstance().init(this);
        DbHelp.init(this);
//        MLibs.init();
        XLogUtil.showActivity(this,true);
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

}
