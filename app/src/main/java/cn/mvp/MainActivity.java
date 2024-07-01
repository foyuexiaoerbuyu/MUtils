package cn.mvp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.util.CodeUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cn.mvp.acty.BaseActivity;
import cn.mvp.acty.ElectricQuantityActivity;
import cn.mvp.acty.zfb.ZfbActivity;
import cn.mvp.chat1.Chat1Activity;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.utils.ClipboardUtils;
import cn.mvp.mlibs.utils.DeviceUtils;
import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.GsonUtil;
import cn.mvp.mlibs.utils.NetworkUtils;
import cn.mvp.mlibs.utils.OkHttpUtil;
import cn.mvp.mlibs.utils.SDCardUtils;
import cn.mvp.mlibs.utils.StringUtil;

public class MainActivity extends BaseActivity {

    private boolean isRefuse;
    private TextView mTv;

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            mTv.setText(sharedText);
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
        findViewById(R.id.main_btn_creat_qr_code).setOnClickListener(v -> {
            String content = String.valueOf(ClipboardUtils.getText(MainActivity.this));
            Bitmap qrCode = CodeUtils.createQRCode(content, 600);
            BitmapDrawable drawbale = new BitmapDrawable(MainActivity.this.getResources(), qrCode);
            mTv.setCompoundDrawablesWithIntrinsicBounds(null, drawbale, null, null);
            mTv.setText(content);
        });
        mTv = findViewById(R.id.main_tv);
        EditText editText = findViewById(R.id.edt1);
        mTv.setOnClickListener(v -> {
            ClipboardUtils.copyText(MainActivity.this, mTv.getText());
            toast("已复制内容");
        });
        findViewById(R.id.main_btn_ip_qr_code).setOnClickListener(v -> showIp());
        findViewById(R.id.main_btn_chat).setOnClickListener(v -> {
            Chat1Activity.open(MainActivity.this);//WebSocketClient实现
        });
        Button connBtn = findViewById(R.id.main_btn_conn_service);
        connBtn.setOnClickListener(v -> {
            ToastUtils.show("待实现功能...");
        });
        findViewById(R.id.main_btn_base_info).setOnClickListener(v -> {
            mTv.setText(DeviceUtils.getDeviceInfo());//获取手机基本信息
        });
        findViewById(R.id.main_btn_base_tv_tools).setOnClickListener(v -> {
            final String[] items3 = new String[]{"上传剪贴板文件"};
            new AlertDialog.Builder(this).setTitle("选择工具").setItems(items3, (dialogInterface, pos) -> {
                if (pos == 0) {
                    new Thread(() -> {
                        String url = "http://192.144.219.245:8088/shareFile/upload";
                        String filePath = SDCardUtils.getInternalCacheDir(MainActivity.this) + "/tmp_剪贴板文件.txt";
                        String text = ClipboardUtils.getText(MainActivity.this);
                        if (StringUtil.isBlank(text)) {
                            ToastUtils.show("剪贴板内容为空");
                            return;
                        }
                        Log.i("调试信息", filePath + "\n" + text);
                        FileUtils.writeFile(filePath, text);
                        OkHttpUtil.UploadFile file = new OkHttpUtil.UploadFile(filePath);
                        try {
                            String s = OkHttpUtil.uploadFile(url, file, null);
                            Log.i("调试信息", "s = " + s);
                            ToastUtils.show(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                            ToastUtils.show(e.getMessage());
                            Log.e("调试信息", "上传失败!!!", e);
                        }
                    }).start();
                } else if (pos == 1) {
                }
            }).create().show();
        });

        findViewById(R.id.main_btn_test_page).setOnClickListener(v -> {
            final String[] items3 = new String[]{"支付宝花呗", "剩余电量"};
            new AlertDialog.Builder(this).setTitle("选择跳转页面").setItems(items3, (dialogInterface, pos) -> {
                if (pos == 0) {
                    ZfbActivity.open(MainActivity.this);
                } else if (pos == 1) {
                    ElectricQuantityActivity.open(MainActivity.this);
                }
            }).create().show();
        });
        findViewById(R.id.main_btn_camera).setOnClickListener(v -> {
            takePicture();
        });
        findViewById(R.id.btn1).setOnClickListener(v -> {
            // TODO: 2023/9/12 按钮1

        });


        findViewById(R.id.btn2).setOnClickListener(v -> {
            // TODO: 2023/9/12 按钮2
//            TestActivity.open(MainActivity.this);
        });

        showIp();
        initPermissions();
//        TestActivity1.open(this);
    }

    private void initPermissions() {
        SDCardUtils.openAllFileAccessPermissions(MainActivity.this, 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {//悬浮窗权限
            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent1, 2);
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
            /*try {
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
            }*/
        }
    }


    public void print(String msg) {
        mTv.setText(msg);
    }


    private void takePicture() {
        PictureSelector.create(MainActivity.this).openCamera(SelectMimeType.ofImage()).forResult(new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(ArrayList<LocalMedia> result) {
                for (LocalMedia localMedia : result) {
                    try {
                        FileUtils.moveFile(localMedia.getRealPath(), SDCardUtils.getPublicDownDir() + "." + FileUtils.getFileName(localMedia.getRealPath()));
                    } catch (FileNotFoundException e) {
                        Log.e("调试信息", "onR1esult:  --", e);
                    }
                }
                Log.i("调试信息", "onResult:  " + GsonUtil.toJson(result));
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void showIp() {
        String content = NetworkUtils.getIpAddressByWifi(MainActivity.this);
        Bitmap qrCode = CodeUtils.createQRCode(content, 600);
        BitmapDrawable drawbale = new BitmapDrawable(MainActivity.this.getResources(), qrCode);
        mTv.setCompoundDrawablesWithIntrinsicBounds(null, drawbale, null, null);
        mTv.setText(content);
    }

    @Override
    public void initData() {
//        ClipboardActivity.open(this);
//        TestActivity1.open(this);
    }

    @Override
    public int setView() {
        return R.layout.activity_main;
    }
}