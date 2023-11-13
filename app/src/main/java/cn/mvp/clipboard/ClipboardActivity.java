package cn.mvp.clipboard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cn.mvp.R;

public class ClipboardActivity extends AppCompatActivity {
    public static void open(Context context) {
        Intent starter = new Intent(context, ClipboardActivity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard);

        startStopButton = findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopService();
            }
        });
    }

    private Button startStopButton;

    private boolean isServiceRunning;

    private void startStopService() {
//        Intent serviceIntent = new Intent(this, ClipboardListenerService.class);
        if (!isServiceRunning) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(serviceIntent);
//            } else {
//                startService(serviceIntent);
//            }

            // 启动服务
            startService(new Intent(this, ClipboardListenerService.class).setAction("START"));
            startStopButton.setText("停止监听");
        } else {
//            serviceIntent.setAction("STOP");
//            startService(serviceIntent);
// 停止服务
            startService(new Intent(this, ClipboardListenerService.class).setAction("STOP"));
            startStopButton.setText("开始监听");
        }
        isServiceRunning = !isServiceRunning;

    }
}