package cn.mvp.chat3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import org.java_websocket.client.WebSocketClient;

import cn.mvp.R;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.utils.StringUtil;

public class Chat3Activity extends AppCompatActivity implements View.OnClickListener {


    private TextView showMessage;
    private EditText editText;
    private WebSocketClient webSocketClient;
    private StringBuilder sb = new StringBuilder();

    public static void open(Context context) {
        Intent starter = new Intent(context, Chat3Activity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat3);
        showMessage = findViewById(R.id.chat3_show_message);
        editText = findViewById(R.id.chat3_edit_text);
        findViewById(R.id.chat3_btn_conn).setOnClickListener(this);
        findViewById(R.id.chat3_btn_qr_scan).setOnClickListener(this);
        findViewById(R.id.chat3_tv_send).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat3_tv_send:
                String tmp = editText.getText().toString().trim();
                if (StringUtil.isBlank(tmp)) {
                    ToastUtils.show("发送消息不能为空");
                    return;
                }
                break;
            case R.id.chat3_btn_conn:
                MMKV.defaultMMKV().encode(Constant.SP_KEY_WEBSOCKET_ADDRESS, editText.getText().toString().trim());

                break;
            case R.id.chat3_btn_qr_scan:
                break;
            default:
                break;
        }
    }
}