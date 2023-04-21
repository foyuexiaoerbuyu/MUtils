package cn.mvp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import cn.mvp.chat.ChatActivity;
import cn.mvp.chat1.Chat1Activity;

public class MainActivity extends AppCompatActivity {

    private boolean isRefuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckBox cbOpenActyShow = findViewById(R.id.main_cb_open_acty_show);
        cbOpenActyShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                
            }
        });
        findViewById(R.id.btn1).setOnClickListener(v -> {
            ChatActivity.open(MainActivity.this);
        });

        findViewById(R.id.btn2).setOnClickListener(v -> {
            Chat1Activity.open(MainActivity.this);
        });
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
    }
}