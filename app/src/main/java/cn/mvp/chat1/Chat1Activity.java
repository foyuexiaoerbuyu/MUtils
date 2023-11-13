package cn.mvp.chat1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.Intents;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
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
import cn.mvp.mlibs.utils.GsonUtils;
import cn.mvp.mlibs.utils.SDCardUtils;
import cn.mvp.mlibs.utils.StringUtil;

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
//        editText.setText("172.31.254.234:8887");
        editText.setText(MMKV.defaultMMKV().decodeString(Constant.SP_KEY_WEBSOCKET_ADDRESS));
//        startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);
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
                webSocketClient.send(tmp);
                sb.append("\n客户端发送消息：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("\n");
                sb.append(tmp);
//                sb.append("\n");
                showMessage.setText(sb.toString());
                editText.setText("");
                break;
            case R.id.chat1_btn_conn:
                MMKV.defaultMMKV().encode(Constant.SP_KEY_WEBSOCKET_ADDRESS, editText.getText().toString().trim());
                connService();
                break;
            case R.id.chat1_btn_qr_scan:
                startActivityForResult(new Intent(this, CaptureActivity.class), Constant.REQUEST_CODE_SCAN_QRCODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocketClient != null) {
            //通常在1000到4999之间，不包括保留的代码
            webSocketClient.close(1002, "app断开连接服务");
        }
    }

    private void connService() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
        String host = editText.getText().toString().trim();
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
                Msg msg = GsonUtils.fromJson(message, Msg.class);
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
            Log.i("调试信息", "connService:  " + GsonUtils.toJson(localSocketAddress));
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
            MessageDialog.show(this, "提示", "请选择操作")
                    .setOkButton("复制", new OnDialogButtonClickListener() {
                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {
                            ClipboardUtils.copyToClipboard(Chat1Activity.this, result);
                            return false;
                        }
                    }).setCancelButton("启动服务", new OnDialogButtonClickListener() {
                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {
                            connService();
                            return false;
                        }
                    })
                    .show();
        }
    }
}
