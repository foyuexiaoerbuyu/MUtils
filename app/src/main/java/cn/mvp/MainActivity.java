package cn.mvp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.petterp.floatingx.FloatingX;
import com.petterp.floatingx.assist.helper.AppHelper;
import com.petterp.floatingx.assist.helper.ScopeHelper;

import cn.mvp.acty.BaseActivity;
import cn.mvp.acty.ShortcutSettingActy;
import cn.mvp.mlibs.log.Log;
import cn.mvp.mlibs.utils.UIUtils;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppHelper helper = AppHelper.builder()
                .setLayout(R.layout.item_floating)
                .enableFx()
                .build();
        FloatingX.init(helper);
        ScopeHelper.builder()
                .setLayout(R.layout.item_floating)
                .build()
                .toControl(this).show();
        ScopeHelper.builder()
                .setLayout(R.layout.item_floating)
                .build()
                .toControl(this).show();
//                .toControl(fragment)
//                .toControl(viewgroup)

        UIUtils.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShortcutSettingActy.openActivity(MainActivity.this);
            }
        }, 2000);

//        CrashHandlerUtil.getInstance().
//        PermissionsUtils.requestDefPermissions(this);
//        TimeMangeActivity.openActivity(MainActivity.this);
//        XXPermissions.with(this).permission(Manifest.permission.WRITE_APN_SETTINGS).request(new OnPermission() {
//            @Override
//            public void hasPermission(List<String> granted, boolean all) {
//
//            }
//
//            @Override
//            public void noPermission(List<String> denied, boolean quick) {
//
//            }
//        });


        findViewById(R.id.btn1).setOnClickListener(v -> {
            initConfig();
//            aVPDN = new arcVPDN(this);
//            Log.i("....");
//            //Login();
//            if (!aVPDN.Exisit("网络名称")) {
//                //调用vpdn设置界面
//                startActivityForResult(new Intent(android.provider.Settings.ACTION_APN_SETTINGS), 0);
//            }
//            int apnid = aVPDN.GetApn("网络名称");
//            aVPDN.SetDefault(apnid);

        });

        findViewById(R.id.btn2).setOnClickListener(v -> {
            Log.i("....0000..");
        });
        EditText edt = findViewById(R.id.edt1);
//        edt.addTextChangedListener(new EditTextJudgeNumberWatcher(edt));
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable etPrice) {
                String tmpStr = etPrice.toString();
                int length = tmpStr.length();
                if (length == 1 && (tmpStr.equals("0") || tmpStr.equals("."))) {
                    etPrice.replace(0, 1, "");
                }

            }
        });
    }

    private void initConfig() {


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