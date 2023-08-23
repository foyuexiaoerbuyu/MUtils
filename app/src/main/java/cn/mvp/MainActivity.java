package cn.mvp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;
import com.king.zxing.util.CodeUtils;

import java.io.IOException;
import java.net.DatagramSocket;

import cn.mvp.acty.BaseActivity;
import cn.mvp.acty.ElectricQuantityActivity;
import cn.mvp.acty.zfb.ZfbActivity;
import cn.mvp.chat.ChatActivity;
import cn.mvp.chat1.Chat1Activity;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.utils.ClipboardUtils;
import cn.mvp.mlibs.utils.DeviceUtils;
import cn.mvp.mlibs.utils.IntentUtil;
import cn.mvp.mlibs.utils.NetworkUtils;
import cn.mvp.mlibs.utils.StringUtil;
import cn.mvp.mlibs.utils.VerifyUtils;
import cn.mvp.test.TestActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private boolean isRefuse;
    private TextView mTv;
    private DatagramSocket mClientSocket;
    private byte[] mReceiveData;


    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            mTv.setText(sharedText);
        }
    }

    // 带回授权结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 检查是否有权限
            if (Environment.isExternalStorageManager()) {
                isRefuse = false;
                // 授权成功
            } else {
                isRefuse = true;
                // 授权失败
            }
        }
        if (requestCode == Constant.REQUEST_CODE_SCAN_QRCODE) {
            try {
                VerifyUtils.checkTrue(StringUtil.isBlank(data), "二维码数据为空");
                String result = data.getStringExtra(Intents.Scan.RESULT);
                ClipboardUtils.copyToClipboard(MainActivity.this, result);
                ToastUtils.show("已复制二维码信息到剪切板: " + result);
                String trim = result.trim();
                Log.i("调试信息", "onActivityRtrimesult:  " + trim);
                Log.i("调试信息", "onActivityResult:  " + trim.startsWith("http://") + " = " + trim.startsWith("https://") + " = " + trim.startsWith("www."));
                if (trim.startsWith("http://") || trim.startsWith("https://") || trim.startsWith("www.")) {
                    Log.i("调试信息", "onActivityResult:  " + trim);
                    IntentUtil.goBrowser(this, trim);
                }
            } catch (IOException e) {
                ToastUtils.show(e.getLocalizedMessage());
            }
        }
    }


    public void print(String msg) {
        mTv.setText(msg);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_btn_creat_qr_code) {
            String content = String.valueOf(ClipboardUtils.getText(this));
            Bitmap qrCode = CodeUtils.createQRCode(content, 600);
            BitmapDrawable drawbale = new BitmapDrawable(this.getResources(), qrCode);
            mTv.setCompoundDrawablesWithIntrinsicBounds(null, drawbale, null, null);
            mTv.setText(content);
        } else if (v.getId() == R.id.main_btn_ip_qr_code) {
            String content = NetworkUtils.getIpAddressByWifi(this);
            Bitmap qrCode = CodeUtils.createQRCode(content, 600);
            BitmapDrawable drawbale = new BitmapDrawable(this.getResources(), qrCode);
            boolean checkedCopy = ((Switch) findViewById(R.id.main_btn_sw_copy)).isChecked();
            if (checkedCopy) {
                ClipboardUtils.copyToClipboard(this, content);
            }
            mTv.setCompoundDrawablesWithIntrinsicBounds(null, drawbale, null, null);
            mTv.setText(content);
        }
    }

    @Override
    public void initView() {
        // 处理接收到的Intent
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // 处理纯文本的分享内容
            }
        }

//        PermissionsUtils.requestDefPermissions(this);
        findViewById(R.id.main_btn_qr_code).setOnClickListener(v -> startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE));
        findViewById(R.id.main_btn_creat_qr_code).setOnClickListener(this);
        mTv = findViewById(R.id.main_tv);
        findViewById(R.id.main_btn_ip_qr_code).setOnClickListener(this);
        findViewById(R.id.main_btn_chat).setOnClickListener(v -> {
            Chat1Activity.open(MainActivity.this);//WebSocketClient实现
        });
        findViewById(R.id.main_btn_base_info).setOnClickListener(v -> {
            mTv.setText(DeviceUtils.getDeviceInfo());//获取手机基本信息
        });
        findViewById(R.id.main_btn_base_tv_test).setOnClickListener(v -> {
            TestActivity.open(MainActivity.this);//测试按钮
        });

        findViewById(R.id.main_btn_test_page).setOnClickListener(v -> {
            final String[] items3 = new String[]{"支付宝花呗", "剩余电量"};
            new AlertDialog.Builder(this)
                    .setTitle("选择跳转页面")
                    .setItems(items3, (dialogInterface, pos) -> {
                        if (pos == 0) {
                            ZfbActivity.open(MainActivity.this);
                        } else if (pos == 1) {
                            ElectricQuantityActivity.open(MainActivity.this);
                        }
                    }).create().show();
        });
        findViewById(R.id.btn1).setOnClickListener(v -> ChatActivity.open(MainActivity.this));
        findViewById(R.id.btn2).setOnClickListener(v -> {
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public int setView() {
        return R.layout.activity_main;
    }
}