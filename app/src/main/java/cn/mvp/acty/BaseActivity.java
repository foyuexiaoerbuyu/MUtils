package cn.mvp.acty;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;

import java.util.Timer;

import butterknife.ButterKnife;
import cn.mvp.mlibs.log.XLogUtil;

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "BaseActivity";
    private Timer mTimer;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(setView());
        ButterKnife.bind(this);
        receiverData();
        initView();
        initData();
    }

    public void receiverData() {

    }

    public abstract void initView();

    public abstract void initData();

    public abstract int setView();

    public void toast(String msg) {
        XLogUtil.getChildLog(msg);
        ToastUtils.showShort(msg);
    }


    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    public void showProgressDialog(String msg) {
//        new AlertDialog.Builder(this)
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

//    /** 字体大小不随系统改变(暂时去掉) */
//    @Override
//    public Resources getResources() {
//        Resources res = super.getResources();
//        Configuration config = new Configuration();
//        config.setToDefaults();
//        res.updateConfiguration(config, res.getDisplayMetrics());
//        return res;
//    }
}