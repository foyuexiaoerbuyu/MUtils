package cn.mvp.chat1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.tencent.mmkv.MMKV;

import java.net.ConnectException;

import cn.mvp.R;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.utils.ClipboardUtils;
import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.NetworkUtils;
import cn.mvp.mlibs.utils.StringUtil;
import cn.mvp.mlibs.utils.UIUtils;
import cn.mvp.mlibs.weight.dialog.InputAlertDialog;

/**
 * https://blog.huangyuanlove.com/2017/12/25/Android%E4%B8%AD%E4%BD%BF%E7%94%A8WebSocket/
 * WebSocketClient实现 可以用
 */
public class Chat1Activity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 默认连接ip,最后一位为空
     */
    private String mDefConnIp;

    public static void open(Context context) {
        Intent starter = new Intent(context, Chat1Activity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    private TextView showMessage;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);
        showMessage = findViewById(R.id.chat1_show_message);
        editText = findViewById(R.id.chat1_edit_text);
        findViewById(R.id.chat1_btn_conn).setOnClickListener(this);
        findViewById(R.id.chat1_btn_reconnection).setOnClickListener(this);
        findViewById(R.id.chat1_btn_qr_scan).setOnClickListener(this);
        findViewById(R.id.chat1_tv_send).setOnClickListener(this);
        findViewById(R.id.chat1_btn_clipboard_send).setOnClickListener(v -> {
            String text = ClipboardUtils.getText(Chat1Activity.this);
            sendMsg(text);
        });
        editText.setText(MMKV.defaultMMKV().decodeString(Constant.SP_KEY_WEBSOCKET_ADDRESS));
//        startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);
        String num = MMKV.defaultMMKV().decodeString("chat_ip", "");
        StringBuilder sb = new StringBuilder(NetworkUtils.getIpAddressByWifi(this) + ":8887");
        mDefConnIp = sb.replace(sb.lastIndexOf(".") + 1, sb.indexOf(":"), "").toString();
        Log.i("调试信息", "m12:  " + sb);
        connService(sb.replace(sb.lastIndexOf(".") + 1, sb.indexOf(":"), num).toString());
    }

    private void showConnServiceDialog(String str) {
        InputAlertDialog inputAlertDialog = new InputAlertDialog(this);
        inputAlertDialog.setEditText(str);
        inputAlertDialog.setCancelBtnClickDismiss(false);
        inputAlertDialog.setCancelClick("端口", (editText, inputStr) -> {
            if (inputStr.contains(":")) {
                editText.setSelection(inputStr.indexOf(":") + 1, inputStr.length());
            }
        });

        inputAlertDialog.setOkClick(inputStr -> {
            MMKV.defaultMMKV().encode("chat_ip", inputStr.substring(inputStr.lastIndexOf(".") + 1, inputStr.indexOf(":")));
            connService(inputStr);
        });
        inputAlertDialog.show();

        inputAlertDialog.showSoftKeyboard(str.indexOf(":"));

    }

    private void print(String contetn) {
        runOnUiThread(() -> showMessage.append(contetn));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat1_tv_send:
                sendMsg(editText.getText().toString().trim());
                break;
            case R.id.chat1_btn_conn:
                showConnServiceDialog(mDefConnIp);
                break;
            case R.id.chat1_btn_qr_scan:
                startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);
                break;
            case R.id.chat1_btn_reconnection:
                ChatWebSocketClient.getInstance().reconnection();
                break;
            default:
                break;
        }
    }

    private void sendMsg(String msg) {
        if (StringUtil.isEmpty(msg)) {
            ToastUtils.show("消息不能为空");
            return;
        }
        if (ChatWebSocketClient.getInstance().sendMsg(msg)) {
            showMessage.append("客户端消息：" + DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL) + "\n   " + msg + "\n");
            editText.setText("");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if ( ChatWebSocketClient.getInstance().getWebSocketClient() != null) {
//            //通常在1000到4999之间，不包括保留的代码
//            ChatWebSocketClient.getInstance().getWebSocketClient().close(1002, "app断开连接服务");
//        }
    }

    private void connService(String ip) {
        Log.i("调试信息", "connService:  " + ip);
        if (StringUtil.isEmpty(ip)) {
            ToastUtils.show("请在输入框输入服务器地址");
            return;
        }
        ChatWebSocketClient.getInstance().connService(ip, new ChatWebSocketClient.IReceiver() {

            @Override
            public void onOpen() {
                //连接时如果有数据发送输入框内的数据
                String msg = editText.getText().toString();
                if (msg.trim().length() > 0) {
                    sendMsg(msg);
                }
            }

            @Override
            public void onReceiverMsg(String msg) {
                print("服务端消息: " + DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL) +
                        "\n   " + msg + "\n");
            }

            @Override
            public void onErr(Exception e) {
                e.printStackTrace();
                print(e.getMessage());
                if (e instanceof ConnectException) {
                    UIUtils.runOnUIThread(() -> showConnServiceDialog(mDefConnIp));
                }
            }

            @Override
            public void log(String log) {
                print(log);
            }

            @Override
            public void progress(String msg, int currPrs, WebSocketChatMsg fileInfo) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_SCAN_QRCODE && data != null) {
//            String result = data.getStringExtra(Intents.Scan.RESULT);
//            Log.i("调试信息", "onActivityResult: " + result);
//            editText.setText(result);
//            ToastUtils.show("result: " + result);
//            connService(result);
        }
    }
}
