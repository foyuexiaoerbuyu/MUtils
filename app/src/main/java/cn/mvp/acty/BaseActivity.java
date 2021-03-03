package cn.mvp.acty;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import cn.mvp.utils.XLog;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        XLog.commonLog(XLog.TAG, "当前所在Activity  " + getClass().getSimpleName());
    }
}
