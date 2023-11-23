package cn.mvp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;
import com.king.zxing.util.CodeUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.mvp.acty.BaseActivity;
import cn.mvp.acty.ElectricQuantityActivity;
import cn.mvp.acty.zfb.ZfbActivity;
import cn.mvp.chat1.Chat1Activity;
import cn.mvp.global.CfgInfo;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.socket.SocketUtils;
import cn.mvp.mlibs.utils.ClipboardUtils;
import cn.mvp.mlibs.utils.DeviceUtils;
import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.GsonUtil;
import cn.mvp.mlibs.utils.IntentUtil;
import cn.mvp.mlibs.utils.NetworkUtils;
import cn.mvp.mlibs.utils.SDCardUtils;
import cn.mvp.mlibs.utils.StringUtil;
import cn.mvp.mlibs.utils.VerifyUtils;
import cn.mvp.mlibs.weight.dialog.InputAlertDialog;
import cn.mvp.utils.SpUtils;

public class MainActivity extends BaseActivity {

    private boolean isRefuse;
    private TextView mTv;

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            mTv.setText(sharedText);
        }
    }

    private void showConnServiceDialog() {
        String str = StringUtil.defaultIfBlank(SpUtils.getCfgInfo().getLastConnIp(), NetworkUtils.getIpAddressByWifi(this) + ":8887");
        InputAlertDialog inputAlertDialog = new InputAlertDialog(this);
        inputAlertDialog.setEditText(str);
        inputAlertDialog.setCancelBtnClickDismiss(false);
        inputAlertDialog.setCancelClick("端口", (editText, inputStr) -> {
            if (inputStr.contains(":")) {
                editText.setSelection(inputStr.indexOf(":") + 1, inputStr.length());
            }
        });
        //            MMKV.defaultMMKV().encode("chat_ip", inputStr.substring(inputStr.lastIndexOf(".") + 1, inputStr.indexOf(":")));
        inputAlertDialog.setOkClick(this::connService);
        inputAlertDialog.setInputType(InputType.TYPE_CLASS_TEXT);
        inputAlertDialog.show();

        inputAlertDialog.showSoftKeyboard(str.indexOf(":"));

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
            String[] items = SpUtils.getCfgInfo().getConnectIpsArr();
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setItems(items, (dialogInterface, pos) -> {
                        toast("开始连接：" + items[pos]);
                        connService(items[pos]);
                    }).create();
            dialog.show();
            dialog.getListView().setOnItemLongClickListener((parent, view, pos, id) -> {
                SpUtils.delIp(items[pos]);
                toast("删除成功");
                return true;
            });
        });
        connBtn.setOnLongClickListener(v -> {
            showConnServiceDialog();
            return false;
        });
        findViewById(R.id.main_btn_base_info).setOnClickListener(v -> {
            mTv.setText(DeviceUtils.getDeviceInfo());//获取手机基本信息
        });
        findViewById(R.id.main_btn_base_tv_test).setOnClickListener(v -> {
//            TestActivity.open(MainActivity.this);//测试按钮
//            FloatingBtnForActy dragViewLayout = new FloatingBtnForActy(this, R.mipmap.ic_launcher);
//            dragViewLayout.show(this);
//            floatView.setOnClickListener(v1 -> Log.i("调试信息", "onClick:  "));
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
        findViewById(R.id.main_btn_camera).setOnClickListener(v -> {
            takePicture();
        });
        findViewById(R.id.btn1).setOnClickListener(v -> {
            // TODO: 2023/9/12 按钮1
//            String host = "192.168.10.9";
//            List<String> ips = Arrays.asList("192.168.10.9:9090", "192.168.10.9:9090");
        });
        findViewById(R.id.btn2).setOnClickListener(v -> {
            // TODO: 2023/9/12 按钮2
        });

        showIp();
        CfgInfo cfgInfo = SpUtils.getCfgInfo();
        if (cfgInfo.getLastConnIp() != null) {
            connService(cfgInfo.getLastConnIp());
        }
        SDCardUtils.openAllFileAccessPermissions(MainActivity.this, 1);
    }

    private SocketUtils socketUtils = new SocketUtils((e, errMsg) -> {
        e.printStackTrace();
        if (e instanceof ConnectException) {
            ToastUtils.show("连接服务器异常...");
            return;
        }
        ToastUtils.show(errMsg);
    });


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

    private void connService(String ip) {
        String receiverPath = SDCardUtils.getExternalPublicStorageDirectory() + "/01tmp/";
        socketUtils.connService(ip.split(":")[0], Integer.parseInt(ip.split(":")[1]), receiverPath, new SocketUtils.IReceiverMsg() {
            @Override
            public void connSuccess() {
                SpUtils.saveNewIp(ip);
                SpUtils.setLastConnIp(ip);
            }

            @Override
            public void receiverMsg(String receiveMsg) {
                Log.i("调试信息", "接收到服务端信息: " + receiveMsg);
                if (receiveMsg.startsWith("cmd_setClipboardText_")) {
                    //设置客户端剪切板
                    ClipboardUtils.copyText(MainActivity.this, receiveMsg.replace("cmd_setClipboardText_", ""));
                    ToastUtils.show("已复制剪贴板");
                } else if (receiveMsg.startsWith("cmd_getClipboardText")) {
                    //发送客户端剪切板给服务端
                    socketUtils.sendMsgToService("cmd_setClipboardText_" + ClipboardUtils.getText(MainActivity.this));
                    ToastUtils.show("已发送剪贴板");
                } else if (receiveMsg.startsWith("cmd_pullFiles")) {
                    //服务端希望拉取客户端文件数据
                    String[] dirFiles = new File(receiverPath).list();
                    if (receiveMsg.contains("_")) {
                        String[] pullFiles = GsonUtil.fromJsonToStrArr(receiveMsg.replace("cmd_pullFiles_", "").trim());
                        for (String pullFileName : pullFiles) {
                            for (String fileName : dirFiles) {
                                if (fileName.equals(pullFileName)) {
                                    socketUtils.sendFileToService(receiverPath + fileName);
                                }
                            }
                        }
                        socketUtils.sendMsgToService("cmd_pullFiles_success");
                        return;
                    }
                    for (String fileName : dirFiles) {
                        socketUtils.sendFileToService(receiverPath + fileName);
                    }
                    socketUtils.sendMsgToService("cmd_pullFiles_success");
                } else if (receiveMsg.startsWith("cmd_getFiles")) {
                    //服务端希望获取客户端文件数据
                    Log.i("调试信息", "receiverMsg:  " + receiverPath + " " + FileUtils.isFileExist(receiverPath) + " " + FileUtils.isFolderExist(receiverPath));
                    String msg = "cmd_setFiles" + new Gson().toJson(new File(receiverPath).list());
                    socketUtils.sendMsgToService(msg);
                    Log.i("调试信息", "发送遍历到的文件夹列表:  " + msg);
                } else if (receiveMsg.startsWith("cmd_delFiles")) {
                    //服务端希望删除客户端文件数据
                    String[] delFiles = GsonUtil.fromJsonToStrArr(receiveMsg.replace("cmd_delFiles", ""));
                    Log.i("调试信息", "delFiles = " + Arrays.toString(delFiles));
                    for (String file : delFiles) {
                        Log.i("调试信息", "file = " + file);
                        FileUtils.deleteFile(receiverPath + file);
                    }
                    socketUtils.sendMsgToService("cmd_delFiles_success");
                }
            }

            @Override
            public void log(int type, String msg) {
                ToastUtils.show(msg);
            }
        });
    }


    private void takePicture() {
        PictureSelector.create(MainActivity.this).openCamera(SelectMimeType.ofImage())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
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

    }

    @Override
    public int setView() {
        return R.layout.activity_main;
    }
}