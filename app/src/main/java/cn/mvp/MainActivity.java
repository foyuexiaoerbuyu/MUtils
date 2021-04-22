package cn.mvp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import cn.mvp.acty.BaseActivity;
import cn.mvp.mlibs.log.Log;
import cn.mvp.test.arcVPDN;

public class MainActivity extends BaseActivity {

    arcVPDN aVPDN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        PermissionsUtils.requestDefPermissions(this);
//        TimeMangeActivity.openActivity(MainActivity.this);
        XXPermissions.with(this).permission(Manifest.permission.WRITE_APN_SETTINGS).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {

            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {

            }
        });


        findViewById(R.id.btn1).setOnClickListener(v -> {
            aVPDN = new arcVPDN(this);
            Log.i("....");
            //Login();
            if (!aVPDN.Exisit("网络名称")) {
                //调用vpdn设置界面
                startActivityForResult(new Intent(android.provider.Settings.ACTION_APN_SETTINGS), 0);
            }
            int apnid = aVPDN.GetApn("网络名称");
            aVPDN.SetDefault(apnid);

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