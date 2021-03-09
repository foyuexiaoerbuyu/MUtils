package cn.mvp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Switch;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

import cn.mvp.acty.BaseActivity;
import cn.mvp.mlibs.log.Log;
import cn.mvp.mlibs.utils.ContactUtils;
import cn.mvp.mlibs.utils.UIUtils;
import cn.mvp.mlibs.service.imgbackup.PhotoCaptureService;
import cn.mvp.utils.PermissionsUtils;
import cn.mvp.utils.XLog;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionsUtils.requestDefPermissions(this);
//        TimeMangeActivity.openActivity(MainActivity.this);

        findViewById(R.id.btn1).setOnClickListener(v -> {
            Log.i("....");
        });

        findViewById(R.id.btn2).setOnClickListener(v -> {
            Log.i("....0000..");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}