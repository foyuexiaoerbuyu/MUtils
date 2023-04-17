package cn.mvp.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import cn.mvp.R;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, IReceiveListener {
    private static final String TAG = "调试信息";
    private EditText mEdt1;
    private MultiClient4 mMultiClient4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);
        initPermissions();

        initConnent();
        mEdt1 = findViewById(R.id.edt1);
        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        mEdt1.setText("自动生成信息: " + System.currentTimeMillis());
    }

    private void initConnent() {
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   mMultiClient4 = new MultiClient4(ChatActivity.this);
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
            case R.id.btn0:
                mEdt1.setText("自动生成信息: " + System.currentTimeMillis());
                break;
            case R.id.btn1:
                String inputStr = mEdt1.getText().toString() + (++tag);
                if (mMultiClient4 != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mMultiClient4.sendMsg(new Msg(inputStr,null));
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
               Toast.makeText(ChatActivity.this,content,Toast.LENGTH_SHORT).show();
           }
       });
    }

    @Override
    public void err(Exception e) {

    }
}