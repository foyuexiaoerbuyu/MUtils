package cn.mvp.global;

import android.app.Application;

import com.hjq.toast.ToastUtils;

import cn.mvp.db.DbHelp;
import cn.mvp.mlibs.MLibs;
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
    }
}
