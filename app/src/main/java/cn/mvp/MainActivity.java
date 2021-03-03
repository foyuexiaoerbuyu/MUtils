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
import cn.mvp.service.PhotoCaptureService;
import cn.mvp.utils.PermissionsUtils;
import cn.mvp.utils.XLog;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionsUtils.requestDefPermissions(this);
//        TimeMangeActivity.openActivity(MainActivity.this);

        initSelectPhotoCaptureBackup();

        XXPermissions.with(this).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    UIUtils.tipToast("部分权限未通过");
                }
            }
        });

        String namse = "13540409316,18227010923,15283819108,13408638937,13541856871,18382404253,15908400893,15884641106,15884456188,13659053465,15108305082,15982997113,15928717498,13882204073,15008462202,15828579553,13990376310,18281380338,18380487751,18783087852,15882588474,13540704245,18244252011,13795760702,15928694700,13540827215,13679649177,15883125947,18398777722,18482179842,15082349162,13882162121,18483231909,13558960813,18784598086,13880051966,13541112105,18280376537,13778669829,15892816067,13550915971,18380492901,15708475063,13608079995,15182058944,13458650667,18328175597,18380439776,13709036139,15828498651";
        String[] split = namse.split(",");
        ArrayList<ContactUtils.Tb_contacts> tb_contacts1 = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            ContactUtils.Tb_contacts tb_contacts = new ContactUtils.Tb_contacts();
            tb_contacts.setName(split[i]);
            tb_contacts.setNumber(split[i]);
            tb_contacts1.add(tb_contacts);
        }
        try {
            ContactUtils.batchAddContact(this, tb_contacts1);
        } catch (Exception e) {
            XLog.printExceptionInfo(e);
        }
        findViewById(R.id.btn1).setOnClickListener(v -> {

            Log.i("....");
        });

        findViewById(R.id.btn2).setOnClickListener(v -> {
            Log.i("....0000..");
        });
    }

    private void initSelectPhotoCaptureBackup() {
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch photoCaptureBackup = findViewById(R.id.btn_setting_photo_capture_backup);
        photoCaptureBackup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PhotoCaptureService.startService(MainActivity.this, true);
            } else {
                PhotoCaptureService.stopService(MainActivity.this);
            }
            UIUtils.tipToast(MainActivity.this, isChecked ? "开启照片截图备份" : "关闭照片截图备份");
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