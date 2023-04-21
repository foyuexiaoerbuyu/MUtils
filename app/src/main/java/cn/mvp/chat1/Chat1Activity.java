package cn.mvp.chat1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Date;

import cn.mvp.R;

/**
 * https://blog.huangyuanlove.com/2017/12/25/Android%E4%B8%AD%E4%BD%BF%E7%94%A8WebSocket/
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

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            sb.append("服务器返回数据：");
            sb.append(msg.obj.toString());
            sb.append("\n");
            showMessage.setText(sb.toString());
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);
        showMessage = findViewById(R.id.chat1_show_message);
        editText = findViewById(R.id.chat1_edit_text);
        findViewById(R.id.send).setOnClickListener(this);
        URI serverURI = URI.create("ws://172.31.254.234:8887");
        webSocketClient = new WebSocketClient(serverURI) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                sb.append("onOpen at time：");
                sb.append(new Date());
                sb.append("服务器状态：");
                sb.append(handshakedata.getHttpStatusMessage());
                sb.append("\n");
                showMessage.setText(sb.toString());
            }

            @Override
            public void onMessage(String message) {
                Message handlerMessage = Message.obtain();
                handlerMessage.obj = message;
                handler.sendMessage(handlerMessage);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                sb.append("onClose at time：");
                sb.append(new Date());
                sb.append("\n");
                sb.append("onClose info:");
                sb.append(code);
                sb.append(reason);
                sb.append(remote);
                sb.append("\n");
                showMessage.setText(sb.toString());
            }

            @Override
            public void onError(Exception ex) {
                sb.append("onError at time：");
                sb.append(new Date());
                sb.append("\n");
                sb.append(ex);
                sb.append("\n");
                showMessage.setText(sb.toString());
            }
        };
        webSocketClient.connect();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (webSocketClient.isClosed() || webSocketClient.isClosing()) {
                    Toast.makeText(this, "Client正在关闭", Toast.LENGTH_SHORT).show();
                    webSocketClient.connect();
                    break;
                }
                webSocketClient.send(editText.getText().toString().trim());
                sb.append("客户端发送消息：");
                sb.append(new Date());
                sb.append("\n");
                sb.append(editText.getText().toString().trim());
                sb.append("\n");
                showMessage.setText(sb.toString());
                editText.setText("");
                break;
            default:
                break;
        }
    }
}
