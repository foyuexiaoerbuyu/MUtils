package cn.mvp.chat1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;
import com.tencent.mmkv.MMKV;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.mvp.R;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.utils.ClipboardUtils;
import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.DeviceUtils;
import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.GsonUtil;
import cn.mvp.mlibs.utils.NetworkUtils;
import cn.mvp.mlibs.utils.SDCardUtils;
import cn.mvp.mlibs.utils.StringUtil;
import cn.mvp.mlibs.weight.dialog.InputAlertDialog;

/**
 * https://blog.huangyuanlove.com/2017/12/25/Android%E4%B8%AD%E4%BD%BF%E7%94%A8WebSocket/
 * WebSocketClient实现 可以用
 */
public class Chat1Activity extends AppCompatActivity implements View.OnClickListener {

    public static void open(Context context) {
        Intent starter = new Intent(context, Chat1Activity.class);
//    starter.putExtra();
        context.startActivity(starter);
    }

    private TextView showMessage;
    private EditText editText;
    private WebSocketClient webSocketClient;
    private StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);
        showMessage = findViewById(R.id.chat1_show_message);
        editText = findViewById(R.id.chat1_edit_text);
        findViewById(R.id.chat1_btn_conn).setOnClickListener(this);
        findViewById(R.id.chat1_btn_qr_scan).setOnClickListener(this);
        findViewById(R.id.chat1_tv_send).setOnClickListener(this);
        findViewById(R.id.chat1_btn_clipboard_send).setOnClickListener(v -> {
            String text = ClipboardUtils.getText(Chat1Activity.this);
            Log.i("调试信息", "onCreate:  " + text);
            sendMsg(text);
        });
//        editText.setText("172.31.254.234:8887");
        editText.setText(MMKV.defaultMMKV().decodeString(Constant.SP_KEY_WEBSOCKET_ADDRESS));
//        startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);

        showConnServiceDialog();
    }

    private void showConnServiceDialog() {
        InputAlertDialog inputAlertDialog = new InputAlertDialog(this);
        String ip = NetworkUtils.getIpAddressByWifi(this);
        Log.i("调试信息", "m1:  " + ip);
        String str = ip.substring(0, ip.lastIndexOf(".") + 1);
        inputAlertDialog.setEditText(str + ":8887");
        inputAlertDialog.setCancelBtnClickDismiss(false);
        inputAlertDialog.setCancelClick("端口", (editText, inputStr) -> {
            if (inputStr.contains(":")) {
                editText.setSelection(inputStr.indexOf(":") + 1, inputStr.length());
            }
        });
        inputAlertDialog.setOkClick(this::connService);
        inputAlertDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputAlertDialog.show();

        inputAlertDialog.showInputDialog(str.length());

    }

    private void print(String contetn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage.setText(contetn);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat1_tv_send:
                String tmp = editText.getText().toString().trim();
                if (StringUtil.isBlank(tmp)) {
                    ToastUtils.show("发送消息不能为空");
                    return;
                }
//括号里是client没有初试过，用别的方法判断也可以，反正一定要判断到 client没有初始化过。
                if (webSocketClient == null) {
                    return;
                }
                if (!webSocketClient.isOpen()) {
                    if (webSocketClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                        try {
                            webSocketClient.connect();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else if (webSocketClient.getReadyState().equals(ReadyState.CLOSING) || webSocketClient.getReadyState().equals(ReadyState.CLOSED)) {
                        webSocketClient.reconnect();
                    }
                    return;
                }
//                if (webSocketClient.isClosed() || webSocketClient.isClosing()) {
//                    Toast.makeText(this, "Client正在关闭", Toast.LENGTH_SHORT).show();
//                    webSocketClient.connect();
//                    break;
//                }
                sendMsg(tmp);
                break;
            case R.id.chat1_btn_conn:
                showConnServiceDialog();
                break;
            case R.id.chat1_btn_qr_scan:
                startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);
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
        webSocketClient.send(msg);
        sb.append("\n客户端发送消息：");
        sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
        sb.append("\n");
        sb.append(msg);
//                sb.append("\n");
        showMessage.setText(sb.toString());
        editText.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocketClient != null) {
            //通常在1000到4999之间，不包括保留的代码
            webSocketClient.close(1002, "app断开连接服务");
        }
    }

    private void connService(String host) {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
        if (StringUtil.isEmpty(host)) {
            ToastUtils.show("请在输入框输入服务器地址");
            return;
        }
        URI serverURI = URI.create("ws://" + host);//"ws://172.31.254.234:8887"
        Map<String, String> headers = new HashMap<>();

        headers.put("client_name", DeviceUtils.getDeviceModel() + "_" + DeviceUtils.getManufacturer());//连接名
        webSocketClient = new WebSocketClient(serverURI, headers) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                sb.append("onOpen at time：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("服务器状态：");
                sb.append(handshakedata.getHttpStatusMessage());
                sb.append("\n");
                print(sb.toString());
                Log.i("调试信息", "onOpen:  " + sb);
            }

            @Override
            public void onMessage(String message) {
                Msg msg = GsonUtil.fromJson(message, Msg.class);
                if (msg.getMsgType() == 0) {
                    sb.append("\n服务端返回数据：").append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL)).append("\n");
                    sb.append(msg.getMsgContent());
                    print(sb.toString());
                } else {
                    File directory = SDCardUtils.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String name = directory + File.separator + msg.getFileName();
                    Log.i("调试信息", "文件名:  " + name);
                    FileUtils.writeFile(name, msg.getFileData(), true);
                    Log.i("调试信息", "File received and saved!");
                }
                Log.i("调试信息", "onMessage:  " + message);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                super.onMessage(bytes);

                File directory = SDCardUtils.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String name = directory + "/largefile.apk";
                Log.i("调试信息", "文件名:  " + name);
                FileUtils.writeFile(name, bytes, true);
                Log.i("调试信息", "File received and saved!");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                sb.append("onClose at time：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("\n");
                sb.append(" 与服务器连接断开: ").append(code);
                sb.append(" 关闭原因: ").append(reason);
                sb.append(remote);
                sb.append("\n");
                print(sb.toString());
                Log.i("调试信息", "onClose:  " + sb);
                if (webSocketClient != null) {
                    webSocketClient.close();
                }

            }

            @Override
            public void onError(Exception ex) {
                sb.append("onError at time：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("\n");
                sb.append(ex);
                sb.append("\n");
                print(sb.toString());
                Log.i("调试信息", "onError:  " + sb);
            }
        };
        InetSocketAddress localSocketAddress = webSocketClient.getLocalSocketAddress();
        if (localSocketAddress != null) {
            Log.i("调试信息", "connService:  " + localSocketAddress.getHostString());
            Log.i("调试信息", "connService:  " + localSocketAddress.getHostName());
            Log.i("调试信息", "connService:  " + localSocketAddress.getPort());
            Log.i("调试信息", "connService:  " + GsonUtil.toJson(localSocketAddress));
        }
        webSocketClient.connect();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_SCAN_QRCODE && data != null) {
            String result = data.getStringExtra(Intents.Scan.RESULT);
            Log.i("调试信息", "onActivityResult: " + result);
            editText.setText(result);
            ToastUtils.show("result: " + result);
            connService(result);
        }
    }
}
