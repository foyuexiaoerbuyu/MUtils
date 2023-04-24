package cn.mvp.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;

import java.io.Console;
import java.io.IOException;

import cn.mvp.MainActivity;
import cn.mvp.R;
import cn.mvp.global.Constant;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, IReceiveListener {
    private static final String TAG = "调试信息";
    private EditText mEdt1;
    private MultiClient4 mMultiClient4;

    public static void open(Context context) {
        Intent starter = new Intent(context, ChatActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initPermissions();
//        startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);
//        initConnent(null);
        mEdt1 = findViewById(R.id.chat_edt1);
        findViewById(R.id.chat_btn0).setOnClickListener(this);
        findViewById(R.id.chat_btn1).setOnClickListener(this);
//        mEdt1.setText("192.168.");
    }

    private void initConnent(String host) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mMultiClient4 = new MultiClient4(ChatActivity.this, host);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("调试信息", "onCreate:  连接失败... ");
                }
            }
        }).start();
    }

    private static void initPermissions() {
//        String[] perms = {Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE};
//        Log.i("调试信息", "initPermissions: " + !EasyPermissions.hasPermissions(this, perms));
//        if (!EasyPermissions.hasPermissions(this, perms)) {
//            EasyPermissions.requestPermissions(this, "部分权限未通过,可能影响功能使用", 0, perms);
//        }
    }


    int tag = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_btn0:
                initConnent(mEdt1.getText().toString());
                break;
            case R.id.chat_btn1:
                String inputStr = "自动生成信息: " + System.currentTimeMillis() + (++tag);
                if (mMultiClient4 != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mMultiClient4.sendMsg(new Msg(inputStr, null));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                Log.i("调试信息", "onClick:  " + (mMultiClient4 != null) + "  " + inputStr);
                break;
            default:
        }
    }

    @Override
    public void receiveData(String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void err(Exception e) {
        ToastUtils.show(e.getLocalizedMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_SCAN_QRCODE && data != null) {
            String result = data.getStringExtra(Intents.Scan.RESULT);
            Log.i("调试信息", "onActivityResult:  " + "result = " + result);
            initConnent(result);
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }
}